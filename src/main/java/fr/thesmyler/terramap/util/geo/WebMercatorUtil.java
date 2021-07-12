package fr.thesmyler.terramap.util.geo;

import fr.thesmyler.terramap.util.math.Vec2d;

/**
 * A set of methods to work with the web mercator map projection.
 * 
 * @see <a href="https://en.wikipedia.org/wiki/Web_Mercator for more details">Wikipedia's article on Web Mercator</a>
 * 
 * @author SmylerMC
 *
 */
public final class WebMercatorUtil {
    
    private WebMercatorUtil() {}

    /* Constants */
    public static final int TILE_DIMENSIONS = 256;
    public static final double LIMIT_LATITUDE_RADIANS = 2 * Math.atan(Math.pow(Math.E, Math.PI)) - Math.PI/2;
    public static final double LIMIT_LATITUDE = Math.toDegrees(LIMIT_LATITUDE_RADIANS);
    
    public static final GeoBounds PROJECTION_BOUNDS = new GeoBounds(new GeoPoint(-180d, -LIMIT_LATITUDE), new GeoPoint(180d, LIMIT_LATITUDE));

    /* 
     * This is not really a limit of web Mercator,
     * but since tile positions are encoded as integers,
     * this is the max value that avoid overflows.
     */
    public static final int MAX_ZOOM = 30;
    
    /**
     * Projects the given geoPoint onto a web-Mercator map
     * 
     * @param geo - a geographic position
     * @param zoomLevel - the zoom level to use when projecting
     * 
     * @return the corresponding position on the 2d plane
     */
    public static Vec2d fromGeo(GeoPoint geo, double zoomLevel) {
        double x = getXFromLongitude(geo.longitude, zoomLevel);
        double y = getYFromLatitude(geo.latitude, zoomLevel);
        return new Vec2d(x, y);
    }
    
    /**
     * Reverses the Web-Mercator projection for a given projected point
     * 
     * @param position - a position on the 2d plane to reverse
     * @param zoomLevel - the zoom level to use for the projection
     * 
     * @return the corresponding geographic position
     */
    public static GeoPoint toGeo(Vec2d position, double zoomLevel) {
        double longitude = getLongitudeFromX(position.x, zoomLevel);
        double latitude = getLatitudeFromY(position.y, zoomLevel);
        return new GeoPoint(longitude, latitude);
    }

    /**
     * 
     * @param longitude The longitude in degrees, between -180.0 and 180.0
     * @param zoomLevel The web-mercator zoom level
     * 
     * @return The X pixel position corresponding to the given longitude
     * on a web-mercator map of the given zoom level, with 0;0 being the top left corner of the map.
     */
    public static double getXFromLongitude(double longitude, double zoomLevel){
        return WebMercatorUtil.getXFromLongitudeRads(Math.toRadians(longitude), zoomLevel);
    }

    /**
     * 
     * @param latitude The latitude in degrees, between -90.0 and 90.0
     * @param zoomlevel The web-mercator zoom level
     * 
     * @return The Y pixel position corresponding to the given latitude
     * on a web-mercator map of the given zoom level, with 0;0 being the top left corner of the map.
     */
    public static double getYFromLatitude(double latitude, double zoomlevel){
        return WebMercatorUtil.getYFromLatitudeRads(Math.toRadians(latitude), zoomlevel);
    }

    /**
     * 
     * @param longitude The longitude in radians, between -pi and pi
     * @param zoomLevel The web-mercator zoom level
     * 
     * @return The X position corresponding to the given longitude
     * on a web-mercator map of the given zoom level, with 0;0 being the top left corner of the map.
     */
    public static double getXFromLongitudeRads(double longitude, double zoom){
        return Math.pow(2, zoom + 7) * (longitude + Math.PI) / Math.PI;
    }


    /**
     * 
     * @param latitude The latitude in radians, between -pi/2 and pi/2
     * @param zoom The web-mercator zoom level
     * 
     * @return The Y position corresponding to the given latitude
     * on a web-mercator map of the given zoom level, with 0;0 being the top left corner of the map.
     */
    public static double getYFromLatitudeRads(double latitude, double zoom){
        return 128d / Math.PI * Math.pow(2, zoom) * (Math.PI - Math.log(Math.tan( Math.PI / 4  + latitude / 2)));
    }


