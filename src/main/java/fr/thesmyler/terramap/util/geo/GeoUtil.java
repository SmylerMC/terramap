package fr.thesmyler.terramap.util.geo;

import static java.lang.Math.*;

/**
 * A collection of algorithm to solve geographic problems
 * 
 * @author SmylerMC
 *
 */
public final class GeoUtil {
    
    private GeoUtil() {}

    /**
     * Radius of the Earth in meters
     */
    public static final double EARTH_RADIUS = 6371000;

    /**
     * @param longitude in degrees
     * @return the same longitude, between -180 and 180
     * 
     * @throws IllegalArgumentException if the longitude is not a finite double
     */
    public static double getLongitudeInRange(double longitude) {
        if(!Double.isFinite(longitude)) throw new IllegalArgumentException("longitude cannot be infinite or NaN");
        if(longitude == 0d) return 0d; // In case of -0d
        if(-180d <= longitude && longitude <= 180d) return longitude;
        return longitude - floor(longitude / 360d + .5d) * 360d;
    }

    /**
     * @param latitude in degrees
     * @return the same latitude
     * 
     * @throws IllegalArgumentException if the latitude is not a finite double or is not within the [-90°, 90°] range
     */
    public static double getLatitudeInRange(double latitude) {
        if(!Double.isFinite(latitude)) throw new IllegalArgumentException("latitude cannot be infinite or NaN");
        if(abs(latitude) > 90d) throw new IllegalArgumentException("latitude cannot be greater than 90°");
        if(latitude == 0d) return 0d; // In case of -0d
        return latitude;
    }

    /**
     * @param azimuth in degrees
     * @return the same azimuth, between 0 and 360
     * 
     * @throws IllegalStateException if the azimuth is not a finite double
     */
    public static float getAzimuthInRange(float azimuth) {
        if(!Float.isFinite(azimuth)) throw new IllegalArgumentException("azimuth cannot be infinite or NaN");
        return (float) (azimuth - floor(azimuth / 360)*360);
    }

    /**
     * Computes the distance between two points on the surface of the Earth, assuming a spherical Earth.
     * An implementation of Haversine's formula to compute the great circle distance between two points.
     * 
     * @param longitude1 in degrees
     * @param latitude1 in degrees
     * @param longitude2 in degrees
     * @param latitude2 in degrees
     * 
     * @return an approximation of the distance between the two points, in meters
     */
    public static double distanceHaversine(double longitude1, double latitude1, double longitude2, double latitude2) {
        double dLon = toRadians(longitude1 - longitude2) / 2d;
        double dLat = toRadians(latitude1 - latitude2) / 2d;
        double cosLat1 = cos(toRadians(latitude1));
        double cosLat2 = cos(toRadians(latitude2));
        double sinDLon = sin(dLon);
        double sinDLat = sin(dLat);
        double arc = sinDLat*sinDLat + cosLat1*cosLat2 * sinDLon*sinDLon;
        return 2 * EARTH_RADIUS * asin(sqrt(arc));
    }

}
