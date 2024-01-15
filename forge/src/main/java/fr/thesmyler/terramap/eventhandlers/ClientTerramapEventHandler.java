package fr.thesmyler.terramap.eventhandlers;

import fr.thesmyler.smylibgui.event.HudScreenInitEvent;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.gui.HudScreenHandler;
import fr.thesmyler.terramap.gui.screens.LayerRenderingOffsetPopup;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.util.geo.GeoServices;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraplusplus.util.CardinalDirection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiChat;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import java.util.Objects;

/**
 * Event handler for the physical client
 *
 */
@SideOnly(Side.CLIENT)
public class ClientTerramapEventHandler {

    @SubscribeEvent
    public void onRenderHUD(RenderGameOverlayEvent.Text event) {
        if(Minecraft.getMinecraft().gameSettings.showDebugInfo) {
            GeographicProjection proj = TerramapClientContext.getContext().getProjection();
            if(proj != null) {
                event.getLeft().add("");
                double x = Minecraft.getMinecraft().player.posX;
                double z = Minecraft.getMinecraft().player.posZ;
                float yaw = Minecraft.getMinecraft().player.rotationYaw;
                try {
                    double[] coords = proj.toGeo(x, z);
                    float azimuth = proj.azimuth(x, z, yaw);
                    String lon = GeoServices.formatGeoCoordForDisplay(coords[0]);
                    String lat = GeoServices.formatGeoCoordForDisplay(coords[1]);
                    String azimuthStr = GeoServices.formatAzimuthForDisplay(azimuth);
                    String cardinal = CardinalDirection.azimuthToFacing(azimuth).realName();
                    event.getLeft().add("Position: " + lat + "° " + lon + "° Looking at: " + azimuthStr + "° (" + cardinal + ")");
                } catch(OutOfProjectionBoundsException e) {
                    event.getLeft().add("Out of projection bounds");
                }
            }
            event.getLeft().add("Terramap world UUID: " + TerramapClientContext.getContext().getWorldUUID());
            event.getLeft().add("Terramap proxy UUID: " + TerramapClientContext.getContext().getProxyUUID());
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        KeyBindings.checkBindings();
    }

    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
        Minecraft.getMinecraft().addScheduledTask(TerramapClientContext::resetContext); // This event is called from the network thread
    }

    @SubscribeEvent
    public void onClientConnected(ClientConnectedToServerEvent event) {
        Minecraft.getMinecraft().addScheduledTask(() -> TerramapClientContext.getContext().reloadState()); // This event is called from the network thread
    }

    @SubscribeEvent
    public void onChangeDimension(PlayerChangedDimensionEvent event) {
        // Not called on client...
        TerramapMod.logger.info(event.player.world.isRemote);
        if(event.player.world.isRemote) {
            TerramapClientContext.getContext().resetWorld();
        }
    }

    @SubscribeEvent
    public void onHudInit(HudScreenInitEvent event) {
        HudScreenHandler.init(event.getHudScreen());
        TerramapClientContext.getContext().setupMaps();
        TerramapClientContext.getContext().tryShowWelcomeToast();
    }

    @SubscribeEvent
    public void onGuiScreenInit(InitGuiEvent event) {
        if(event.getGui() instanceof GuiDownloadTerrain) {
            TerramapClientContext.getContext().resetWorld();
        }
    }
    
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        if(event.getGui() instanceof GuiChat && Minecraft.getMinecraft().currentScreen instanceof LayerRenderingOffsetPopup) {
            /*
             * Take care of propagating offset changes once the popup is closed
             * when the minimap background offset was changed from a popup opened from the chat,
             * by right-clicking the minimap.
             */
            LayerRenderingOffsetPopup popup = (LayerRenderingOffsetPopup) Minecraft.getMinecraft().currentScreen;
            MapLayer layer = popup.getLayer();
            TerramapClientContext.getContext().getSavedState().minimap.layers.stream()
                    .filter(l -> l.z == layer.getZ() && Objects.equals(l.type, layer.getType()) && l.settings.equals(layer.saveSettings()))
                    .forEach(l -> {
                        l.cartesianOffset.set(layer.getRenderingOffset());
                        l.rotationOffset = layer.getRotationOffset();
                    });
            TerramapClientContext.getContext().saveState();
        }
    }

}
