package fr.thesmyler.terramap.util.geo;

import fr.thesmyler.terramap.util.Immutable;
import fr.thesmyler.terramap.util.Mutable;
import fr.thesmyler.terramap.util.math.Vec2dImmutable;
import net.buildtheearth.terraplusplus.util.geo.LatLng;

/**
 * A point in the WGS:84 coordinate system.
 * Two points are considered equal when they represent the same place,
 * which means that points corresponding to the North Pole will be equal even if they have different longitudes.
 * Similarly, points at the same latitude on the antimeridian will be equal,
 * independently of whether their longitude is -180 or 180.
 *
 * @param <T> the type of the implementing subclass that any method returning a {@link GeoPoint} will return
 * @author SmylerMC
 *
 */
public interface GeoPoint<T extends GeoPoint<?>> extends Mutable<GeoPointImmutable>, Immutable<GeoPointMutable> {

    /**
     * @return this point's latitude, in the appropriate [-90°, 90°] range
     */
    double latitude();

    /**
     * @return this point's longitude, in the appropriate [-180°, 180°] range
     */
    double longitude();

    /**
     * Estimates the distance between this point and another one as best as possible, ignoring altitude.
     *
     * @param other another point
     *
     * @return the distance between this point and the other, in meters
     */
    default double distanceTo(GeoPoint<?> other) {
        return GeoUtil.distanceHaversine(this.longitude(), this.latitude(), other.longitude(), other.latitude());
    }

    /**
     * @return this point as a {longitude, latitude} double array
     */
    default double[] asArray() {
        return new double[] { this.longitude(), this.latitude() };
    }

    /**
     * @return a {@link LatLng} that represents the same point
     */
    default LatLng asLatLng() {
        return new LatLng(this.latitude(), this.longitude());
    }

    /**
     * @return a {@link Vec2dImmutable} of which the X component is the longitude of this point
     * and the Y component its latitude
     */
    default Vec2dImmutable asVec2d() {
        return new Vec2dImmutable(this.longitude(), this.latitude());
    }

    /**
     * @param longitude a new longitude, that will be converted to the [-180°, 180°] range id needed
     * @return a new GeoPoint with the same latitude as this one but a different longitude
     *
     * @throws IllegalArgumentException if the longitude is not a finite number
     */
    T withLongitude(double longitude);

    /**
     * @param latitude a new latitude
     * @return a new GeoPoint with the same longitude as this one but a different latitude
     *
     * @throws IllegalArgumentException if the latitude is not a finite number in the [-90°, 90°] range
     */
    T withLatitude(double latitude);

    @Override
    default GeoPointMutable getMutable() {
        return new GeoPointMutable(this.longitude(), this.latitude());
    }

    @Override
    default GeoPointImmutable getImmutable() {
        return new GeoPointImmutable(this.longitude(), this.latitude());
    }

}
