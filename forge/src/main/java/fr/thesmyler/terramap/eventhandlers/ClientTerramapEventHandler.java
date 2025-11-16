package fr.thesmyler.terramap.eventhandlers;

import fr.thesmyler.smylibgui.event.HudScreenInitEvent;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.gui.HudScreenHandler;
import fr.thesmyler.terramap.gui.screens.LayerRenderingOffsetPopup;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.input.KeyBindings;
import net.smyler.terramap.geo.GeoPoint;
import net.smyler.terramap.world.ForgeWorldClientside;
import net.smyler.terramap.world.WorldClientside;
import net.smyler.terramap.geo.GeoServices;
import net.buildtheearth.terraplusplus.util.CardinalDirection;
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
import net.smyler.smylib.gui.popups.Popup;
import net.smyler.terramap.geo.OutOfGeoBoundsException;

import java.util.Objects;
import java.util.UUID;

import static java.util.Map.Entry.comparingByKey;
import static java.util.stream.Collectors.joining;
import static net.minecraft.client.Minecraft.getMinecraft;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.terramap.Terramap.getTerramap;
import static net.smyler.terramap.Terramap.getTerramapClient;
import static net.smyler.terramap.geo.GeoServices.formatGeoPointForDisplay;

/**
 * Event handler for the physical client
 *
 */
@SideOnly(Side.CLIENT)
public class ClientTerramapEventHandler {

    @SubscribeEvent
    public void onRenderHUD(final RenderGameOverlayEvent.Text event) {
        if (getMinecraft().gameSettings.showDebugInfo) {
            getTerramapClient().mainPlayer().ifPresent(player -> {
                event.getLeft().add("");
                try {
                    GeoPoint location = player.location();
                    float azimuth = player.azimuth();
                    String azimuthStr = GeoServices.formatAzimuthForDisplay(player.azimuth());
                    String cardinal = CardinalDirection.azimuthToFacing(azimuth).realName();
                    event.getLeft().add("Position: " + formatGeoPointForDisplay(location) + " Looking at: " + azimuthStr + "° (" + cardinal + ")");
                } catch(OutOfGeoBoundsException ignored) {
                    event.getLeft().add("Out of projection bounds");
                }
            });
            event.getLeft().add("Terramap world UUID: " + getTerramapClient().world().flatMap(WorldClientside::uuid).map(UUID::toString).orElse("missing"));
            event.getLeft().add("Terramap proxy UUID: " + TerramapClientContext.getContext().getProxyUUID());
            event.getLeft().add("Terramap world caches: " + TerramapMod.proxy.getClient()
                    .worldCache()
                    .stats()
                    .entrySet()
                    .stream()
                    .sorted(comparingByKey())
                    .map(e -> e.getKey() + ": " + e.getValue())
                    .collect(joining(", ")));
        }
    }

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
        KeyBindings.checkBindings();
    }

    @SubscribeEvent
    public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
        getMinecraft().addScheduledTask(() -> {
            // This event is called from the network thread
            TerramapClientContext.resetContext();
            TerramapMod.proxy.getClient().setWorld(null);
        });
    }

    @SubscribeEvent
    public void onClientConnected(ClientConnectedToServerEvent event) {
        getMinecraft().addScheduledTask(() -> {
            // This event is called from the network thread
            TerramapClientContext.getContext().reloadState();
        });
    }

    @SubscribeEvent
    public void onChangeDimension(PlayerChangedDimensionEvent event) {
        // Not called on client...
        getTerramap().logger().info(event.player.world.isRemote);
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
            ForgeWorldClientside world = TerramapMod.proxy.getClient().worldCache().getTerraWorld(getMinecraft().world);
            TerramapMod.proxy.getClient().setWorld(world);
            TerramapClientContext.getContext().resetWorld();
        }
    }
    
    @SubscribeEvent
    public void onGuiOpen(GuiOpenEvent event) {
        Popup popup = getGameClient().getTopPopup();
        if(event.getGui() instanceof GuiChat && popup instanceof LayerRenderingOffsetPopup) {
            /*
             * Take care of propagating offset changes once the popup is closed
             * when the minimap background offset was changed from a popup opened from the chat,
             * by right-clicking the minimap.
             */
            LayerRenderingOffsetPopup layerPopup = (LayerRenderingOffsetPopup) popup;
            MapLayer layer = layerPopup.getLayer();
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
