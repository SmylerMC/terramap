package fr.thesmyler.terramap;

import org.apache.logging.log4j.Logger;

import fr.thesmyler.terramap.caching.CacheManager;
import fr.thesmyler.terramap.proxy.TerramapProxy;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;

@Mod(modid = TerramapMod.MODID, name = TerramapMod.NAME, acceptableRemoteVersions = "*")
public class TerramapMod {
	
    public static final String MODID = "terramap";
    public static final String NAME = "Terramap";
    private static  String version;
	public static final String AUTHOR_EMAIL = "smyler at mail dot com";
			

    public static Logger logger;
    public static CacheManager cacheManager;
    
    /* Proxy things */
    private static final String CLIENT_PROXY_CLASS = "fr.thesmyler.terramap.proxy.TerramapClientProxy";
	private static final String SERVER_PROXY_CLASS = "fr.thesmyler.terramap.proxy.TerramapServerProxy";
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
    
    @NetworkCheckHandler
    public boolean isRemoteCompatible(String remoteVersion) {
    	logger.info("Checking compatibility with remote version: " + remoteVersion); //TODO Remove debug statement
    	//Version prior to 1.0.0-beta5 do not have hello packet but projection sync packet
    	if(remoteVersion.contains("1.0.0-beta4")) return false;
    	if(remoteVersion.contains("1.0.0-beta3")) return false;
    	if(remoteVersion.contains("1.0.0-beta2")) return false;
    	if(remoteVersion.contains("1.0.0-beta1")) return false;
    	return true; //Vanilla is compatible
    }
    
        
}
