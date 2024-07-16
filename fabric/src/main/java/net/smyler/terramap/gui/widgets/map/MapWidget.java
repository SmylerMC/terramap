package net.smyler.terramap.gui.widgets.map;

import java.util.*;
import java.util.function.Supplier;

import net.smyler.smylib.gui.Font;
import net.smyler.smylib.math.DoubleRange;
import net.smyler.smylib.math.Vec2dMutable;

import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.widgets.Widget;
import net.smyler.smylib.gui.widgets.text.TextAlignment;
import net.smyler.smylib.gui.widgets.text.TextWidget;
import net.smyler.terramap.MapContext;
import net.smyler.terramap.gui.widgets.map.layer.OnlineRasterMapLayer;
import net.smyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import net.smyler.terramap.util.CopyrightHolder;
import net.smyler.smylib.text.ImmutableText;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.util.geo.GeoPoint;
import net.smyler.terramap.util.geo.GeoPointMutable;
import net.smyler.terramap.util.geo.GeoPointReadOnly;

import static java.util.Comparator.comparingInt;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.text.ImmutableText.of;
import static net.smyler.smylib.text.ImmutableText.ofPlainText;

/**
 * The core component of Terramap: the map widget itself.
 * This is in fact a {@link FlexibleWidgetContainer} that groups together the various components of the map, which are:
 * <ul>
 *  <li>The map's layers</li>
 *  <li>The map's input processing layer (internal to the map)</li>
 *  <li>The map's markers</li>
 *  <li>The map's copyright notice</li>
 *  <li>The map's scale widget</li>
 *  <li>The map's right click menu</li>
 * </ul>
 *
 * @author SmylerMC
 *
 */
//TODO keep track of errors according to layers
public class MapWidget extends FlexibleWidgetContainer {

    private boolean interactive = true;
    private boolean focusedZoom = true; // Zoom where the cursor is (true) or at the center of the map (false) when using the wheel
    private boolean enableRightClickMenu = true;
    private boolean allowsQuickTp = true;
    private boolean showCopyright = true;
    private boolean debugMode = false;
    private boolean visible = true;

    private final InputLayer inputLayer;
    private final MapController controller = new MapController(this);
    private final List<MapLayer> layers = new ArrayList<>();
    private final List<MapLayer> layersReadOnly = Collections.unmodifiableList(this.layers);

    private final GeoPointMutable mouseLocation = new GeoPointMutable();
    private long lastUpdateTime = Long.MIN_VALUE;

    protected double tileScaling;

    private final MapMenuWidget rightClickMenu;

    private final TextWidget copyright;
    private final ScaleIndicatorWidget scale = new ScaleIndicatorWidget(-1);

    private final TextWidget errorText;

    private final List<ReportedError> reportedErrors = new ArrayList<>();
    private static final int MAX_ERRORS_KEPT = 10;

    private final MapContext context;
    static final DoubleRange ZOOM_RANGE = new DoubleRange(0d, 25d);

    public static final double MIN_TILE_SCALING = 1e-3;

    /**
     * Constructs a new map widget.
     *
     * @param x             the X coordinate of the map in the parent container's coordinate space (in pixels)
     * @param y             the Y coordinate of the map in the parent container's coordinate space (in pixels)
     * @param z             the Z index of the map in the parent container's widget stack
     * @param width         the width of the map (in pixels)
     * @param height        the height of the map (in pixels)
     * @param context       the context for which this map is being created
     * @param tileScaling   a rendering scale factor
     *
     * @throws IllegalArgumentException if tileScaling is smaller than {@link MapWidget#MIN_TILE_SCALING}
     */
    public MapWidget(float x, float y, int z, float width, float height, MapContext context, double tileScaling) {
        super(x, y, z, width, height);

        this.context = context;

        if (tileScaling < MIN_TILE_SCALING) {
            throw new IllegalArgumentException("Constructing a map widget with a tile scaling value that's too small " + tileScaling);
        }
        this.tileScaling = tileScaling;

        Font font = getGameClient().defaultFont();
        Font smallFont = getGameClient().smallestFont();

        this.copyright = new TextWidget(Integer.MAX_VALUE, ImmutableText.EMPTY, smallFont) {
            @Override
            public boolean isVisible(WidgetContainer parent) {
                return MapWidget.this.showCopyright;
            }
        };
        this.copyright.setBackgroundColor(Color.DARK_OVERLAY).setPadding(3).setAlignment(TextAlignment.LEFT).setShadow(false);
        super.addWidget(this.copyright);

        this.errorText = new TextWidget(Integer.MAX_VALUE, font) {
            @Override
            public boolean isVisible(WidgetContainer parent) {
                return !MapWidget.this.reportedErrors.isEmpty() && MapWidget.this.context == MapContext.FULLSCREEN;
            }
        };
        this.errorText.setBackgroundColor(Color.ERROR_OVERLAY).setPadding(5).setAlignment(TextAlignment.CENTER).setShadow(false).setBaseColor(Color.WHITE);
        super.addWidget(errorText);

        this.rightClickMenu = new MapMenuWidget(this);

        this.scale.setX(15).setY(this.getHeight() - 30);
        super.addWidget(this.scale);


        InputLayer layer = new InputLayer(this);
        layer.setZ(0);
        super.addWidget(layer);
        this.inputLayer = layer;
        this.controller.inputLayer = this.inputLayer;


        this.setDoScissor(true);


        this.updateMouseGeoPos(this.getWidth()/2, this.getHeight()/2);

        this.updateLayersViewports();

    }

