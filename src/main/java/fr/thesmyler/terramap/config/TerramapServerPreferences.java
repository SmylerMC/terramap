package fr.thesmyler.terramap.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import fr.thesmyler.terramap.TerramapMod;

public class TerramapServerPreferences {

	public static final String FILENAME = "terramapserver.json";
	private static Map<String, Map<String, String>> preferences = new HashMap<String, Map<String, String>>();
	private static File file = null;

	private static final String GEN_SETTINGS_KEY = "genSettings";
	private static final String MAP_STATE_KEY = "mapState";

	public static String getServerGenSettings(String serverId) {
		return preferences.getOrDefault(serverId, new HashMap<String, String>())
				.getOrDefault(GEN_SETTINGS_KEY, "");
	}
	
	public static String getServerMapState(String serverId) {
		String state = preferences.getOrDefault(serverId, new HashMap<String, String>())
				.getOrDefault(MAP_STATE_KEY, "");
		return state;
	}

	public static void setServerMapState(String serverId, String mapState) {
		Map<String, String> serv = preferences.get(serverId);
		if(serv == null) {
			serv =  new HashMap<String, String>();
			preferences.put(serverId, serv);
		}
		serv.put(MAP_STATE_KEY, mapState);
	}
	
	public static void setServerGenSettings(String serverId, String genSettings) {
		Map<String, String> serv = preferences.get(serverId);
		if(serv == null) {
			serv =  new HashMap<String, String>();
			preferences.put(serverId, serv);
		}
		serv.put(GEN_SETTINGS_KEY, genSettings);
	}

	public static void save() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(preferences);
		try {
			Files.write(str, file, Charset.defaultCharset());
		} catch (IOException e) {
			TerramapMod.logger.error("Failed to write server preferences to " + file.getAbsolutePath());
			e.printStackTrace();
		}
	}

	public static void load() {
		if(!file.exists()) {
			preferences = new HashMap<String, Map<String, String>>();
			TerramapMod.logger.info("Server preference file did not exist, used default");
		} else {
			try {
				String text = String.join("\n", Files.readLines(file, Charset.defaultCharset()));
				Gson gson = new Gson();
				preferences = gson.fromJson(text, new TypeToken<Map<String, Map<String, String>>>(){}.getType());
			} catch (IOException | JsonSyntaxException e) {
				TerramapMod.logger.error("Failed to load server preference file, setting to default");
				TerramapMod.logger.catching(e);
				preferences = new HashMap<String, Map<String, String>>();
			}
		}
	}
	
	public static void setFile(File file) {
		if(TerramapServerPreferences.file == null) {
			TerramapServerPreferences.file = file;
		} else {
			TerramapMod.logger.error("Tried to set server preference file but it was already");
		}
	}
}
