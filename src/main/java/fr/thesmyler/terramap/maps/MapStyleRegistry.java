package fr.thesmyler.terramap.maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import com.google.common.io.Files;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;

public class MapStyleRegistry {

	private static final String BUILT_IN_MAPS = "/assets/terramap/mapstyles.json";
	private static File configMapsFile;
	private static Map<String, TiledMap> availableMaps = new HashMap<String, TiledMap>();

	public static Map<String, TiledMap> getTiledMaps() {
		Map<String, TiledMap> map = new LinkedHashMap<String, TiledMap>();
		map.putAll(getBuiltIns());
		return map;
	}

	private static Map<String, TiledMap> getBuiltIns() {
		return availableMaps;
	}

	public static void loadBuiltIns() {
		try {
			Path path = Paths.get(MapStyleRegistry.class.getResource(BUILT_IN_MAPS).toURI());
			try(BufferedReader txtReader = java.nio.file.Files.newBufferedReader(path)) {
				String json = "";
				String line = txtReader.readLine();
				while(line != null) {
					json += line;
					line = txtReader.readLine();
				}
				availableMaps = loadFromJson(json, TiledMapProvider.BUILT_IN);
			}
		} catch(Exception e) {
			TerramapMod.logger.fatal("Failed to read built-in map styles, Terramap is likely to not work properly!");
			TerramapMod.logger.catching(e);
		}

	}
	
	public static void loadFromOnline(String hostname) {
		File file = new File(TerramapMod.cacheManager.getCachingPath() + "/mapstyles.json");
		try {
			URL url = resolveUpdateURL(hostname);
			TerramapMod.cacheManager.downloadUrlToFile(url, file);
		} catch (NamingException | IOException e) {
			TerramapMod.logger.error("Failed to download updated map style file, let's hope the cache has a good version!");
			TerramapMod.logger.catching(e);
		}
		try {
			availableMaps.putAll(loadFromFile(file, TiledMapProvider.ONLINE));
		} catch (IOException e) {
			TerramapMod.logger.error("Failed to load updated map styles, will fallback to built-ins");
			TerramapMod.logger.catching(e);
		}
	}
	
	public static void loadFromConfigFile() {
		if(configMapsFile == null) {
			TerramapMod.logger.error("Map config file was null!");
			return;
		}
		if(!configMapsFile.exists()) {
			try {
				TerramapMod.logger.debug("Map config file did not exist, creating a blank one.");
				MapStyleFile mapFile = new MapStyleFile(new MapFileMetadata(0, "Add custom map styles here. See an example at styles.terramap.thesmyler.fr (open in your browser, do not add http or https prefix)"));
				GsonBuilder builder = new GsonBuilder();
				builder.setPrettyPrinting();
				Files.write(builder.create().toJson(mapFile), configMapsFile, Charset.defaultCharset());
			} catch (IOException e) {
				TerramapMod.logger.error("Failed to create map style config file!");
				TerramapMod.logger.catching(e);
			}
		} else {
			try {
				availableMaps.putAll(loadFromFile(configMapsFile, TiledMapProvider.CUSTOM));
			} catch (Exception e) {
				TerramapMod.logger.error("Failed to read map style config file!");
				TerramapMod.logger.catching(e);
			}
		}
	}
	
	private static TiledMap readFromSaved(String id, SavedMapStyle saved, TiledMapProvider provider, long version, String comment) {
		//TODO Do some checks to make sure the parsed map styles are right
		return new TiledMap(
				saved.url,
				saved.min_zoom,
				saved.max_zoom,
				TerramapConfig.ClientAdvanced.maxTileLoad,
				id,
				saved.name,
				saved.copyright,
				provider,
				version,
				comment
			);
	}
	
	private static Map<String, TiledMap> loadFromFile(File file, TiledMapProvider provider) throws IOException {
		String json = String.join("", java.nio.file.Files.readAllLines(file.toPath()));
		Map<String, TiledMap> maps =  loadFromJson(json, provider);
		return maps;
	}

	private static Map<String, TiledMap> loadFromJson(String json, TiledMapProvider provider) {
		Gson gson = new Gson();
		MapStyleFile savedStyles = gson.fromJson(json, MapStyleFile.class);
		Map<String, TiledMap> styles = new HashMap<String, TiledMap>();
		for(String id: savedStyles.maps.keySet()) {
			TiledMap style = readFromSaved(id, savedStyles.maps.get(id), provider, savedStyles.metadata.version, savedStyles.metadata.comment);
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
	
	public static void setConfigMapFile(File file) {
		configMapsFile = file;
	}
	
	static class SavedMapStyle {

		String url;
		Map<String, String> name;
		Map<String, String> copyright;
		int min_zoom;
		int max_zoom;

	}

	static class MapStyleFile {

		Map<String, SavedMapStyle> maps;
		MapFileMetadata metadata;
		
		MapStyleFile(MapFileMetadata metadata) {
			this.metadata = metadata;
			this.maps = new HashMap<String, SavedMapStyle>();
		}
		
		MapStyleFile() {
			this(new MapFileMetadata());
		}

	}

	static class MapFileMetadata {
		
		long version;
		String comment;
		
		MapFileMetadata(long version, String comment) {
			this.comment = comment;
			this.version = version;
		}
		
		MapFileMetadata() {
			this(0, "");
		}

	}
}
