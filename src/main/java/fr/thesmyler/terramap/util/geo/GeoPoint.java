package fr.thesmyler.terramap.util.geo;

import net.buildtheearth.terraplusplus.util.geo.LatLng;

/**
 * A point in the WGS:84 coordinate system
 * 
 * @author SmylerMC
 * 
 * TODO Refactor most of the code to use this class
 *
 */
public class GeoPoint {
    
    public final double longitude, latitude;
    
    /**
     * Constructs a new point from the given coordinates in degrees.
     * The coordinates are adjusted to be in the ]-180°, 180°] x [-90°, 90°] ranges.
     * The coordinates do not change if they already are in the appropriate ranges.
     * 
     * @param longitude in degrees
     * @param latitude in degrees
     * 
     * @throws IllegalArgumentException if either latitude or longitude is not a finite number
     */
    public GeoPoint(double longitude, double latitude) {
        this.longitude = GeoUtil.getLongitudeInRange(longitude);
        this.latitude = GeoUtil.getLatitudeInRange(latitude);
    }
    
    /**
     * Delegate constructor to {@link #GeoPoint(double, double)}
     * 
     * @param lola a double array of the form {longitude, latitude}
     */
    public GeoPoint(double[] lola) {
        this(lola[0], lola[1]);
    }
    
    /**     
     * Delegate constructor to {@link #GeoPoint(double, double)}
     * 
     * @param lola
     */
    public GeoPoint(LatLng lola) {
        this(lola.getLng(), lola.getLat());
    }
    
    /**
     * Estimates the distance between this point and an other one as best as possible, ignoring altitude
     * 
     * @param other an other point
     * 
     * @return the distance between this point and the other, in meters
     */
    public double distanceTo(GeoPoint other) {
        return GeoUtil.distanceHaversine(this.longitude, this.latitude, other.longitude, other.latitude);
    }
    
    /**
     * @return this point as a {longitude, latitude} double array
     */
    public double[] asArray() {
        return new double[] { this.longitude, this.latitude };
    }

}
