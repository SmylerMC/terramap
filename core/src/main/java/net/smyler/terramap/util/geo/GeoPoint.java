package net.smyler.terramap.util.geo;

import net.smyler.smylib.Immutable;
import net.smyler.smylib.Mutable;
import net.smyler.smylib.math.Vec2dImmutable;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.toRadians;
import static java.util.Objects.requireNonNull;
import static net.smyler.terramap.util.geo.GeoUtil.distanceHaversine;

/**
 * A point in the WGS:84 coordinate system.
 * Latitude is within the [-90°, 90°] range.
 * Longitude is within the [-180°, 180°] range.
 *
 * @author Smyler
 *
 */
public interface GeoPoint extends Mutable<GeoPointImmutable>, Immutable<GeoPointMutable> {

    /**
     * This point's latitude, expressed in degrees.
     *
     * @return this point's latitude, in the appropriate [-90°, 90°] range
     */
    double latitude();

    /**
     * This point's longitude, expressed in degrees.
     *
     * @return this point's longitude, in the appropriate [-180°, 180°] range
     */
    double longitude();

    /**
     * This point's longitude, expressed in radians.
     *
     * @return this point's longitude, in the appropriate [-pi, pi] range
     */
    default double longitudeRad() {
        return toRadians(this.longitude());
    }

    /**
     * This point's latitude, expressed in radians.
     *
     * @return this point's latitude, in the appropriate [-pi/2, pi/2] range
     */
    default double latitudeRad() {
        return toRadians(this.latitude());
    }

    /**
     * Estimates the distance between this point and another one as best as possible, ignoring altitude.
     *
     * @param other another point
     *
     * @return the distance between this point and the other, in meters
     *
     * @throws NullPointerException if the other point is null
     */
    default double distanceTo(@NotNull GeoPoint other) {
        requireNonNull(other);
        return distanceHaversine(this, other);
    }

    /**
     * Converts this point to an array of length two,
     * containing the longitude and latitude in degrees (in that order).
     *
     * @return this point as a {longitude, latitude} double array
     */
    default double @NotNull [] asArray() {
        return new double[] { this.longitude(), this.latitude() };
    }

    /**
     * Converts this point to an {@link Vec2dImmutable immutable vector}.
     *
     * @return a {@link Vec2dImmutable} of which the X component is the longitude of this point
     * and the Y component its latitude, both expressed in degrees
     */
    default @NotNull Vec2dImmutable asVec2d() {
        return new Vec2dImmutable(this.longitude(), this.latitude());
    }

    @Override
    default @NotNull GeoPointMutable getMutable() {
        return new GeoPointMutable(this.longitude(), this.latitude());
    }

    @Override
    default @NotNull GeoPointImmutable getImmutable() {
        return new GeoPointImmutable(this.longitude(), this.latitude());
    }

}
