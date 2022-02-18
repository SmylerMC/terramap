package fr.thesmyler.terramap.gui.widgets.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import fr.thesmyler.terramap.config.TerramapConfig;
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
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget.MenuEntry;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.gui.screens.LayerRenderingOffsetPopup;
import fr.thesmyler.terramap.gui.widgets.map.layer.McChunksLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
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
import fr.thesmyler.terramap.maps.raster.IRasterTiledMap;
import fr.thesmyler.terramap.util.ICopyrightHolder;
import net.buildtheearth.terraplusplus.control.PresetEarthGui;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

import static java.lang.Math.*;

/**
 * The core component of Terramap: the map widget itself.
 * This is in fact a {@link FlexibleWidgetContainer} that groups together the various components of the map, which are:
 * <ul>
 *  <li>The map's background layer</li>
 *  <li>The map's overlay layers</li>
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
    private RasterMapLayer background;
    private final List<MapLayer> overlayLayers = new ArrayList<>();
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

    private MenuWidget rightClickMenu;
    private MenuEntry teleportMenuEntry;
    private MenuEntry copyBlockMenuEntry;
    private MenuEntry copyChunkMenuEntry;
    private MenuEntry copyRegionMenuEntry;
    private MenuEntry copy3drMenuEntry;
    private MenuEntry copy2drMenuEntry;
    private MenuEntry setProjectionMenuEntry;

    private final TextWidget copyright;
    private final ScaleIndicatorWidget scale = new ScaleIndicatorWidget(-1);

    private final Profiler profiler = new Profiler();
    private static final GuiScreen CHAT_SENDER_GUI = new GuiScreen() {}; // The only reason this exists is so we can use it to send chat messages
    static { CHAT_SENDER_GUI.mc = Minecraft.getMinecraft(); }

    private final TextWidget errorText;

    private final List<ReportedError> reportedErrors = new ArrayList<>();
    private static final int MAX_ERRORS_KEPT = 10;

    private final MapContext context;

    public static final int BACKGROUND_Z = Integer.MIN_VALUE;
    public static final int CONTROLLER_Z = 0;
    static final DoubleRange ZOOM_RANGE = new DoubleRange(0d, 25d);

    public MapWidget(float x, float y, int z, float width, float height, IRasterTiledMap map, MapContext context, double tileScaling) {
        super(x, y, z, width, height);
        this.controller = new MapController(this);
        this.inputLayer = new InputLayer(this);
        this.controller.inputLayer = this.inputLayer;
        this.setDoScissor(true);
        this.context = context;
        this.tileScaling = tileScaling;
        Font font = SmyLibGui.DEFAULT_FONT;
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

        this.createRightClickMenu();

        super.addWidget(this.inputLayer);

        this.setBackground(new RasterMapLayer(this, map));

        this.scale.setX(15).setY(this.getHeight() - 30);
        super.addWidget(this.scale);
        this.updateRightClickMenuEntries();
        this.updateMouseGeoPos(this.getWidth()/2, this.getHeight()/2);

        for(MarkerController<?> controller: MarkerControllerManager.createControllers(this.context)) {
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
        
        McChunksLayer chunks = new McChunksLayer(this);
        chunks.setZ(-1000);
        this.addOverlayLayer(chunks);

    }

    public MapWidget(int z, IRasterTiledMap map, MapContext context, double tileScaling) {
        this(0, 0, z, 50, 50, map, context, tileScaling);
    }

    @Override
    public void init() {
        this.copyright.setFont(Util.getSmallestFont());
    }

    /**
     * Adds an overlay layer to this map.
     * <p>
     * The layer's Z level is important as anything higher than {@link #CONTROLLER_Z} that captures inputs will could stop inputs from reaching the map's controller layer.
     * 
     * @param layer - the overlay to add
     * @return this map, for chaining
     * @throws IllegalArgumentException if the layer's Z level is set to a reserved value, like {@link #BACKGROUND_Z} or {@link #CONTROLLER_Z}
     */
    public MapWidget addOverlayLayer(MapLayer layer) {
        switch(layer.getZ()) {
            case BACKGROUND_Z:
                throw new IllegalArgumentException("Z level " + layer.getZ() + " is reserved for background layer");
            case CONTROLLER_Z:
                throw new IllegalArgumentException("Z level " + layer.getZ() + " is reserved for controller layer");
        }
        if (layer.map != this) throw new IllegalArgumentException("Trying to add a layer that does not belong to this map");
        this.overlayLayers.add(layer);
        super.addWidget(layer);
        this.updateCopyright();
        return this;
    }

    /**
     * Removes the given overlay layer from this map.
     * 
     * @param layer a layer to remove from this map
     */
    public void removeOverlayLayer(MapLayer layer) {
        this.overlayLayers.remove(layer);
        this.discardPreviousErrors(layer); // We don't care about errors for this overlay anymore
        super.removeWidget(layer);
        this.updateCopyright();
    }

    /**
     * Sets this map's background style
     * 
     * @param background a layer to set as this map's background
     */
    private void setBackground(RasterMapLayer background) {
        if (background.map != this) throw new IllegalArgumentException("Trying to add a layer that does not belong to this map");
        background.z = BACKGROUND_Z;
        this.discardPreviousErrors(this.background); // We don't care about errors for this background anymore
        super.removeWidget(this.background);
        super.addWidget(background);
        this.background = background;
        this.updateCopyright();
        this.controller.setMinZoom(this.background.getTiledMap().getMinZoom());
        this.controller.setMaxZoom(TerramapConfig.CLIENT.unlockZoom ? 25: this.background.getTiledMap().getMaxZoom());
    }

    /**
     * Sets this map background layer to a {@link RasterMapLayer} constructed from the given {@link IRasterTiledMap}.
     * 
     * @param map - a raster tiled map
     */
    public void setBackground(IRasterTiledMap map) {
        this.setBackground(new RasterMapLayer(this, map));
    }
    
    /**
     * @return this map's background layer
     */
    public RasterMapLayer getBackgroundLayer() {
        return this.background;
    }
    
    /**
     * @return all overlay layers active on this map
     */
    public MapLayer[] getOverlayLayers() {
        return this.overlayLayers.toArray(new MapLayer[0]);
    }

    /**
     * Adds a marker to this map
     * 
     * @param marker a marker to add to this map
     */
    private void addMarker(Marker marker) {
        this.markers.add(marker);
        this.addWidget(marker);
    }

    /**
     * Removes the given marker from this map.
     * Marker should be added via a {@link MarkerController}
     * 
     * @param marker a marker to remove from this map
     */
    public void removeMarker(Marker marker) {
        this.markers.remove(marker);
        this.removeWidget(marker);
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
            String errorText = I18n.format("terramap.mapwidget.error.header") + "\n" + this.reportedErrors.get((int) ((System.currentTimeMillis() / 3000)%this.reportedErrors.size())).message;
            this.errorText.setText(new TextComponentString(errorText));
        }

        this.profiler.endStartSection("update-markers");
        this.updateMarkers(mouseX, mouseY);

        this.profiler.endStartSection("rest-of-screen");

        this.lastUpdateTime = currentTime;
    }

    /**
     * Adds a widget to the screen. Since this is a map before being a screen,
     * {@link #addOverlayLayer(MapLayer) addMapLayer} should be used instead
     * and other types of widget should not be added to the map directly
     * but rather on the parent screen.
     * 
     * @param widget to add
     * @throws IllegalArgumentException if the widget has an incompatible z value
     */
    @Override @Deprecated
    public WidgetContainer addWidget(IWidget widget) {
        if(widget instanceof MapLayer) {
            this.addOverlayLayer((MapLayer)widget);
        } else {
            switch(widget.getZ()) {
                case BACKGROUND_Z:
                    throw new IllegalArgumentException("Z level " + widget.getZ() + " is reserved for background layer");
                case CONTROLLER_Z:
                    throw new IllegalArgumentException("Z level " + widget.getZ() + " is reserved for controller layer");
            }
            super.addWidget(widget);
        }
        return this;
    }

    /**
     * Removes the given object from this widget container.
     * @deprecated widgets should be added to the parent widget container, not to this map.
     * @param widget the widget to remove
     */
    @Override @Deprecated
    public WidgetContainer removeWidget(IWidget widget) {
        this.overlayLayers.remove(widget);
        return super.removeWidget(widget);
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

    private void updateCopyright() {
        ITextComponent component = new TextComponentString("");
        for(IWidget widget: this.widgets) 
            if(widget instanceof ICopyrightHolder){
                if(component.getFormattedText().length() > 0) component.appendText(" | ");
                component.appendSibling(((ICopyrightHolder)widget).getCopyright(SmyLibGui.getGameContext().getLanguage()));
            }
        this.copyright.setText(component);
        this.copyright.setVisibility(component.getFormattedText().length() > 0);
    }

    private void updateMouseGeoPos(float mouseX, float mouseY) {
        this.inputLayer.getLocationAtPositionOnWidget(this.mouseLocation, mouseX, mouseY);
    }

    //TODO Refactoring: move this monstrosity somewhere else
    private void createRightClickMenu() {
        Font font = SmyLibGui.DEFAULT_FONT;
        this.rightClickMenu = new MenuWidget(1500, font);
        this.teleportMenuEntry = this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.teleport"), () ->
            this.teleportPlayerTo(this.mouseLocation)
        );
        MenuEntry centerHere = this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.center"), () ->
            this.controller.moveLocationToCenter(this.mouseLocation, true)
        );
        centerHere.enabled = this.interactive;
        MenuWidget copySubMenu = new MenuWidget(this.rightClickMenu.getZ(), font);
        copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.geo"), () ->
            GuiScreen.setClipboardString("" + this.mouseLocation.latitude() + " " + this.mouseLocation.longitude())
        );
        this.copyBlockMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.block"), ()->{
            try {
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
                String dispX = "" + round(coords[0]);
                String dispY = "" + round(coords[1]);
                GuiScreen.setClipboardString(dispX + " " + dispY);
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; // Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copyblock"));
                this.scheduleBeforeUpdate(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.copyChunkMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.chunk"), ()->{
            try {
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
                String dispX = "" + floorDiv(round(coords[0]), 16);
                String dispY = "" + floorDiv(round(coords[1]), 16);
                GuiScreen.setClipboardString(dispX + " " + dispY);
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; // Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copychunk"));
                this.scheduleBeforeUpdate(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.copyRegionMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.region"), ()->{
            try {
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
                String dispX = "" + floorDiv(round(coords[0]), 512);
                String dispY = "" + floorDiv(round(coords[1]), 512);
                GuiScreen.setClipboardString("r." + dispX + "." + dispY + ".mca");
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; // Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copyregion"));
                this.scheduleBeforeUpdate(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.copy3drMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.3dr"), ()->{
            try {
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
                String dispX = "" + floorDiv(round(coords[0]), 256);
                String dispY = "" + floorDiv(round(coords[1]), 256);
                GuiScreen.setClipboardString(dispX + ".0." + dispY + ".3dr");
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; //Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copy2dregion"));
                this.scheduleBeforeUpdate(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.copy2drMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.2dr"), ()->{
            try {
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLocation.longitude(), this.mouseLocation.latitude());
                String dispX = "" + floorDiv(round(coords[0]), 512);
                String dispY = "" + floorDiv(round(coords[1]), 512);
                GuiScreen.setClipboardString(dispX + "." + dispY + ".2dr");
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; //Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copy2dregion"));
                this.scheduleBeforeUpdate(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy"), copySubMenu);
        this.rightClickMenu.addSeparator();
        MenuWidget openSubMenu = new MenuWidget(this.rightClickMenu.getZ(), font);
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_osm"), () ->
            GeoServices.openInOSMWeb(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude())
        );
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_bte"), () ->
            GeoServices.openInBTEMap(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude())
        );
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_gmaps"), () -> {
            MainPlayerMarker playerMarker = this.getMainPlayerMarker();
            if(playerMarker != null) {
                if(playerMarker.isVisible(MapWidget.this)) {
                    GeoPoint<?> playerLocation = playerMarker.getLocation();
                    GeoServices.openPlaceInGoogleMaps(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude(), playerLocation.longitude(), playerLocation.latitude());
                } else {
                    GeoServices.openInGoogleMaps(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude());
                }
            } else {
                GeoServices.openInGoogleMaps(round((float)this.controller.getZoom()), this.mouseLocation.longitude(), this.mouseLocation.latitude());
            }

        });
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_gearth_web"), () ->
            GeoServices.opentInGoogleEarthWeb(this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude())
        );
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_gearth_pro"), () ->
            GeoServices.openInGoogleEarthPro(this.mouseLocation.longitude(), this.mouseLocation.latitude())
        );
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_bing"), () ->
            GeoServices.openInBingMaps((int) this.controller.getZoom(), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude())
        );
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_wikimapia"), () ->
            GeoServices.openInWikimapia((int) this.controller.getZoom(), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude())
        );
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_yandex"), () ->
            GeoServices.openInYandex((int) this.controller.getZoom(), this.mouseLocation.longitude(), this.mouseLocation.latitude(), this.mouseLocation.longitude(), this.mouseLocation.latitude())
        );
        this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open"), openSubMenu);
        this.rightClickMenu.addSeparator();
        this.setProjectionMenuEntry = this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.set_proj"), ()-> {
            EarthGeneratorSettings stg = TerramapClientContext.getContext().getGeneratorSettings();
            Minecraft.getMinecraft().displayGuiScreen(new PresetEarthGui(null, stg != null ? stg.toString(): PresetEarthGui.DEFAULT_PRESETS.get("default"), s ->  {
                TerramapClientContext.getContext().setGeneratorSettings(EarthGeneratorSettings.parse(s));
                TerramapClientContext.getContext().saveSettings();
            }));
        });

        this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.offset"), () -> new LayerRenderingOffsetPopup(this.background).show());
    }

    private void updateRightClickMenuEntries() {
        boolean hasProjection = TerramapClientContext.getContext().getProjection() != null;
        this.teleportMenuEntry.enabled = true;
        this.copyBlockMenuEntry.enabled = hasProjection;
        this.copyChunkMenuEntry.enabled = hasProjection;
        this.copyRegionMenuEntry.enabled = hasProjection;
        this.copy3drMenuEntry.enabled = hasProjection;
        this.copy2drMenuEntry.enabled = hasProjection;
        this.setProjectionMenuEntry.enabled = (!TerramapClientContext.getContext().isInstalledOnServer() && TerramapClientContext.getContext().isOnEarthWorld());
    }

    protected void teleportPlayerTo(GeoPoint<?> position) {
        String cmdFormat = TerramapClientContext.getContext().getTpCommand();
        String cmd = cmdFormat.replace("{longitude}", "" + position.longitude()).replace("{latitude}", "" + position.latitude());
        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null && (cmd.contains("{x}") || cmd.contains("{z}"))) {
            String s = System.currentTimeMillis() + ""; // Just a random string
            this.reportError(s, I18n.format("terramap.mapwidget.error.tp"));
            this.scheduleBeforeUpdate(() -> this.discardPreviousErrors(s), 5000);
            return;
        }
        if(projection != null) {
            try {
                double[] xz = TerramapClientContext.getContext().getProjection().fromGeo(position.longitude(), position.latitude());
                cmd = cmd.replace("{x}", "" + xz[0]).replace("{z}", "" + xz[1]);
            } catch (OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; // Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.tp"));
                this.scheduleBeforeUpdate(() -> this.discardPreviousErrors(s), 5000);
            }
        }
        CHAT_SENDER_GUI.sendChatMessage(cmd, false);
    }

    /**
     * @return the {@link FeatureVisibilityController} active for this map (the {@link Map} returned is a copy)
     */
    public Map<String, FeatureVisibilityController> getVisibilityControllers() {
        Map<String, FeatureVisibilityController> m = new LinkedHashMap<>(this.markerControllers);
        if(this.directionVisibility != null ) m.put(this.directionVisibility.getSaveName(), this.directionVisibility);
        if(this.nameVisibility != null) m.put(this.nameVisibility.getSaveName(), this.nameVisibility);
        for(MapLayer layer: this.overlayLayers) {
            if(layer instanceof FeatureVisibilityController) m.put(layer.getId(), (FeatureVisibilityController)layer);
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
     * @return the {@link IRasterTiledMap} used by this map's background layer
     */
    public IRasterTiledMap getBackgroundStyle() {
        return this.background.getTiledMap();
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
     */
    public void setTileScaling(double tileScaling) {
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

    /**
     * @return whether this map's background has a rendering offset set.
     */
    public boolean doesBackgroundHaveRenderingOffset() {
        return this.background.hasRenderingOffset();
    }

    InputLayer getInputLayer() {
        return this.inputLayer;
    }

    protected MenuWidget getRightClickMenu() {
        return this.rightClickMenu;
    }

}
