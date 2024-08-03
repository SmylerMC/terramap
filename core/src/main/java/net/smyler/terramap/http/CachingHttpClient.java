package net.smyler.terramap.http;

import java.util.concurrent.CompletableFuture;

/**
 * ALL http implementation used by Terramap should implement caching,
 * but may not necessarily expose programmatically for management
 * (e.g. if Terramap uses Terra++'s http client, Terra++ takes care of
 * cache management).
 */
public interface CachingHttpClient extends HttpClient {

    /**
     * Computes statistics about the current cache usage.
     *
     * @return a future of the client's cache statistics
     */
    CompletableFuture<CacheStatistics> cacheStatistics();

    /**
     * Performs regular maintenance on cache, removing long stale entries etc.
     *
     * @return a future containing statistics on the entries that were removed from the cache
     */
    CompletableFuture<CacheStatistics> cacheCleanup();

    /**
     * Fully clears the cache.
     *
     * @return a future containing statistics on the entries that were removed from the cache
     */
    CompletableFuture<CacheStatistics> cacheClear();

}
