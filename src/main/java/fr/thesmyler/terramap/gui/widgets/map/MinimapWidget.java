package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.LayerRenderingOffsetPopup;
import fr.thesmyler.terramap.gui.widgets.map.layer.McChunksLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.*;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.maps.SavedMapState;
import fr.thesmyler.terramap.maps.raster.IRasterTiledMap;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiScreen;

import java.util.Optional;

import static fr.thesmyler.terramap.util.math.Math.clamp;

public class MinimapWidget extends MapWidget {

    private final MenuWidget.MenuEntry setBackgroundOffsetMenuEntry;

    public MinimapWidget(int z) {
        super(z, MapContext.MINIMAP, TerramapConfig.CLIENT.getEffectiveTileScaling());

        this.setCopyrightVisibility(false);
        this.setScaleVisibility(false);
        this.getVisibilityControllers().get(PlayerNameVisibilityController.ID).setVisibility(false);

        this.setBackgroundOffsetMenuEntry = this.getRightClickMenu().addEntry(
                SmyLibGui.getTranslator().format("terramap.mapwidget.rclickmenu.offset"),
                () -> this.getRasterBackgroundLayer().ifPresent(
                        l -> new LayerRenderingOffsetPopup(l).show()
                    )
        );
    }

    @Override
    public void init() {
        super.init();
        this.setTileScaling(TerramapConfig.CLIENT.getEffectiveTileScaling());
        if (Minecraft.getMinecraft().currentScreen != null) {
            this.saveState();
        } else {
            this.loadState();
        }
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        this.forceTracking();
        this.setBackgroundOffsetMenuEntry.enabled = this.getBackgroundLayer().isPresent();
        super.onUpdate(mouseX, mouseY, parent);
    }

    private void saveState() {
        TerramapClientContext.getContext().getSavedState().minimap = this.save();
        TerramapClientContext.getContext().saveState();
    }

    private void loadState() {
        SavedMapState state = TerramapClientContext.getContext().getSavedState().minimap;

        state.visibilitySettings.put(AnimalMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        state.visibilitySettings.put(MobMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        state.visibilitySettings.put(OtherPlayerMarkerController.ID, TerramapConfig.CLIENT.minimap.showOtherPlayers);
        state.visibilitySettings.put(PlayerDirectionsVisibilityController.ID, TerramapConfig.CLIENT.minimap.playerDirections);
        state.visibilitySettings.put(McChunksLayer.ID, TerramapConfig.CLIENT.minimap.chunksRender);
        Marker player = this.getMainPlayerMarker();
        if (player != null) {
            state.trackedMarker = player.getIdentifier();
        }

        MapController controller = this.getController();

        controller.setTracksRotation(TerramapConfig.CLIENT.minimap.playerRotation);
        if (!TerramapConfig.CLIENT.minimap.playerRotation) {
            controller.setRotation(0f, false);
        }

        this.restore(state);

        Optional<RasterMapLayer> background = this.getRasterBackgroundLayer();
        float zoom = TerramapConfig.CLIENT.minimap.zoomLevel;
        if (background.isPresent()) {
            IRasterTiledMap style = TerramapClientContext.getContext().getMapStyles().get(TerramapConfig.CLIENT.minimap.style);
            RasterMapLayer layer = background.get();
            if (style != null) {
                layer.setTiledMap(style);
            }
            style = layer.getTiledMap();
            zoom = clamp(zoom, style.getMinZoom(), style.getMaxZoom());
        }
        controller.setZoom(zoom, false);

    }

    private void forceTracking() {
        GeographicProjection projection = TerramapClientContext.getContext().getProjection();
        GuiScreen screen = Minecraft.getMinecraft().currentScreen;
        if (projection == null) {
            return; // We can't track anyway if we can't calculate where the player is
        }
        if (screen instanceof GuiChat) {
            return; // We want to user to be free to interact with the map when the chat is open
        }
        if (screen instanceof LayerRenderingOffsetPopup) {
            /*
             * The layer rendering offset screen holds a reference to the layer it is working with and uses it for calculations,
             * we don't want to screw that up by moving the center or rotating.
             */
            return;
        }

        Marker player = this.getMainPlayerMarker();
        if (player != null) {
            this.getController().track(player);
        }
    }

}
