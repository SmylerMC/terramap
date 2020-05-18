package fr.smyler.terramap.proxy;

import java.io.IOException;

import fr.smyler.terramap.TerramapConfiguration;
import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.caching.CacheManager;
import fr.smyler.terramap.input.KeyBindings;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class TerramapClientProxy extends TerramapProxy{

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapMod.logger.debug("Terramap client pre-init");
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
	}

	@Override
	public void init(FMLInitializationEvent event) {
		TerramapMod.logger.debug("Terramap client init");
    	KeyBindings.registerBindings();
    	RasterWebTile.registerErrorTexture();
	}

}
