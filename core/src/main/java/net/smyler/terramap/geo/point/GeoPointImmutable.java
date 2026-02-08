package net.smyler.terramap.geo.point;

import java.util.Locale;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.terramap.geo.GeoUtil.getLatitudeInRange;
import static net.smyler.terramap.geo.GeoUtil.getLongitudeInRange;

/**
 * An immutable implementation of {@link GeoPoint}.
 *
 * @author Smyler
 */
public class GeoPointImmutable implements GeoPoint {
    
    public static final GeoPointImmutable ORIGIN = new GeoPointImmutable(0d, 0d);
    public static final GeoPointImmutable NORTH_POLE = new GeoPointImmutable(0d, 90d);
    public static final GeoPointImmutable SOUTH_POLE = new GeoPointImmutable(0d, -90d);
    
    private final double longitude, latitude;
    
    /**
     * Constructs a new point from the given coordinates in degrees.
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
    public GeoPointImmutable(double longitude, double latitude) {
        this.latitude = getLatitudeInRange(latitude);
        this.longitude = getLongitudeInRange(longitude);
    }
    
    /**
     * Constructs a new point by extracting coordinates from an array of length two.
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
    public GeoPointImmutable(double @NotNull [] lola) {
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
    public GeoPointImmutable(@NotNull GeoPoint point) {
        requireNonNull(point);
        this.longitude = getLongitudeInRange(point.longitude());
        this.latitude = getLatitudeInRange(point.latitude());
    }

    @Override
    public double longitude() {
        return this.longitude;
    }

    @Override
    public double latitude() {
        return this.latitude;
    }

    /**
     * Creates a copy of this point with a different longitude.
     *
     * @param longitude the new longitude, expressed in degrees
     *
     * @return the new point
     */
    public @NotNull GeoPointImmutable withLongitude(double longitude) {
        if (longitude == this.longitude) {
            return this;
        }
        return new GeoPointImmutable(longitude, this.latitude);
    }

    /**
     * Creates a copy of this point with a different latitude.
     *
     * @param latitude the new latitude, expressed in degrees
     *
     * @return the new point
     */
    public @NotNull GeoPointImmutable withLatitude(double latitude) {
        if (latitude == this.latitude) {
            return this;
        }
        return new GeoPointImmutable(this.longitude, latitude);
    }

    @Override
    public @NotNull GeoPointImmutable getImmutable() {
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Double.hashCode(this.longitude);
        result = prime * result + Double.hashCode(this.latitude);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !obj.getClass().equals(GeoPointImmutable.class)) {
            return false;
        }
        GeoPoint other = (GeoPoint) obj;
        return this.latitude == other.latitude() && this.longitude == other.longitude();
    }

    @Override
    public String toString() {
        return String.format(Locale.US, "GeoPointImmutable[lon=%s°, lat=%s°]", this.longitude(), this.latitude());
    }

}
