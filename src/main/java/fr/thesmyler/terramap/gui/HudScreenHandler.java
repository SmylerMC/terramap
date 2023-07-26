package fr.thesmyler.terramap.gui;

import java.util.*;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.LayerRenderingOffsetPopup;
import fr.thesmyler.terramap.gui.screens.config.HudConfigScreen;
import fr.thesmyler.terramap.gui.widgets.RibbonCompassWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapController;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.McChunksLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AnimalMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MobMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerDirectionsVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerNameVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.maps.raster.IRasterTiledMap;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;

import static java.lang.Math.max;
import static java.lang.Math.min;
import static java.util.Comparator.comparing;
import static java.util.Comparator.naturalOrder;

public abstract class HudScreenHandler {

    private static MapWidget map;
    private static RibbonCompassWidget compass;

    public static void init(WidgetContainer screen) {

        screen.removeAllWidgets();
        screen.cancelAllScheduled();

        if(TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP) && !(Minecraft.getMinecraft().currentScreen instanceof HudConfigScreen)) {
            if(map == null) {
                map = new MapWidget(10, MapContext.MINIMAP, TerramapConfig.CLIENT.minimap.getEffectiveTileScaling());
                map.setCopyrightVisibility(false);
                map.setScaleVisibility(false);
                map.getVisibilityControllers().get(PlayerNameVisibilityController.ID).setVisibility(false);
                map.scheduleBeforeEachUpdate(() -> {
                    if(TerramapClientContext.getContext().getProjection() != null
                            && !(Minecraft.getMinecraft().currentScreen instanceof GuiChat)
                            && !(Minecraft.getMinecraft().currentScreen instanceof LayerRenderingOffsetPopup)) {
                        Marker player = map.getMainPlayerMarker();
                        if (player != null) map.getController().track(player);
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
        map.setTileScaling(TerramapConfig.CLIENT.minimap.getEffectiveTileScaling());
        map.setX(Math.round(TerramapConfig.CLIENT.minimap.posX / 100 * screen.getWidth()));
        map.setY(Math.round(TerramapConfig.CLIENT.minimap.posY / 100 * screen.getHeight()));
        map.setWidth(Math.round(TerramapConfig.CLIENT.minimap.width / 100 * screen.getWidth()));
        map.setHeight(Math.round(TerramapConfig.CLIENT.minimap.height / 100 * screen.getHeight()));

        map.restore(TerramapClientContext.getContext().getSavedState().minimap);

        MapController controller = map.getController();
        map.trySetFeatureVisibility(AnimalMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        map.trySetFeatureVisibility(MobMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        map.trySetFeatureVisibility(OtherPlayerMarkerController.ID, TerramapConfig.CLIENT.minimap.showOtherPlayers);
        map.trySetFeatureVisibility(PlayerDirectionsVisibilityController.ID, TerramapConfig.CLIENT.minimap.playerDirections);
        map.trySetFeatureVisibility(McChunksLayer.ID, TerramapConfig.CLIENT.minimap.chunksRender);
        
        controller.setTracksRotation(TerramapConfig.CLIENT.minimap.playerRotation);
        if(!TerramapConfig.CLIENT.minimap.playerRotation) {
            controller.setRotation(0f, false);
        }

        Optional<IRasterTiledMap> background = map.getLayers().stream()
                .filter(l -> l instanceof RasterMapLayer)
                .min(comparing(MapLayer::getZ))
                .map(l -> (RasterMapLayer)l)
                .map(RasterMapLayer::getTiledMap);
        float zoom;
        if (background.isPresent()) {
            zoom = max(background.get().getMinZoom(), TerramapConfig.CLIENT.minimap.zoomLevel);
            zoom = min(background.get().getMaxZoom(), zoom);
        } else {
            zoom = TerramapConfig.CLIENT.minimap.zoomLevel;
        }
        controller.setZoom(zoom, false);

        map.setVisibility(TerramapConfig.CLIENT.minimap.enable && TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP));
    }

    public static void zoomInMinimap() {
        if(map == null || !TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP)) return;
        map.getController().setZoomStaticLocation(map.getController().getCenterLocation());
        map.getController().zoom(1, true);
        TerramapConfig.CLIENT.minimap.zoomLevel = (float) map.getController().getTargetZoom();
        TerramapConfig.sync();
    }

    public static void zoomOutMinimap() {
        if(map == null || !TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP)) return;
        map.getController().setZoomStaticLocation(map.getController().getCenterLocation());
        map.getController().zoom(-1, true);
        TerramapConfig.CLIENT.minimap.zoomLevel = (float) map.getController().getTargetZoom();
        TerramapConfig.sync();
    }

    /**
     * Toggles the minimap visibility and updates the config accordingly.
     * If the compass is enabled in the config, sync it visibility to the minimap, else ignores it.
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
