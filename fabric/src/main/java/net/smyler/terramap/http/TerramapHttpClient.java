package net.smyler.terramap.http;

import org.apache.logging.log4j.Logger;

import java.net.URI;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Objects;
import java.util.concurrent.CancellationException;
import java.util.concurrent.CompletableFuture;


public class TerramapHttpClient implements HttpClient {

    private static final String USER_AGENT = "Experimental Terramap version https://github.com/SmylerMC/terramap";
    private final java.net.http.HttpClient client = java.net.http.HttpClient.newBuilder().build();
    private final Logger logger;

    public TerramapHttpClient(Logger logger) {
        this.logger = logger;
        logger.warn("You are using an HTTP client that does not support caching. This is not suitable for production.");
    }

    @Override
    public CompletableFuture<byte[]> get(String url) {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("User-Agent", USER_AGENT)
                .GET()
                .build();
        this.log(request);
        CompletableFuture<HttpResponse<byte[]>> response = this.client.sendAsync(request, HttpResponse.BodyHandlers.ofByteArray());
        CompletableFuture<byte[]> bytes = response.thenApply(HttpResponse::body);
        bytes.exceptionally(t -> {
            if (t instanceof CancellationException) {
                response.cancel(true);
            }
            return null;
        });
        return bytes;
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
        this.logger.debug("{} {} {}",
                request.method(),
                request.uri(),
                request.version().map(Objects::toString).orElse("")
        );
    }

}
