package net.smyler.terramap.util.geo;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.util.Locale;

import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.terramap.util.geo.GeoUtil.getLatitudeInRange;
import static net.smyler.terramap.util.geo.GeoUtil.getLongitudeInRange;

/**
 * A mutable implementation of {@link GeoPoint}.
 *
 * @author Smyler
 */
public class GeoPointMutable implements GeoPoint, Cloneable {

    private double longitude, latitude;
    private GeoPointView readOnly;

    /**
     * Initializes a new point from the given coordinates in degrees.
     * The longitude get adjusted to be in the [-180°, 180°] range.
     * Latitude needs to be within the [-90°, 90°] range.
     * The coordinates do not change if they already are in the appropriate ranges.
     *
     * @param longitude in degrees
     * @param latitude in degrees
     *
     * @throws IllegalArgumentException if either latitude or longitude is not a finite number,
     *  or if latitude is not within the appropriate range.
     */
    public GeoPointMutable(double longitude, double latitude) {
        this.latitude = getLatitudeInRange(latitude);
        this.longitude = getLongitudeInRange(longitude);
    }

    /**
     * Initializes a new point by extracting coordinates from an array of length two.
     * The first array item provides the longitude, the second the latitude.
     * Both are expected to be expressed in degrees.
     * The longitude get adjusted to be in the [-180°, 180°] range.
     * Latitude needs to be within the [-90°, 90°] range.
     * The coordinates do not change if they already are in the appropriate ranges.
     *
     * @param lola a double array of the form {longitude, latitude}
     *
     * @throws NullPointerException if the array is null
     * @throws IllegalArgumentException if either the latitude or the longitude is not a finite number,
     *  or if latitude is not within the appropriate range.
     */
    public GeoPointMutable(double @NotNull [] lola) {
        requireNonNull(lola);
        checkArgument(lola.length == 2, "Expected 2 values for latitude and longitude");
        this.longitude = getLongitudeInRange(lola[0]);
        this.latitude = getLatitudeInRange(lola[1]);
    }

    /**
     * Constructs a new point by extracting coordinates from another point.
     *
     * @param point a point to copy
     * @throws NullPointerException if the point is null
     */
    public GeoPointMutable(@NotNull GeoPoint point) {
        requireNonNull(point);
        this.longitude = getLongitudeInRange(point.longitude());
        this.latitude = getLatitudeInRange(point.latitude());
    }

    /**
     * Initializes a new point with coordinates 0°N 0°W.
     */
    public GeoPointMutable() {
        this(0, 0);
    }

    @Override
    public double latitude() {
        return this.latitude;
    }

    @Override
    public double longitude() {
        return this.longitude;
    }

    /**
     * Updates this point's longitude.
     *
     * @param longitude the new longitude, expressed in degrees
     * @return this point
     *
     * @throws IllegalArgumentException if the new longitude is not a finite
     */
    @Contract("_ -> this")
    public @NotNull GeoPointMutable setLongitude(double longitude) {
        this.longitude = getLongitudeInRange(longitude);
        return this;
    }

    /**
     * Updates this point.
     *
     * @param longitude a new longitude, expressed in degrees
     * @param latitude a new latitude, expressed in degrees
     * @return this point
     * @throws IllegalArgumentException if either longitude or latitude is not a within the appropriate range
     */
    @Contract("_, _ -> this")
    public GeoPointMutable set(double longitude, double latitude) {
        double newLongitude = getLongitudeInRange(longitude);
        double newLatitude = getLatitudeInRange(latitude);
        this.longitude = newLongitude;
        this.latitude = newLatitude;
        return this;
    }

    /**
     * Updates this point.
     *
     * @param point a point to copy the position from
     * @return this point
     * @throws NullPointerException if the other point is null
     */
    @Contract("_ -> this")
    public GeoPointMutable set(@NotNull GeoPoint point) {
        requireNonNull(point);
        return this.set(point.longitude(), point.latitude());
    }

    /**
     * Updates this point by extracting new coordinates from a double array of length 2.
     * The first array item is used and the longitude and the second one as the latitude,
     * both are expected to be expressed in degrees.
     *
     * @param location a location to copy the position from, as a {longitude ,latitude} double array
     * @return this point
     *
     * @throws NullPointerException if the array is null
     * @throws IllegalArgumentException if the array is not of length 2 or either the longitude or latitude is not in range
     */
    @Contract("_ -> this")
    public GeoPointMutable set(double @NotNull [] location) {
        requireNonNull(location);
        checkArgument(location.length == 2, "Expected 2 values for latitude and longitude");
        return this.set(location[0], location[1]);
    }

    /**
     * Updates this point's latitude.
     *
     * @param latitude the new latitude, expressed in degrees
     * @return this point
     *
     * @throws IllegalArgumentException if the new latitude is not a finite number within the [-90°, 90°] bounds
     */
    @Contract("_ -> this")
    public GeoPointMutable setLatitude(double latitude) {
        this.latitude = getLatitudeInRange(latitude);
        return this;
    }

    @Override
    public @NotNull GeoPointMutable getMutable() {
        return this;
    }

    public GeoPointView getReadOnlyView() {
        if (this.readOnly == null) {
            this.readOnly = new GeoPointView(this);
        }
        return this.readOnly;
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "GeoPointMutable[lon=%s°, lat=%s°]", this.longitude(), this.latitude());
    }

    @Override
    public GeoPointMutable clone() {
        try {
            GeoPointMutable clone = (GeoPointMutable) super.clone();
            clone.readOnly = null;  // Set this back to null so the clone can create its own view when requested
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }

}
