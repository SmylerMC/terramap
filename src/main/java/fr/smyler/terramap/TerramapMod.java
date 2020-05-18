package fr.smyler.terramap;

import org.apache.logging.log4j.Logger;
import fr.smyler.terramap.caching.CacheManager;
import fr.smyler.terramap.proxy.TerramapProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

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
    
    /* Proxy things */
    private static final String CLIENT_PROXY_CLASS = "fr.smyler.terramap.proxy.TerramapClientProxy";
	private static final String SERVER_PROXY_CLASS = "fr.smyler.terramap.proxy.TerramapServerProxy";
    @SidedProxy(clientSide = TerramapMod.CLIENT_PROXY_CLASS, serverSide = TerramapMod.SERVER_PROXY_CLASS)
	public static TerramapProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
    	logger = event.getModLog();
    	TerramapMod.proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	TerramapMod.proxy.init(event);
    }
    
        
}
