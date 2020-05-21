package fr.smyler.terramap.proxy;

import java.io.File;
import java.io.IOException;

import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.caching.CacheManager;
import fr.smyler.terramap.config.TerramapConfiguration;
import fr.smyler.terramap.config.TerramapServerPreferences;
import fr.smyler.terramap.input.KeyBindings;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.network.TerramapPacketHandler;
import io.github.terra121.EarthGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapClientProxy extends TerramapProxy{

	private EarthGeneratorSettings genSettings;
	
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

    	File servPrefs = new File(event.getModConfigurationDirectory().getAbsoluteFile() + TerramapServerPreferences.FILENAME);
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

		if(settings == null) {
			TerramapMod.logger.error("Received a null projection from Server");
		} else {
			TerramapMod.logger.info("Got generation settings from server: " + settings.toString());
			this.genSettings = settings;
		}
		
	}

	@Override
	public EarthGeneratorSettings getCurrentEarthGeneratorSettings(World world) {
		return this.genSettings;
	}

	@Override
	public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		if(event.player.equals(Minecraft.getMinecraft().player)) {
			this.genSettings = null;
			TerramapMod.logger.debug("Removed genSettings");
		}
		
	}

}
