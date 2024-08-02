package net.smyler.terramap.http;

import net.smyler.smylib.Pair;
import net.smyler.smylib.Strings;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinWorkerThread;
import java.util.concurrent.atomic.AtomicLong;

import static java.lang.Math.max;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import static java.util.Arrays.stream;
import static net.smyler.smylib.Objects.optionalBiMapSupplier;


public class TerramapHttpClient implements HttpClient {

    private final ForkJoinPool forkJoinPool = new ForkJoinPool(20, HttpWorkerThread::new, this::unhandledException, true);
    private final AtomicLong workerCounter = new AtomicLong(0);

    private static final String USER_AGENT = "Experimental Terramap version https://github.com/SmylerMC/terramap";
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
            .ofPattern("EEE, dd MMM yyyy HH:mm:ss 'GMT'", Locale.ENGLISH)
            .withZone(ZoneId.of("GMT"));
    private static final int HTTP_OK = 200;
    private static final int HTTP_NO_CONTENT = 204;
    private static final int HTTP_NOT_MODIFIED = 304;

    private final java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder()
            .followRedirects(java.net.http.HttpClient.Redirect.NORMAL)
            .executor(this.forkJoinPool)
            .build();
    private final Logger logger;
    private final HttpCache cache;

    private static final float CACHE_HEURISTIC = 0.25f;

    public TerramapHttpClient(Logger logger, HttpCache cache) {
        this.logger = logger;
        this.cache = cache;
    }

    @Override
    public CompletableFuture<byte[]> get(String url) {

        URI uri = URI.create(url);

        // Lookup cache, use it if fresh
        CacheEntry cache = this.cache.lookup(uri);
        if (cache != null && cache.isFresh()) {
            this.logger.debug("Fresh: {}", uri);
            return CompletableFuture.supplyAsync(() -> this.readCache(cache), this.forkJoinPool);
        }

        // Send HTTP request
        HttpRequest request = this.createRequest(uri, cache);
        this.log(request);
        CompletableFuture<HttpResponse<byte[]>> response = this.client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray());
        CompletableFuture<Pair<HttpResponse<byte[]>, byte[]>> content = response.thenApplyAsync(r -> this.getContent(r, cache), this.forkJoinPool);
        CompletableFuture<byte[]> bytes = content.thenApply(Pair::right);

        content.thenAcceptAsync(p -> this.cacheResponse(p.left(), p.right()), this.forkJoinPool);

        // Make sure a cancellation of the downstream future is propagated to the request
        bytes.exceptionally(t -> {
            if (t instanceof CancellationException) {
                response.cancel(true);
            }
            return null;
        });

        return bytes;
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

    private Pair<HttpResponse<byte[]>, byte[]> getContent(HttpResponse<byte[]> response, CacheEntry cache) {
        byte[] content;
        if (response.statusCode() == HTTP_NOT_MODIFIED) {
            if (cache == null) {
                this.logger.warn("Unexpected response: 304 not modified but cache is null");
                return Pair.of(response, new byte[0]);
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
        return Pair.of(response, content);
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
        logger.warn(
                "Trying to set max concurrent requests to {}: {}. This is not yet supported",
                host,
                maxConcurrentRequests
        );
    }

    private void log(HttpRequest request) {
        this.logger.trace("{} {} {}",
                request.method(),
                request.uri(),
                request.version().map(Objects::toString).orElse("")
        );
    }

    private class HttpWorkerThread extends ForkJoinWorkerThread {
        protected HttpWorkerThread(ForkJoinPool pool) {
            super(pool);
            this.setName("HTTP thread " + TerramapHttpClient.this.workerCounter.getAndIncrement());
        }
    }

    private void unhandledException(Thread thread, Throwable throwable) {
        this.logger.error("Unhandled exception in HTTP client");
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

}
