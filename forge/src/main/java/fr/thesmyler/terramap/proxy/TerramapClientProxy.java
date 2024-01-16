package fr.thesmyler.terramap.proxy;

import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.smylibgui.screen.TestScreen;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.command.OpenMapCommand;
import fr.thesmyler.terramap.eventhandlers.ClientTerramapEventHandler;
import fr.thesmyler.terramap.gui.HudScreenHandler;
import fr.thesmyler.terramap.gui.widgets.markers.MarkerControllerManager;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.raster.MapStylesLibrary;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.network.NetHandlerPlayClient;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.client.ClientCommandHandler;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.game.WrappedMinecraft;

import javax.imageio.ImageIO;

public class TerramapClientProxy extends TerramapProxy {

    @Override
    public Side getSide() {
        return Side.CLIENT;
    }

    @Override
    public void preInit(FMLPreInitializationEvent event) {
        TerramapMod.logger.debug("Terramap client pre-init");
        TerramapNetworkManager.registerHandlers(Side.CLIENT);
        if (!ImageIO.getImageReadersBySuffix("webp").hasNext()) {
            TerramapMod.logger.warn("ImageIO does not have WebP support, triggering a plugin scan!");
            ImageIO.scanForPlugins();
            if (ImageIO.getImageReadersBySuffix("webp").hasNext()) {
                TerramapMod.logger.info("Found a WebP ImageIO reader.");
            } else {
                TerramapMod.logger.error("Could not find a WebP ImageIO reader! WebP will not be supported.");
            }
        }
    }

    @Override
    public void init(FMLInitializationEvent event) {
        TerramapMod.logger.debug("Terramap client init");
        GameClient game = new WrappedMinecraft();
        MinecraftForge.EVENT_BUS.register(HudScreen.class);
        MinecraftForge.EVENT_BUS.register(game);
        SmyLib.initializeGameClient(game, TerramapMod.logger);
        if ("true".equals(System.getProperty("terramap.showTestScreen"))) {
            MinecraftForge.EVENT_BUS.register(TestScreen.class);
        }

        MinecraftForge.EVENT_BUS.register(new ClientTerramapEventHandler());
        KeyBindings.registerBindings();
        MarkerControllerManager.registerBuiltInControllers();
        MapStylesLibrary.reload();
        ClientCommandHandler.instance.registerCommand(new OpenMapCommand());
    }

    @Override
    public void onServerStarting(FMLServerStartingEvent event) {
        // Nothing to do here
    }

    @Override
    public GameType getGameMode(EntityPlayer e) {
        if(e instanceof EntityPlayerMP) {
            EntityPlayerMP player = (EntityPlayerMP)e;
            return player.interactionManager.getGameType();
        }
        NetHandlerPlayClient connection = Minecraft.getMinecraft().getConnection();
        if (connection == null) {
            return GameType.NOT_SET;
        }
        if(e instanceof AbstractClientPlayer) {
            NetworkPlayerInfo i = connection.getPlayerInfo(e.getUniqueID());
            if(i != null) return i.getGameType();
        }
        TerramapMod.logger.error("Failed to determine player gamemode.");
        return GameType.NOT_SET;
    }

    @Override
    public void onConfigChanged(OnConfigChangedEvent event) {
        if (event.getModID().equals(TerramapMod.MODID)) {
            if(TerramapMod.proxy.isClient() && HudScreen.getContent() != null) {
                HudScreenHandler.updateMinimap();
            }
        }
    }

}
