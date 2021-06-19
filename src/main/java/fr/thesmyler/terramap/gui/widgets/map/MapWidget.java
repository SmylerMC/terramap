package fr.thesmyler.terramap.gui.widgets.map;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.util.Util;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget.MenuEntry;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
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
import fr.thesmyler.terramap.gui.widgets.markers.markers.AbstractMovingMarker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.MainPlayerMarker;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.ICopyrightHolder;
import fr.thesmyler.terramap.maps.IRasterTiledMap;
import fr.thesmyler.terramap.util.GeoServices;
import fr.thesmyler.terramap.util.GeoUtil;
import fr.thesmyler.terramap.util.Mat2d;
import fr.thesmyler.terramap.util.Vec2d;
import fr.thesmyler.terramap.util.WebMercatorUtil;
import net.buildtheearth.terraplusplus.control.PresetEarthGui;
import net.buildtheearth.terraplusplus.dep.net.daporkchop.lib.common.util.PValidation;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.profiler.Profiler;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

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
    private boolean trackRotation = false;

    private final ControllerMapLayer controller;
    private RasterMapLayer background;
    private List<MapLayer> overlayLayers = new ArrayList<>();
    private List<Marker> markers = new ArrayList<>();
    private final Map<String, MarkerController<?>> markerControllers = new LinkedHashMap<String, MarkerController<?>>();
    private RightClickMarkerController rcmMarkerController;
    private MainPlayerMarkerController mainPlayerMarkerController;
    private OtherPlayerMarkerController otherPlayerMarkerController;
    private MainPlayerMarker mainPlayerMarker;
    private Marker trackingMarker;
    private String restoreTrackingId;
    private PlayerDirectionsVisibilityController directionVisibility;
    private PlayerNameVisibilityController nameVisibility;

    private double mouseLongitude, mouseLatitude;
    private long lastUpdateTime = Long.MIN_VALUE;

    private float drag = 0.3f;
    private float zoomSnapping = 1f;
    private float zoomResponsiveness = 0.01f;
    private float rotationResponsiveness = 0.005f;
    protected double tileScaling;

    private MenuWidget rightClickMenu;
    private MenuEntry teleportMenuEntry;
    private MenuEntry copyBlockMenuEntry;
    private MenuEntry copyChunkMenuEntry;
    private MenuEntry copyRegionMenuEntry;
    private MenuEntry copy3drMenuEntry;
    private MenuEntry copy2drMenuEntry;
    private MenuEntry setProjectionMenuEntry;

    private TextWidget copyright;
    private ScaleIndicatorWidget scale = new ScaleIndicatorWidget(-1);

    private final Profiler profiler = new Profiler();
    private static final GuiScreen CHAT_SENDER_GUI = new GuiScreen() {}; // The only reason this exists is so we can use it to send chat messages
    static { CHAT_SENDER_GUI.mc = Minecraft.getMinecraft(); }

    private TextWidget errorText;

    private List<ReportedError> reportedErrors = new ArrayList<>();
    private static final int MAX_ERRORS_KEPT = 10;

    private final MapContext context;

    public static final int BACKGROUND_Z = Integer.MIN_VALUE;
    public static final int CONTROLLER_Z = 0;

    public MapWidget(float x, float y, int z, float width, float height, IRasterTiledMap map, MapContext context, double tileScaling) {
        super(x, y, z, width, height);
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

        this.controller = new ControllerMapLayer(this.tileScaling);
        super.addWidget(this.controller);

        this.setBackgroud(new RasterMapLayer(map, this.tileScaling));

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
        
        McChunksLayer chunks = new McChunksLayer(tileScaling);
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
     * @throws InvalidLayerLevelException if the layer's Z level is set to a reserved value, like {@link #BACKGROUND_Z} or {@link #CONTROLLER_Z}
     */
    public MapWidget addOverlayLayer(MapLayer layer) {
        switch(layer.getZ()) {
            case BACKGROUND_Z:
                throw new IllegalArgumentException("Z level " + layer.getZ() + " is reserved for background layer");
            case CONTROLLER_Z:
                throw new IllegalArgumentException("Z level " + layer.getZ() + " is reserved for controller layer");
        }
        this.overlayLayers.add(layer);
        super.addWidget(layer);
        this.updateCopyright();
        return this;
    }

    /**
     * Removes the given overlay layer from this map.
     * 
     * @param layer
     * @return this map, for chaining
     */
    public MapWidget removeOverlayLayer(MapLayer layer) {
        this.overlayLayers.remove(layer);
        this.discardPreviousErrors(layer); // We don't care about errors for this overlay anymore
        super.removeWidget(layer);
        this.updateCopyright();
        return this;
    }

    private MapWidget setBackgroud(RasterMapLayer background) {
        background.z = BACKGROUND_Z;
        this.discardPreviousErrors(this.background); // We don't care about errors for this background anymore
        background.setTileScaling(this.tileScaling);
        super.removeWidget(this.background);
        super.addWidget(background);
        this.background = background;
        this.updateCopyright();
        return this;
    }

    /**
     * Sets this map background layer to a {@link RasterMapLayer} constructed from the given {@link IRasterTiledMap}.
     * 
     * @param map - a raster tiled map
     */
    public void setBackground(IRasterTiledMap map) {
        this.setBackgroud(new RasterMapLayer(map, this.tileScaling));
    }
    
    /**
     * @return this map's background layer
     */
    public RasterMapLayer getBackgroundLayer() {
        return this.background;
    }
    
    public MapLayer[] getOverlayLayers() {
        return this.overlayLayers.toArray(new MapLayer[0]);
    }

    private void addMarker(Marker marker) {
        this.markers.add(marker);
        this.addWidget(marker);
    }

    /**
     * Removes the given marker from this map.
     * Marker should be added via a {@link MarkerController}
     * 
     * @param marker
     * @return this map, for chaining
     */
    public MapWidget removeMarker(Marker marker) {
        this.markers.remove(marker);
        this.removeWidget(marker);
        return this;
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        this.profiler.startSection("draw");
        super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
        this.profiler.endSection();
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {

        this.profiler.startSection("update-movement");
        long ctime = System.currentTimeMillis();
        long dt = ctime - this.lastUpdateTime;

        /* 
         * These things do time dependent integration operations, so if the integration step is irrelevant, skip
         * We also want to do that before calling the super method, so any position change is taken into account when updating markers
         */
        if(dt > 0 && dt < 1000) {
            this.controller.processInertia(dt);
            this.controller.processZoom(dt);
            this.controller.processRotation(dt);
        }
        
        if(this.trackingMarker != null) {
            if(this.widgets.contains(this.trackingMarker) && Double.isFinite(this.trackingMarker.getLongitude()) && Double.isFinite(this.trackingMarker.getLatitude())) {
                this.trackingMarker.onUpdate(mouseX - this.trackingMarker.getX(), mouseY - this.trackingMarker.getY(), this); // Force update so we don't lag behind, this one needs to be updated twice
                if(!Double.isFinite(this.trackingMarker.getLatitude()) || !Double.isFinite(this.trackingMarker.getLongitude())) {
                    this.trackingMarker = null;
                } else {
                    this.setCenterLongitude(this.trackingMarker.getLongitude());
                    this.setCenterLatitude(this.trackingMarker.getLatitude());
                    if(this.trackRotation && this.trackingMarker instanceof AbstractMovingMarker) {
                        float azimuth = ((AbstractMovingMarker)this.trackingMarker).getAzimuth();
                        if(Float.isFinite(azimuth)) this.setRotation(-azimuth);
                    }
                }
            } else {
                this.trackingMarker = null;
            }
        }

        this.profiler.endStartSection("update-all");
        super.onUpdate(mouseX, mouseY, parent);

        this.profiler.endStartSection("update-misc");
        this.copyright.setAnchorX(this.getWidth() - 3).setAnchorY(this.getHeight() - this.copyright.getHeight()).setMaxWidth(this.getWidth());
        this.scale.setX(15).setY(this.copyright.getAnchorY() - 15);
        this.errorText.setAnchorX(this.getWidth() / 2).setAnchorY(0).setMaxWidth(this.getWidth() - 40);
        if(!this.rightClickMenu.isVisible(this)) this.updateMouseGeoPos(mouseX, mouseY);

        this.syncOverlaysWithController();

        this.profiler.endStartSection("update-markers");
        this.updateMarkers(mouseX, mouseY);

        this.profiler.endStartSection("update-errors");
        if(this.reportedErrors.size() > 0) {
            String errorText = I18n.format("terramap.mapwidget.error.header") + "\n" + this.reportedErrors.get((int) ((System.currentTimeMillis() / 3000)%this.reportedErrors.size())).message;
            this.errorText.setText(new TextComponentString(errorText));
        }
        this.profiler.endSection();

        this.lastUpdateTime = ctime;
    }

    /**
     * Handles all inputs for this map
     * 
     * @author SmylerMC
     */
    private class ControllerMapLayer extends MapLayer {

        double zoomLongitude, zoomLatitude;
        double zoomTarget = 0;
        float rotationTarget = 0;
        float speedX, speedY;
        float rotateAroundX = Float.NaN;
        float rotateAroundY = Float.NaN;
        double rotateAroundLongitude, rotateAroundLatitude;
        private float lastUpdateMouseX = Float.NaN;
        private float lastUpdateMouseY = Float.NaN;

        public ControllerMapLayer(double tileScaling) {
            super(tileScaling);
            this.z = CONTROLLER_Z;
        }
        
        @Override
        public String getId() {
            return "controller";
        }

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            // If we are processing rotation input, draw pentagons at the corresponding spot
            if(!Float.isNaN(this.rotateAroundX) && !Float.isNaN(this.rotateAroundY)) {
                int vertexCount = 5;
                float radiusLarge = 5;
                float radiusSmall = 2;
                double[] verticesLarge = new double[vertexCount*2];
                double[] verticesSmall = new double[vertexCount*2];
                Vec2d pos = new Vec2d(0, -1);
                Mat2d rot = Mat2d.forRotation(-Math.PI*2 / vertexCount);
                for(int i = 0; i < vertexCount; i++) {
                    Vec2d absPosLarge = pos.scale(radiusLarge).add(x + this.rotateAroundX, y + this.rotateAroundY);
                    Vec2d absPosSmall = pos.scale(radiusSmall).add(x + this.rotateAroundX, y + this.rotateAroundY);
                    verticesLarge[2*i] = absPosLarge.x;
                    verticesLarge[2*i + 1] = absPosLarge.y;
                    verticesSmall[2*i] = absPosSmall.x;
                    verticesSmall[2*i + 1] = absPosSmall.y;
                    pos = rot.prod(pos);
                }
                RenderUtil.drawPolygon(Color.DARK_OVERLAY, verticesLarge);
                RenderUtil.drawPolygon(Color.DARK_OVERLAY, verticesSmall);
            }
        }

        @Override
        public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
            this.cancelMovement();
            this.cancelRotationInput();
            if(MapWidget.this.isShortcutEnabled()) {
                MapWidget.this.teleportPlayerTo(MapWidget.this.mouseLongitude, MapWidget.this.mouseLatitude);
                if(MapWidget.this.getContext().equals(MapContext.FULLSCREEN)) {
                    Minecraft.getMinecraft().displayGuiScreen(null);
                }
            }
            if(MapWidget.this.enableRightClickMenu && mouseButton == 1 && Math.abs(MapWidget.this.getMouseLatitude()) <= WebMercatorUtil.LIMIT_LATITUDE) {
                parent.showMenu(mouseX, mouseY, MapWidget.this.rightClickMenu);
            }
            if(MapWidget.this.isInteractive() && mouseButton == 2 && Float.isNaN(this.rotateAroundX) && Float.isNaN(this.rotateAroundY)) {
                this.rotateAroundX = mouseX;
                this.rotateAroundY = mouseY;
                double[] lola = this.getScreenGeoPos(mouseX, mouseY);
                this.rotateAroundLongitude = lola[0];
                this.rotateAroundLatitude = lola[1];
                MapWidget.this.trackingMarker = null;
            }
            return false;
        }

        @Override
        public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
            this.cancelMovement();
            this.cancelRotationInput();
            // We don't care about double right clicks
            if(mouseButton != 0) this.onClick(mouseX, mouseY, mouseButton, parent);

            if(MapWidget.this.isInteractive() && mouseButton == 0) {
                double[] lola = this.getScreenGeoPos(mouseX, mouseY);
                if(MapWidget.this.focusedZoom) this.zoom(lola[0], lola[1], 1);
                else this.zoom(1);
            }
            return false;
        }

        @Override
        public boolean onParentClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
            this.cancelRotationInput();
            return super.onParentClick(mouseX, mouseY, mouseButton, parent);
        }

        @Override
        public boolean onParentDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
            this.cancelRotationInput();
            return super.onParentDoubleClick(mouseX, mouseY, mouseButton, parent);
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, @Nullable WidgetContainer parent, long dt) {
            this.cancelRotationInput();
            if(MapWidget.this.isInteractive() && mouseButton == 0) {
                this.moveMap(dX, dY);
                this.speedX = dX / dt;
                this.speedY = dY / dt;
            }
        }

        @Override
        public void onUpdate(float mouseX, float mouseY, @Nullable WidgetContainer parent) {
            // If we are currently taking rotation inputs, rotate the map
            if(MapWidget.this.isInteractive() && !Float.isNaN(this.rotateAroundX) && !Float.isNaN(this.rotateAroundY) && !Float.isNaN(this.lastUpdateMouseX) && !Float.isNaN(this.lastUpdateMouseY)) {
                Vec2d pos = new Vec2d(mouseX - this.rotateAroundX, mouseY - this.rotateAroundY);
                Vec2d previousPos = new Vec2d(this.lastUpdateMouseX - this.rotateAroundX, this.lastUpdateMouseY - this.rotateAroundY);
                if(pos.normSquared() != 0d && previousPos.normSquared() != 0d) {
                    pos = pos.normalize();
                    previousPos = previousPos.normalize();
                    double acosa = pos.dotProd(previousPos);
                    float angle = (float) Math.toDegrees(Math.acos(acosa));
                    if(pos.crossProd(previousPos) > 0) angle *= -1;
                    if(Double.isFinite(angle)) {
                        this.setRotation(this.getRotation() + angle);
                        Vec2d newPos = this.getScreenPos(this.rotateAroundLongitude, this.rotateAroundLatitude);
                        double ndX = newPos.x - this.rotateAroundX;
                        double ndY = newPos.y - this.rotateAroundY;
                        double[] lola = this.getScreenGeoPos(this.getWidth() / 2 + ndX, this.getHeight() / 2 + ndY);
                        this.setCenterLongitude(lola[0]);
                        this.setCenterLatitude(lola[1]);
                    }
                }
            }

            // Use to keep track of the mouse latitude and longitude
            this.lastUpdateMouseX = mouseX;
            this.lastUpdateMouseY = mouseY;
        }

        @Override
        public boolean onMouseWheeled(float mouseX, float mouseY, int amount, @Nullable WidgetContainer parent) {
            if(MapWidget.this.isInteractive()) {
                double z = amount > 0? 1: -1;
                z *= MapWidget.this.zoomSnapping;
                if(MapWidget.this.focusedZoom) {
                    double[] lola = this.getScreenGeoPos(mouseX, mouseY);
                    this.zoom(lola[0], lola[1], z);
                } else {
                    this.zoom(z);
                }
            }
            return false;
        }

        public void zoom(double val) {
            this.zoom(this.getCenterLongitude(), this.getCenterLatitude(), val);
        }

        public void zoom(double longitude, double latitude, double zoom) {
            this.cancelRotationInput();
            this.zoomLongitude = longitude;
            this.zoomLatitude = latitude;
            this.zoomTarget += zoom;
        }

        private void processInertia(long dt) {
            if((this.speedX != 0 || this.speedY != 0) && dt < 1000 && !Mouse.isButtonDown(0)) {
                float dX = this.speedX * dt;
                float dY = this.speedY * dt;
                this.speedX -= MapWidget.this.drag*this.speedX;
                this.speedY -= MapWidget.this.drag*this.speedY;
                if(Math.abs(this.speedX) < 0.01f && Math.abs(this.speedY) < 0.01f) {
                    this.speedX = 0f;
                    this.speedY = 0f;
                }
                if(Math.abs(dX) < 100 && Math.abs(dY) < 100) {
                    this.moveMap(dX, dY);
                }

            }
        }

        private void processZoom(long dt) {

            // Round up the targeted zoom level to the nearest multiple of the snapping value and ensure it is within bounds
            double zoomTarget = Math.round(this.zoomTarget / MapWidget.this.zoomSnapping) * MapWidget.this.zoomSnapping;
            double maxZoom = TerramapConfig.CLIENT.unlockZoom? 25: getMaxZoom();
            zoomTarget = Math.min(zoomTarget, maxZoom);
            zoomTarget = Math.max(MapWidget.this.getMinZoom(), zoomTarget);

            // If we are close enough of the desired zoom level, just finish reaching it
            if(Math.abs(this.getZoom() - zoomTarget) < 0.01d) {
                this.zoomTarget = zoomTarget;
                this.setZoom(zoomTarget);
                return;
            }

            MapWidget.this.rightClickMenu.hide(null);

            // Compute a delta to the new zoom value, exponential decay, and ensure it is within bounds
            double maxDzoom = zoomTarget - this.getZoom();
            double dzoom = MapWidget.this.zoomResponsiveness * maxDzoom * dt;
            dzoom = maxDzoom > 0 ? Math.min(dzoom, maxDzoom) : Math.max(dzoom, maxDzoom);

            // The position that needs to stay static and how far it is from the center of the screen
            Vec2d pos = this.getScreenPos(this.zoomLongitude, this.zoomLatitude);
            double dX = pos.x - this.getWidth()/2;
            double dY = pos.y - this.getHeight()/2;

            /*
             *  Get the scale factor from the previous zoom to the new one
             *  Then do some basic arithmetic to know much the center of the screen should move
             */
            double factor = Math.pow(2, dzoom);
            double ndX = dX * (1 - factor);
            double ndY = dY * (1 - factor);
            this.speedX *= factor;
            this.speedY *= factor;

            super.setZoom(this.getZoom() + dzoom); // That's what we are here for

            // And move so the static point is static
            double[] lola = this.getScreenGeoPos((double)this.getWidth()/2 - ndX, (double)this.getHeight()/2 - ndY);
            this.setCenterLongitude(lola[0]);
            this.setCenterLatitude(lola[1]);
        }

        private void processRotation(long dt) {
            float currentRotation = this.getRotation();

            float actualRotationTarget = this.rotationTarget;
            float d0 = Math.abs(this.rotationTarget - currentRotation);
            float d1 = Math.abs(this.rotationTarget - currentRotation - 360f);
            float d2 = Math.abs(this.rotationTarget - currentRotation + 360f);
            if(d1 < d0) {
                actualRotationTarget -= 360f;
            } else if(d2 < d0) {
                actualRotationTarget += 360f;
            }

            if(Math.abs(currentRotation - actualRotationTarget) < 0.1f) {
                this.setRotation(actualRotationTarget);
                return;
            }

            this.cancelRotationInput();

            float maxDRot = actualRotationTarget - currentRotation;
            float drot = MapWidget.this.rotationResponsiveness * maxDRot * dt;
            drot = maxDRot > 0 ? Math.min(drot, maxDRot) : Math.max(drot, maxDRot);

            super.setRotation(currentRotation + drot);
        }

        public void moveMap(float dX, float dY) {
            MapWidget.this.trackingMarker = null;
            double[] lola = this.getScreenGeoPos(this.getWidth()/2 - dX, this.getHeight()/2 - dY);
            this.setCenterLongitude(lola[0]);
            this.setCenterLatitude(lola[1]);
        }

        @Override
        public String getTooltipText() {
            return isShortcutEnabled() ? I18n.format("terramap.mapwidget.shortcuts.tp"): "";
        }

        @Override
        public long getTooltipDelay() {
            return 0;
        }

        public void cancelMovement() {
            this.speedX = this.speedY = 0f;
        }

        public void cancelRotationInput() {
            this.rotateAroundLatitude = this.rotateAroundLongitude = Double.NaN;
            this.rotateAroundX = this.rotateAroundY = Float.NaN;
        }

        @Override
        public void setZoom(double zoom) {
            super.setZoom(zoom);
            this.zoomTarget = zoom;
        }

        @Override
        public void setRotation(float rotation) {
            super.setRotation(rotation);
            this.rotationTarget = rotation;
        }

        @Override
        public MapLayer copy() {
            return new ControllerMapLayer(this.getTileScaling());
        }

        @Override
        public String name() {
            return "Controller";
        }

        @Override
        public String description() {
            return "Controller";
        }

    }

    /**
     * Adds a widget to the screen. Since this is a map before being a screen,
     * {@link #addOverlayLayer(MapLayer) addMapLayer} should be used instead
     * and other types of widget should not be added to the map directly
     * but rather on the parent screen.
     * 
     * @param widget to add
     * @throws InvalidLayerLevelException if the widget has an incompatible z value
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

    private void syncOverlaysWithController() {
        this.controller.setDimensions(this.getWidth(), this.getHeight());
        for(MapLayer layer: this.overlayLayers) {
            layer.setDimensions(this.getWidth(), this.getHeight());
            layer.setTileScaling(this.tileScaling);
            layer.setCenterLongitude(this.controller.getCenterLongitude());
            layer.setCenterLatitude(this.controller.getCenterLatitude());
            layer.setZoom(this.controller.getZoom());
            layer.setRotation(this.controller.getRotation());
        }
        this.background.setDimensions(this.getWidth(), this.getHeight());
        this.background.setTileScaling(this.tileScaling);
        this.background.setCenterLongitude(this.controller.getCenterLongitude());
        this.background.setCenterLatitude(this.controller.getCenterLatitude());
        this.background.setZoom(this.controller.getZoom());
        this.background.setRotation(this.controller.getRotation());
    }

    private void updateMarkers(float mouseX, float mouseY) {
        // Gather the existing classes
        Map<Class<?>, List<Marker>> markers = new HashMap<Class<?>, List<Marker>>();
        for(MarkerController<?> controller: this.markerControllers.values()) {
            markers.put(controller.getMarkerType(), new ArrayList<Marker>());
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
                        this.track(markerToAdd);
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
                component.appendSibling(((ICopyrightHolder)widget).getCopyright(SmyLibGui.getLanguage()));
            }
        this.copyright.setText(component);
        this.copyright.setVisibility(component.getFormattedText().length() > 0);
    }

    private void updateMouseGeoPos(float mouseX, float mouseY) {
        double lola[] = this.controller.getScreenGeoPos(mouseX, mouseY);
        this.mouseLongitude = lola[0];
        this.mouseLatitude = lola[1];
    }

    private void createRightClickMenu() {
        Font font = SmyLibGui.DEFAULT_FONT;
        this.rightClickMenu = new MenuWidget(1500, font);
        this.teleportMenuEntry = this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.teleport"), () -> {
            this.teleportPlayerTo(this.mouseLongitude, this.mouseLatitude);
        });
        MenuEntry centerHere = this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.center"), () -> {
            this.setCenterPosition(this.mouseLongitude, this.mouseLatitude);
        });
        centerHere.enabled = this.interactive;
        MenuWidget copySubMenu = new MenuWidget(this.rightClickMenu.getZ(), font);
        copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.geo"), () -> {
            GuiScreen.setClipboardString("" + this.mouseLatitude + " " + this.mouseLongitude);
        });
        this.copyBlockMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.block"), ()->{
            try {
                String strToCopy = "Outside projection";
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
                String dispX = "" + Math.round(coords[0]);
                String dispY = "" + Math.round(coords[1]);
                strToCopy = dispX + " " + dispY;
                GuiScreen.setClipboardString(strToCopy);
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; // Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copyblock"));
                this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.copyChunkMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.chunk"), ()->{
            try {
                String strToCopy = "Outside projection";
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
                String dispX = "" + Math.floorDiv(Math.round(coords[0]), 16);
                String dispY = "" + Math.floorDiv(Math.round(coords[1]), 16);
                strToCopy = dispX + " " + dispY;
                GuiScreen.setClipboardString(strToCopy);
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; // Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copychunk"));
                this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.copyRegionMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.region"), ()->{
            try {
                String strToCopy = "Outside projection";
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
                String dispX = "" + Math.floorDiv(Math.round(coords[0]), 512);
                String dispY = "" + Math.floorDiv(Math.round(coords[1]), 512);
                strToCopy = "r." + dispX + "." + dispY + ".mca";
                GuiScreen.setClipboardString(strToCopy);
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; // Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copyregion"));
                this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.copy3drMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.3dr"), ()->{
            try {
                String strToCopy = "Outside projection";
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
                String dispX = "" + Math.floorDiv(Math.round(coords[0]), 256);
                String dispY = "" + Math.floorDiv(Math.round(coords[1]), 256);
                strToCopy = dispX + ".0." + dispY + ".3dr";
                GuiScreen.setClipboardString(strToCopy);
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; //Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copy2dregion"));
                this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.copy2drMenuEntry = copySubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy.2dr"), ()->{
            try {
                String strToCopy = "Outside projection";
                double[] coords = TerramapClientContext.getContext().getProjection().fromGeo(this.mouseLongitude, this.mouseLatitude);
                String dispX = "" + Math.floorDiv(Math.round(coords[0]), 512);
                String dispY = "" + Math.floorDiv(Math.round(coords[1]), 512);
                strToCopy = dispX + "." + dispY + ".2dr";
                GuiScreen.setClipboardString(strToCopy);
            } catch(OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; //Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.copy2dregion"));
                this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
            }
        });
        this.rightClickMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.copy"), copySubMenu);
        this.rightClickMenu.addSeparator();
        MenuWidget openSubMenu = new MenuWidget(this.rightClickMenu.getZ(), font);
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_osm"), () -> {
            GeoServices.openInOSMWeb(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
        });
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_bte"), () -> {
            GeoServices.openInBTEMap(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
        });
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_gmaps"), () -> {
            if(this.getMainPlayerMarker() != null) {
                double markerLon = this.getMainPlayerMarker().getLongitude();
                double markerLat = this.getMainPlayerMarker().getLatitude();
                if(Double.isFinite(markerLon) && Double.isFinite(markerLat)) {
                    GeoServices.openPlaceInGoogleMaps(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude(), markerLon, markerLat);
                } else {
                    GeoServices.openInGoogleMaps(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude());
                }
            } else {
                GeoServices.openInGoogleMaps(Math.round((float)this.getZoom()), this.getMouseLongitude(), this.getMouseLatitude());
            }

        });
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_gearth_web"), () -> {
            GeoServices.opentInGoogleEarthWeb(this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
        });
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_gearth_pro"), () -> {
            GeoServices.openInGoogleEarthPro(this.getMouseLongitude(), this.getMouseLatitude());
        });
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_bing"), () -> {
            GeoServices.openInBingMaps((int) this.getZoom(), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
        });
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_wikimapia"), () -> {
            GeoServices.openInWikimapia((int) this.getZoom(), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
        });
        openSubMenu.addEntry(I18n.format("terramap.mapwidget.rclickmenu.open_yandex"), () -> {
            GeoServices.openInYandex((int) this.getZoom(), this.getMouseLongitude(), this.getMouseLatitude(), this.getMouseLongitude(), this.getMouseLatitude());
        });
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

    private void teleportPlayerTo(double longitude, double latitude) {
        String cmd = TerramapClientContext.getContext().getTpCommand().replace("{longitude}", ""+longitude).replace("{latitude}", ""+latitude);
        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        if(projection == null && (cmd.contains("{x}") || cmd.contains("{z}"))) {
            String s = System.currentTimeMillis() + ""; // Just a random string
            this.reportError(s, I18n.format("terramap.mapwidget.error.tp"));
            this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
            return;
        }
        if(projection != null) {
            try {
                double[] xz = TerramapClientContext.getContext().getProjection().fromGeo(longitude, latitude);
                cmd = cmd.replace("{x}", "" + xz[0]).replace("{z}", "" + xz[1]);
            } catch (OutOfProjectionBoundsException e) {
                String s = System.currentTimeMillis() + ""; // Just a random string
                this.reportError(s, I18n.format("terramap.mapwidget.error.tp"));
                this.scheduleWithDelay(() -> this.discardPreviousErrors(s), 5000);
            }
        }
        CHAT_SENDER_GUI.sendChatMessage(cmd, false);
    }

    private boolean isShortcutEnabled() {
        return this.isInteractive() && Keyboard.isKeyDown(KeyBindings.MAP_SHORTCUT.getKeyCode()) && this.allowsQuickTp;
    }

    /**
     * @return the {@link FeatureVisibilityController} active for this map (the {@link Map} returned is a copy)
     */
    public Map<String, FeatureVisibilityController> getVisibilityControllers() {
        Map<String, FeatureVisibilityController> m = new LinkedHashMap<>();
        m.putAll(this.markerControllers);
        if(this.directionVisibility != null ) m.put(this.directionVisibility.getSaveName(), this.directionVisibility);
        if(this.nameVisibility != null) m.put(this.nameVisibility.getSaveName(), this.nameVisibility);
        for(MapLayer layer: this.overlayLayers) {
            if(layer instanceof FeatureVisibilityController) m.put(layer.getId(), (FeatureVisibilityController)layer);
        }
        return m;
    }

    /**
     * @return the current zoom of this map
     */
    public double getZoom() {
        return this.controller.getZoom();
    }

    /**
     * @return The maximum zoom this map can operate at, based on the current settings
     */
    public double getMaxZoom() {
        return TerramapConfig.CLIENT.unlockZoom? 25: this.background.getMap().getMaxZoom();
    }

    /**
     * @return The minimum zoom this map can operate at, based on the current settings
     */
    public double getMinZoom() {
        return this.background.getMap().getMinZoom();
    }

    /**
     * Sets this map's zoom level.
     * Unless the given zoom level is not a multiple of the current zoom snapping value, this will be done without animation.
     * 
     * @param zoom
     * @return this map, for chaining
     */
    public MapWidget setZoom(double zoom) {
        this.controller.setZoom(Math.max(this.getMinZoom(), Math.min(this.getMaxZoom(), zoom)));
        this.controller.zoomLongitude = this.controller.getCenterLongitude();
        this.controller.zoomLatitude = this.controller.getCenterLatitude();
        return this;
    }

    /**
     * Sets this map zoom, animated.
     * 
     * @param zoom
     */
    public void setZoomWithAnimation(double zoom) {
        this.controller.zoomTarget = zoom;
    }

    /**
     * Zooms of the given amount, with an animation.

     * @param zoom
     * @return this map, for chaining
     */
    public MapWidget zoom(double zoom) {
        this.controller.zoom(zoom);
        return this;
    }

    /**
     * Set the zoom snapping value for this map.
     * When the map zoom is not a multiple of this value, it will adjust itself to change to the closets multiple, with an animation.
     * This also affects the mouse wheel zoom sensibility.
     * This is usually 1d.
     * 
     * @return this map, for chaining
     */
    public float getZoomSnapping() {
        return this.zoomSnapping;
    }

    /**
     * Set the zoom snapping value for this map.
     * When the map zoom is not a multiple of this value, it will adjust itself to change to the closets multiple, with an animation.
     * This also affects the mouse wheel zoom sensibility.
     * This is usually 1d.
     * 
     * @param value - the value to set
     */
    public void setZoomSnapping(float value) {
        this.zoomSnapping = value;
    }

    /**
     * Set the speed at which zooming is animated.
     * 
     * @return this map's zoom responsiveness
     */
    public float getZoomResponsiveness() {
        return this.zoomResponsiveness;
    }

    /** 
     * @param value the speed at which zooming is animated
     */
    public void setZoomResponsiveness(float value) {
        this.zoomResponsiveness = value;
    }
    
    /**
     * @return the zoom level this map is trying to reach once the zooming animation is over
     */
    public double getZoomTarget() {
        return this.controller.zoomTarget;
    }

    /**
     * @return the longitude of the center of this map
     */
    public double getCenterLongitude() {
        return this.controller.getCenterLongitude();
    }

    /**
     * Set's this map center longitude
     * 
     * @param longitude
     * @return this map, for chaining
     */
    public MapWidget setCenterLongitude(double longitude) {
        this.controller.setCenterLongitude(longitude);
        return this;
    }

    /**
     * @return the latitude of the center of this map
     */
    public double getCenterLatitude() {
        return this.controller.getCenterLatitude();
    }

    /**
     * Set's this map center latitude
     * 
     * @param latitude
     * @return this map, for chaining
     */
    public MapWidget setCenterLatitude(double latitude) {
        this.controller.setCenterLatitude(latitude);
        return this;
    }

    /**
     * @return the geographic position at the center of this map, as a double array {longitude, latitude}
     */
    public double[] getCenterPosition() {
        return new double[] {this.getCenterLongitude(), this.getCenterLatitude()};
    }

    /**
     * Sets this map's center position
     * 
     * @param longitude
     * @param latitude
     * 
     * @return this map, for chaining
     */
    public MapWidget setCenterPosition(double longitude, double latitude) {
        return this.setCenterLongitude(longitude).setCenterLatitude(latitude);
    }

    /**
     * Sets this map's center position.
     * 
     * @param position - a double array of the form {longitude, latitude}
     * 
     * @return this map, for chaining
     */
    public MapWidget setCenterPosition(double[] position) {
        this.setCenterPosition(position[0], position[1]);
        return this;
    }

    /**
     * @return this map's current rotation
     */
    public float getRotation() {
        return this.controller.getRotation();
    }

    /**
     * Sets this map current rotation
     * 
     * @param rotation
     */
    public void setRotation(float rotation) {
        this.controller.setRotation(rotation);
    }

    /**
     * Change this map rotation, animated
     * 
     * @param rotation
     */
    public void setRotationWithAnimation(float rotation) {
        this.controller.rotationTarget = GeoUtil.getAzimuthInRange(rotation);
    }

    /**
     * @return the last known mouse longitude
     */
    public double getMouseLongitude() {
        return this.mouseLongitude;
    }

    /**
     * @return the last known mouse latitude
     */
    public double getMouseLatitude() {
        return this.mouseLatitude;
    }

    /**
     * @return the last known mouse position as a double array {longitude, latitude}
     */
    public double[] getMousePosition() {
        return new double[] {this.mouseLongitude, this.mouseLatitude};
    }

    /**
     * Sets this map's size
     * 
     * @param width
     * @param height
     */
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        this.scale.setY(this.getHeight() - 20);
    }

    /**
     * @return whether or not this map accepts input at all
     */
    public boolean isInteractive() {
        return this.interactive;
    }

    /**
     * Set whether or not this map takes input
     * 
     * @param yesNo
     * @return whether or not this map is interactive
     */
    public MapWidget setInteractive(boolean yesNo) {
        this.interactive = yesNo;
        return this;
    }

    /**
     * @return Whether or not this map's right click menu is enabled
     */
    public boolean isRightClickMenuEnabled() {
        return this.enableRightClickMenu;
    }

    /**
     * Sets whether or not this map's right click menu is enabled.
     * This is independent from {@link #setInteractive(boolean)}.
     * 
     * @param yesNo
     * @return this map, for chaining
     */
    public MapWidget setRightClickMenuEnabled(boolean yesNo) {
        this.enableRightClickMenu = yesNo;
        return this;
    }

    /**
     * Enabled this map's right click menu
     * 
     * @return this map, for chaining
     */
    public MapWidget enableRightClickMenu() {
        return this.setRightClickMenuEnabled(true);
    }

    /**
     * Disables this map's right click menu
     * 
     * @return this map, for chaining
     */
    public MapWidget disableRightClickMenu() {
        return this.setRightClickMenuEnabled(false);
    }

    /**
     * @return whether or not this map copyright is visible
     */
    public boolean getCopyrightVisibility() {
        return this.showCopyright;
    }

    /**
     * Sets whether or not this map's copyright is visible
     * 
     * @param yesNo
     * @return this map, for chaining
     */
    public MapWidget setCopyrightVisibility(boolean yesNo) {
        this.showCopyright = yesNo;
        return this;
    }

    /**
     * Moves this map without any animation
     * TODO animated variant
     * 
     * @param dX
     * @param dY
     */
    public void moveMap(float dX, float dY) {
        controller.moveMap(dX, dY);
    }

    /** 
     * @param longitude
     * @param latitude
     * @return the position of a geographic place on this widget's coordinate system
     */
    public double[] getScreenPos(double longitude, double latitude) {
        return this.controller.getScreenPos(longitude, latitude).asArray();
    }

    /**
     * @param x
     * @param y
     * @return the geographic coordinates of a given point in the map's coordinate system as a double array {longitude, latitude}
     */
    public double[] getScreenGeoPos(double x, double y) {
        return this.controller.getScreenGeoPos(x, y);
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
     * @param x
     * @return this map, for chaining
     */
    public MapWidget setScaleX(float x) {
        this.scale.setX(x);
        return this;
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
     * Sets the width of this map scale widget
     * 
     * @param width
     * @return this map, for chaining
     */
    public MapWidget setScaleWidth(float width) {
        this.scale.setWidth(width);
        return this;
    }

    /**
     * @return whether of not this map's scale widget is visible
     */
    public boolean getScaleVisibility() {
        return this.scale.isVisible(this);
    }

    /**
     * Sets the visibility of this map's scale widget
     * 
     * @param yesNo
     * @return this map, for chaining
     */
    public MapWidget setScaleVisibility(boolean yesNo) {
        this.scale.setVisibility(yesNo);
        return this;
    }

    /**
     * @return this map's {@link MapContext}
     */
    public MapContext getContext() {
        return this.context;
    }

    /**
     * @return whether or not this map is tracking a marker
     */
    public boolean isTracking() {
        return this.trackingMarker != null;
    }

    /**
     * @return whether or not this map will rotate with the markers it tracks if they implement {@link AbstractMovingMaker}
     */
    public boolean doesMapTrackRotation() {
        return this.trackRotation;
    }

    /**
     * Sets whether or not this map should rotate with the marker it tracks if they implement {@link AbstractMovingMarker}
     * @param yesNo
     */
    public void setTrackRotation(boolean yesNo) {
        this.trackRotation = yesNo;
    }

    /**
     * @return the marker this map is tracking, or null if this map is not tracking anything
     */
    public Marker getTracking() {
        return this.trackingMarker;
    }

    /**
     * Start tracking the given marker
     * 
     * @param marker
     */
    public void track(Marker marker) {
        this.trackingMarker = marker;
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
        return this.background.getMap();
    }

    /**
     * Tries to set a features visibility, or does nothing if the feature does not exist for this map.
     * 
     * @param controllerId - the id of the {@link FeatureVisibilityController} to set the visibility for
     * @param value - visibility
     * 
     * @return this map, for chaining
     */
    public MapWidget trySetFeatureVisibility(String controllerId, boolean value) {
        FeatureVisibilityController c = this.getVisibilityControllers().get(controllerId);
        if(c != null) c.setVisibility(value);
        return this;
    }

    /**
     * Tries to resume tracking a marker using its string id, does nothing if the marker is not found
     * 
     * @param markerId
     */
    public void restoreTracking(String markerId) {
        this.restoreTrackingId = markerId;
    }

    /**
     * @return whether or not this map shows debug information
     */
    public boolean isDebugMode() {
        return debugMode;
    }

    /**
     * Activates and resets this map's profiler and start showing debug information on this map
     * 
     * @param debugMode
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
     * Sets this map rendering scale factor
     * 
     * @param tileScaling
     */
    public void setTileScaling(double tileScaling) {
        this.tileScaling = tileScaling;
        this.controller.setTileScaling(tileScaling);
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visible;
    }

    /**
     * Sets this map visibility
     * 
     * @param yesNo
     * @return this map, for chaining
     */
    public MapWidget setVisibility(boolean yesNo) {
        this.visible = yesNo;
        return this;
    }

    /**
     * @return this map inertia drag coefficient
     */
    public float getInertia() {
        return this.drag;
    }

    /**
     * Sets this map inertia drag coefficient. Higher means more drag, the map stops faster.
     * 
     * @param inertia
     * 
     * @throws IllegalArgumentException if the inertia is not strictly positive
     */
    public void setInertia(float inertia) {
        PValidation.checkArg(inertia > 0f, "Map inertia needs to be strictly positive");
        this.drag = inertia;
    }

    /**
     * Reports an error that will be shown in this map's error area until discarded with {@link #discardPreviousErrors(Object)}
     * 
     * @param source - the source of the error, which can used as a key to discard errors afterwards
     * @param errorMessage - the error message
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
     * Discard the errors from the given source
     * 
     * @param source
     */
    public void discardPreviousErrors(Object source) {
        List<ReportedError> errsToRm = new ArrayList<>();
        for(ReportedError e: this.reportedErrors) {
            if(e.source.equals(source)) errsToRm.add(e);
        }
        this.reportedErrors.removeAll(errsToRm);
    }

    private class ReportedError {

        private Object source;
        private String message;

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
     * Stops all passive inputs (inputs that does not require the user to actively press a button, e.g. rotation)
     */
    public void stopPassiveInputs() {
        this.controller.cancelRotationInput();
    }

    /**
     * @return whether or not this map zooms to the mouse position (true) or to its center (false).
     */
    public boolean isFocusedZoom() {
        return focusedZoom;
    }

    /**
     * Sets whether or not this map zooms to the mouse position (true) or to its center (false).
     */
    public void setFocusedZoom(boolean focusedZoom) {
        this.focusedZoom = focusedZoom;
    }

    /**
     * @return whether or not user can teleport with the ctrl+click shortcut.
     */
    public boolean allowsQuickTp() {
        return allowsQuickTp;
    }

    /**
     * Sets whether or not user can teleport with the ctrl+click shortcut.
     */
    public void setAllowsQuickTp(boolean allowsQuickTp) {
        this.allowsQuickTp = allowsQuickTp;
    }

    /**
     * @return whether or not this map's background has a rendering offset set.
     */
    public boolean doesBackgroundHaveRenderingOffset() {
        return this.background.getRenderDeltaLatitude() != 0d || this.background.getRenderDeltaLongitude() != 0d;
    }

}
