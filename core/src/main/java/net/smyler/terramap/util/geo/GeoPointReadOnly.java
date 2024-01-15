package net.smyler.terramap.util.geo;

public final class GeoPointReadOnly extends GeoPointAbstract<GeoPointImmutable> {

    private final GeoPoint<?> delegate;

    public GeoPointReadOnly(GeoPoint<?> delegate) {
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
