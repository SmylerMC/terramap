package fr.thesmyler.terramap;

import java.io.File;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.thesmyler.terramap.util.json.EarthGeneratorSettingsAdapter;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.smyler.smylib.json.TextJsonAdapter;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.TerramapClient;
import net.smyler.terramap.http.HttpClient;
import net.smyler.terramap.http.TerraplusplusHttpClient;
import org.apache.logging.log4j.Logger;

import fr.thesmyler.terramap.TerramapVersion.InvalidVersionString;
import fr.thesmyler.terramap.TerramapVersion.ReleaseType;
import fr.thesmyler.terramap.eventhandlers.CommonTerramapEventHandler;
import net.smyler.terramap.tilesets.raster.RasterTileSetManager;
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

//TODO Credit TwelveMonkeys in the readme
@Mod(modid=Terramap.MOD_ID, useMetadata=true, dependencies="required-after:terraplusplus@[1.0.569,)")
public class TerramapMod implements Terramap {

    @Deprecated
    private static TerramapVersion version; // Read from the metadata

    private final HttpClient http = new TerraplusplusHttpClient();

    private RasterTileSetManager rasterTileSetManager;

    // These are notable versions
    @Deprecated
    public static final TerramapVersion OLDEST_COMPATIBLE_CLIENT = new TerramapVersion(1, 0, 0, ReleaseType.BETA, 6, 0);
    @Deprecated
    public static final TerramapVersion OLDEST_TERRA121_TERRAMAP_VERSION = new TerramapVersion(1, 0, 0, ReleaseType.BETA, 6, 7);

    private Logger logger;

    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(EarthGeneratorSettings.class, new EarthGeneratorSettingsAdapter())
            .registerTypeHierarchyAdapter(Text.class, new TextJsonAdapter())
            .create();

    private final Gson gsonPretty = new GsonBuilder()
            .registerTypeAdapter(EarthGeneratorSettings.class, new EarthGeneratorSettingsAdapter())
            .registerTypeHierarchyAdapter(Text.class, new TextJsonAdapter())
            .setPrettyPrinting()
            .create();

    /* Proxy things */
    private static final String CLIENT_PROXY_CLASS = "fr.thesmyler.terramap.proxy.TerramapClientProxy";
    private static final String SERVER_PROXY_CLASS = "fr.thesmyler.terramap.proxy.TerramapServerProxy";
    @SidedProxy(clientSide = TerramapMod.CLIENT_PROXY_CLASS, serverSide = TerramapMod.SERVER_PROXY_CLASS)
    public static TerramapProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        this.logger = event.getModLog();
        Terramap.InstanceHolder.setInstance(this);
        String versionStr = event.getModMetadata().version;
        if (System.getProperties().containsKey("terramap.debug")) {
            this.logger.info("Debug flag is set, forcing a development version string.");
            versionStr= "${version}";
        }
        try {
            TerramapMod.version = new TerramapVersion(versionStr);
        } catch(InvalidVersionString e) {
            this.logger.error("Failed to parse Terramap version number from string {}, will be assuming a 1.0.0 release.", versionStr);
            TerramapMod.version = new TerramapVersion(1, 0, 0);
        }
        this.logger.info("Terramap version: {}", this.version());
        TerramapMod.proxy.preInit(event);
        File tileSetsConfigFile = new File(event.getModConfigurationDirectory().getAbsolutePath() + "/terramap_user_styles.json");
        this.rasterTileSetManager = new RasterTileSetManager(tileSetsConfigFile);
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
        MinecraftForge.EVENT_BUS.register(new CommonTerramapEventHandler());
        TerramapMod.proxy.init(event);
        PermissionManager.registerNodes();
        this.rasterTileSetManager().loadFromConfigFile();
    }

    @Deprecated
    public static TerramapVersion getVersion() {
        try {
            return TerramapMod.version != null ? TerramapMod.version: new TerramapVersion("0.0.0");
        } catch (InvalidVersionString e) {
            throw new IllegalStateException("Version 0.0.0 should not be invalid");
        }
    }

    @SuppressWarnings("unused")  // This is called by FML despite IDEs not seeing it
    @NetworkCheckHandler
    public boolean isRemoteCompatible(Map<String, String> remote, Side side) {
        return true; // Anything should be ok, the actual check is done in the server event handler
    }

    @EventHandler
    public void onServerStarts(FMLServerStartingEvent event) {
        proxy.onServerStarting(event);
    }


    @Override
    public String version() {
        return getVersion().toString();
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

    @Override
    public TerramapClient client() {
        return proxy.getClient();
    }

    @Override
    public HttpClient http() {
        return this.http;
    }

    @Override
    public Gson gson() {
        return this.gson;
    }

    @Override
    public Gson gsonPretty() {
        return this.gsonPretty;
    }

    @Override
    public RasterTileSetManager rasterTileSetManager() {
        return this.rasterTileSetManager;
    }

}
