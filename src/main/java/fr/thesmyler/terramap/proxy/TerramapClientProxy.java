package fr.thesmyler.terramap.proxy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.TerramapUtils;
import fr.thesmyler.terramap.caching.CacheManager;
import fr.thesmyler.terramap.config.TerramapConfiguration;
import fr.thesmyler.terramap.config.TerramapClientPreferences;
import fr.thesmyler.terramap.gui.GuiTiledMap;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.TiledMap;
import fr.thesmyler.terramap.maps.TiledMaps;
import fr.thesmyler.terramap.maps.tiles.RasterWebTile;
import fr.thesmyler.terramap.network.S2CTerramapHelloPacket;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapClientProxy extends TerramapProxy {

	private static GuiTiledMap tiledMap = null;


	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapMod.logger.debug("Terramap client pre-init");
		TerramapNetworkManager.registerHandlers(Side.CLIENT);
		try {
			TerramapMod.cacheManager = new CacheManager(TerramapConfiguration.cachingDir);
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
		KeyBindings.registerBindings();
		RasterWebTile.registerErrorTexture();
	}

	@Override
	public void onServerHello(S2CTerramapHelloPacket pkt) {
		TerramapMod.logger.info("Got server hello, remote version is " + pkt.serverVersion);
		TerramapMod.logger.debug("sync players: " + pkt.syncPlayers + " sync spec: " + pkt.syncSpectators + " hasFe: " + pkt.hasFe);
		TerramapServer.setServer(new TerramapServer(pkt.serverVersion, pkt.syncPlayers, pkt.syncSpectators, pkt.hasFe, pkt.settings));
	}

	@Override
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
	}

	@Override
	public void onPlayerLoggedIn(PlayerLoggedInEvent event) {
	}

	public static GuiTiledMap getTiledMap() {
		if(TerramapClientProxy.tiledMap == null) TerramapClientProxy.setTiledMap();
		return TerramapClientProxy.tiledMap;
	}

	private static void setTiledMap() {
		List<TiledMap<?>> maps = new ArrayList<TiledMap<?>>();
		if(TerramapUtils.isPirate()) {
			maps.add(TiledMaps.WATERCOLOR);
		}
		if(TerramapUtils.isBaguette()){
			maps.add(TiledMaps.OSM_FRANCE);
		}
		maps.add(TiledMaps.OSM);
		maps.add(TiledMaps.OSM_HUMANITARIAN);
		maps.add(TiledMaps.TERRAIN);
		TerramapClientProxy.tiledMap = new GuiTiledMap(maps.toArray(new TiledMap[maps.size()]));
	}

	public static void resetTiledMap() {
		TerramapClientProxy.tiledMap = null;
	}

	@Override
	public double getDefaultGuiSize() {
		double[] acceptableFactors = {2.0, 1.0, 0.5, 0.25, 0.125};
		double bestFactor = acceptableFactors[0];
		ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
		double computedFactor = 1f/scaledRes.getScaleFactor();
		for(double factor: acceptableFactors)
			if(Math.abs(computedFactor - factor) < Math.abs(bestFactor - computedFactor)) bestFactor = factor;
		return bestFactor;
	}

}
