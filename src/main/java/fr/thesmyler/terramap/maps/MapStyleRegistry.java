package fr.thesmyler.terramap.maps;

import java.io.BufferedReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import com.google.common.reflect.TypeToken;
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
		@SuppressWarnings("serial")
		Map<String, SavedMapStyle> savedStyles = gson.fromJson(json, new TypeToken<Map<String, SavedMapStyle>>() {}.getType());
		Map<String, TiledMap> styles = new HashMap<String, TiledMap>();
		for(String id: savedStyles.keySet()) {
			SavedMapStyle savedStyle = savedStyles.get(id);
			TiledMap style = new TiledMap(
					savedStyle.url,
					savedStyle.min_zoom,
					savedStyle.max_zoom,
					TerramapConfig.maxTileLoad,
					id,
					savedStyle.name,
					savedStyle.copyright,
					provider
					);
			styles.put(id, style);
			//TODO Do some checks to make sure the parsed map styles are right
		}
		return styles;
	}

	public static class SavedMapStyle {

		//TODO Priority
		public String url;
		public Map<String, String> name;
		public String copyright;
		int min_zoom;
		int max_zoom;

	}

}
