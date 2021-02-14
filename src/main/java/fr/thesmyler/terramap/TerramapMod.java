package fr.thesmyler.terramap;

import java.io.File;
import java.util.Map;

import org.apache.logging.log4j.Logger;

import fr.thesmyler.terramap.TerramapVersion.InvalidVersionString;
import fr.thesmyler.terramap.TerramapVersion.ReleaseType;
import fr.thesmyler.terramap.eventhandlers.CommonTerramapEventHandler;
import fr.thesmyler.terramap.maps.MapStylesLibrary;
import fr.thesmyler.terramap.permissions.PermissionManager;
import fr.thesmyler.terramap.proxy.TerramapProxy;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.network.NetworkCheckHandler;
import net.minecraftforge.fml.relauncher.Side;

@Mod(modid=TerramapMod.MODID, useMetadata=true, dependencies="required-after:terraplusplus@[0.1.519,)")
public class TerramapMod {
	
    public static final String MODID = "terramap";
	public static final String AUTHOR_EMAIL = "smyler at mail dot com";
	public static final String STYLE_UPDATE_HOSTNAME = "styles.terramap.thesmyler.fr";
	private static TerramapVersion version; // Read from the metadata
	public static final TerramapVersion OLDEST_COMPATIBLE_CLIENT = new TerramapVersion(1, 0, 0, ReleaseType.BETA, 6, 0);
	public static final TerramapVersion OLDEST_COMPATIBLE_SERVER = new TerramapVersion(1, 0, 0, ReleaseType.BETA, 6, 0);

    public static Logger logger;
    
    /* Proxy things */
    private static final String CLIENT_PROXY_CLASS = "fr.thesmyler.terramap.proxy.TerramapClientProxy";
	private static final String SERVER_PROXY_CLASS = "fr.thesmyler.terramap.proxy.TerramapServerProxy";
    @SidedProxy(clientSide = TerramapMod.CLIENT_PROXY_CLASS, serverSide = TerramapMod.SERVER_PROXY_CLASS)
	public static TerramapProxy proxy;
    
    @EventHandler
    public void preInit(FMLPreInitializationEvent event) throws InvalidVersionString {
    	logger = event.getModLog();
    	TerramapMod.version = new TerramapVersion(event.getModMetadata().version);
    	TerramapMod.logger.info("Terramap version: " + getVersion());
    	TerramapMod.proxy.preInit(event);
    	File mapStyleFile = new File(event.getModConfigurationDirectory().getAbsolutePath() + "/terramap_user_styles.json");
    	MapStylesLibrary.setConfigMapFile(mapStyleFile);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	MinecraftForge.EVENT_BUS.register(new CommonTerramapEventHandler());
    	TerramapMod.proxy.init(event);
    	PermissionManager.registerNodes();
    	MapStylesLibrary.loadFromConfigFile();
    }
    
    public static TerramapVersion getVersion() {
    	return TerramapMod.version;
    }
    
    public static String getUserAgent() {
    	return "Terramap for the BTE project v" + TerramapMod.getVersion() +
    			" at https://github.com/SmylerMC/terramap by " + AUTHOR_EMAIL;
    }
    
    @NetworkCheckHandler
    public boolean isRemoteCompatible(Map<String, String> remote, Side side) {
    	return true; //Anything should be ok, the actual check is done in the server event handler
    }
    
    @EventHandler
    public void onServerStarts(FMLServerStartingEvent event) {
    	proxy.onServerStarting(event);
    }
    
        
}
