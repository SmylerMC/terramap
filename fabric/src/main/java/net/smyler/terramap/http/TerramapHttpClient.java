package net.smyler.terramap.http;

import net.smyler.smylib.Strings;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.Objects.optionalBiMapSupplier;
import static net.smyler.terramap.http.CacheStatistics.CacheType.ERROR;
import static net.smyler.terramap.http.HttpStatusCodes.*;
import static net.smyler.smylib.Strings.isNullOrEmpty;


public class TerramapHttpClient implements CachingHttpClient {

    private final ForkJoinPool forkJoinPool = new ForkJoinPool(20, HttpWorkerThread::new, this::unhandledException, true);
    private final ForkJoinPool semaphoreAcquireExecutor = new ForkJoinPool(1, HttpWorkerThread::new, this::unhandledException, true);
    private final AtomicLong workerCounter = new AtomicLong(0);

    private static final String USER_AGENT = "Experimental Terramap version https://github.com/SmylerMC/terramap";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
            .withZone(ZoneId.of("GMT"));

    private final ConcurrentHashMap<String, Semaphore> concurrentRequestsCounters = new ConcurrentHashMap<>();

    private final java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
            .executor(this.forkJoinPool)
            .build();
    private final Logger logger;
    private final HttpCache cache;

    private static final float CACHE_HEURISTIC = 0.25f;

    public TerramapHttpClient(Logger logger, Path cacheDirectory) {
        this.logger = logger;HttpCache cache;
        try {
            requireNonNull(cacheDirectory);
            Files.createDirectories(cacheDirectory);
            cache = new DiskCache(cacheDirectory, this.logger);
        } catch (Exception e) {
            this.logger.warn("Failed to create cache directory, falling back to memory cache");
            this.logger.catching(e);
            cache = new MemoryCache();
        }
        this.cache = cache;
    }

    @Override
    public CompletableFuture<byte[]> get(String url) {

        URI uri = URI.create(url);
        String hostname = uri.getHost();

        // Lookup cache, use it if fresh
        CacheEntry cache = this.cache.lookup(uri);
        if (cache != null && cache.isFresh()) {
            return CompletableFuture.supplyAsync(() -> {
                byte[] data =  this.readCache(cache);
                this.logger.debug("[   ] {} {} (was fresh)", data.length, uri);
                return data;
            }, this.forkJoinPool);
        }

        //final Semaphore semaphore = null;
        final Semaphore semaphore = this.concurrentRequestsCounters.get(hostname);

        // Prepare request
        HttpRequest request = this.createRequest(uri, cache);

        final RequestContext context = new RequestContext(
                uri, hostname, request, cache, semaphore
        );

        // Request semaphore to respect concurrent request limits
        CompletableFuture<Void> permitAcquired = CompletableFuture.runAsync(context::acquirePermit, this.semaphoreAcquireExecutor);

        // Send the request
        CompletableFuture<HttpResponse<byte[]>> response = permitAcquired.thenComposeAsync(context::sendRequest,
                this.forkJoinPool
        );

        response.exceptionally(t -> {
            this.logger.error(t);
            return null;
        });

        // Release concurrent request semaphore
        response.whenCompleteAsync(context::releasePermit, this.forkJoinPool);

        // Get resource content, either from the request or the cache
        CompletableFuture<byte[]> content = response.thenApplyAsync(context::readContent, this.forkJoinPool);

        // Update cache
        content.thenCombineAsync(response, context::cache, this.forkJoinPool);

        // Make sure a cancellation of the downstream future is propagated to the request
        content.exceptionally(t -> {
            if (t instanceof CancellationException) {
                this.logger.trace("Canceled request to {}", uri);
                //response.cancel(true);
            }
            return null;
        });

        this.logger.trace("Queued request to {}", uri);
        return content;
    }

    private HttpRequest createRequest(URI uri, CacheEntry cache) {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.GET()
                .uri(uri)
                .header("User-Agent", USER_AGENT);
        if (cache != null) {
            if (cache.etag() != null) {
                builder.header("If-None-Match", cache.etag());
            } else {
                builder.header("If-Modified-Since", DATE_TIME_FORMATTER.format(cache.lastModifiedDateTime()));
            }
        }
        return builder.build();
    }

    private byte @Nullable [] getContent(HttpResponse<byte[]> response, CacheEntry cache) {
        byte[] content;
        if (response.statusCode() == HTTP_NOT_MODIFIED) {
            if (cache == null) {
                this.logger.warn("Unexpected response: 304 not modified but cache is null");
                return null;
            }
            content = this.readCache(cache);
        } else {
            content = response.body();
        }
        this.logger.debug(
                "[{}] {} {} (was {})",
                response.statusCode(), content.length, response.uri(),
                cache == null ? "not cached": cache.isFresh() ? "fresh" : "stale"
        );
        return content;
    }

