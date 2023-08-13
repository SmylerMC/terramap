package fr.thesmyler.terramap.gui.widgets.map;

import java.util.*;
import java.util.function.Supplier;

import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.maps.SavedLayerState;
import fr.thesmyler.terramap.maps.SavedMapState;
import fr.thesmyler.terramap.util.geo.*;
import fr.thesmyler.terramap.util.math.DoubleRange;
import fr.thesmyler.terramap.util.math.Vec2dMutable;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.util.Util;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.gui.widgets.markers.MarkerControllerManager;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.FeatureVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MainPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerDirectionsVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerNameVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.RightClickMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.MainPlayerMarker;
import fr.thesmyler.terramap.util.CopyrightHolder;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import static java.util.Comparator.comparingInt;

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
    private final MapController controller;
    private final List<MapLayer> layers = new ArrayList<>();
    private final List<MapLayer> layersReadOnly = Collections.unmodifiableList(this.layers);
    private final List<Marker> markers = new ArrayList<>();
    private final Map<String, MarkerController<?>> markerControllers = new LinkedHashMap<>();
    private RightClickMarkerController rcmMarkerController;
    private MainPlayerMarkerController mainPlayerMarkerController;
    private OtherPlayerMarkerController otherPlayerMarkerController;
    private MainPlayerMarker mainPlayerMarker;
    private String restoreTrackingId;
    private PlayerDirectionsVisibilityController directionVisibility;
    private PlayerNameVisibilityController nameVisibility;

    private final GeoPointMutable mouseLocation = new GeoPointMutable();
    private long lastUpdateTime = Long.MIN_VALUE;

    protected double tileScaling;

    private final MapMenuWidget rightClickMenu;

    private final TextWidget copyright;
    private final ScaleIndicatorWidget scale = new ScaleIndicatorWidget(-1);

    private final Profiler profiler = new Profiler();

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

        Font font = SmyLibGui.getDefaultFont();
        Font smallFont = Util.getSmallestFont();

        this.copyright = new TextWidget(Integer.MAX_VALUE, new TextComponentString(""), smallFont) {
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
                return MapWidget.this.reportedErrors.size() > 0 && MapWidget.this.context == MapContext.FULLSCREEN;
            }
        };
        this.errorText.setBackgroundColor(Color.ERROR_OVERLAY).setPadding(5).setAlignment(TextAlignment.CENTER).setShadow(false).setBaseColor(Color.WHITE);
        super.addWidget(errorText);

        this.rightClickMenu = new MapMenuWidget(this);

        this.scale.setX(15).setY(this.getHeight() - 30);
        super.addWidget(this.scale);

        this.controller = new MapController(this);

        InputLayer layer = new InputLayer(this);
        layer.setZ(0);
        super.addWidget(layer);
        this.inputLayer = layer;
        this.controller.inputLayer = this.inputLayer;


        this.setDoScissor(true);


        this.updateMouseGeoPos(this.getWidth()/2, this.getHeight()/2);

        for (MarkerController<?> controller: MarkerControllerManager.createControllers(this.context)) {
            if(controller instanceof RightClickMarkerController) {
                this.rcmMarkerController = (RightClickMarkerController) controller;
            } else if(controller instanceof MainPlayerMarkerController) {
                this.mainPlayerMarkerController = (MainPlayerMarkerController) controller;
            } else if(controller instanceof OtherPlayerMarkerController) {
                this.otherPlayerMarkerController = (OtherPlayerMarkerController) controller;
            }
            this.markerControllers.put(controller.getId(), controller);
        }

        if(this.mainPlayerMarkerController != null && this.otherPlayerMarkerController != null) {
            this.directionVisibility = new PlayerDirectionsVisibilityController(this.mainPlayerMarkerController, this.otherPlayerMarkerController);
            this.nameVisibility = new PlayerNameVisibilityController(this.mainPlayerMarkerController, this.otherPlayerMarkerController);
        }

        this.updateLayersViewports();

    }

    public MapWidget(int z, MapContext context, double tileScaling) {
        this(0, 0, z, 50, 50, context, tileScaling);
    }

    @Override
    public void init() {
        this.copyright.setFont(Util.getSmallestFont());
        this.updateLayersViewports();
    }

    /**
     * Creates a layer on this map.
     *
     * @param layerTypeId a layer type identifier, among those to those registered in global {@link MapLayerLibrary}.
     *
     * @return the newly created layer
     * @throws IllegalArgumentException if there is no such layer type id
     */
    public MapLayer createLayer(String layerTypeId) throws IllegalArgumentException {
        Supplier<MapLayer> constructor = MapLayerLibrary.INSTANCE.getLayerConstructor(layerTypeId);
        if (constructor == null) throw new IllegalArgumentException("No such layer type registered: " + layerTypeId);
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
        other.setUserOverlay(layer.isUserOverlay());
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

    /**
     * Adds a marker to this map
     *
     * @param marker a marker to add to this map
     */
    private void addMarker(Marker marker) {
        this.markers.add(marker);
        super.addWidget(marker);
    }

    /**
     * Removes the given marker from this map.
     * Marker should be added via a {@link MarkerController}
     *
     * @param marker a marker to remove from this map
     */
    public void removeMarker(Marker marker) {
        this.markers.remove(marker);
        super.removeWidget(marker);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        this.profiler.endSection(); // End rest of the screen section
        this.profiler.startSection("draw");
        super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
        this.profiler.endSection();
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {

        this.profiler.startSection("update-movement");
        long currentTime = System.currentTimeMillis();
        long dt = currentTime - this.lastUpdateTime;

        if(this.controller.isTracking()) {
            Marker tracked = this.controller.getTrackedMarker();
            if(this.widgets.contains(tracked)) {
                // Force update, so we don't lag behind, this one needs to be updated twice
                tracked.onUpdate(mouseX - tracked.getX(), mouseY - tracked.getY(), this);
            } else {
                this.controller.stopTracking();
            }
        }

        this.controller.update(dt);

        this.profiler.endStartSection("update-all");
        super.onUpdate(mouseX, mouseY, parent);

        this.copyright.setAnchorX(this.getWidth() - 3).setAnchorY(this.getHeight() - this.copyright.getHeight()).setMaxWidth(this.getWidth());
        this.scale.setX(15).setY(this.copyright.getAnchorY() - 15);
        this.errorText.setAnchorX(this.getWidth() / 2).setAnchorY(0).setMaxWidth(this.getWidth() - 40);
        if(!this.rightClickMenu.isVisible(this)) this.updateMouseGeoPos(mouseX, mouseY);
        if(this.reportedErrors.size() > 0) {
            String errorText = SmyLibGui.getTranslator().format("terramap.mapwidget.error.header") + "\n" + this.reportedErrors.get((int) ((System.currentTimeMillis() / 3000)%this.reportedErrors.size())).message;
            this.errorText.setText(new TextComponentString(errorText));
        }

        this.profiler.endStartSection("update-markers");
        this.updateMarkers(mouseX, mouseY);

        this.profiler.endStartSection("rest-of-screen");

        this.lastUpdateTime = currentTime;
    }

    /**
     * Maps do not support directly adding or removing widgets.
     * This method will always throw an exception.
     *
     * @throws UnsupportedOperationException in any case
     */
    @Override @Deprecated
    public WidgetContainer addWidget(IWidget widget) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    /**
     * Maps do not support directly adding or removing widgets.
     * This method will always throw an exception.
     *
     * @throws UnsupportedOperationException in any case
     */
    @Override @Deprecated
    public WidgetContainer removeWidget(IWidget widget) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    private void updateMarkers(float mouseX, float mouseY) {
        // Gather the existing classes
        Map<Class<?>, List<Marker>> markers = new HashMap<>();
        for(MarkerController<?> controller: this.markerControllers.values()) {
            markers.put(controller.getMarkerType(), new ArrayList<>());
        }

        // Sort the markers by class
        for(Marker marker: this.markers) {
            for(Class<?> clazz: markers.keySet()) {
                if(clazz.isInstance(marker)) {
                    markers.get(clazz).add(marker);
                }
            }
        }

        // Update the markers
        for(MarkerController<?> controller: this.markerControllers.values()) {
            Marker[] existingMarkers = markers.get(controller.getMarkerType()).toArray(new Marker[] {});
            Marker[] newMarkers = controller.getNewMarkers(existingMarkers, this);
            for(Marker markerToAdd: newMarkers) {
                this.addMarker(markerToAdd);
            }
            if(controller.getMarkerType().equals(MainPlayerMarker.class) && newMarkers.length > 0) {
                this.mainPlayerMarker = (MainPlayerMarker) newMarkers[0];
            }
            if(this.restoreTrackingId != null) {
                for(Marker markerToAdd: newMarkers) {
                    String id = markerToAdd.getIdentifier();
                    if(id != null && id.equals(this.restoreTrackingId)) {
                        this.controller.track(markerToAdd);
                        this.restoreTrackingId = null;
                        TerramapMod.logger.debug("Restored tracking with " + id);
                    }
                }
            }
        }

        // Update right click marker visibility
        if(this.rcmMarkerController != null) this.rcmMarkerController.setVisibility(this.rightClickMenu.isVisible(this));

        for(Marker marker: this.markers) marker.onUpdate(mouseX, mouseY, this);

    }

    public void updateCopyright() {
        ITextComponent component = new TextComponentString("");
        for(IWidget widget: this.widgets) 
            if(widget instanceof CopyrightHolder){
                if(component.getFormattedText().length() > 0) component.appendText(" | ");
                ITextComponent copyright = ((CopyrightHolder)widget).getCopyright(SmyLibGui.getGameContext().getLanguage());
                component.appendSibling(copyright);
            }
        this.copyright.setText(component);
        this.copyright.setVisibility(component.getFormattedText().length() > 0);
    }

    private void updateMouseGeoPos(float mouseX, float mouseY) {
        this.inputLayer.getLocationAtPositionOnWidget(this.mouseLocation, mouseX, mouseY);
    }

    /**
     * @return the {@link FeatureVisibilityController} active for this map (the {@link Map} returned is a copy)
     */
    public Map<String, FeatureVisibilityController> getVisibilityControllers() {
        Map<String, FeatureVisibilityController> m = new LinkedHashMap<>(this.markerControllers); // Order matters !
        if (this.directionVisibility != null ) m.put(this.directionVisibility.getSaveName(), this.directionVisibility);
        if (this.nameVisibility != null) m.put(this.nameVisibility.getSaveName(), this.nameVisibility);
        for (MapLayer layer: this.layers) {
            if (layer instanceof FeatureVisibilityController) {
                FeatureVisibilityController featureVisibilityController = (FeatureVisibilityController) layer;
                m.put(featureVisibilityController.getSaveName(), featureVisibilityController);
            }
        }
        return m;
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
     * @return the {@link MainPlayerMarker} marker for this client, or null if it does not exist
     */
    public MainPlayerMarker getMainPlayerMarker() {
        return this.mainPlayerMarker;
    }

    /**
     * Tries to set a feature's visibility, or does nothing if the feature does not exist for this map.
     * 
     * @param controllerId - the id of the {@link FeatureVisibilityController} to set the visibility for
     * @param value - visibility
     * 
     */
    public void trySetFeatureVisibility(String controllerId, boolean value) {
        FeatureVisibilityController c = this.getVisibilityControllers().get(controllerId);
        if(c != null) c.setVisibility(value);
    }

    /**
     * Tries to resume tracking a marker using its string id, does nothing if the marker is not found.
     * 
     * @param markerId the id of a marker to find and track
     */
    public void restoreTracking(String markerId) {
        this.restoreTrackingId = markerId;
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
        this.profiler.profilingEnabled = debugMode;
        if(!debugMode) this.profiler.clearProfiling();
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

    public SavedMapState save() {
        SavedMapState state = new SavedMapState();
        state.center.set(this.controller.getTargetLocation());
        state.zoom = this.controller.getTargetZoom();
        state.rotation = this.controller.getTargetRotation();
        Marker tracked = this.controller.getTrackedMarker();
        state.trackedMarker = tracked != null ? tracked.getIdentifier(): null;
        for (MapLayer layer: this.layers) {
            SavedLayerState layerState = new SavedLayerState();
            layerState.type = layer.getType();
            layerState.z = layer.getZ();
            layerState.overlay = layer.isUserOverlay();
            layerState.cartesianOffset.set(layer.getRenderingOffset());
            layerState.rotationOffset = layer.getRotationOffset();
            layerState.alpha = layer.getAlpha();
            layerState.settings = layer.saveSettings();
            state.layers.add(layerState);
        }
        state.visibilitySettings.clear();
        this.getVisibilityControllers().values().forEach(c -> state.visibilitySettings.put(c.getSaveName(), c.getVisibility()));
        return state;
    }

    public void restore(SavedMapState state) {
        this.controller.moveLocationToCenter(state.center, false);
        this.controller.setRotationStaticLocation(state.center);
        this.controller.setRotation(state.rotation, false);
        this.controller.setZoomStaticLocation(state.center);
        this.controller.setZoom(state.zoom, false);
        this.restoreTracking(state.trackedMarker);
        new ArrayList<>(this.layers) // Avoid co-modification problems
                .forEach(this::removeLayer);
        for (SavedLayerState layerState: state.layers) {
            MapLayer layer = this.createLayer(layerState.type);
            this.setLayerZ(layer, layerState.z);
            layer.setAlpha(layerState.alpha);
            layer.setRenderingOffset(layerState.cartesianOffset);
            layer.setRotationOffset(layerState.rotationOffset);
            layer.setUserOverlay(layerState.overlay);
            layer.loadSettings(layerState.settings);
        }
        Map<String, FeatureVisibilityController> controllers = this.getVisibilityControllers();
        for (String key: state.visibilitySettings.keySet()) {
            FeatureVisibilityController controller = controllers.get(key);
            if (controller != null) controller.setVisibility(state.visibilitySettings.get(key));
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
    public Optional<RasterMapLayer> getRasterBackgroundLayer() {
        return this.getBackgroundLayer().map(l -> l instanceof RasterMapLayer ? (RasterMapLayer) l: null);
    }

    /**
     * @return this map's profiler
     */
    public Profiler getProfiler() {
        return this.profiler;
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
