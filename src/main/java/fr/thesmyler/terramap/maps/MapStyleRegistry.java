package fr.thesmyler.terramap.maps;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import com.google.gson.Gson;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;

public class MapStyleRegistry {

	private static final String BUILT_IN_MAPS = "/assets/terramap/mapstyles.json";
	private static Map<String, TiledMap> builtinsAndOnline = new HashMap<String, TiledMap>();

	public static Map<String, TiledMap> getTiledMaps() {
		Map<String, TiledMap> map = new LinkedHashMap<String, TiledMap>();
		map.putAll(getBuiltIns());
		return map;
	}

	private static Map<String, TiledMap> getBuiltIns() {
		return builtinsAndOnline;
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
				builtinsAndOnline = loadFromJson(json, TiledMapProvider.BUILT_IN);
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
			String json = String.join("", Files.readAllLines(file.toPath()));
			Map<String, TiledMap> maps =  loadFromJson(json, TiledMapProvider.ONLINE);
			builtinsAndOnline.putAll(maps);
		} catch (IOException e) {
			TerramapMod.logger.error("Failed to load updated map styles, will fallback to built-ins");
			TerramapMod.logger.catching(e);
		}
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
