package fr.thesmyler.terramap.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import fr.thesmyler.terramap.TerramapMod;

//TODO Use this
public class TerramapServerPreferences {

	public static final String FILENAME = "terramap_server_preferences.json";
	
	private static File file = null;
	public static Preferences preferences = new Preferences();

	public static boolean shouldDisplayPlayer(UUID uuid) {
		return preferences.players.containsKey(uuid) ? preferences.players.get(uuid).display : TerramapConfiguration.playersDisplayDefault;
	}
	
	public static void setShouldDisplayPlayer(UUID uuid, boolean yesNo) {
		PlayerPreferences pp = preferences.players.getOrDefault(uuid, new PlayerPreferences());
		pp.display = yesNo;
		if(!preferences.players.containsKey(uuid)) preferences.players.put(uuid, pp);
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
			preferences = new Preferences();
			TerramapMod.logger.info("Server preference file did not exist, used default");
		} else {
			try {
				String text = String.join("\n", Files.readLines(file, Charset.defaultCharset()));
				Gson gson = new Gson();
				preferences = gson.fromJson(text, Preferences.class);
			} catch (IOException | JsonSyntaxException e) {
				TerramapMod.logger.error("Failed to load server preference file, setting to default");
				TerramapMod.logger.catching(e);
				preferences = new Preferences();
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
	
	private static class Preferences {
		public Map<UUID, PlayerPreferences> players = new HashMap<UUID, PlayerPreferences>();
	}
	
	private static class PlayerPreferences {
		public boolean display = TerramapConfiguration.playersDisplayDefault;
	}
	
}
