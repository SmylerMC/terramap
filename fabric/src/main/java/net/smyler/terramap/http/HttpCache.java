package net.smyler.terramap.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.net.URI;
import java.util.function.Predicate;

public interface HttpCache {

    void put(@NotNull URI uri, long lastModified, long maxAge, @Nullable String etag, boolean immutable, boolean mustRevalidate, byte @NotNull [] body);

    @Nullable CacheEntry lookup(URI uri);

    CacheStatistics statistics() throws IOException;

    CacheStatistics cleanup(Predicate<CacheEntry> predicate) throws IOException;

}
