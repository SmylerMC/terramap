package fr.thesmyler.terramap.config;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;

import fr.thesmyler.terramap.TerramapMod;
import net.minecraft.world.WorldServer;

/**
 * 
 * @author SmylerMC
 *
 */
public class TerramapServerPreferences {

	public static final String FILENAME = "terramap_server_preferences.json";

	private static Map<String, WorldPreferences> preferences = new HashMap<String, WorldPreferences>();

	private static boolean loggedDebugError = false;
	private static long lastErrorLog = Long.MIN_VALUE;

	private static Gson GSON = new GsonBuilder().setPrettyPrinting().create();

	/**
	 * Tells whether or not the given player should be visible on the map on a given world
	 * Does not save anything, if nothing is saved for that player yet, it returns the config default value
	 * 
	 * @param world
	 * @param uuid
	 * @return a boolean indicating if the player should be visible or not
	 */
	public static boolean shouldDisplayPlayer(WorldServer world, UUID uuid) {
		try {
			WorldPreferences preferences = TerramapServerPreferences.getWorldPreferences(world);
			synchronized(preferences) {
				return preferences.players.containsKey(uuid) ? preferences.players.get(uuid).display : TerramapConfig.SERVER.playersDisplayDefault;
			}
		} catch(Exception e) {
			if(!loggedDebugError) {
				TerramapMod.logger.error("Failed to get player display preferences. This error will only be displayed once.");
				TerramapMod.logger.catching(e);
				loggedDebugError = true;
			}
			return TerramapConfig.SERVER.playersDisplayDefault;
		}
	}

	/**
	 * Sets a players display preference for the specified world
	 * 
	 * @param world
	 * @param uuid
	 * @param yesNo
	 */
	public static void setShouldDisplayPlayer(WorldServer world, UUID uuid, boolean yesNo) {
		try {
			synchronized(preferences) {
				WorldPreferences worldPreferences = TerramapServerPreferences.getWorldPreferences(world);
				synchronized(worldPreferences) {
					PlayerPreferences pp = worldPreferences.players.getOrDefault(uuid, new PlayerPreferences());
					pp.display = yesNo;
					worldPreferences.players.put(uuid, pp);
				}
			}
			saveWorldPreferences(world);
		} catch(Exception e) {
			TerramapMod.logger.error("Failed to set player display preferences! See stack trace:");
			TerramapMod.logger.catching(e);
		}
	}

	/**
	 * 
	 * @param world
	 * @return
	 */
	public static UUID getWorldUUID(WorldServer world) {
		try {
			WorldPreferences prefs = TerramapServerPreferences.getWorldPreferences(world);
			synchronized(prefs) {
				UUID uuid = prefs.world_uuid;
				if(uuid.getLeastSignificantBits() == 0 && uuid.getMostSignificantBits() == 0) {
					uuid = UUID.randomUUID();
					prefs.world_uuid = uuid;
					saveWorldPreferences(world);
					TerramapMod.logger.info("Generated uuid " + uuid + " for world " + world.getSaveHandler().getWorldDirectory().getName());
				}
				return uuid;
			}
		} catch(Exception e) {
			TerramapMod.logger.warn("Failed to get world UUID, using 0 instead!");
		}
		return new UUID(0, 0);
	}

	public static void unloadWorldPreferences(WorldServer world) {
		try {
			File file = TerramapServerPreferences.getFileForWorld(world);
			synchronized(preferences) {
				TerramapServerPreferences.preferences.remove(file.getAbsolutePath());
			}
		} catch(Exception e) {
			TerramapMod.logger.warn("Failed to unload a world server preferences");
		}
	}

	/**
	 * Saves the currently loaded preferences for the given file
	 * /!\ Be careful, if this world's preferences were not loaded first, they will be overwritten with the default values
	 * 
	 * @param world
	 */
	public static void saveWorldPreferences(WorldServer world) {
		try {
			File file = TerramapServerPreferences.getFileForWorld(world);
			WorldPreferences prefs = TerramapServerPreferences.getWorldPreferences(world);
			synchronized(prefs) {
				save(file, prefs);
			}
		} catch(Exception e) {
			long t = System.currentTimeMillis();
			if(t > lastErrorLog + 10000) {
				TerramapMod.logger.error("Failed to save server preferences");
				TerramapMod.logger.catching(e);
				lastErrorLog = t;
			}
		}
	}

	/**
	 * Saves all currently loaded server world preferences
	 */
	public static void saveAllPreferences() {
		try {
			synchronized(preferences) {
				for(String key: TerramapServerPreferences.preferences.keySet()) {
					WorldPreferences preferences = TerramapServerPreferences.preferences.get(key);
					File file = new File(key);
					TerramapServerPreferences.save(file, preferences);
				}
			}
		} catch(Exception e) {
			long t = System.currentTimeMillis();
			if(t > lastErrorLog + 10000) {
				TerramapMod.logger.error("Failed to save server preferences");
				TerramapMod.logger.catching(e);
				lastErrorLog = t;
			}
		}
	}

	/**
	 * Loads the specified world's server preferences
	 * 
	 * @param world
	 */
	public static void loadWorldPreferences(WorldServer world) {
		File fileToLoad = TerramapServerPreferences.getFileForWorld(world);
		WorldPreferences preferences = new WorldPreferences();
		if(fileToLoad.exists()) {
			try {
				String text = String.join("\n", Files.readAllLines(fileToLoad.toPath(), Charset.defaultCharset()));
				preferences = GSON.fromJson(text, WorldPreferences.class);
			} catch (IOException | JsonSyntaxException e) {
				TerramapMod.logger.error("Failed to load server preference file, setting to default");
				TerramapMod.logger.catching(e);
				preferences = new WorldPreferences();
			}
		} else {
			TerramapMod.logger.info("Loaded new empty server preferences as file did not exist");
		}
		synchronized(TerramapServerPreferences.preferences) {
			TerramapServerPreferences.preferences.put(fileToLoad.getAbsolutePath(), preferences);
		}
	}
	
	private static void save(File file, WorldPreferences preferences) throws IOException {
		String str = GSON.toJson(preferences);
		Files.write(file.toPath(), str.getBytes(Charset.defaultCharset()));
	}

	private static File getFileForWorld(WorldServer world) {
		return new File(world.getSaveHandler().getWorldDirectory().getAbsoluteFile() + File.separator + TerramapServerPreferences.FILENAME);
	}

	private static WorldPreferences getWorldPreferences(WorldServer world) {
		File file = TerramapServerPreferences.getFileForWorld(world);
		synchronized(preferences) {
			WorldPreferences prefs = TerramapServerPreferences.preferences.getOrDefault(file.toString(), new WorldPreferences());
			preferences.put(file.getAbsolutePath(), prefs);
			return prefs;
		}
	}

	private static class WorldPreferences {
		public UUID world_uuid = new UUID(0, 0);
		public Map<UUID, PlayerPreferences> players = new HashMap<UUID, PlayerPreferences>();
	}

	private static class PlayerPreferences {
		public boolean display = TerramapConfig.SERVER.playersDisplayDefault;
	}

}