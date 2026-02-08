package net.smyler.terramap.geo.point;

import net.smyler.smylib.Immutable;
import net.smyler.smylib.Mutable;
import net.smyler.smylib.math.Vec2dImmutable;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.lang.Math.abs;
import static java.lang.Math.toRadians;
import static java.util.Objects.requireNonNull;
import static net.smyler.terramap.geo.GeoUtil.distanceHaversine;

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
        if (this.isEquivalentTo(other)) {
            return 0d;
        }
        return distanceHaversine(this, other);
    }

    /**
     * Checks whether this {@link GeoPoint} represents the exact same place as another {@link GeoPoint}.
     * Except for some edge cases, this simply checks that both point's latitude and longitude are equal.
     * <br><br>
     * Edge cases:
     * <ul>
     *     <li>
     *         The longitude is irrelevant when comparing two points lying on the same geographic pole,
     *         and therefore ignored (latitude of 90° at the North Pole, -90° at the South Pole).
     *     </li>
     *     <li>
     *         Points on the antiméridian may have longitude -180° or 180° and will be equivalent regardless of the sign.
     *     </li>
     * </ul>
     * <br><br>
     * Specifying <code>null</code> as the other point always returns false.
     * <br><br>
     * Differences from {@link Object#equals(Object)}:
     * <ul>
     *     <li>this method only compares {@link GeoPoint GeoPoints}</li>
     *     <li>the edge cases presented above are not equal according to {@link Object#equals(Object)}</li>
     * </ul>
     *
     * @param other the other point to compare to
     * @return whether both point represent to same place on Earth
     *
     * @see Object#equals(Object)
     * @see GeoPoint#isWithinRange(GeoPoint, double)
     */
    @Contract("null -> false")
    default boolean isEquivalentTo(@Nullable GeoPoint other) {
        if (this == other) {
            return true;
        }
        if (other == null) {
            return false;
        }
        double thisLat = this.latitude();
        double otherLat = other.latitude();
        if (thisLat != otherLat) {
            return false;
        }
        if (abs(thisLat) == 90d) {
            return true; // We don't care about longitude at the poles
        }
        double thisLong = this.longitude();
        double otherLong = other.longitude();
        if ((thisLong == -180d || thisLong == 180d) && thisLong + otherLong == 0d) {
            return true; // Antimeridian can be both 180 or -180
        }
        return thisLong == otherLong;
    }

    /**
     * Checks that this point and another point are within a given range of each others.
     *
     * @param other the other point
     * @param rangeMeters the range (in meters)
     * @return whether the distance between this point and the other is less than the given range
     */
    default boolean isWithinRange(@Nullable GeoPoint other, double rangeMeters) {
        if (other == null) {
            return false;
        }
        return this.distanceTo(other) <= rangeMeters;
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
