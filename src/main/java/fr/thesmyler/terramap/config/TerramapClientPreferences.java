package fr.thesmyler.terramap.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.util.json.EarthGeneratorSettingsAdapter;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * Preferences settings the client needs to store about servers
 *
 */
@SideOnly(Side.CLIENT)
public class TerramapClientPreferences {

    public static final String FILENAME = "terramap_client_preferences.json";
    private static File file = null;
    private static ClientPreferences preferences = new ClientPreferences();

    private static final Gson GSON = new GsonBuilder()
            .registerTypeAdapter(EarthGeneratorSettings.class, new EarthGeneratorSettingsAdapter())
            .setPrettyPrinting()
            .create();

    private static void initPreferences() {
        if(preferences == null) {
            preferences = new ClientPreferences();
        }
    }

    public static SavedTerramapState getSavedState(String serverIdentifier) {
        initPreferences();
        SavedTerramapState state = preferences.servers.get(serverIdentifier);
        if (state == null) {
            state = new SavedTerramapState();
            preferences.servers.put(serverIdentifier, state);
        }
        return state;
    }

    public static void save() {
        try {
            initPreferences();
            String str = GSON.toJson(preferences);
            Files.write(file.toPath(), str.getBytes(Charset.defaultCharset()));
        } catch (IOException e) {
            TerramapMod.logger.error("Failed to write client preferences to " + file.getAbsolutePath());
            TerramapMod.logger.catching(e);
        }
    }

    public static void load() {
        if(!file.exists()) {
            preferences = new ClientPreferences();
            TerramapMod.logger.info("Client preference file did not exist, used default");
        } else {
            try {
                String text = String.join("\n", Files.readAllLines(file.toPath(), Charset.defaultCharset()));
                preferences = GSON.fromJson(text, ClientPreferences.class);
            } catch (IOException | JsonSyntaxException e) {
                TerramapMod.logger.error("Failed to load client preference file, setting to default");
                TerramapMod.logger.catching(e);
                preferences = new ClientPreferences();
            }
        }
    }

    public static void setFile(File file) {
        if(TerramapClientPreferences.file == null) {
            TerramapClientPreferences.file = file;
        } else {
            TerramapMod.logger.error("Tried to set client preference file but it was already");
        }
    }

    private static class ClientPreferences {
        final Map<String, SavedTerramapState> servers = new HashMap<>();
    }

}
