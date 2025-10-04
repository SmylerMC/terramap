package net.smyler.terramap.tilesets.raster;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;

import javax.naming.NamingException;
import javax.naming.directory.Attributes;
import javax.naming.directory.InitialDirContext;

import net.smyler.smylib.text.Text;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.geo.WebMercatorBounds;

import static java.lang.Integer.parseInt;
import static java.util.Collections.unmodifiableMap;
import static net.smyler.smylib.Preconditions.checkState;
import static net.smyler.terramap.Terramap.getTerramap;


public class RasterTileSetManager {

    private static final String BUILT_IN_MAPS = "assets/terramap/mapstyles.json";

    private final File configMapsFile;

    private final Map<String, RasterTileSet> baseMaps = new HashMap<>();
    private final Map<String, RasterTileSet> baseMapsReadOnly = unmodifiableMap(this.baseMaps);
    private final Map<String, UrlRasterTileSet> userMaps = new HashMap<>();
    private final Map<String, UrlRasterTileSet> userMapsReadOnly = unmodifiableMap(this.userMaps);

    public RasterTileSetManager(File file) {
        this.configMapsFile = file;
    }

    /**
     * Provides access to the default Terramap raster map styles,
     * which were loaded from the JAR and from the online configuration file.
     * <br>
     * Those maps have to be loaded beforehand with {@link RasterTileSetManager#loadBuiltIns()}
     * and {@link RasterTileSetManager#loadFromOnline(String)}.
     *
     * @return a read-only vue of a map that contains id => {@link RasterTileSet} couples
     */
    public Map<String, RasterTileSet> getBaseMaps() {
        return this.baseMapsReadOnly;
    }

    /**
     * Provides access to the map styles configured by the user in config/terramap_user_styles.json.
     *
     * @return a read-only vue of a map that contains id => {@link RasterTileSet} couples
     */
    public Map<String, UrlRasterTileSet> getUserMaps() {
        return this.userMapsReadOnly;
    }

    /**
     * Loads map styles from the mod's jar.
     */
    public void loadBuiltIns() {
        RasterTileSetProvider.BUILT_IN.setLastError(null);
        String path = BUILT_IN_MAPS;
        try {
            // https://github.com/MinecraftForge/MinecraftForge/issues/5713
            InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
            checkState(in != null, "Resource not found: " + path);
            try(BufferedReader txtReader = new BufferedReader(new InputStreamReader(in))) {
                StringBuilder json = new StringBuilder();
                String line = txtReader.readLine();
                while(line != null) {
                    json.append(line);
                    line = txtReader.readLine();
                }
                this.baseMaps.putAll(loadFromJson(json.toString(), RasterTileSetProvider.BUILT_IN));
            }
        } catch(Exception e) {
            getTerramap().logger().fatal("Failed to read built-in map styles, Terramap is likely to not work properly!");
            getTerramap().logger().fatal("Path: {}", path);
            getTerramap().logger().catching(e);
            RasterTileSetProvider.BUILT_IN.setLastError(e);
        }

    }

