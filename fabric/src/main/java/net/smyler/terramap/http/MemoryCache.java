package net.smyler.terramap.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.net.URI;
import java.util.Arrays;
import java.util.concurrent.ConcurrentHashMap;

public class MemoryCache implements HttpCache {

    ConcurrentHashMap<URI, CacheEntry> cache = new ConcurrentHashMap<>();

    @Override
    public void put(@NotNull URI uri, long lastModified, long maxAge, @Nullable String etag, boolean immutable, boolean mustRevalidate, byte @NotNull [] body) {
        byte[] copy = Arrays.copyOf(body, body.length);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        this.cache.put(uri, new CacheEntry(
                uri,
                lastModified, maxAge, etag,
                immutable,
                mustRevalidate,
                () -> new ByteArrayInputStream(copy)
        ));
    }

    @Override
    public @Nullable CacheEntry lookup(URI uri) {
        return this.cache.get(uri);
    }

}