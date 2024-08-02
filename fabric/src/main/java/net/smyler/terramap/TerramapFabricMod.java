package net.smyler.terramap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.util.version.StringVersion;
import net.minecraft.client.Minecraft;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.game.WrappedMinecraft;
import net.smyler.smylib.json.TextJsonAdapter;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.http.HttpClient;
import net.smyler.terramap.http.MemoryCache;
import net.smyler.terramap.http.TerramapHttpClient;
import net.smyler.terramap.tilesets.raster.RasterTileSetManager;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;


public class TerramapFabricMod implements ModInitializer, Terramap {

    private Version version;

    private final Logger logger = getLogger("terramap");
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(Text.class, new TextJsonAdapter())
            .create();
    private final Gson gsonPretty = new GsonBuilder()
            .registerTypeAdapter(Text.class, new TextJsonAdapter())
            .setPrettyPrinting()
            .create();
    private final HttpClient httpClient = new TerramapHttpClient(this.logger, new MemoryCache());
    private RasterTileSetManager rasterTileSetManager;

    @Override
    public void onInitialize() {
        FabricLoader fabric = FabricLoader.getInstance();
        this.version = fabric.getModContainer(Terramap.MOD_ID)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .orElse(new StringVersion("Unknown"));
        this.logger.info("Initializing Terramap version {}", this.version());
        this.rasterTileSetManager = new RasterTileSetManager(fabric.getConfigDir().toFile());
        Terramap.InstanceHolder.setInstance(this);
        SmyLib.initializeGameClient(new WrappedMinecraft(Minecraft.getInstance()), this.logger);
    }

    @Override
    public String version() {
        return this.version.toString();
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

    @Override
    public HttpClient http() {
        return this.httpClient;
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
