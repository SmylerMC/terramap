package fr.smyler.terramap;

import java.io.IOException;

import org.apache.logging.log4j.Logger;

import fr.smyler.terramap.caching.CacheManager;
import fr.smyler.terramap.input.KeyBindings;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid = TerramapMod.MODID, name = TerramapMod.NAME, version = TerramapMod.VERSION)
public class TerramapMod {
	
    public static final String MODID = "terramap";
    public static final String NAME = "Terramap";
    public static final String VERSION = "0.0.0";
	public static final String AUTHOR_EMAIL = "smyler at mail dot com";
	public static final String HTTP_USER_AGENT =
			"Terramap for the BTE project v" + VERSION +
			" at https://github.com/SmylerMC/terramap by " + AUTHOR_EMAIL;

    public static Logger logger;
    public static CacheManager cacheManager;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	logger = event.getModLog();
    	TerramapMod.logger.debug("Terramap pre-init...");
    	if(event.getSide() == Side.SERVER)
        	logger.error("Terramap is a Client side only mod and we are loading on a dedicated server, you may want to remove Terramap to avoid any issue");
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

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	TerramapMod.logger.debug("Terramap init...");
    	KeyBindings.registerBindings();
    	RasterWebTile.registerErrorTexture();
    }
    
        
}
