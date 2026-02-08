package net.smyler.terramap.http;

import java.util.concurrent.CompletableFuture;
import net.smyler.terramap.Terramap;

/**
 * Asynchronous HTTP client abstraction.
 * The implementation should focus on implementing
 * efficient GET requests with compliant caching.
 * Implementations should support cancellation of the CompletableFuture.
 * <br>
 * Access the singleton from {@link Terramap#http()}
 *
 * @author Smyler
 */
public interface HttpClient {

    CompletableFuture<byte[]> get(String url);

    void setMaxConcurrentRequests(String host, int maxConcurrentRequests);

}
