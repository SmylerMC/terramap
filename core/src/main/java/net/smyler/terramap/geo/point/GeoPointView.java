package net.smyler.terramap.geo.point;

import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

/**
 * A read-only view on another {@link GeoPoint}.
 *
 * @author Smyler
 */
public final class GeoPointView implements GeoPoint {

    private final @NotNull GeoPoint delegate;

    public GeoPointView(@NotNull GeoPoint delegate) {
        requireNonNull(delegate);
        this.delegate = delegate;
    }

    @Override
    public double latitude() {
        return this.delegate.latitude();
    }

    @Override
    public double longitude() {
        return this.delegate.longitude();
    }

    @Override
    public String toString() {
        return "GeoPointView[" + this.delegate + ']';
    }

}
