package fr.thesmyler.terramap.gui;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.smylibgui.widgets.WarningWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.LayerRenderingOffsetPopup;
import fr.thesmyler.terramap.gui.screens.config.HudConfigScreen;
import fr.thesmyler.terramap.gui.widgets.RibbonCompassWidget;
import fr.thesmyler.terramap.gui.widgets.map.MinimapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.OnlineRasterMapLayer;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;

import static net.smyler.smylib.SmyLib.getGameClient;

public abstract class HudScreenHandler {

    private static MinimapWidget map;
    private static RibbonCompassWidget compass;
    private final static WarningWidget offsetWarning = new WarningWidget(0, 0, 50);

    public static void init(WidgetContainer screen) {

        screen.removeAllWidgets();
        screen.cancelAllScheduled();

        if(TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP) && !(Minecraft.getMinecraft().currentScreen instanceof HudConfigScreen)) {
            if (map == null) {
                map = new MinimapWidget(10);
            }
            if (!(Minecraft.getMinecraft().currentScreen instanceof LayerRenderingOffsetPopup)) updateMinimap();
            screen.addWidget(map);
            offsetWarning.setTooltip(getGameClient().getTranslator().format("terramap.hud.minimap.offset_warning"));
            screen.addWidget(offsetWarning);

            float compassX = TerramapConfig.CLIENT.compass.posX * 0.01f * screen.getWidth();
            float compassY = TerramapConfig.CLIENT.compass.posY * 0.01f * screen.getHeight();
            float compassWidth = TerramapConfig.CLIENT.compass.width * 0.01f * screen.getWidth();

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

        map.setPosition(
            TerramapConfig.CLIENT.minimap.posX / 100f * screen.getWidth(),
            TerramapConfig.CLIENT.minimap.posY / 100f * screen.getHeight()
        );
        map.setSize(
            TerramapConfig.CLIENT.minimap.width / 100f * screen.getWidth(),
            TerramapConfig.CLIENT.minimap.height / 100f * screen.getHeight()
        );
        offsetWarning.setPosition(map.getX(), map.getY());

        map.setVisibility(TerramapConfig.CLIENT.minimap.enable && TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP));
        offsetWarning.setVisibility(
                map.isVisible(null)
                    &&
                map.getRasterBackgroundLayer()
                        .map(OnlineRasterMapLayer::hasRenderingOffset)
                        .orElse(false)
        );
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
        if (map != null && compass != null && TerramapClientContext.getContext().allowsMap(MapContext.MINIMAP)) {
            map.setVisibility(!map.isVisible(null));
            compass.setVisibility(map.isVisible(null));
            TerramapConfig.CLIENT.minimap.enable = map.isVisible(null);
            TerramapConfig.CLIENT.compass.enable = compass.isVisible(null);
            TerramapConfig.sync();
        }
    }

}
