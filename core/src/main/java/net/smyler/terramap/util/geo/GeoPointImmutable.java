package net.smyler.terramap.util.geo;

import java.util.Locale;

import static java.lang.Math.abs;
import static net.smyler.terramap.util.geo.GeoUtil.getLatitudeInRange;
import static net.smyler.terramap.util.geo.GeoUtil.getLongitudeInRange;

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
     * Delegate constructor to {@link #GeoPointImmutable(double, double)}
     * 
     * @param lola a double array of the form {longitude, latitude}
     */
    public GeoPointImmutable(double[] lola) {
        this(lola[0], lola[1]);
    }
    
    @Override
    public double longitude() {
        return this.longitude;
    }

    @Override
    public double latitude() {
        return this.latitude;
    }

    public GeoPointImmutable withLongitude(double longitude) {
        if (longitude == this.longitude) {
            return this;
        }
        return new GeoPointImmutable(longitude, this.latitude);
    }

    public GeoPointImmutable withLatitude(double latitude) {
        if (latitude == this.latitude) {
            return this;
        }
        return new GeoPointImmutable(this.longitude, latitude);
    }

    @Override
    public GeoPointImmutable getImmutable() {
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        double latitude = this.latitude();
        double longitude = abs(latitude) == 90d ? 0d: this.longitude();
        if (longitude == -180d) {
            longitude = 180d;
        }
        result = prime * result + Double.hashCode(longitude);
        result = prime * result + Double.hashCode(latitude);
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
        GeoPointImmutable other = (GeoPointImmutable) obj;
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

    @Override
    public String toString() {
        return String.format(Locale.US, "GeoPointImmutable[lon=%s°, lat=%s°]", this.longitude(), this.latitude());
    }

}