    public MapWidget(int z, MapContext context, double tileScaling) {
        this(0, 0, z, 50, 50, context, tileScaling);
    }

    @Override
    public void init() {
        this.copyright.setFont(getGameClient().smallestFont());
        this.updateLayersViewports();
    }

    /**
     * Creates a layer on this map.
     *
     * @param layerTypeId a layer type identifier, among those to those registered in global {@link MapLayerRegistry}.
     *
     * @return the newly created layer
     * @throws IllegalArgumentException if there is no such layer type id
     */
    public MapLayer createLayer(String layerTypeId) throws IllegalArgumentException {
        MapLayerRegistry.LayerRegistration<?> registration = MapLayerRegistry.INSTANCE.getRegistrations(layerTypeId);
        if (registration == null) throw new IllegalArgumentException("No such layer type registered: " + layerTypeId);
        Supplier<? extends MapLayer> constructor = registration.getConstructor();
        MapLayer layer = constructor.get();
        layer.setMap(this);
        layer.setType(layerTypeId);
        super.addWidget(layer);
        this.layers.add(layer);
        layer.initialize();
        this.updateCopyright();
        return layer;
    }

    /**
     * Creates a copy for this map of a given layer.
     *
     * @param layer the layer to copy
     *
     * @return a copy of the layer, which belongs to this map
     */
    public final MapLayer copyLayer(MapLayer layer) {
        MapLayer other = this.createLayer(layer.getType());
        other.setAlpha(layer.getAlpha());
        other.setRenderingOffset(layer.getRenderingOffset());
        other.setIsUserLayer(layer.isUserLayer());
        other.setRotationOffset(layer.getRotationOffset());
        this.setLayerZ(other, layer.getZ());
        other.loadSettings(layer.saveSettings());
        return other;
    }

    /**
     * Removes a layer from the map.
     *
     * @param layer the layer to remove
     */
    public void removeLayer(MapLayer layer) {
        super.removeWidget(layer);
        this.layers.remove(layer);
        this.updateCopyright();
        this.discardPreviousErrors(layer);
    }

    public void setLayerZ(MapLayer layer, int z) {
        if (layer.getMap() != this) throw new IllegalArgumentException("Cannot move a layer that does not belong to this map");
        super.removeWidget(layer);
        layer.setZ(z);
        super.addWidget(layer);
        this.updateCopyright();
    }

