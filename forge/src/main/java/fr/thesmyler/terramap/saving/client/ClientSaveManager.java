package fr.thesmyler.terramap.saving.client;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.smyler.smylib.game.MinecraftServerInfo;
import fr.thesmyler.terramap.util.json.EarthGeneratorSettingsAdapter;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.minecraft.client.multiplayer.ServerData;
import net.smyler.terramap.Terramap;

import java.io.*;
import java.nio.file.Path;
import java.util.UUID;

import static java.nio.file.Files.*;

/**
 * Handles all data saved by the Terramap client.
 *
 * @author Smyler
 */
public class ClientSaveManager {

    private Path saveDirectory;
    private Path worldDirectory;
    private Path serverDirectory;
    private Path proxyDirectory;

    private static final String EXTENSION = ".json";
    private static final String DEFAULT_SAVE_PATH = "/assets/terramap/defaultstate.json";
    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(EarthGeneratorSettings.class, new EarthGeneratorSettingsAdapter())
            .setPrettyPrinting()
            .create();

    /**
     * Constructs a new {@link ClientSaveManager} given a save directory.
     *
     * @param saveDirectory a path to a directory where data will be saved
     */
    public ClientSaveManager(Path saveDirectory) {
        this.saveDirectory = saveDirectory.toAbsolutePath();
        this.worldDirectory = this.saveDirectory.resolve("worlds");
        this.serverDirectory = this.saveDirectory.resolve("servers");
        this.proxyDirectory = this.saveDirectory.resolve("proxies");
    }

    /**
     * Loads a {@link SavedClientState} associated with a specific world, given the world UUID.
     * The world may be on a remote server, the only thing that matters is that we are uniquely identifying it using its ID.
     *
     * @param worldUUID the UUID of the world to retrieve the save for
     *
     * @return {@link SavedClientState} associated with the world
     */
    public SavedClientState loadWorldState(UUID worldUUID) {
        return this.loadFromPath(this.worldDirectory.resolve(worldUUID + EXTENSION));
    }

    /**
     * Loads a {@link SavedClientState} associated with a specific server, given the server's {@link ServerData}.
     * This method will usually be used when a remote world cannot be uniquely identified and we can only identify the server.
     *
     * @param serverInfo the information of the server to retrieve the save for
     *
     * @return {@link SavedClientState} associated with the server
     */
    public SavedClientState loadServerState(MinecraftServerInfo serverInfo) {
        return this.loadFromPath(this.worldDirectory.resolve(serverInfo.host + EXTENSION));
    }

    /**
     * Loads a {@link SavedClientState} associated with a specific Sledgehammer proxy, given the proxy's {@link UUID}.
     *
     * @param proxyUUID the information of the proxy to retrieve the save for
     *
     * @return {@link SavedClientState} associated with the proxy
     */
    public SavedClientState loadProxyState(UUID proxyUUID) {
        return this.loadFromPath(this.worldDirectory.resolve(proxyUUID + EXTENSION));
    }

    /**
     * Gets the default {@link SavedClientState}. The default settings are stored in an internal resource.
     *
     * @return a {@link SavedClientState} with the default settings
     */
    public SavedClientState getDefaultState() {
        InputStream stream = this.getClass().getResourceAsStream(DEFAULT_SAVE_PATH);
        if (stream == null) {
            Terramap.instance().logger().error("Missing internal resource: default client state");
            return new SavedClientState();
        }
        try (InputStreamReader reader = new InputStreamReader(stream)) {
            return GSON.fromJson(reader, SavedClientState.class);
        } catch (IOException e) {
            Terramap.instance().logger().error("Failed to read internal default map state");
            Terramap.instance().logger().catching(e);
            return new SavedClientState();
        }
    }

    /**
     * Saves a {@link SavedClientState} associated with a specific world, given the world UUID.
     * The world may be on a remote server, the only thing that matters is that we are uniquely identifying it using its ID.
     *
     * @param worldUUID the UUID of the world to retrieve the save for
     * @param state     the state to save
     *
     */
    public void saveWorldState(UUID worldUUID, SavedClientState state) {
        this.saveStateToPath(this.worldDirectory.resolve(worldUUID + EXTENSION), state);
    }

    /**
     * Saves a {@link SavedClientState} associated with a specific server, given the server's {@link ServerData}.
     * This method will usually be used when a remote world cannot be uniquely identified and we can only identify the server.
     *
     * @param serverData    the information of the server to retrieve the save for
     * @param state         the state to save
     */
    public void saveServerState(ServerData serverData, SavedClientState state) {
        this.saveStateToPath(this.serverDirectory.resolve(serverData.serverIP + EXTENSION), state);
    }

    /**
     * Saves a {@link SavedClientState} associated with a specific Sledgehammer proxy, given the proxy's {@link UUID}.
     *
     * @param proxyUUID the information of the proxy to retrieve the save for
     * @param state     the state to save
     */
    public void saveProxyState(UUID proxyUUID, SavedClientState state) {
        this.saveStateToPath(this.serverDirectory.resolve(proxyUUID + EXTENSION), state);
    }

    /**
     * Creates all subdirectories necessary to save data.
     *
     * @throws IOException  if creating any directory fails
     */
    public void createDirectoryIfNecessary() throws IOException {
        this.saveDirectory = this.prepareDirectory(this.saveDirectory);
        this.worldDirectory = this.prepareDirectory(this.worldDirectory);
        this.serverDirectory = this.prepareDirectory(this.serverDirectory);
        this.proxyDirectory = this.prepareDirectory(this.proxyDirectory);
    }

    private SavedClientState loadFromPath(Path path) {
        try (FileReader reader = new FileReader(path.toFile())) {
            return GSON.fromJson(reader, SavedClientState.class);
        } catch (FileNotFoundException ignored) {
            // Let's not spam the console when it's just a new save.
        } catch (IOException e) {
            Terramap.instance().logger().error("Failed to read a saved client state, will fallback to a new one");
            Terramap.instance().logger().catching(e);
        }
        return this.getDefaultState();
    }

    private void saveStateToPath(Path path, SavedClientState state) {
        try (FileWriter writer = new FileWriter(path.toFile())) {
            GSON.toJson(state, writer);
        } catch (IOException e) {
            Terramap.instance().logger().error("Failed to save a client state");
            Terramap.instance().logger().catching(e);
        }
    }

    private Path prepareDirectory(Path directory) throws IOException {
        if (!exists(directory)) {
            Terramap.instance().logger().debug("Created directory {}", directory);
            createDirectories(directory);
        } else if (!isDirectory(directory) || ! isWritable(directory)) {
            Terramap.instance().logger().error("{} exists and is not a directory, or is not writeable. Terramap will fallback to a temporary directory instead.", directory);
            directory = createTempDirectory(directory, "terramap");
            directory.toFile().deleteOnExit();
        }
        return directory;
    }

}