    private void cacheResponse(HttpResponse<byte[]> response, byte[] content) {
        int statusCode = response.statusCode();
        if (statusCode != HTTP_OK && statusCode != HTTP_NO_CONTENT && statusCode != HTTP_NOT_MODIFIED) {
            return;
        }
        Optional<String> header = response.headers().firstValue("Cache-Control");
        if (header.isPresent()) {
            this.cacheResponseWithCacheControl(response, header.get(), content);
        } else {
            this.cacheResponseWithHeuristics(response, content);
        }
    }

    private void cacheResponseWithCacheControl(HttpResponse<byte[]> response, String cacheControl, byte[] content) {
        CacheControlResponseDirectives directives = CacheControlResponseDirectives.from(cacheControl);

        if (directives.noStore() || directives.mustUnderstand()) {
            this.logger.trace("Cache no-store for {}", response.uri());
            return;
        }

        String etag = response.headers().firstValue("Etag")
                .orElse(null);
        Optional<Long> expires = response.headers().firstValue("Expires")
                .flatMap(this::parseHttpDate);
        Optional<Long> date = response.headers().firstValue("Date")
                .flatMap(this::parseHttpDate);

        long age = response.headers().firstValue("Age")
                .flatMap(Strings::parseOptionalLong)
                .orElse(0L);
        long lastModified = response.headers()
                .firstValue("Last-Modified")
                .flatMap(this::parseHttpDate)
                .orElseGet(() -> date.orElse(round(currentTimeMillis() / 1_000d) - age));
        boolean immutable = directives.immutable();
        long maxAge;

        if (directives.noCache()) {
            maxAge = 0;
        } else {
            maxAge = directives.maxAge()
                    .or(optionalBiMapSupplier(expires, date, (e, d) -> e - d))
                    .orElse(0L);
        }

        URI uri = response.uri();
        this.logger.trace(
                "Cache control for {}, lastModified={} age={} max-age={} etag='{}' immutable={}",
                uri, lastModified, age, maxAge, etag, immutable
        );
        this.cache.put(response.uri(), lastModified, maxAge, etag, immutable, directives.mustRevalidate(), content);
    }

    private void cacheResponseWithHeuristics(HttpResponse<byte[]> response, byte[] content) {
        String etagHeader = response.headers().firstValue("Etag")
                .orElse(null);
        long date = response.headers().firstValue("Date")
                .flatMap(this::parseHttpDate)
                .orElseGet(() -> round(currentTimeMillis() / 1_000d));
        long lastModified = response.headers().firstValue("Last-Modified")
                .flatMap(this::parseHttpDate)
                .orElse(date);
        long age = response.headers().firstValue("Age")
                .flatMap(Strings::parseOptionalLong)
                .orElse(date - lastModified);
        long maxAge = max(0, round(age * CACHE_HEURISTIC));
        this.logger.trace(
                "Cache heuristic for {}: lastModified={} age={} maxAge={} etag={}",
                response.uri(), lastModified, age, maxAge, etagHeader
        );
        this.cache.put(response.uri(), lastModified, maxAge, etagHeader, false, true, content);
    }

    private Optional<Long> parseHttpDate(String date) {
        return Optional.ofNullable(
                switch(DATE_TIME_FORMATTER.parse(date)) {
                    case LocalDateTime e -> e.toEpochSecond(ZoneOffset.UTC);
                    case ZonedDateTime zt -> zt.toEpochSecond();
                    default -> null;
                }
        );
    }

    private byte[] readCache(final CacheEntry cache) {
        try (InputStream in = cache.body().get()) {
            return in.readAllBytes();
        } catch (IOException e) {
            this.logger.warn("Error while reading HTTP cache");
            this.logger.catching(e);
            return new byte[0];
        }
    }

    @Override
    public void setMaxConcurrentRequests(String host, int maxConcurrentRequests) {
        // We need to replace the old semaphore, we can't just release to the desired number,
        // that could cause a thread condition if a request is pending
        host = URI.create(host).getHost();
        if (isNullOrEmpty(host)) {
            return;
        }
        this.logger.debug("Setting max concurrent request to host {} to {}", host, maxConcurrentRequests);
        Semaphore newSemaphore = new Semaphore(maxConcurrentRequests);
        this.concurrentRequestsCounters.put(host, newSemaphore);
    }

