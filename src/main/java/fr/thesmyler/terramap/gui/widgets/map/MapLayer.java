package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.GeoUtil;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.Mat2d;
import fr.thesmyler.terramap.util.math.Vec2d;
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
    private GeoPoint center = GeoPoint.ORIGIN;
    private double zoom;
    private Vec2d renderingOffset = Vec2d.NULL;
    private float rotation;
    private double tileScaling;

    private Mat2d directRotation = Mat2d.INDENTITY;
    private Mat2d inverseRotation = Mat2d.INDENTITY;
    private Vec2d upperLeftRenderCorner = Vec2d.NULL;
    private Vec2d renderCenter = Vec2d.NULL;
    
    private boolean isUserOverlay = false;
    private float alpha = 1.0f;

    public MapLayer(double tileScaling) {
        if(Double.isInfinite(tileScaling)) throw new RuntimeException("tileScaling cannot be null");
        this.tileScaling = tileScaling;
    }
    
    protected Vec2d getPositionOnMap(GeoPoint geoPos) {
        return WebMercatorUtil.fromGeo(geoPos, this.zoom).scale(1d / this.tileScaling);
    }

    /**
     * Computes the position on this widget of a given geographic point
     * 
     * @param longitude
     * @param latitude
     * 
     * @return a Vec2d with longitude and latitude as x and y, in degrees
     */
    protected Vec2d getScreenPosition(GeoPoint geoPos) {
        Vec2d pos = this.getPositionOnMap(geoPos).subtract(this.renderCenter);
        pos = this.directRotation.prod(pos);
        return pos.add(this.viewPortWidth / 2, this.viewPortHeight / 2);
    }
    
    /**
     * @return the coordinates of the upper left corner of the rendering viewport in the web Mercator map
     */
    protected Vec2d getUpperLeftRenderCorner() {
        return this.upperLeftRenderCorner;
    }
    
    public GeoPoint getScreenlocation(double x, double y) {
        Vec2d pos = new Vec2d(x - this.viewPortWidth / 2, y - this.viewPortHeight / 2);
        pos = this.inverseRotation.prod(pos);
        pos = pos.add(
                this.extendedWidth / 2 + this.upperLeftRenderCorner.x,
                this.extendedHeight / 2 + this.upperLeftRenderCorner.y);
        pos = pos.scale(this.tileScaling);
        return WebMercatorUtil.toGeo(pos, this.zoom);
    }
    
    protected Vec2d getRenderPos(GeoPoint geoPos) {
        return this.getPositionOnMap(geoPos).subtract(this.upperLeftRenderCorner);
    }
    
    protected GeoPoint getRenderLocation(Vec2d renderScreenPos) {
        return WebMercatorUtil.toGeo(renderScreenPos.add(this.upperLeftRenderCorner).scale(this.tileScaling), this.zoom);
    }

    private void updateViewPorts() {
        this.directRotation = Mat2d.forRotation(Math.toRadians(this.rotation));
        this.inverseRotation = this.directRotation.transpose(); // For rotations, the inverse is the transposed
        Vec2d dim = new Vec2d(this.viewPortWidth, this.viewPortHeight);
        this.extendedWidth = dim.hadamardProd(this.directRotation.column1()).taxicabNorm();
        this.extendedHeight = dim.hadamardProd(this.directRotation.column2()).taxicabNorm();
        this.renderCenter = this.getPositionOnMap(this.center).add(this.renderingOffset.scale(256d * Math.pow(2d, this.zoom) / this.tileScaling));
        this.upperLeftRenderCorner = this.renderCenter.subtract(this.extendedWidth / 2, this.extendedHeight / 2);
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
    
    public GeoPoint getCenterLocation() {
        return this.center;
    }
    
    public void setCenter(GeoPoint position) {
        this.center = position;
        this.updateViewPorts();
    }

    public double getZoom() {
        return zoom;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
        this.updateViewPorts();
    }

    public void setZ(int z) {
        this.z = z;
    }

    public void setDimensions(float width, float height) {
        if(width == this.viewPortWidth && height == this.viewPortHeight) return;
        this.viewPortWidth = width;
        this.viewPortHeight = height;
        this.updateViewPorts();
    }

    public float getRotation() {
        return this.rotation;
    }

    public void setRotation(float rotation) {
        if(rotation == this.rotation) return;
        this.rotation = GeoUtil.getAzimuthInRange(rotation);
        this.updateViewPorts();
    }

    public double getTileScaling() {
        return this.tileScaling;
    }

    public void setTileScaling(double tileScaling) {
        this.tileScaling = tileScaling;
        this.updateViewPorts();
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
    
    public Vec2d getRenderingOffset() {
        return this.renderingOffset;
    }
    
    /**
     * Sets the rendering offset for this layer.
     * The offset should be normalized (an offset of 1 being shifting by the entire map's size)
     * 
     * @param offset
     */
    public void setRenderingOffset(Vec2d offset) {
        this.renderingOffset = offset;
        this.updateViewPorts();
    }
    
    /**
     * Sets the rendering offset for this layer.
     * The offset is given in pixel for the current zoom level
     * 
     * @param offset
     */
    public void setPixelRenderingOffset(Vec2d offset) {
        this.renderingOffset = offset.downscale(Math.pow(2d, this.zoom) * 256);
        this.updateViewPorts();
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
        other.center = this.center;
        other.alpha = this.alpha;
        other.rotation = this.rotation;
        other.renderingOffset = this.renderingOffset;
        other.viewPortWidth = this.viewPortWidth;
        other.viewPortHeight = this.viewPortHeight;
        other.isUserOverlay = this.isUserOverlay;
        other.updateViewPorts();
    }
}
