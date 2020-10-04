package fr.thesmyler.terramap.maps;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.gson.Gson;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;

public class MapStyleRegistry {

	private static final String BUILT_IN_MAPS = "/assets/terramap/mapstyles.json";
	private static Map<String, TiledMap> builtins = new HashMap<String, TiledMap>();

	public static Map<String, TiledMap> getTiledMaps() {
		Map<String, TiledMap> map = new LinkedHashMap<String, TiledMap>();
		map.putAll(getBuiltIns());
		return map;
	}

	private static Map<String, TiledMap> getBuiltIns() {
		return builtins;
	}

	public static void loadBuiltIns() {
		try {
			Path path = Paths.get(MapStyleRegistry.class.getResource(BUILT_IN_MAPS).toURI());
			try(BufferedReader txtReader = Files.newBufferedReader(path)) {
				String json = "";
				String line = txtReader.readLine();
				while(line != null) {
					json += line;
					line = txtReader.readLine();
				}
				builtins = loadFromJson(json, TiledMapProvider.BUILT_IN);
			}
		} catch(Exception e) {
			TerramapMod.logger.fatal("Failed to read built-in map styles, Terramap is likely to not work properly!");
			TerramapMod.logger.catching(e);
		}

	}

	public static Map<String, TiledMap> loadFromJson(String json, TiledMapProvider provider) {
		Gson gson = new Gson();
		MapStyleFile savedStyles = gson.fromJson(json, MapStyleFile.class);
		Map<String, TiledMap> styles = new HashMap<String, TiledMap>();
		for(String id: savedStyles.maps.keySet()) {
			SavedMapStyle savedStyle = savedStyles.maps.get(id);
			TiledMap style = new TiledMap(
					savedStyle.url,
					savedStyle.min_zoom,
					savedStyle.max_zoom,
					TerramapConfig.maxTileLoad,
					id,
					savedStyle.name,
					savedStyle.copyright,
					provider,
					savedStyles.metadata.version,
					savedStyles.metadata.comment
					);
			styles.put(id, style);
			//TODO Do some checks to make sure the parsed map styles are right
		}
		return styles;
	}
	
	static class MapStyleFile {
		
		Map<String, SavedMapStyle> maps;
		MapFileMetadata metadata;
		
	}

	static class SavedMapStyle {

		String url;
		Map<String, String> name;
		Map<String, String> copyright;
		int min_zoom;
		int max_zoom;

	}
	
	static class MapFileMetadata {
		
		long version;
		String comment;
		
	}

}