    @Override
    public CompletableFuture<CacheStatistics> cacheStatistics() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.cache.statistics();
            } catch (IOException e) {
                this.logger.warn("Computing cache statistics failed");
                this.logger.catching(e);
                return new CacheStatistics(0, 0, ERROR);
            }
        }, this.forkJoinPool);
    }

    @Override
    public CompletableFuture<CacheStatistics> cacheCleanup() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.cache.cleanup(e -> !e.isFresh() && e.age() > e.maxAge() * 10);
            } catch (IOException e) {
                this.logger.warn("Cache cleanup failed");
                this.logger.catching(e);
                return new CacheStatistics(0, 0, ERROR);
            }
        }, this.forkJoinPool);
    }

    @Override
    public CompletableFuture<CacheStatistics> cacheClear() {
        return CompletableFuture.supplyAsync(() -> {
            try {
                return this.cache.cleanup(e -> true);
            } catch (IOException e) {
                this.logger.warn("Cache clear failed");
                this.logger.catching(e);
                return new CacheStatistics(0, 0, ERROR);
            }
        }, this.forkJoinPool);
    }

    private class HttpWorkerThread extends ForkJoinWorkerThread {
        protected HttpWorkerThread(ForkJoinPool pool) {
            super(pool);
            this.setName("HTTP thread " + TerramapHttpClient.this.workerCounter.getAndIncrement());
        }
    }

    private void unhandledException(Thread thread, Throwable throwable) {
        this.logger.error("Unhandled exception in HTTP client in thread {}", thread.getName());
        this.logger.catching(throwable);
    }

    private static class CacheControlResponseDirectives {
        private final Map<String, String> content = new HashMap<>();

        public boolean mustRevalidate() {
            return this.content.containsKey("must-revalidate");
        }

        public boolean noCache() {
            // In reality no-cache is more powerful than just a boolean switch
            return this.content.containsKey("no-cache");
        }

        public boolean noStore() {
            return this.content.containsKey("no-store");
        }

        public boolean immutable() {
            return this.content.containsKey("immutable");
        }

        public boolean mustUnderstand() {
            return this.content.containsKey("must-understand");
        }

        public Optional<Long> maxAge() {
            return Optional.ofNullable(this.content.get("max-age")).flatMap(Strings::parseOptionalLong);
        }

        public static CacheControlResponseDirectives from(String headerValue) {
            CacheControlResponseDirectives directives = new CacheControlResponseDirectives();
            stream(headerValue.split(","))
                    .map(String::strip)
                    .map(s -> s.split("=", 2))
                    .forEach(kv -> directives.content.put(kv[0].toLowerCase(), kv.length > 1 ? kv[1] : null));
            return directives;
        }

    }

    private class RequestContext {

        private final @NotNull URI uri;
        private final @NotNull String host;

        private final @NotNull HttpRequest request;
        private final @Nullable CacheEntry cache;
        private final @Nullable Semaphore semaphore;

        private final Logger logger = TerramapHttpClient.this.logger;

        RequestContext(@NotNull URI uri, @NotNull String host, @NotNull HttpRequest request, @Nullable CacheEntry cache, @Nullable Semaphore semaphore) {
            this.uri = uri;
            this.host = host;
            this.request = request;
            this.cache = cache;
            this.semaphore = semaphore;
        }

        void acquirePermit() {
            try {
                if (this.semaphore != null) {
                    this.semaphore.acquire();
                    this.logger.trace("Acquired semaphore for {}", this.uri);
                }
            } catch (InterruptedException e) {
                this.logger.error("Interrupted when acquiring request semaphore for host {}", this.host);
                this.logger.error(e);
            }
        }

        CompletableFuture<HttpResponse<byte[]>> sendRequest(Void v) {
            this.logger.trace(
                    "{} {} {}",
                    this.request.method(),
                    this.request.version().map(Object::toString).orElse(""),
                    this.request.uri()
            );
            return TerramapHttpClient.this.client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray());
        }

        void releasePermit(HttpResponse<byte[]> response, Throwable throwable) {
            if (this.semaphore != null) {
                this.semaphore.release();
                this.logger.trace("Released semaphore for {}", this.host);
            }
        }

        byte[] readContent(HttpResponse<byte[]> response) {
            return TerramapHttpClient.this.getContent(response, this.cache);
        }

        Void cache(byte[] content, HttpResponse<byte[]> response) {
            // Content might have been read from cache

            if (content == null) {
                // HTTP errors should null
                return null;
            }

            TerramapHttpClient.this.cacheResponse(response, content);
            return null;
        }

    }

    private Thread createThread(Runnable task) {
        return Thread.ofVirtual()
                .name("Terramap HTTP " + this.workerCounter.incrementAndGet())
                .unstarted(task);
    }

}