    /** 
     * @param y a coordinate on a web-mercator map, as an integer with 0;0 as the top left corner
     * @param zoom The web-mercator zoom level
     * 
     * @return The corresponding latitude in degrees, between -180.0 and 180.0
     */
    public static double getLatitudeFromY(double y, double zoom){
        return Math.toDegrees(WebMercatorUtil.getLatitudeFromYRads(y, zoom));
    }


    /**
     * @param x a coordinate on a web-mercator map, as an integer with 0;0 as the top left corner
     * @param zoom The web-mercator zoom level
     * 
     * @return The corresponding longitude in degrees, between -90.0 and 90.0
     */
    public static double getLongitudeFromX(double x, double zoom){
        return Math.toDegrees(WebMercatorUtil.getLongitudeFromXRads(x, zoom));
    }


    /** 
     * @param y a coordinate on a web-mercator map, as an integer with 0;0 as the top left corner
     * @param zoom The web-mercator zoom level
     * 
     * @return The corresponding latitude in radians, between -pi and pi
     */
    public static double getLatitudeFromYRads(double y, double zoom){
        return 2 * Math.atan(Math.exp(-(y * Math.PI / Math.pow(2, zoom + 7) - Math.PI))) - Math.PI / 2;
    }


    /**
     * @param x a coordinate on a web-mercator map, as an integer with 0;0 as the top left corner
     * @param zoom The web-mercator zoom level
     * 
     * @return The corresponding longitude in radians, between -pi/2 and pi/2
     */
    public static double getLongitudeFromXRads(double x, double zoom){
        return Math.PI * x / Math.pow(2, 7 + zoom) - Math.PI;
    }


    /**
     * 
     * @param x
     * @param zoomLevel
     * @return the x coordinate of the tile at the given x position in the world, assuming map is scaled at zoom level zoomLevel
     */
    public static int getTileXAt(long x){
        return (int) Math.floor((double)x / WebMercatorUtil.TILE_DIMENSIONS);
    }

    /**
     * 
     * @param y
     * @return the y coordinate of the tile at the given z position in the world, assuming map is scaled at zoom level zoomLevel
     */
    public static int getTileYAt(long y){
        return (int) Math.floor((double)y / WebMercatorUtil.TILE_DIMENSIONS);
    }


    /**
     * 
     * @param x
     * @param z
     * @param zoom
     * @return true if the block at the given x and z is on the real world scaled at zoom
     */
    public static boolean isInWorld(long x, long z, int zoom){
        long tX = getTileXAt(x);
        long tY = getTileYAt(z);
        double mS = Math.pow(2, zoom);
        return tX >= 0 && tX < mS && tY >= 0 && tY < mS;
    }


    /**
     * @param x
     * @param z
     * @param zoom
     * @return true if zoom, x, y is a valid tile position
     */
    public static boolean isValidTilePosition(int zoom, int tX, int tY){
        int mS = 1 << zoom;
        return zoom >= 0 && zoom < MAX_ZOOM && tX >= 0 && tX < mS && tY >= 0 && tY < mS;
    }

    /**
     * Returns the size of the map in pixel
     * 
     * @param zoomLvl the zoom level of the map to consider
     * 
     * @return The size of a side of the map, in pixel
     */
    public static long getMapDimensionInPixel(int zoomLvl) {
        return WebMercatorUtil.getDimensionsInTile(zoomLvl) * WebMercatorUtil.TILE_DIMENSIONS;
    }


    /**
     * Returns the length of a side of a map of the given  zoom level, in tile
     * It is simply 2 raised to the power of the zoom
     * 
     * @param zoomlvl the zoom level of the map to consider
     * @return 2^zoomlvl
     */
    public static int getDimensionsInTile(int zoomlvl) {
        return 1 << zoomlvl;
    }

    public static boolean isPositionOnMap(double longitude, double latitude) {
        return !Double.isNaN(longitude) && !Double.isNaN(latitude) && Math.abs(latitude) <= LIMIT_LATITUDE;
    }

}
