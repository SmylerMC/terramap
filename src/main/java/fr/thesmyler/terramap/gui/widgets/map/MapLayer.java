package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.util.GeoUtil;
import fr.thesmyler.terramap.util.Mat2d;
import fr.thesmyler.terramap.util.Vec2d;
import fr.thesmyler.terramap.util.WebMercatorUtil;
import net.minecraft.client.renderer.GlStateManager;

/**
 * A layer of a map.
 * Takes care of some of the math for the implementing subclasses.
 * Especially, provides abstraction for rendering maps with rotation.
 * Deals with two sets of dimensions:
 * {@link #viewPortWidth} and {@link #viewPortHeight} are the width and height of the actual map widget
 * {@link #extendedHeight} and {@link #extendedHeight} are the width and height of the rendered map, which needs to be bigger to cover the whole area after rotation is applied
 * 
 * @author SmylerMC
 *
 */
public abstract class MapLayer implements IWidget {

    protected int z;
    private float viewPortWidth, viewPortHeight;
    private double extendedWidth, extendedHeight;
    private double centerLatitude, centerLongitude, zoom;
    private double renderDeltaLon, renderDeltaLat;
    private float rotation;
    private double tileScaling;

    private Mat2d directRotation = Mat2d.INDENTITY;
    private Mat2d inverseRotation = Mat2d.INDENTITY;
    private double upperLeftX;
    private double upperLeftY;
    private double renderCenterX, renderCenterY;
    
    private boolean isUserOverlay = false;
    private float alpha = 1.0f;

    public MapLayer(double tileScaling) {
        if(Double.isInfinite(tileScaling)) throw new RuntimeException("tileScaling cannot be null");
        this.tileScaling = tileScaling;
    }

    /**
     * Computes the x coordinate on the Web-Mercator map scaled by zoom level and tile scaling from a longitude
     * 
     * @param longitude
     * @return x
     */
    protected double getMapX(double longitude) {
        return WebMercatorUtil.getXFromLongitude(longitude, this.zoom) / this.tileScaling;
    }

    /**
     * Computes the y coordinate on the Web-Mercator map scaled by zoom level and tile scaling from a longitude
     * 
     * @param latitude
     * @return y
     */
    protected double getMapY(double latitude) {
        return WebMercatorUtil.getYFromLatitude(latitude, this.zoom) / this.tileScaling;
    }

    /**
     * Computes the position on this widget of a given geographic point
     * 
     * @param longitude
     * @param latitude
     * 
     * @return a Vec2d with longitude and latitude as x and y, in degrees
     */
    protected Vec2d getScreenPos(double longitude, double latitude) {
        Vec2d pos = new Vec2d(
                this.getMapX(longitude) - this.renderCenterX,
                this.getMapY(latitude) - this.renderCenterY);
        pos = this.directRotation.prod(pos);
        return pos.add(this.viewPortWidth / 2, this.viewPortHeight / 2);
    }

    protected double getUpperLeftX() {
        return this.upperLeftX;
    }

    protected double getUpperLeftY() {
        return this.upperLeftY;
    }

    public double[] getScreenGeoPos(double x, double y) {
        Vec2d pos = new Vec2d(x - this.viewPortWidth / 2, y - this.viewPortHeight / 2);
        pos = this.inverseRotation.prod(pos);
        pos = pos.add(
                this.extendedWidth / 2 + this.upperLeftX,
                this.extendedHeight / 2 + this.upperLeftY);
        pos = pos.scale(this.tileScaling);
        double lon = GeoUtil.getLongitudeInRange(WebMercatorUtil.getLongitudeFromX(pos.x, this.zoom));
        double lat = WebMercatorUtil.getLatitudeFromY(pos.y, this.zoom);
        return new double[] {lon, lat};
    }
    
    protected double getRenderX(double longitude) {
        return this.getMapX(longitude) - this.upperLeftX;
    }
    
    protected double getRenderY(double latitude) {
        return this.getMapY(latitude) - this.upperLeftY;
    }
    
    protected double getRenderLongitude(double x) {
        return WebMercatorUtil.getLongitudeFromX((this.upperLeftX + x) * this.tileScaling, this.zoom);
    }
    
    protected double getRenderLatitude(double y) {
        return WebMercatorUtil.getLatitudeFromY((this.upperLeftY + y) * this.tileScaling, this.zoom);
    }

