package net.smyler.terramap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.api.ModContainer;
import net.fabricmc.loader.api.Version;
import net.fabricmc.loader.api.metadata.ModMetadata;
import net.fabricmc.loader.impl.util.version.StringVersion;
import net.minecraft.client.Minecraft;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.game.Key;
import net.smyler.smylib.game.WrappedMinecraft;
import net.smyler.smylib.json.TextJsonAdapter;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.gui.screens.TerramapScreen;
import net.smyler.terramap.http.CachingHttpClient;
import net.smyler.terramap.http.HttpClient;
import net.smyler.terramap.http.TerramapHttpClient;
import net.smyler.terramap.tilesets.raster.RasterTileSetManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledThreadPoolExecutor;

import static java.util.concurrent.TimeUnit.MINUTES;
import static net.smyler.smylib.SmyLib.getGameClient;
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
    private CachingHttpClient httpClient;
    private RasterTileSetManager rasterTileSetManager;

    private final ScheduledExecutorService scheduler = new ScheduledThreadPoolExecutor(1, this::createSchedulerThread);

    @Override
    public void onInitialize() {
        FabricLoader fabric = FabricLoader.getInstance();
        this.version = fabric.getModContainer(Terramap.MOD_ID)
                .map(ModContainer::getMetadata)
                .map(ModMetadata::getVersion)
                .orElse(new StringVersion("Unknown"));
        this.logger.info("Initializing Terramap version {}", this.version());
        this.rasterTileSetManager = new RasterTileSetManager(fabric.getConfigDir().resolve("terramap_user_styles.json").toFile());
        Terramap.InstanceHolder.setInstance(this);
        SmyLib.initializeGameClient(new WrappedMinecraft(Minecraft.getInstance()), this.logger);
        GameClient client = getGameClient();

        Path cacheDir = client.gameDirectory().resolve("terramap").resolve("cache");
        this.httpClient = new TerramapHttpClient(this.logger, cacheDir);
        this.scheduler.scheduleAtFixedRate(() -> {
            this.httpClient.cacheCleanup().thenAccept(s -> {
                this.logger.info("Cleaned up HTTP cache, removed {} entries ({}o)", s.entries(), s.size());
            }).exceptionally(e -> {
                this.logger.error("Failed to clean up HTTP cache");
                this.logger.catching(e);
                return null;
            });
        }, 10, 10, MINUTES);

        Terramap.instance().rasterTileSetManager().reload(TerramapConfig.enableDebugMaps);
        ClientTickEvents.END_CLIENT_TICK.register(c -> {
            if (client.keyboard().isKeyPressed(Key.KEY_M)) {
                client.displayScreen(new TerramapScreen(client.getCurrentScreen()));
            }
        });
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

    private Thread createSchedulerThread(Runnable task) {
        Thread thread = new Thread(task);
        thread.setDaemon(true);
        thread.setName("Terramap Scheduler");
        return thread;
    }
}
