package net.smyler.terramap.http;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.net.URI;

public interface HttpCache {

    void put(@NotNull URI uri, long lastModified, long maxAge, @Nullable String etag, boolean immutable, boolean mustRevalidate, byte @NotNull [] body);

    @Nullable CacheEntry lookup(URI uri);

}
