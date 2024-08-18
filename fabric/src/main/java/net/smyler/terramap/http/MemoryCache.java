package net.smyler.terramap.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;

import static net.smyler.terramap.http.CacheStatistics.CacheType.MEMORY;

public class MemoryCache implements HttpCache {

    private final ConcurrentHashMap<URI, MemoryCacheEntry> cache = new ConcurrentHashMap<>();


    @Override
    public void put(@NotNull URI uri, long lastModified, long maxAge, @Nullable String etag, boolean immutable, boolean mustRevalidate, byte @NotNull [] body) {
        byte[] copy = Arrays.copyOf(body, body.length);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.cache.put(uri, new MemoryCacheEntry(new CacheEntry(
                uri,
                lastModified, maxAge, etag,
                immutable,
                mustRevalidate,
                () -> new ByteArrayInputStream(copy)
        ), body.length));
    }

    @Override
    public @Nullable CacheEntry lookup(URI uri) {
        MemoryCacheEntry entry = this.cache.get(uri);
        if (entry == null) {
            return null;
        }
        return entry.entry;
    }

    @Override
    public CacheStatistics statistics() {
        AtomicLong counter = new AtomicLong();
        long size = this.cache.values().stream()
                .peek(e -> counter.getAndIncrement())
                .map(MemoryCacheEntry::size)
                .reduce(0L, Long::sum);
        return new CacheStatistics(counter.get(), size, MEMORY);
    }

    @Override
    public CacheStatistics cleanup(Predicate<CacheEntry> predicate) {
        Collection<MemoryCacheEntry> values = this.cache.values();
        long removedCount = 0;
        long removedSize = 0;
        for (Iterator<MemoryCacheEntry> iterator = values.iterator(); iterator.hasNext(); ) {
            MemoryCacheEntry entry = iterator.next();
            if (predicate.test(entry.entry)) {
                iterator.remove();
                removedSize += entry.size();
                removedCount++;
            }
        }
        return new CacheStatistics(removedCount, removedSize, MEMORY);
    }

    private record MemoryCacheEntry(CacheEntry entry, long size) {

    }

}