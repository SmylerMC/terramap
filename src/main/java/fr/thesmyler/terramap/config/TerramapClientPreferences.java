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
import fr.thesmyler.terramap.gui.screens.TerramapScreenSavedState;
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
	private static ClientPreferences preferences = new ClientPreferences();
	
	private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	public static String getServerGenSettings(String serverId) {
		try {
			initPreferences();
			return preferences.servers.containsKey(serverId) ? preferences.servers.get(serverId).genSettings: "";
		} catch(Exception e) {
			TerramapMod.logger.warn("Failed to get server gen settings");
			TerramapMod.logger.catching(e);
			return "";
		}
	}

	public static TerramapScreenSavedState getServerSavedScreen(String serverId) {
		try {
			initPreferences();
			return preferences.servers.containsKey(serverId) ? preferences.servers.get(serverId).mapState: null;
		} catch(Exception e) {
			TerramapMod.logger.warn("Failed to get saved screen state");
			TerramapMod.logger.catching(e);
			return null;
		}
	}
	
	public static boolean getServerHasShownWelcome(String serverId) {
		try {
			initPreferences();
			return preferences.servers.containsKey(serverId) ? preferences.servers.get(serverId).hasShownWelcome: false;
		} catch(Exception e) {
			TerramapMod.logger.warn("Failed to query whether or not welcome was shown for " + serverId);
			TerramapMod.logger.catching(e);
			return false;
		}
	}

	public static void setServerSavedScreen(String serverId, TerramapScreenSavedState mapState) {
		try {
			initPreferences();
			ServerPreferences serv = preferences.servers.getOrDefault(serverId, new ServerPreferences());
			serv.mapState = mapState;
			preferences.servers.put(serverId, serv);
		} catch(Exception e) {
			TerramapMod.logger.warn("Failed to set saved screen state");
			TerramapMod.logger.catching(e);
		}
	}

	public static void setServerGenSettings(String serverId, String genSettings) {
		try {
			initPreferences();
			ServerPreferences serv = preferences.servers.getOrDefault(serverId, new ServerPreferences());
			serv.genSettings = genSettings;
			if(!preferences.servers.containsKey(serverId)) preferences.servers.put(serverId, serv);
		} catch(Exception e) {
			TerramapMod.logger.warn("Failed to set gen settings");
			TerramapMod.logger.catching(e);
		}
	}
	
	public static void setServerHasShownWelcome(String serverId, boolean yesNo) {
		try {
			initPreferences();
			ServerPreferences serv = preferences.servers.getOrDefault(serverId, new ServerPreferences());
			serv.hasShownWelcome = yesNo;
			if(!preferences.servers.containsKey(serverId)) preferences.servers.put(serverId, serv);
		} catch(Exception e) {
			TerramapMod.logger.warn("Failed to set gen settings");
			TerramapMod.logger.catching(e);
		}
	}
	
	private static void initPreferences() {
		if(preferences == null || preferences.servers == null) {
			preferences = new ClientPreferences();
		}
	}

	public static void save() {
		try {
			if(preferences == null || preferences.servers == null) {
				preferences = new ClientPreferences();
			}
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
				Gson gson = new Gson();
				preferences = gson.fromJson(text, ClientPreferences.class);
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
		Map<String, ServerPreferences> servers = new HashMap<String, ServerPreferences>();
	}

	private static class ServerPreferences {
		public String genSettings = "";
		public TerramapScreenSavedState mapState = new TerramapScreenSavedState();
		public boolean hasShownWelcome =  false;
	}

}
