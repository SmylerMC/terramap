package fr.thesmyler.terramap.util.geo;

import net.minecraft.util.math.Vec3d;

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
     * @throws IllegalStateException if the longitude is not a finite double
     */
    public static double getLongitudeInRange(double longitude) {
        if(!Double.isFinite(longitude)) throw new IllegalArgumentException("longitude cannot be infinite or NaN");
        if(longitude == 0d) return 0d; // In case of -0d
        if(-180d <= longitude && longitude <= 180d) return longitude;
        return longitude - Math.floor(longitude / 360d + .5d) * 360d;
    }

    /**
     * @param latitude in degrees
     * @return the same latitude
     * 
     * @throws IllegalStateException if the latitude is not a finite double or is not within the [-90°, 90°] range
     */
    public static double getLatitudeInRange(double latitude) {
        if(!Double.isFinite(latitude)) throw new IllegalArgumentException("latitude cannot be infinite or NaN");
        if(Math.abs(latitude) > 90d) throw new IllegalArgumentException("latitude cannot be greater than 90°");
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
        return (float) (azimuth - Math.floor(azimuth / 360)*360);
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
        double dLon = Math.toRadians(longitude1 - longitude2) / 2d;
        double dLat = Math.toRadians(latitude1 - latitude2) / 2d;
        double cosLat1 = Math.cos(Math.toRadians(latitude1));
        double cosLat2 = Math.cos(Math.toRadians(latitude2));
        double sinDLon = Math.sin(dLon);
        double sinDLat = Math.sin(dLat);
        double arc = sinDLat*sinDLat + cosLat1*cosLat2 * sinDLon*sinDLon;
        return 2 * EARTH_RADIUS * Math.asin(Math.sqrt(arc));
    }
    
    /**
     * Computes the distance between two points on the surface of the Earth, assuming a spherical Earth.
     * An implementation of Haversine's formula to compute the great circle distance between two points.
     * 
     * @param point1
     * @param point2
     * 
     * @return an approximation of the distance between the two points, in meters
     */
    public static double distanceHaversine(GeoPoint point1, GeoPoint point2) {
        return distanceHaversine(point1.longitude, point1.latitude, point2.longitude, point2.latitude);
    }
    
    public static double distanceToGreateCircle(GeoPoint point, GeoPoint geo1, GeoPoint geo2) {
        //TODO Test and javadoc
        Vec3d vec = point.unitCartesianPosition();
        Vec3d vec1 = geo1.unitCartesianPosition();
        Vec3d vec2 = geo2.unitCartesianPosition();
        Vec3d normal = vec1.crossProduct(vec2).normalize();
        if(normal.lengthSquared() == 0d) return point.distanceTo(geo1);
        double angle = Math.asin(vec.dotProduct(normal));
        return Math.abs(angle) * EARTH_RADIUS;
    }
    
    public static double distanceToGeodesic(GeoPoint point, GeoPoint geo1, GeoPoint geo2) {
        //TODO Test and javadoc
        Vec3d vec = point.unitCartesianPosition();
        Vec3d vec1 = geo1.unitCartesianPosition();
        Vec3d vec2 = geo2.unitCartesianPosition();
        Vec3d vec12 = vec2.subtract(vec1);
        Vec3d pos = vec.subtract(vec1);
        double projected = vec12.dotProduct(pos) / vec12.length();
        if(projected < 0d) return geo1.distanceTo(point);
        if(projected > 1d) return geo2.distanceTo(point);
        Vec3d normal = vec1.crossProduct(vec2).normalize();
        if(normal.lengthSquared() == 0d) return point.distanceTo(geo1);
        double angle = Math.asin(vec.dotProduct(normal));
        return Math.abs(angle) * EARTH_RADIUS;
    }

}
