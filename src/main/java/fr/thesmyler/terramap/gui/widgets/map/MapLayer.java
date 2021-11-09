package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.util.DefaultThreadLocal;
import fr.thesmyler.terramap.util.geo.*;
import fr.thesmyler.terramap.util.math.*;
import net.minecraft.client.renderer.GlStateManager;

import javax.annotation.Nullable;

import static fr.thesmyler.terramap.util.geo.GeoUtil.getAzimuthInRange;
import static java.lang.Math.pow;
import static java.lang.Math.toRadians;

/**
 * A layer of a map.
 * Takes care of some of the math for the implementing subclasses.
 * Especially, provides abstraction for rendering maps with rotation.
 * {@link #renderSpaceDimensions} is the width and height of the rendered map,
 * which needs to be bigger to cover the whole area after rotation is applied.
 * 
 * @author SmylerMC
 *
 */
public abstract class MapLayer implements IWidget {

    protected final MapWidget map;
    protected final MapController controller;

    protected int z;
    private final Vec2dMutable renderSpaceDimensions = new Vec2dMutable();
    private final Vec2dMutable renderSpaceDimensionsHalf = new Vec2dMutable(); // We need so much we might as well keep a copy
    protected final Vec2dMutable renderingOffset = new Vec2dMutable();
    private float rotation;
    private float rotationOffset; //TODO GUI

    private Mat2d directRotation = Mat2d.IDENTITY;
    private Mat2d inverseRotation = Mat2d.IDENTITY;
    protected final Vec2dMutable upperLeftRenderCorner = new Vec2dMutable();
    private final Vec2dMutable renderCenter = new Vec2dMutable();

    private final ThreadLocal<Vec2dMutable> calculationHelper = new DefaultThreadLocal<>(Vec2dMutable::new);
    
    private boolean isUserOverlay = false;
    private float alpha = 1.0f;
    private static final DoubleRange ALPHA_RANGE = new DoubleRange(0d, 1d);

    public MapLayer(MapWidget map) {
        this.map = map;
        this.controller = map.getController();
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, @Nullable WidgetContainer parent) {
        this.updateViewPorts();
    }

    /**
     * Computes the position on the entire map of a given point, scaled to this map's tile scaling and zoom.
     *
     * @param destination   a mutable vector to store the result in
     * @param geoPos        the position to convert
     *
     * @throws NullPointerException if either destination or geoPos is null
     */
    protected void getPositionInMercatorSpace(Vec2dMutable destination, GeoPoint<?> geoPos) {
        WebMercatorUtil.fromGeo(destination, geoPos, this.controller.getZoom()).scale(1d / this.map.getTileScaling());
    }

    /**
     * Computes the position on this widget of a given geographic point
     *
     * @param destination   a mutable destination vector to store the result in.
     * @param geoPos        a position to convert
     *
     * @throws NullPointerException if either destination or geoPos is null
     */
    protected void getPositionOnWidget(Vec2dMutable destination, GeoPoint<?> geoPos) {
        this.getPositionInMercatorSpace(destination, geoPos);
        destination.subtract(this.renderCenter)
                   .apply(this.directRotation)
                   .add(this.map.getWidth() / 2, this.map.getHeight() / 2);
    }
    
    /**
     * @return the coordinates of the upper left corner of the rendering viewport in the web Mercator map
     */
    protected Vec2dReadOnly getUpperLeftRenderCornerPositionInMercatorSpace() {
        return this.upperLeftRenderCorner.getReadOnly();
    }

    /**
     * Gets the real world location of a position on the widget
     *
     * @param destination   a point to store the result in
     * @param x             the X component of a position on the widget
     * @param y             the Y component of a position on the widget
     *
     * @throws NullPointerException if destination is null
     */
    public void getLocationAtPositionOnWidget(GeoPointMutable destination, double x, double y) {
        Vec2dMutable pos = this.calculationHelper.get();
        pos.set(x - this.map.getWidth() / 2, y - this.map.getHeight() / 2);
        pos.apply(this.inverseRotation);
        pos.add(this.renderSpaceDimensionsHalf);
        pos.add(this.upperLeftRenderCorner);
        pos.scale(this.map.getTileScaling());
        WebMercatorUtil.toGeo(destination, pos, this.controller.getZoom());
    }

    /**
     * Gets the real world location of a position on the widget
     *
     * @param destination   a point to store the result in
     * @param position      a position on the widget
     *
     * @throws NullPointerException if either destination or position is null
     */
    public void getLocationAtPositionOnWidget(GeoPointMutable destination, Vec2d<?> position) {
        this.getLocationAtPositionOnWidget(destination, position.x(), position.y());
    }

