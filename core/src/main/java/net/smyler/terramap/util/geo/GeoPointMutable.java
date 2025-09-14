package net.smyler.terramap.util.geo;

import static net.smyler.terramap.util.geo.GeoUtil.getLatitudeInRange;
import static net.smyler.terramap.util.geo.GeoUtil.getLongitudeInRange;

public class GeoPointMutable extends GeoPointAbstract {

    private double longitude, latitude;
    private GeoPointView readOnly;

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
    public GeoPointMutable(double longitude, double latitude) {
        this.latitude = getLatitudeInRange(latitude);
        this.longitude = getLongitudeInRange(longitude);
    }

    /**
     * Delegate constructor to {@link #GeoPointMutable(double, double)}
     *
     * @param lola a double array of the form {longitude, latitude}
     */
    public GeoPointMutable(double[] lola) {
        this(lola[0], lola[1]);
    }

    /**
     * Initializes this geographic point as 0°N 0°W
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

    @Override
    public GeoPointMutable withLongitude(double longitude) {
        this.longitude = getLongitudeInRange(longitude);
        return this;
    }

    /**
     * Updates this point.
     *
     * @param longitude a new longitude
     * @param latitude a new latitude
     * @return this point
     * @throws IllegalArgumentException if either longitude or latitude is not a within the appropriate range
     */
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
     */
    public GeoPointMutable set(GeoPoint point) {
        return this.set(point.longitude(), point.latitude());
    }

    /**
     * Updates this point.
     *
     * @param location a location to copy the position from, as a {longitude ,latitude} double array
     * @return this point
     */
    public GeoPointMutable set(double[] location) {
        return this.set(location[0], location[1]);
    }

    @Override
    public GeoPointMutable withLatitude(double latitude) {
        this.latitude = getLatitudeInRange(latitude);
        return this;
    }

    @Override
    public GeoPointMutable getMutable() {
        return this;
    }

    public GeoPointView getReadOnlyView() {
        if (this.readOnly == null) {
            this.readOnly = new GeoPointView(this);
        }
        return this.readOnly;
    }

}
