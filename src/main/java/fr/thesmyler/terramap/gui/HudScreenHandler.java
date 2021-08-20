package fr.thesmyler.terramap.gui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.LayerRenderingOffsetPopup;
import fr.thesmyler.terramap.gui.screens.config.HudConfigScreen;
import fr.thesmyler.terramap.gui.widgets.RibbonCompassWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.McChunksLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AnimalMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MobMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerDirectionsVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerNameVisibilityController;
import fr.thesmyler.terramap.maps.raster.IRasterTiledMap;
import fr.thesmyler.terramap.util.math.Vec2d;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

public abstract class HudScreenHandler {

    private static MapWidget map;
    private static RibbonCompassWidget compass;

    public static void init(WidgetContainer screen) {

        screen.removeAllWidgets();
        screen.cancellAllScheduled();

        if(TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP) && !(Minecraft.getMinecraft().currentScreen instanceof HudConfigScreen)) {
            if(map == null) {
                map = new MapWidget(10, TerramapClientContext.getContext().getMapStyles().values().toArray(new IRasterTiledMap[0])[0], MapContext.MINIMAP, TerramapConfig.CLIENT.minimap.getEffectiveTileScaling());
                map.setCopyrightVisibility(false);
                map.setScaleVisibility(false);
                map.getVisibilityControllers().get(PlayerNameVisibilityController.ID).setVisibility(false);
                map.scheduleBeforeEachUpdate(() -> {
                    if(TerramapClientContext.getContext().getProjection() != null
                            && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)
                            && !(Minecraft.getMinecraft().currentScreen instanceof LayerRenderingOffsetPopup)) {
                        map.track(map.getMainPlayerMarker());
                    }
                });
            }
            if(!(Minecraft.getMinecraft().currentScreen instanceof LayerRenderingOffsetPopup)) updateMinimap();
            screen.addWidget(map);

            int compassX = (int) Math.round(TerramapConfig.CLIENT.compass.posX * 0.01 * screen.getWidth());
            int compassY = (int) Math.round(TerramapConfig.CLIENT.compass.posY * 0.01 * screen.getHeight());
            int compassWidth = (int) Math.round(TerramapConfig.CLIENT.compass.width * 0.01 * screen.getWidth());

            compass = new RibbonCompassWidget(compassX, compassY, 20, compassWidth);
            screen.addWidget(compass);
            screen.scheduleBeforeEachUpdate(() -> {
                GeographicProjection p = TerramapClientContext.getContext().getProjection();
                if(p != null) {
                    double x = Minecraft.getMinecraft().player.posX;
                    double z = Minecraft.getMinecraft().player.posZ;
                    float a = Minecraft.getMinecraft().player.rotationYaw;
                    try {
                        compass.setAzimuth(p.azimuth(x, z, a));
                        compass.setVisibility(TerramapConfig.CLIENT.compass.enable);
                    } catch (OutOfProjectionBoundsException e) {
                        compass.setVisibility(false);
                    }
                }
            });
            compass.setVisibility(TerramapConfig.CLIENT.compass.enable);
            screen.addWidget(compass);
        }
    }

    public static void updateMinimap() {
        WidgetContainer screen = HudScreen.getContent();
        if(map == null) {
            init(screen);
            return;
        }
        map.setX(Math.round(TerramapConfig.CLIENT.minimap.posX / 100 * screen.getWidth()));
        map.setY(Math.round(TerramapConfig.CLIENT.minimap.posY / 100 * screen.getHeight()));
        map.setWidth(Math.round(TerramapConfig.CLIENT.minimap.width / 100 * screen.getWidth()));
        map.setHeight(Math.round(TerramapConfig.CLIENT.minimap.height / 100 * screen.getHeight()));
        map.trySetFeatureVisibility(AnimalMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        map.trySetFeatureVisibility(MobMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        map.trySetFeatureVisibility(OtherPlayerMarkerController.ID, TerramapConfig.CLIENT.minimap.showOtherPlayers);
        map.trySetFeatureVisibility(PlayerDirectionsVisibilityController.ID, TerramapConfig.CLIENT.minimap.playerDirections);
        map.trySetFeatureVisibility(McChunksLayer.ID, TerramapConfig.CLIENT.minimap.chunksRender);
        map.setTrackRotation(TerramapConfig.CLIENT.minimap.playerRotation);
        if(!TerramapConfig.CLIENT.minimap.playerRotation) map.setRotation(0f);
        Map<String, IRasterTiledMap> styles = TerramapClientContext.getContext().getMapStyles();
        IRasterTiledMap bg = styles.get(TerramapConfig.CLIENT.minimap.style);
        if(bg == null || ! bg.isAllowedOnMinimap()) {
            ArrayList<IRasterTiledMap> maps = new ArrayList<>(styles.values());
            maps.sort(Collections.reverseOrder());
            bg = maps.get(0);
        }
        map.setBackground(bg);
        
        for(MapLayer layer: map.getOverlayLayers()) {
            Vec2d offset = TerramapClientContext.getContext().getMinimapRenderOffset(layer.getId());
            layer.setRenderingOffset(offset);
        }
        
        RasterMapLayer backLayer = map.getBackgroundLayer();
        Vec2d offset = TerramapClientContext.getContext().getMinimapRenderOffset(backLayer.getId());
        backLayer.setRenderingOffset(offset);
        
        float zoomLevel = Math.max(bg.getMinZoom(), TerramapConfig.CLIENT.minimap.zoomLevel);
        zoomLevel = Math.min(bg.getMaxZoom(), zoomLevel);
        map.setZoom(zoomLevel);
        map.setZoom(TerramapConfig.CLIENT.minimap.zoomLevel);

        map.setTileScaling(TerramapConfig.CLIENT.minimap.getEffectiveTileScaling());
        map.setVisibility(TerramapConfig.CLIENT.minimap.enable && TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP));
    }

    public static void zoomInMinimap() {
        if(map == null || !TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP)) return;
        map.zoom(1);
        TerramapConfig.CLIENT.minimap.zoomLevel = (float) map.getZoomTarget();
        TerramapConfig.sync();
    }

    public static void zoomOutMinimap() {
        if(map == null || !TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP)) return;
        map.zoom(-1);
        TerramapConfig.CLIENT.minimap.zoomLevel = (float) map.getZoomTarget();
        TerramapConfig.sync();
    }

    /**
     * Toggles the minimap visibility and updates the config accordingly.
     * If the compass is enabled in the config, sync it's visibility to the minimap, else ignores it.
     */
    public static void toggleWidgets() {
        if(map != null && compass != null && TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP)) {
            map.setVisibility(!map.isVisible(null));
            compass.setVisibility(map.isVisible(null));
            TerramapConfig.CLIENT.minimap.enable = map.isVisible(null);
            TerramapConfig.CLIENT.compass.enable = compass.isVisible(null);
            TerramapConfig.sync();
        }
    }
}
