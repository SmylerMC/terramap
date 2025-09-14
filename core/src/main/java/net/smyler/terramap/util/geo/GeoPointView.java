package net.smyler.terramap.util.geo;

/**
 * A read-only view on a mutable {@link GeoPoint}.
 *
 * @author Smyler
 */
public final class GeoPointView extends GeoPointAbstract {

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
    public GeoPointImmutable withLongitude(double longitude) {
        return new GeoPointImmutable(longitude, this.delegate.latitude());
    }

    @Override
    public GeoPointImmutable withLatitude(double latitude) {
        return new GeoPointImmutable(this.delegate.longitude(), latitude);
    }

}