    public List<MapLayer> getLayers() {
        return this.layersReadOnly;
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {

        long currentTime = System.currentTimeMillis();
        long dt = currentTime - this.lastUpdateTime;

        this.controller.update(dt);

        super.onUpdate(mouseX, mouseY, parent);

        this.copyright.setAnchorX(this.getWidth() - 3).setAnchorY(this.getHeight() - this.copyright.getHeight()).setMaxWidth(this.getWidth());
        this.scale.setX(15).setY(this.copyright.getAnchorY() - 15);
        this.errorText.setAnchorX(this.getWidth() / 2).setAnchorY(0).setMaxWidth(this.getWidth() - 40);
        if(!this.rightClickMenu.isVisible(this)) this.updateMouseGeoPos(mouseX, mouseY);
        if(!this.reportedErrors.isEmpty()) {
            String errorText = getGameClient().translator().format("terramap.mapwidget.error.header") + "\n" + this.reportedErrors.get((int) ((System.currentTimeMillis() / 3000)%this.reportedErrors.size())).message;
            this.errorText.setText(ofPlainText(errorText));
        }

        this.lastUpdateTime = currentTime;
    }

    /**
     * Maps do not support directly adding or removing widgets.
     * This method will always throw an exception.
     *
     * @throws UnsupportedOperationException in any case
     */
    @Override @Deprecated
    public WidgetContainer addWidget(Widget widget) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Maps do not support directly adding or removing widgets.
     * This method will always throw an exception.
     *
     * @throws UnsupportedOperationException in any case
     */
    @Override @Deprecated
    public WidgetContainer removeWidget(Widget widget) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public void updateCopyright() {
        ImmutableText component = ImmutableText.EMPTY;
        ImmutableText separator = ofPlainText(" | ");
        for(Widget widget: this.widgets)
            if(widget instanceof CopyrightHolder){
                if(!component.getFormattedText().isEmpty()) {
                    component = component.withNewSiblings(separator);
                }
                Text copyright = ((CopyrightHolder)widget).getCopyright(getGameClient().translator().language());
                component = component.withNewSiblings(of(copyright));
            }
        this.copyright.setText(component);
        this.copyright.setVisibility(!component.getFormattedText().isEmpty());
    }

    private void updateMouseGeoPos(float mouseX, float mouseY) {
        this.inputLayer.getLocationAtPositionOnWidget(this.mouseLocation, mouseX, mouseY);
    }

    /**
     * @return the location hovered by the mouse
     */
    public GeoPointReadOnly getMouseLocation() {
        return this.mouseLocation.getReadOnly();
    }

    /**
     * @return this map's controller
     */
    public MapController getController() {
        return this.controller;
    }

    /**
     * Sets this map's size
     * 
     * @param width a new width for this map
     * @param height a new height for this map
     */
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        this.scale.setY(this.getHeight() - 20);
    }

    /**
     * @return whether this map accepts input at all
     */
    public boolean isInteractive() {
        return this.interactive;
    }

    /**
     * Sets whether this map takes input.
     * 
     * @param yesNo whether this map should accept user inputs
     */
    public void setInteractive(boolean yesNo) {
        this.interactive = yesNo;
    }

    /**
     * @return Whether this map's right click menu is enabled
     */
    public boolean isRightClickMenuEnabled() {
        return this.enableRightClickMenu;
    }

    /**
     * Sets whether this map's right click menu is enabled.
     * This is independent from {@link #setInteractive(boolean)}.
     * 
     * @param yesNo whether to enable this map's right click menu
     */
    public void setRightClickMenuEnabled(boolean yesNo) {
        this.enableRightClickMenu = yesNo;
    }

    /**
     * @return whether this map copyright is visible
     */
    public boolean getCopyrightVisibility() {
        return this.showCopyright;
    }

    /**
     * Sets whether this map's copyright is visible.
     * 
     * @param yesNo whether to show this map's copyright notice
     */
    public void setCopyrightVisibility(boolean yesNo) {
        this.showCopyright = yesNo;
    }

    /**
     * Computes the position on this widget of a given {@link GeoPoint}.
     *
     * @param destination   a vector to store the result in
     * @param location      the location to compute the position of
     */
    public void getScreenPosition(Vec2dMutable destination, GeoPoint<?> location) {
        this.inputLayer.getPositionOnWidget(destination, location);
    }


    /**
     * Computes the location displayed at a given position on the widget.
     *
     * @param destination   a point to store the result in
     * @param x             a coordinate on this map's coordinate system
     * @param y             a coordinate on this map's coordinate system
     */
    public void getScreenLocation(GeoPointMutable destination, double x, double y) {
        this.inputLayer.getLocationAtPositionOnWidget(destination, x, y);
    }

    /**
     * @return the X coordinate of this map's scale widget
     */
    public float getScaleX() {
        return this.scale.getX();
    }

    /**
     * Sets the X coordinate of this map's scale widget
     * 
     * @param x a new X coordinate in the widget for the scale
     */
    public void setScaleX(float x) {
        this.scale.setX(x);
    }

    /**
     * @return the Y coordinate of this map's scale widget
     */
    public float getScaleY() {
        return this.scale.getY();
    }

    /**
     * @return the width of this map's scale widget
     */
    public float getScaleWidth() {
        return this.scale.getWidth();
    }

    /**
     * Sets the width of this map scale widget.
     * 
     * @param width a new width for this map's scale
     */
    public void setScaleWidth(float width) {
        this.scale.setWidth(width);
    }

    /**
     * @return whether of not this map's scale widget is visible
     */
    public boolean getScaleVisibility() {
        return this.scale.isVisible(this);
    }

    /**
     * Sets the visibility of this map's scale widget.
     * 
     * @param yesNo whether to display this map's scale
     */
    public void setScaleVisibility(boolean yesNo) {
        this.scale.setVisibility(yesNo);
    }

    /**
     * @return this map's {@link MapContext}
     * @deprecated MapContext will soon be deprecated
     */
    @Deprecated
    public MapContext getContext() {
        return this.context;
    }

