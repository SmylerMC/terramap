package fr.thesmyler.terramap.util;

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
     * @throws RuntimeException if the longitude is not a finite double
     */
    public static double getLongitudeInRange(double longitude) {
        if(!Double.isFinite(longitude)) throw new RuntimeException("longitude cannot be infinite");
        double l = longitude;
        while(l> 180d) l -= 360d;
        while(l<-180d) l += 360d;
        return l;
    }

    /**
     * @param latitude in degrees
     * @return the same latitude, between -90 and 90
     * 
     * @throws RuntimeException if the latitude is not a finite double
     */
    public static double getLatitudeInRange(double latitude) {
        if(!Double.isFinite(latitude)) throw new RuntimeException("longitude cannot be infinite");
        double l = latitude;
        while(l> 90d) l -= 180d;
        while(l<-90d) l += 180d;
        return l;
    }

    /**
     * @param azimuth in degrees
     * @return the same azimuth, between 0 and 360
     * 
     * @throws RuntimeException if the azimuth is not a finite double
     */
    public static float getAzimuthInRange(float azimuth) {
        if(!Float.isFinite(azimuth)) throw new RuntimeException("azimuth cannot be infinite");
        while(azimuth >= 360f) azimuth -= 360f;
        while(azimuth < 0f) azimuth += 360f;
        return azimuth;
    }

    /**
     * Computes the distance between two points on the surface of the Earth, assuming a spherical Earth.
     * An implementation of Haversine's formula to compute the great circle distance between two points.
     * 
     * @param longitude1
     * @param latitude1
     * @param longitude2
     * @param latitude2
     * 
     * @return an approximation of the distance between the two points, in meters
     */
    public static double distanceHaversine(double longitude1, double latitude1, double longitude2, double latitude2) {
        double dLon = Math.toRadians(longitude1 - longitude2) / 2d;
        double dLat = Math.toRadians(latitude1 - latitude2) / 2d;
        double cosLat1 = Math.cos(Math.toRadians(latitude1));
        double cosLat2 = Math.cos(Math.toRadians(latitude2));
        double sinDLon = Math.sin(dLon);
        double sinDLat = Math.sin(dLat);
        double arc = sinDLat*sinDLat + cosLat1*cosLat2 * sinDLon*sinDLon;
        return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(arc));
    }

}
