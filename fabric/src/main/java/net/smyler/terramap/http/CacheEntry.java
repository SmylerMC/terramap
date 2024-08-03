package net.smyler.terramap.http;

import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.net.URI;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.function.Supplier;

import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;

public record CacheEntry(URI uri, long lastModified, long maxAge, @Nullable String etag, boolean immutable, boolean mustRevalidate, Supplier<InputStream> body) {

    public boolean isFresh() {
        return currentTimeMillis() / 1_000 < this.lastModified + this.maxAge;
    }

    public LocalDateTime lastModifiedDateTime() {
        return LocalDateTime.ofEpochSecond(this.lastModified, 0, ZoneOffset.UTC);
    }

    public long age() {
        return round(currentTimeMillis() / 1000d) - this.lastModified;
    }

}