    private void updateViewPort() {
        this.directRotation = Mat2d.forRotation(Math.toRadians(this.rotation));
        this.inverseRotation = this.directRotation.transpose(); // For rotations, the inverse is the transposed
        Vec2d dim = new Vec2d(this.viewPortWidth, this.viewPortHeight);
        //FIXME often crops out the corners, probably a floating point precision problem
        this.extendedWidth = dim.hadamardProd(this.directRotation.column1()).taxicabNorm();
        this.extendedHeight = dim.hadamardProd(this.directRotation.column2()).taxicabNorm();
        this.upperLeftX = this.getMapX(this.centerLongitude + this.renderDeltaLon) - this.extendedWidth / 2;
        this.upperLeftY = this.getMapY(this.centerLatitude + this.renderDeltaLat) - this.extendedHeight / 2;
        this.renderCenterX = this.getMapX(this.centerLongitude + this.renderDeltaLon);
        this.renderCenterY = this.getMapY(this.centerLatitude + this.renderDeltaLat);
    }

    protected void applyRotationGl(float drawX, float drawY) {
        GlStateManager.translate(drawX + this.viewPortWidth / 2, drawY + this.viewPortHeight / 2, 0);
        GlStateManager.rotate(this.rotation, 0, 0, 1);
        GlStateManager.translate(-this.extendedWidth / 2, -this.extendedHeight / 2, 0);
    }

    public abstract String getId();
    
    @Override
    public float getX() {
        return 0;
    }

    @Override
    public float getY() {
        return 0;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public float getWidth() {
        return this.viewPortWidth;
    }

    @Override
    public float getHeight() {
        return this.viewPortHeight;
    }

    public double getCenterLatitude() {
        return centerLatitude;
    }

    public void setCenterLatitude(double centerLatitude) {
        this.centerLatitude = centerLatitude;
        this.updateViewPort();
    }

    public double getCenterLongitude() {
        return centerLongitude;
    }

    public void setCenterLongitude(double centerLongitude) {
        this.centerLongitude = centerLongitude;
        this.updateViewPort();
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        this.updateViewPort();
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setDimensions(float width, float height) {
        if(width == this.viewPortWidth && height == this.viewPortHeight) return;
        this.viewPortWidth = width;
        this.viewPortHeight = height;
        this.updateViewPort();
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        if(rotation == this.rotation) return;
        this.rotation = GeoUtil.getAzimuthInRange(rotation);
        this.updateViewPort();
    }

    public double getTileScaling() {
        return this.tileScaling;
    }

    public void setTileScaling(double tileScaling) {
        this.tileScaling = tileScaling;
        this.updateViewPort();
    }

    public double getExtendedWidth() {
        return this.extendedWidth;
    }

    public double getExtendedHeight() {
        return this.extendedHeight;
    }

    public Mat2d getRotationMatrix() {
        return this.directRotation;
    }

    public Mat2d getInverseRotationMatrix() {
        return this.inverseRotation;
    }

    public double getRenderDeltaLongitude() {
        return renderDeltaLon;
    }

    public void setRenderDeltaLongitude(double renderDeltaLon) {
        this.renderDeltaLon = renderDeltaLon;
        this.updateViewPort();
    }

    public double getRenderDeltaLatitude() {
        return renderDeltaLat;
    }

    public void setRenderDeltaLatitude(double renderDeltaLat) {
        this.renderDeltaLat = renderDeltaLat;
        this.updateViewPort();
    }

    public boolean isUserOverlay() {
        return isUserOverlay;
    }

    public void setUserOverlay(boolean isUserOverlay) {
        this.isUserOverlay = isUserOverlay;
    }

    public float getAlpha() {
        return alpha;
    }

    public void setAlpha(float alpha) {
        this.alpha = alpha;
    }
    
    public abstract MapLayer copy();
    
    public abstract String name();
    
    public abstract String description();
    
    protected void copyPropertiesToOther(MapLayer other) {
        other.centerLongitude = this.centerLongitude;
        other.centerLatitude=  this.centerLatitude;
        other.alpha = this.alpha;
        other.rotation = this.rotation;
        other.renderDeltaLon = this.renderDeltaLon;
        other.renderDeltaLat = this.renderDeltaLat;
        other.viewPortWidth = this.viewPortWidth;
        other.viewPortHeight = this.viewPortHeight;
        other.isUserOverlay = this.isUserOverlay;
        other.updateViewPort();
    }
}