    /**
     * @return whether this map shows debug information
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Activates and resets this map's profiler and start showing debug information on this map.
     * 
     * @param debugMode whether this map should be in debug mod
     */
    public void setDebugMode(boolean debugMode) {
        this.debugMode = debugMode;
    }

    /**
     * @return this map rendering scale factor
     */
    public double getTileScaling() {
        return this.tileScaling;
    }

    /**
     * Sets this map rendering scale factor.
     *
     * @param tileScaling a new value for this map's scale factor
     *
     * @throws IllegalArgumentException if tileScaling is below {@link MapWidget#MIN_TILE_SCALING}
     */
    public void setTileScaling(double tileScaling) {
        if (tileScaling < MIN_TILE_SCALING) {
            throw new IllegalArgumentException("Map tile scaling value is too small: " + tileScaling);
        }
        this.tileScaling = tileScaling;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visible;
    }

    /**
     * Sets this map's visibility.
     * 
     * @param yesNo whether this map should be visible
     */
    public void setVisibility(boolean yesNo) {
        this.visible = yesNo;
    }

    /**
     * Reports an error that will be shown in this map's error area,
     * until discarded with {@link #discardPreviousErrors(Object)}.
     * 
     * @param source        the source of the error, which can used as a key to discard errors afterwards
     * @param errorMessage  the error message
     */
    public void reportError(Object source, String errorMessage) {
        ReportedError error = new ReportedError(source, errorMessage);
        if(this.reportedErrors.contains(error)) return;
        this.reportedErrors.add(error);
        if(this.reportedErrors.size() > MAX_ERRORS_KEPT) {
            this.reportedErrors.remove(0);
        }
    }

    /**
     * Discard the errors from the given source.
     * 
     * @param source the object that was used as the key when the error was reported
     */
    public void discardPreviousErrors(Object source) {
        List<ReportedError> errsToRm = new ArrayList<>();
        for(ReportedError e: this.reportedErrors) {
            if(e.source.equals(source)) errsToRm.add(e);
        }
        this.reportedErrors.removeAll(errsToRm);
    }

    private static class ReportedError {

        private final Object source;
        private final String message;

        private ReportedError(Object source, String message) {
            this.source = source;
            this.message = message;
        }
    }

    /**
     * Utility method to access a map's background when it has one.
     * The map's background is considered to be the {@link MapLayer layer} with the lowest negative Z level.
     *
     * @return the map's background as an {@link Optional},
     *         or an empty {@link Optional} if no {@link MapLayer layers} fulfilled the definition of a background.
     */
    public Optional<MapLayer> getBackgroundLayer() {
        return this.getLayers().stream()
                .filter(l -> l.getZ() < 0)
                .min(comparingInt(MapLayer::getZ));
    }

    /**
     * Utility method to access a map's raster background when it has one.
     * The map's raster background is considered to be the {@link MapLayer layer} with the lowest negative Z level,
     * if it is a {@link RasterMapLayer raster layer}.
     *
     * @return the map's raster background as an {@link Optional},
     *         or an empty {@link Optional} if no {@link MapLayer layers} fulfilled the definition of a raster background.
     */
    public Optional<OnlineRasterMapLayer> getRasterBackgroundLayer() {
        return this.getBackgroundLayer().map(l -> l instanceof RasterMapLayer ? (OnlineRasterMapLayer) l: null);
    }

    /**
     * Stops all passive inputs (inputs that does not require the user to actively press a button, e.g. rotation).
     */
    public void stopPassiveInputs() {
        this.inputLayer.isRotating = false;
    }

    /**
     * @return whether this map zooms to the mouse position (true) or to its center (false).
     */
    public boolean isFocusedZoom() {
        return focusedZoom;
    }

    /**
     * Sets whether this map zooms to the mouse position (true) or to its center (false).
     */
    public void setFocusedZoom(boolean focusedZoom) {
        this.focusedZoom = focusedZoom;
    }

    /**
     * @return whether the user can teleport with the ctrl+click shortcut.
     */
    public boolean allowsQuickTp() {
        return allowsQuickTp;
    }

    /**
     * Sets whether the user can teleport with the ctrl+click shortcut.
     */
    public void setAllowsQuickTp(boolean allowsQuickTp) {
        this.allowsQuickTp = allowsQuickTp;
    }

    InputLayer getInputLayer() {
        return this.inputLayer;
    }

    public MapMenuWidget getRightClickMenu() {
        return this.rightClickMenu;
    }

    private void updateLayersViewports() {
        this.inputLayer.updateViewPorts();
        this.layers.forEach(MapLayer::updateViewPorts);
    }

}
