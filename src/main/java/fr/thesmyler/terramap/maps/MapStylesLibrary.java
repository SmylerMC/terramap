package fr.thesmyler.terramap.maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.maps.imp.TerrainPreviewMap;
import fr.thesmyler.terramap.maps.imp.UrlTiledMap;

public class MapStylesLibrary {

	private static final String BUILT_IN_MAPS = "assets/terramap/mapstyles.json";
	private static File configMapsFile;
	private static Map<String, IRasterTiledMap> baseMaps = new HashMap<>();
	private static Map<String, UrlTiledMap> userMaps = new HashMap<>();

	/**
	 * Get the default Terramap maps, loaded from the jar and from the online source
	 * The returned map is a new one, and can be mutated safely.
	 * Does not actually load the maps, this needs to be done beforehand with {@link MapStylesLibrary#loadBuiltIns()} and {@link MapStylesLibrary#loadFromOnline(String)}
	 * 
	 * @return a new map that contains id => TiledMap couples
	 */
	public static Map<String, IRasterTiledMap> getBaseMaps() {
		Map<String, IRasterTiledMap> maps = new HashMap<>();
		maps.putAll(baseMaps);
		return maps;
	}
	
	/**
	 * Get the default Terramap maps, loaded from config/terramap_user_styles.json.
	 * The returned map is a new one, and can be mutated safely.
	 * 
	 * @return a new map that contains id => TiledMap couples
	 */
	public static Map<String, UrlTiledMap> getUserMaps() {
		Map<String, UrlTiledMap> maps = new HashMap<>();
		maps.putAll(userMaps);
		return maps;
	}

	/**
	 * Loads map styles from the mod's jar.
	 */
	public static void loadBuiltIns() {
		TiledMapProvider.BUILT_IN.setLastError(null);
		String path = BUILT_IN_MAPS;
		try {
			// https://github.com/MinecraftForge/MinecraftForge/issues/5713
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			try(BufferedReader txtReader = new BufferedReader(new InputStreamReader(in))) {
				String json = "";
				String line = txtReader.readLine();
				while(line != null) {
					json += line;
					line = txtReader.readLine();
				}
				baseMaps.putAll(loadFromJson(json, TiledMapProvider.BUILT_IN));
			}
		} catch(Exception e) {
			TerramapMod.logger.fatal("Failed to read built-in map styles, Terramap is likely to not work properly!");
			TerramapMod.logger.fatal("Path: " + path);
			TerramapMod.logger.catching(e);
			TiledMapProvider.BUILT_IN.setLastError(e);
		}

	}
	
	public static void loadInternals() {
		TiledMapProvider.INTERNAL.setLastError(null);
		try {
			if(TerramapConfig.enableDebugMaps) {
				TerrainPreviewMap terrain = new TerrainPreviewMap(); 
				baseMaps.put(terrain.getId(), terrain);
			}
		} catch(Exception e) {
			TerramapMod.logger.error("Failed to load internal map styles");
			TerramapMod.logger.catching(e);
			TiledMapProvider.INTERNAL.setLastError(e);
		}
	}
	
	/**
	 * Loads map styles from the online file.
	 * Resolve the TXT field of the given hostname,
	 * parses it as redirect as would most browsers
	 * and does a request to the corresponding url.
	 * The body of that request is then parsed as a map style json config file.
	 * 
	 * This should be called after {@link #loadBuiltIns()} so it overwrites it.
	 * 
	 * @param hostname - the hostname to lookup
	 */
	public static void loadFromOnline(String hostname) {
		TiledMapProvider.ONLINE.setLastError(null);
		try {
			// We can't rely on Terra++ for that because the cache would cause trouble
			URL url = resolveUpdateURL(hostname);
			URLConnection connection = url.openConnection();
			connection.setAllowUserInteraction(false);
			connection.setRequestProperty("User-Agent", TerramapMod.getUserAgent());
			connection.connect();
			try(BufferedReader txtReader = new BufferedReader(new InputStreamReader(connection.getInputStream()))) {
				String json = "";
				String line = txtReader.readLine();
				while(line != null) {
					json += line;
					line = txtReader.readLine();
				}
				baseMaps.putAll(loadFromJson(json, TiledMapProvider.ONLINE));
			}
		} catch (NamingException | IOException e) {
			TerramapMod.logger.error("Failed to download updated map style file, let's hope the cache has a good version!");
			TerramapMod.logger.catching(e);
			TiledMapProvider.ONLINE.setLastError(e);
		}
	}
	
