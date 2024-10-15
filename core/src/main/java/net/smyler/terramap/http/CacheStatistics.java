package net.smyler.terramap.http;

/**
 * Cache usage statistics.
 */
public class CacheStatistics {

    public final long entries;
    public final long size;
    public final CacheType type;

    /**
     * @param entries   number of entries in the cache
     * @param size      total storage space used by the cache
     * @param type      the type of cache in use
     */
    public CacheStatistics(long entries, long size, CacheType type) {
        this.entries = entries;
        this.size = size;
        this.type = type;
    }

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
