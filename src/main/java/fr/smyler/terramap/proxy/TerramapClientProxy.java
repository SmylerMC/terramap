package fr.smyler.terramap.proxy;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.TerramapServer;
import fr.smyler.terramap.TerramapUtils;
import fr.smyler.terramap.caching.CacheManager;
import fr.smyler.terramap.config.TerramapConfiguration;
import fr.smyler.terramap.config.TerramapServerPreferences;
import fr.smyler.terramap.gui.GuiTiledMap;
import fr.smyler.terramap.input.KeyBindings;
import fr.smyler.terramap.maps.TiledMap;
import fr.smyler.terramap.maps.TiledMaps;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.network.TerramapPacketHandler;
import io.github.terra121.EarthGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapClientProxy extends TerramapProxy{

	private static GuiTiledMap tiledMap = null;


	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapMod.logger.debug("Terramap client pre-init");
		TerramapPacketHandler.registerHandlers(Side.CLIENT);
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

		File servPrefs = new File(event.getModConfigurationDirectory().getAbsoluteFile() + "/" + TerramapServerPreferences.FILENAME);
		TerramapServerPreferences.setFile(servPrefs);
		TerramapServerPreferences.load();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		TerramapMod.logger.debug("Terramap client init");
		KeyBindings.registerBindings();
		RasterWebTile.registerErrorTexture();
	}

	@Override
	public void onSyncProjection(EarthGeneratorSettings settings) {

		TerramapServer.setServer(new TerramapServer(true, true, settings));

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
		} if(TerramapUtils.isBaguette()){
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
	public float getDefaultGuiSize() {
		ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
		return 1f/scaledRes.getScaleFactor(); //FIXME Make sure this value is good
	}

}
