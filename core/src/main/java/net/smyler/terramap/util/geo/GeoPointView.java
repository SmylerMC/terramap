package net.smyler.terramap.util.geo;

/**
 * A read-only view on a mutable {@link GeoPoint}.
 *
 * @author Smyler
 */
public final class GeoPointView implements GeoPoint {

    private final GeoPoint delegate;

    public GeoPointView(GeoPoint delegate) {
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
