package fr.thesmyler.terramap.util.geo;

import net.buildtheearth.terraplusplus.util.geo.LatLng;

/**
 * A point in the WGS:84 coordinate system.
 * Two points are considered equal when they represent the same place,
 * which means that points corresponding to the north pole will be equal even if they have different longitudes.
 * Similarly, points at the same latitude on the antimeridian will be equal,
 * independently of whether or not their longitude is -180 or 180.
 * 
 * @author SmylerMC
 * 
 * TODO Refactor most of the code to use this class
 * FIXME points of the antimeridian should be equals
 *
 */
public class GeoPoint {
    
    public static final GeoPoint ORIGIN = new GeoPoint(0d, 0d);
    public static final GeoPoint NORTH_POLE = new GeoPoint(0d, 90d);
    public static final GeoPoint SOUTH_POLE = new GeoPoint(0d, -90d);
    
    public final double longitude, latitude;
    
    private transient int hashCode = 0;
    
    /**
     * Constructs a new point from the given coordinates in degrees.
     * The longitude get adjusted to be in the ]-180°, 180°] range.
     * Latitude needs to be within the [-90°, 90°] range.
     * The coordinates do not change if they already are in the appropriate ranges.
     * 
     * @param longitude in degrees
     * @param latitude in degrees
     * 
     * @throws IllegalArgumentException if either latitude or longitude is not a finite number,
     *  or if latitude is not within the appropriate range.
     */
    public GeoPoint(double longitude, double latitude) {
        this.latitude = GeoUtil.getLatitudeInRange(latitude);
        this.longitude = GeoUtil.getLongitudeInRange(longitude);
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

    @Override
    public int hashCode() {
        if(this.hashCode == 0) {
            // Cache hashCode result
            final int prime = 31;
            int result = 1;
            long temp;
            double lon = Math.abs(this.latitude) == 90d ? 0d: this.longitude;
            if(lon == -180d) lon = 180d;
            temp = Double.doubleToLongBits(lon);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(this.latitude);
            result = prime * result + (int) (temp ^ (temp >>> 32));
            this.hashCode =  result;
        }
        return this.hashCode;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        GeoPoint other = (GeoPoint) obj;
        if(this.latitude != other.latitude) return false;
        if(Math.abs(this.latitude) == 90d) return true; // We don't care about longitude at the poles
        if((this.longitude == -180d || this.longitude == 180d)
                && this.longitude + other.longitude == 0d)
            return true; // Antimeridian can be both 180 or -180
        return this.longitude == other.longitude;
    }
    
    

}
