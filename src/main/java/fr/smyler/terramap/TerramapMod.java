package fr.smyler.terramap;

import org.apache.logging.log4j.Logger;
import fr.smyler.terramap.caching.CacheManager;
import fr.smyler.terramap.proxy.TerramapProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TerramapMod.MODID, name = TerramapMod.NAME)
public class TerramapMod {
	
    public static final String MODID = "terramap";
    public static final String NAME = "Terramap";
    private static  String version;
	public static final String AUTHOR_EMAIL = "smyler at mail dot com";
			

    public static Logger logger;
    public static CacheManager cacheManager;
    
    /* Proxy things */
    private static final String CLIENT_PROXY_CLASS = "fr.smyler.terramap.proxy.TerramapClientProxy";
	private static final String SERVER_PROXY_CLASS = "fr.smyler.terramap.proxy.TerramapServerProxy";
    @SidedProxy(clientSide = TerramapMod.CLIENT_PROXY_CLASS, serverSide = TerramapMod.SERVER_PROXY_CLASS)
	public static TerramapProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	logger = event.getModLog();
    	TerramapMod.version = event.getModMetadata().version;
    	TerramapMod.proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	TerramapMod.proxy.init(event);
    }
    
    public static String getVersion() {
    	return TerramapMod.version;
    }
    
    public static String getUserAgent() {
    	return "Terramap for the BTE project v" + TerramapMod.getVersion() +
    			" at https://github.com/SmylerMC/terramap by " + AUTHOR_EMAIL;
    }
    
        
}