	/**
	 * Load map styles defined in config/terramap_user_styles.json.
	 * The file to load from needs to be set first with {@link #setConfigMapFile(File)}
	 */
	public static void loadFromConfigFile() {
		TiledMapProvider.CUSTOM.setLastError(null);
		if(configMapsFile == null) {
			TerramapMod.logger.error("Map config file was null!");
			TiledMapProvider.CUSTOM.setLastError(new NullPointerException("Map style config files was null"));
			return;
		}
		if(!configMapsFile.exists()) {
			try {
				TerramapMod.logger.debug("Map config file did not exist, creating a blank one.");
				MapStyleFile mapFile = new MapStyleFile(new MapFileMetadata(0, "Add custom map styles here. See an example at styles.terramap.thesmyler.fr (open in your browser, do not add http or https prefix)"));
				GsonBuilder builder = new GsonBuilder();
				builder.setPrettyPrinting();
				Files.write(configMapsFile.toPath(), builder.create().toJson(mapFile).getBytes(Charset.defaultCharset()));
			} catch (IOException e) {
				TerramapMod.logger.error("Failed to create map style config file!");
				TerramapMod.logger.catching(e);
				TiledMapProvider.CUSTOM.setLastError(e);

			}
		} else {
			try {
				userMaps.putAll(loadFromFile(configMapsFile, TiledMapProvider.CUSTOM));
			} catch (Exception e) {
				TerramapMod.logger.error("Failed to read map style config file!");
				TerramapMod.logger.catching(e);
				TiledMapProvider.CUSTOM.setLastError(e);
			}
		}
	}
	
	/**
	 * Set the config file that should contain the user's custom map syles
	 * It will be created if it does not exist.
	 * 
	 * @param file - the json config file
	 */
	public static void setConfigMapFile(File file) {
		configMapsFile = file;
	}
	
	/**
	 * Reload map styles from the jar, online, and config/terramap_user_styles.json
	 */
	public static void reload() {
		baseMaps.clear();
		userMaps.clear();
		loadBuiltIns();
		loadInternals();
		loadFromOnline(TerramapMod.STYLE_UPDATE_HOSTNAME);
		loadFromConfigFile();
	}
	
	private static UrlTiledMap readFromSaved(String id, SavedMapStyle saved, TiledMapProvider provider, long version, String comment) {
		String[] patterns = saved.urls;
		if(patterns == null || patterns.length <= 0) {
			if(saved.url != null) {
				// This is a legacy source, it only has one url
				patterns = new String[] {saved.url};
			} else throw new RuntimeException("Could not find any valid url for map style " + id + "-" + provider + "v" + version);
		}
		return new UrlTiledMap(
				patterns,
				saved.min_zoom,
				saved.max_zoom,
				id,
				saved.name,
				saved.copyright,
				saved.display_priority,
				saved.allow_on_minimap,
				provider,
				version,
				comment,
				saved.max_concurrent_requests,
				saved.debug
			);
	}
	
	private static Map<String, UrlTiledMap> loadFromFile(File file, TiledMapProvider provider) throws IOException {
		String json = String.join("", Files.readAllLines(file.toPath()));
		Map<String, UrlTiledMap> maps =  loadFromJson(json, provider);
		return maps;
	}

	private static Map<String, UrlTiledMap> loadFromJson(String json, TiledMapProvider provider) {
		Gson gson = new Gson();
		MapStyleFile savedStyles = gson.fromJson(json, MapStyleFile.class);
		Map<String, UrlTiledMap> styles = new HashMap<String, UrlTiledMap>();
		for(String id: savedStyles.maps.keySet()) {
			UrlTiledMap style = readFromSaved(id, savedStyles.maps.get(id), provider, savedStyles.metadata.version, savedStyles.metadata.comment);
			if(!TerramapConfig.enableDebugMaps && style.isDebug()) {
				TerramapMod.logger.info("Not loading debug map style " + style.getId());
				continue;
			}
			styles.put(id, style);
		}
		return styles;
	}

	private static URL resolveUpdateURL(String hostname) throws UnknownHostException, NamingException, MalformedURLException {
		InetAddress inetAddress = InetAddress.getByName(hostname);
		InitialDirContext iDirC = new InitialDirContext();
		Attributes attributes = iDirC.getAttributes("dns:/" + inetAddress.getHostName(), new String[] {"TXT"});
		String attribute =  attributes.get("TXT").get().toString();
		try {
			return new URL(attribute.split("\\|")[1]);
		} catch(IndexOutOfBoundsException e) {
			throw new UnknownHostException("TXT record was malformatted");
		}
	}
	
	public static File getFile() {
		return configMapsFile;
	}
	
	private static class SavedMapStyle {

		String url; // Used by legacy versions
		String[] urls;
		Map<String, String> name;
		Map<String, String> copyright;
		int min_zoom;
		int max_zoom;
		int display_priority;
		boolean allow_on_minimap;
		int max_concurrent_requests = 2;
		boolean debug;

	}

	private static class MapStyleFile {

		Map<String, SavedMapStyle> maps;
		MapFileMetadata metadata;
		
		MapStyleFile(MapFileMetadata metadata) {
			this.metadata = metadata;
			this.maps = new HashMap<String, SavedMapStyle>();
		}

	}

	private static class MapFileMetadata {
		
		long version;
		String comment;
		
		MapFileMetadata(long version, String comment) {
			this.comment = comment;
			this.version = version;
		}

	}
}
