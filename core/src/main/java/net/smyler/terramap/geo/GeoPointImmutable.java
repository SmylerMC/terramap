package net.smyler.terramap.geo;

import static net.smyler.terramap.geo.GeoUtil.getLatitudeInRange;
import static net.smyler.terramap.geo.GeoUtil.getLongitudeInRange;

public class GeoPointImmutable extends GeoPointAbstract {
    
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
}