    /**
     * Loads map styles from the online file.
     * Resolve the TXT field of the given hostname,
     * parses it as redirect as would most browsers
     * and does a request to the corresponding url.
     * The body of that request is then parsed as a map style json config file.
     * <br>
     * This should be called after {@link #loadBuiltIns()} so it overwrites it.
     * 
     * @param hostname - the hostname to lookup
     */
    public void loadFromOnline(String hostname) {
        RasterTileSetProvider.ONLINE.setLastError(null);
        String url;
        try {
            url = this.resolveUpdateURL(hostname);
        } catch (UnknownHostException | NamingException e1) {
            getTerramap().logger().error("Failed to resolve map styles urls!");
            getTerramap().logger().catching(e1);
            return;
        }

        getTerramap().http().get(url).whenComplete((b, e) -> {
            if(e != null) {
                getTerramap().logger().error("Failed to download updated map style file!");
                getTerramap().logger().catching(e);
                RasterTileSetProvider.ONLINE.setLastError(e);
            }
            try(BufferedReader txtReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(b)))) {
                StringBuilder json = new StringBuilder();
                String line = txtReader.readLine();
                while(line != null) {
                    json.append(line);
                    line = txtReader.readLine();
                }
                baseMaps.putAll(loadFromJson(json.toString(), RasterTileSetProvider.ONLINE));
            } catch(Exception f) {
                getTerramap().logger().error("Failed to parse updated map style file!");
                getTerramap().logger().catching(e);
                RasterTileSetProvider.ONLINE.setLastError(e);
            }
        });
    }

    /**
     * Load map styles defined in config/terramap_user_styles.json.
     */
    public void loadFromConfigFile() {
        if (this.configMapsFile == null) {
            getTerramap().logger().info("No tile sets config file provided!");
            return;
        }
        RasterTileSetProvider.CUSTOM.setLastError(null);
        if(!this.configMapsFile.exists()) {
            try {
                getTerramap().logger().debug("Map config file did not exist, creating a blank one.");
                TileSetFile mapFile = new TileSetFile(new TileSetFileMetadata(0, "Add custom map styles here. See an example at styles.terramap.thesmyler.fr (open in your browser, do not add http or https prefix)"));
                Files.write(this.configMapsFile.toPath(), getTerramap().gsonPretty().toJson(mapFile).getBytes(Charset.defaultCharset()));
            } catch (IOException e) {
                getTerramap().logger().error("Failed to create map style config file!");
                getTerramap().logger().catching(e);
                RasterTileSetProvider.CUSTOM.setLastError(e);

            }
        } else {
            try {
                this.userMaps.putAll(this.loadFromCustomFile(this.configMapsFile));
            } catch (Exception e) {
                getTerramap().logger().error("Failed to read map style config file!");
                getTerramap().logger().catching(e);
                RasterTileSetProvider.CUSTOM.setLastError(e);
            }
        }
    }

    /**
     * Reload map styles from the jar, online, and config/terramap_user_styles.json
     */
    public void reload(boolean enableDebugTileSet) {
        this.baseMaps.clear();
        this.userMaps.clear();
        this.loadBuiltIns();
        this.loadFromOnline(Terramap.STYLE_UPDATE_HOSTNAME);
        this.loadFromConfigFile();
        if (!enableDebugTileSet) {
            this.baseMaps.values().removeIf(RasterTileSet::isDebug);
            this.userMaps.values().removeIf(RasterTileSet::isDebug);
        }
    }

    private UrlRasterTileSet readFromSaved(String id, TileSetDefinition saved, RasterTileSetProvider provider, long version, String comment) {
        String[] patterns = saved.urls;
        if(patterns == null || patterns.length == 0) {
            if(saved.url != null) {
                // This is a legacy source, it only has one url
                patterns = new String[] {saved.url};
            } else throw new IllegalArgumentException("Could not find any valid url for map style " + id + "-" + provider + "v" + version);
        }
        UrlRasterTileSet map = new UrlRasterTileSet(
                patterns,
                saved.min_zoom,
                saved.max_zoom,
                id,
                saved.display_priority,
                saved.allow_on_minimap,
                provider,
                version,
                comment,
                saved.max_concurrent_requests,
                saved.debug
        );
        saved.name.forEach(map::setNameTranslation);
        saved.copyright.forEach(map::setTranslatedCopyright);
        if (saved.bounds != null) {
            saved.bounds.forEach((i, b) -> {
                try {
                    int zoomLevel = parseInt(i);
                    map.setBounds(zoomLevel, b);
                } catch (NumberFormatException e) {
                    getTerramap().logger().warn("Ignoring invalid zoom level: {}: {}", i, e.getMessage());
                }
            });
        }
        return map;
    }

    private Map<String, UrlRasterTileSet> loadFromCustomFile(File file) throws IOException {
        String json = String.join("", Files.readAllLines(file.toPath()));
        return loadFromJson(json, RasterTileSetProvider.CUSTOM);
    }

    private Map<String, UrlRasterTileSet> loadFromJson(String json, RasterTileSetProvider provider) {
        TileSetFile savedStyles = getTerramap().gson().fromJson(json, TileSetFile.class);
        Map<String, UrlRasterTileSet> styles = new HashMap<>();
        for(String id: savedStyles.maps.keySet()) {
            UrlRasterTileSet style = readFromSaved(id, savedStyles.maps.get(id), provider, savedStyles.metadata.version, savedStyles.metadata.comment);
            styles.put(id, style);
        }
        return styles;
    }

    private String resolveUpdateURL(String hostname) throws UnknownHostException, NamingException {
        InetAddress inetAddress = InetAddress.getByName(hostname);
        InitialDirContext iDirC = new InitialDirContext();
        Attributes attributes = iDirC.getAttributes("dns:/" + inetAddress.getHostName(), new String[] {"TXT"});
        String attribute;
        try {
            attribute =  attributes.get("TXT").get().toString();
        } catch(NullPointerException e) {
            throw new UnknownHostException(String.format("No txt record was found at %s ?? Something is wrong, either with the name server or with your dns provider!", hostname));
        }
        try {
            return attribute.split("\\|")[1].replace("${version}", getTerramap().version());
        } catch(IndexOutOfBoundsException e) {
            throw new UnknownHostException("TXT record was malformatted");
        }
    }

    public File getFile() {
        return this.configMapsFile;
    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static class TileSetDefinition {

        String url; // Used by legacy versions
        String[] urls;
        Map<String, String> name;
        Map<String, Text> copyright;
        int min_zoom;
        int max_zoom;
        int display_priority;
        boolean allow_on_minimap;
        int max_concurrent_requests = 2;
        boolean debug;
        Map<String, WebMercatorBounds> bounds;

    }

    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private static class TileSetFile {

        Map<String, TileSetDefinition> maps;
        TileSetFileMetadata metadata;

        TileSetFile(TileSetFileMetadata metadata) {
            this.metadata = metadata;
            this.maps = new HashMap<>();
        }

    }

    private static class TileSetFileMetadata {

        long version;
        String comment;

        TileSetFileMetadata(long version, String comment) {
            this.comment = comment;
            this.version = version;
        }

    }
}