    /**
     * Gets the position of a geographic point in the rendering space.
     *
     * @param destination   a vector to store the result in
     * @param geoPos        a location to get the position of
     *
     * @throws NullPointerException if either destination or geoPos is null
     */
    protected void getLocationPositionInRenderSpace(Vec2dMutable destination, GeoPoint<?> geoPos) {
        this.getPositionInMercatorSpace(destination, geoPos);
        destination.subtract(this.upperLeftRenderCorner);
    }

    /**
     * Get the geographic location corresponding to a point in the rendering space.
     *
     * @param destination       a location to store the result in
     * @param renderScreenPos   a vector to get the location at
     *
     * @throws NullPointerException if either destination or renderScreenPos is null
     */
    protected void getLocationAtPositionInRenderSpace(GeoPointMutable destination, Vec2d<?> renderScreenPos) {
        renderScreenPos = this.calculationHelper.get().set(renderScreenPos)
                .add(this.upperLeftRenderCorner)
                .scale(this.map.tileScaling);
        WebMercatorUtil.toGeo(destination, renderScreenPos, this.controller.getZoom());
    }

    void updateViewPorts() {

        // Setup rotation matrices
        this.rotation = getAzimuthInRange(this.controller.getRotation() + this.rotationOffset);
        this.directRotation = Mat2d.forRotation(toRadians(this.rotation));
        this.inverseRotation = this.directRotation.transpose(); // For rotations, the inverse is the transposed

        // Calculate the width and height of the rendering space
        double mapWidth = this.map.getWidth();
        double mapHeight = this.map.getHeight();
        Vec2dMutable calc = this.calculationHelper.get().set(mapWidth, mapHeight);
        this.renderSpaceDimensions.x = calc.hadamardProd(this.directRotation.column1()).taxicabNorm();
        calc.set(mapWidth, mapHeight);
        this.renderSpaceDimensions.y = calc.hadamardProd(this.directRotation.column2()).taxicabNorm();
        this.renderSpaceDimensionsHalf.set(this.renderSpaceDimensions).downscale(2d);

        // Update positions for rendering
        this.getPositionInMercatorSpace(this.renderCenter, this.controller.getCenterLocation());
        double offsetScaleFactor = 256.0d * pow(2.0d, this.controller.getZoom()) / this.map.tileScaling;
        this.renderCenter.add(this.renderingOffset.x * offsetScaleFactor, this.renderingOffset.y * offsetScaleFactor);
        this.upperLeftRenderCorner.set(this.renderCenter).subtract(this.renderSpaceDimensionsHalf);

    }

    /**
     * Updates the current OpenGL context to rotate the screen according to this layer's rotation matrix.
     * This method needs to be called before rendering anything in implementing subclasses,
     * or the rendered content will not be rotated according to this layer's settings.
     *
     * The caller is responsible for calling {@link GlStateManager#pushMatrix()} and {@link GlStateManager#popMatrix()}.
     *
     * @param drawX the X coordinate the widget is supposed to be drawn at
     * @param drawY the Y coordinate the widget is supposed to be drawn at
     */
    protected void applyRotationGl(float drawX, float drawY) {
        GlStateManager.translate(drawX + this.map.getWidth() / 2, drawY + this.map.getHeight() / 2, 0);
        GlStateManager.rotate(this.rotation, 0, 0, 1);
        GlStateManager.translate(-this.renderSpaceDimensionsHalf.x, -this.renderSpaceDimensionsHalf.y, 0);
    }

