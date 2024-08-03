package net.smyler.terramap.http;

/**
 * Cache usage statistics.
 *
 * @param entries   number of entries in the cache
 * @param size      total storage space used by the cache
 * @param type      the type of cache in use
 */
public record CacheStatistics(long entries, long size, CacheType type) {

    public enum CacheType {

        /**
         * Cache is fully on disk.
         */
        DISK,

        /**
         * Cache is fully in memory.
         * Should not be used outside of development.
         */
        MEMORY,

        /**
         * Cache uses a mix of disk and memory storage.
         */
        HYBRID,

    }

}
