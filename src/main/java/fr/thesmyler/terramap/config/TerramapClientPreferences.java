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

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.gui.TerramapScreenSavedState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * 
 * Preferences setting the client needs to store about servers
 *
 */
@SideOnly(Side.CLIENT)
public class TerramapClientPreferences {

	public static final String FILENAME = "terramap_client_preferences.json";
	private static File file = null;
	private static Preferences preferences = new Preferences();

	public static String getServerGenSettings(String serverId) {
		return preferences.servers.containsKey(serverId) ? preferences.servers.get(serverId).genSettings: "";
	}
	
	public static TerramapScreenSavedState getServerSavedScreen(String serverId) {
		return preferences.servers.containsKey(serverId) ? preferences.servers.get(serverId).mapState: null;
	}

	public static void setServerSavedScreen(String serverId, TerramapScreenSavedState mapState) {
		ServerPreferences serv = preferences.servers.getOrDefault(serverId, new ServerPreferences());
		serv.mapState = mapState;
		if(!preferences.servers.containsKey(serverId)) preferences.servers.put(serverId, serv);
	}
	
	public static void setServerGenSettings(String serverId, String genSettings) {
		ServerPreferences serv = preferences.servers.getOrDefault(serverId, new ServerPreferences());
		serv.genSettings = genSettings;
		if(!preferences.servers.containsKey(serverId)) preferences.servers.put(serverId, serv);
	}

	public static void save() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		String str = gson.toJson(preferences);
		try {
			Files.write(str, file, Charset.defaultCharset());
		} catch (IOException e) {
			TerramapMod.logger.error("Failed to write client preferences to " + file.getAbsolutePath());
			e.printStackTrace();
		}
	}

	public static void load() {
		if(!file.exists()) {
			preferences = new Preferences();
			TerramapMod.logger.info("Client preference file did not exist, used default");
		} else {
			try {
				String text = String.join("\n", Files.readLines(file, Charset.defaultCharset()));
				Gson gson = new Gson();
				preferences = gson.fromJson(text, Preferences.class);
			} catch (IOException | JsonSyntaxException e) {
				TerramapMod.logger.error("Failed to load client preference file, setting to default");
				TerramapMod.logger.catching(e);
				preferences = new Preferences();
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
	
	private static class Preferences {
		Map<String, ServerPreferences> servers = new HashMap<String, ServerPreferences>();
	}
	
	private static class ServerPreferences {
		public String genSettings = "";
		public TerramapScreenSavedState mapState = new TerramapScreenSavedState();
	}
	
}