    /**
     * @return an id used to save this layer's properties
     * @deprecated this should be removed when proper map serialisation is implemented
     */
    @Deprecated
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
        return this.map.getWidth();
    }

    @Override
    public float getHeight() {
        return this.map.getHeight();
    }

    //TODO Make this only callable from the map
    public void setZ(int z) {
        this.z = z;
    }

    /**
     * @return the dimensions of the render space of this layer
     */
    public Vec2dReadOnly getRenderSpaceDimensions() {
        return this.renderSpaceDimensions.getReadOnly();
    }

    /**
     * @return half the dimensions of the render space of this layer
     */
    public Vec2dReadOnly getRenderSpaceHalfDimensions() {
        return this.renderSpaceDimensionsHalf.getReadOnly();
    }

    /**
     * @return  this layer rotation to use when rendering.
     *          Takes into account the rotation offset as well as the map controller rotation.
     */
    protected float getRotation() {
        return this.rotation;
    }

    /**
     * @return the rotation matrix of this layer
     */
    public Mat2d getRotationMatrix() {
        return this.directRotation;
    }

    /**
     * @return the inverse rotation matrix of this layer
     */
    public Mat2d getInverseRotationMatrix() {
        return this.inverseRotation;
    }

    /**
     * @return the normalized rendering offset of this layer (normalized implies that an offset of 1 shifts by the entire map size)
     */
    public Vec2dReadOnly getRenderingOffset() {
        return this.renderingOffset.getReadOnly();
    }

    /**
     * @return whether this layer renders with an offset
     */
    public boolean hasRenderingOffset() {
        return this.renderingOffset.normSquared() != 0d;
    }
    
    /**
     * Sets the rendering offset for this layer.
     * The offset should be normalized (an offset of 1 being shifting by the entire map's size).
     * 
     * @param offset a new normalized rendering offset
     *
     * @throws NullPointerException if offset is null
     * @throws IllegalArgumentException if offset is not finite
     */
    public void setRenderingOffset(Vec2d<?> offset) {
        if (!offset.isFinite()) throw new IllegalArgumentException("Map offset has to be finite");
        this.renderingOffset.set(offset);
        this.updateViewPorts();
    }
    
    /**
     * Sets the rendering offset for this layer.
     * The offset is given in pixel for the current zoom level.
     * 
     * @param offset a new pixel rendering offset
     *
     * @throws NullPointerException if offset is null
     * @throws IllegalArgumentException if offset is not finite
     */
    public void setPixelRenderingOffset(Vec2d<?> offset) {
        if (!offset.isFinite()) throw new IllegalArgumentException("Layer offset has to be finite");
        this.renderingOffset.set(offset).downscale(pow(2d, this.controller.getZoom()) * 256);
        this.updateViewPorts();
    }

    /**
     * @return the rotation offset of this map
     */
    public float getRotationOffset() {
        return rotationOffset;
    }

    /**
     * Sets the rotation offset of this layer.
     *
     * @param rotationOffset the amount to offset rotation with
     *
     * @throws IllegalArgumentException if rotationOffset is not a finite number
     */
    public void setRotationOffset(float rotationOffset) {
        if (!Double.isFinite(rotationOffset)) throw new IllegalArgumentException("Layer rotation offset has to be finite");
        this.rotationOffset = rotationOffset;
        this.updateViewPorts();
    }

    /**
     * @return the map this layer is a part of
     */
    public MapWidget getMap() {
        return this.map;
    }

    /**
     * @return whether this layer should be treated as an additional layer  configured by the user
     */
    public boolean isUserOverlay() {
        return isUserOverlay;
    }

    /**
     * Sets whether this layer should be treated as an additional layer configured by the user.
     *
     * @param isUserOverlay whether this layer is user configured
     */
    public void setUserOverlay(boolean isUserOverlay) {
        this.isUserOverlay = isUserOverlay;
    }

    /**
     * @return the alpha value (transparency) value this layer should render at (between 0 and 1)
     */
    public float getAlpha() {
        return alpha;
    }

    /**
     * Sets this layer alpha value (transparency).
     *
     * @param alpha a new alpha value, between 0 and 1
     *
     * @throws IllegalArgumentException if alpha is not finite and in range
     */
    public void setAlpha(float alpha) {
        if (!Double.isFinite(alpha) || !ALPHA_RANGE.matches(alpha)) {
            throw new IllegalArgumentException("Layer alpha should be between 0 and 1, not " + alpha);
        }
        this.alpha = alpha;
    }

    /**
     * Copies this layer, except the new layer can be assigned to another map.
     *
     * @param mapFor the map the copy should be assigned to
     *
     * @return a deep copy of this layer
     */
    public abstract MapLayer copy(MapWidget mapFor);

    /**
     * @return a localized name for this layer
     */
    public abstract String name();

    /**
     * @return a localized description of this layer
     */
    public abstract String description();

    /**
     * A utility method to copy this layer's alpha, offsets, and user overlay status to another layer.
     * Useful to call in {@link #copy(MapWidget)}.
     *
     * @param other a layer to copy this one's properties to
     *
     * @throws NullPointerException if other is null
     */
    protected final void copyPropertiesToOther(MapLayer other) {
        other.alpha = this.alpha;
        other.rotationOffset = this.rotationOffset;
        other.renderingOffset.set(this.renderingOffset);
        other.isUserOverlay = this.isUserOverlay;
        other.updateViewPorts();
    }

}
