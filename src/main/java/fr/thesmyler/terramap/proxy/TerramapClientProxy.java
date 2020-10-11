package fr.thesmyler.terramap.proxy;

import java.io.File;
import java.io.IOException;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.caching.CacheManager;
import fr.thesmyler.terramap.config.TerramapClientPreferences;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.eventhandlers.ClientTerramapEventHandler;
import fr.thesmyler.terramap.gui.widgets.markers.MarkerControllerManager;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.MapStyleRegistry;
import fr.thesmyler.terramap.maps.WebTile;
import fr.thesmyler.terramap.network.S2CTerramapHelloPacket;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapClientProxy extends TerramapProxy {

	@Override
	public Side getSide() {
		return Side.CLIENT;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapMod.logger.debug("Terramap client pre-init");
		TerramapNetworkManager.registerHandlers(Side.CLIENT);
		try {
			TerramapMod.cacheManager = new CacheManager(TerramapConfig.ClientAdvanced.cachingDir);
			TerramapMod.cacheManager.createDirectory();
		} catch (IOException e) {
			TerramapMod.logger.catching(e);
			TerramapMod.logger.error("Caching directory doesn't seem to be valid, we will use a temporary one.");
			TerramapMod.logger.error("Make sure your config is correct!");
			TerramapMod.cacheManager = new CacheManager();

		}
		TerramapMod.cacheManager.startWorker();

		File clientPrefs = new File(event.getModConfigurationDirectory().getAbsoluteFile() + "/" + TerramapClientPreferences.FILENAME);
		TerramapClientPreferences.setFile(clientPrefs);
		TerramapClientPreferences.load();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		TerramapMod.logger.debug("Terramap client init");
		SmyLibGui.init(TerramapMod.logger, false);
		MinecraftForge.EVENT_BUS.register(new ClientTerramapEventHandler());
		KeyBindings.registerBindings();
		WebTile.registerErrorTexture();
		MarkerControllerManager.registerBuiltInControllers();
		MapStyleRegistry.loadBuiltIns();
		MapStyleRegistry.loadFromOnline(TerramapMod.STYLE_UPDATE_HOSTNAME);
	}

	@Override
	public void onServerHello(S2CTerramapHelloPacket pkt) {
		TerramapMod.logger.info("Got server hello, remote version is " + pkt.serverVersion);
		TerramapMod.logger.debug(
				"Server version: " + pkt.serverVersion + "\t" +
				"Server worldSettings: " + pkt.worldSettings.toString() + "\t" +
				"Server UUID: " + pkt.worldUUID + "\t" +
				"Sync players: " + pkt.syncPlayers + "\t" +
				"Sync spectators: " + pkt.syncSpectators + "\t" +
				"Enable player radar: " + pkt.enablePlayerRadar + "\t" +
				"Enable animal radar: " + pkt.enableAnimalRadar + "\t" +
				"Enable mob radar: " + pkt.enableMobRadar + "\t" +
				"Enable deco radar: " + pkt.enableDecoRadar + "\t"
			);
		TerramapRemote srv = TerramapRemote.getRemote();
		srv.setServerVersion(pkt.serverVersion);
		srv.setGeneratorSettings(pkt.worldSettings);
		if(pkt.worldUUID.getLeastSignificantBits() != 0 && pkt.worldUUID.getMostSignificantBits() != 0) {
			srv.guessRemoteIdentifier();
			srv.setRemoteIdentifier(srv.getRemoteIdentifier() + pkt.worldUUID.toString());
		}
		srv.setPlayersSynchronizedByServer(pkt.syncPlayers);
		srv.setSpectatorsSynchronizedByServer(pkt.syncSpectators);
		//TODO Take advantage of the radar enable fields
	}

	@Override
	public double getGuiScaleForConfig() {
		double[] acceptableFactors = {0.5d, 1.0d, 2.0d, 4.0d, 8.0d};
		double bestFactor = acceptableFactors[0];
		ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
		double computedFactor = scaledRes.getScaleFactor();
		for(double factor: acceptableFactors)
			if(Math.abs(computedFactor - factor) < Math.abs(bestFactor - computedFactor)) bestFactor = factor;
		return bestFactor;
	}

	@Override
	public void onServerStarting(FMLServerStartingEvent event) {
		//Nothing to do here
	}

}
