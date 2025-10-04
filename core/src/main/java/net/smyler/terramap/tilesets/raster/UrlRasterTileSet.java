package net.smyler.terramap.tilesets.raster;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import net.smyler.smylib.Identifier;
import net.smyler.terramap.util.CopyrightHolder;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.util.ImageUtil;
import net.smyler.terramap.geo.TilePosImmutable;
import net.smyler.terramap.geo.WebMercatorBounds;

import javax.imageio.ImageIO;

import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.Strings.isNullOrEmpty;
import static net.smyler.terramap.Terramap.getTerramap;

/**
 * Instances are usually created in {@link RasterTileSetManager} or packets.
 * 
 * @author Smyler
 *
 */
public class UrlRasterTileSet extends CachingRasterTileSet implements CopyrightHolder {

    private final String[] urlPatterns;
    private final int maxZoom;
    private final int minZoom;
    private final int displayPriority;
    private final boolean allowOnMinimap;

    private final String id;
    private final RasterTileSetProvider provider;
    private final Map<String, String> names = new HashMap<>(); // A map of language key => name
    private final Map<String, Text> copyrights = new HashMap<>();
    private final long version;
    private final String comment;
    private final int maxConcurrentRequests; // How many concurrent http connections are allowed by this map provider. This should be two by default, as that's what OSM requires
    private final boolean debug;
    private final Map<Integer, WebMercatorBounds> bounds = new HashMap<>();

    private Identifier errorTileTexture = null;

    public UrlRasterTileSet(
            String[] urlPatterns,
            int minZoom, int maxZoom,
            String id,
            int displayPriority,
            boolean allowOnMinimap,
            RasterTileSetProvider provider,
            long version,
            String comment,
            int maxConcurrentDownloads,
            boolean debug
    ) {
        checkArgument(urlPatterns.length > 0, "At least one url pattern needed");
        checkArgument(minZoom >= 0, "Zoom level must be at least 0");
        checkArgument(maxZoom >= 0 && maxZoom <= 25, "Zoom level must be at most 25");
        checkArgument(!isNullOrEmpty(id), "A valid map id needs to be provided");
        checkArgument(provider != null, "A valid map provider needs to be provided");
        checkArgument(version >= 0, "Map version number must be positive");
        checkArgument(comment != null, "A valid map comment needs to be provided");
        checkArgument(maxConcurrentDownloads > 0 ,"Max concurrent downloads must be at least 1");
        for(String pattern: urlPatterns) {
            checkUrlPattern(pattern);
        }
        this.urlPatterns = urlPatterns;
        this.maxZoom = maxZoom;
        this.minZoom = minZoom;
        this.id = id;
        this.provider = provider;
        this.version = version;
        this.comment = comment;
        this.allowOnMinimap = allowOnMinimap;
        this.displayPriority = displayPriority;
        this.maxConcurrentRequests = maxConcurrentDownloads;
        this.debug = debug;
    }

    @Override
    public void setup() {
        this.registerErrorTexture();
        this.enforceMaxConcurrentRequests();
        super.setup();
    }

    @Override
    protected RasterTile createNewTile(TilePosImmutable pos) {
        String pat = this.urlPatterns[(pos.getZoom() + pos.getX() + pos.getY()) % this.urlPatterns.length];
        return new UrlRasterTile(pat, pos);
    }

    @Override
    public boolean isDebug() {
        return this.debug;
    }

    /**
     * @return the minimum zoom level supported by this tile set, which is usually 0
     */
    @Override
    public int getMinZoom() {
        return this.minZoom;
    }

    /**
     * @return the maximum zoom level supported by this tile set
     */
    @Override
    public int getMaxZoom() {
        return this.maxZoom;
    }

    /**
     * @return the String id of this map
     */
    @Override
    public String getId() {
        return this.id;
    }

    /**
     * Gets a copyright notice for this map, translated in the appropriate language,
     * or English if it isn't available (missing or json was wrong).
     * If English isn't available either, returns a fallback that simply says there was an error.
     * 
     * 
     * @param localeKey - the language key to get the copyright for
     * @return a copyright as a {@link Text}, translated to the appropriate language.
     */
    @Override
    public Text getCopyright(String localeKey) {
        return this.copyrights.getOrDefault(localeKey, this.copyrights.get("en_us"));
    }

    /**
     * @return the language key => copyright json value map for this map
     */
    public Map<String, Text> getUnlocalizedCopyrights() {
        return this.copyrights;
    }

    /**
     * Gets a name for this map, translated in the appropriate language,
     * or English if it isn't available (missing or json was wrong).
     * If English isn't available either, returns a fallback that simply says there was an error.
     * 
     * 
     * @param localeKey - the language key to get the copyright for
     * @return the name of this map, translated to the appropriate language.
     */
    @Override
    public String getLocalizedName(String localeKey) {
        String result = this.names.getOrDefault(localeKey, this.names.get("en_us"));
        if(result != null) {
            return result;
        } else {
            return this.id;
        }
    }

    /**
     * @return the language key => name value map for this map
     */
    public Map<String, String> getUnlocalizedNames() {
        return this.names;
    }

    /**
     * @return The url pattern used to get the tiles' url for this map
     */
    public String[] getUrlPatterns() {
        return this.urlPatterns;
    }

    /**
     * @return the comment from the map provider metadata
     */
    @Override
    public String getComment() {
        return this.comment;
    }

    /**
     * 
     * @return this map's provider
     */
    @Override
    public RasterTileSetProvider getProvider() {
        return this.provider;
    }

    /**
     * 
     * @return the version of this map's provider
     */
    @Override
    public long getProviderVersion() {
        return this.version;
    }

    /**
     * @return an integer used to calculate the order in which map styles should be displayed. Higher means first.
     */
    @Override
    public int getDisplayPriority() {
        return this.displayPriority;
    }

    /**
     * 
     * @return the number of maximum concurrent requests allowed by this map's web provider TOS. This is 2 for OSM.
     */
    public int getMaxConcurrentRequests() {
        return this.maxConcurrentRequests;
    }

    /**
     * @return Whether this map can be used on the minimap
     */
    @Override
    public boolean isAllowedOnMinimap() {
        return this.allowOnMinimap;
    }

    @Override
    public WebMercatorBounds getBounds(int zoom) {
        return this.bounds.get(zoom);
    }

    @Override
    public Identifier getDefaultTileTexture() {
        return this.errorTileTexture;
    }

    /**
     * Set the localized name of this map in a given language.
     * If the provided value is null, unset the translation for the given language.
     *
     * @param languageKey   a language key (e.g. eu_US)
     * @param value         the translated map name
     */
    public void setNameTranslation(String languageKey, String value) {
        checkArgument(languageKey != null, "Language key cannot be null");
        if (value == null) {
            this.names.remove(languageKey);
        } else {
            this.names.put(languageKey, value);
        }
    }

    /**
     * Set the localized copyright of this map in a given language.
     * If the provided value is null, unset the translation for the given language.
     *
     * @param languageKey   a language key (e.g. en_US)
     * @param value         the translated copyright {@link Text}
     */
    public void setTranslatedCopyright(String languageKey, Text value) {
        checkArgument(languageKey != null, "Language key cannot be null");
        if (value == null) {
            this.copyrights.remove(languageKey);
        } else {
            this.copyrights.put(languageKey, value);
        }
    }

    /**
     * Set the bounds of this map at a given zoom level.
     * If the provided bounds are null, existing bounds are removed from the given zoom level.
     *
     * @param zoomLevel the zoom level for which to apply the bounds
     * @param bounds    the bounds
     */
    public void setBounds(int zoomLevel, WebMercatorBounds bounds) {
        if (bounds == null) {
            this.bounds.remove(zoomLevel);
        } else {
            this.bounds.put(zoomLevel, bounds);
        }
    }

    private void registerErrorTexture() {
        int[] color = {170, 211, 223};
        BufferedImage image = ImageUtil.imageFromColor(256,  256, color);
        this.errorTileTexture = getGameClient().guiDrawContext().loadDynamicTexture(image);
    }

    private void enforceMaxConcurrentRequests() {
        for(String urlPattern: this.getUrlPatterns()) {
            String url = urlPattern.replace("{z}", "0").replace("{x}", "0").replace("{y}", "0");
            try {
                URL parsed = new URL(url);
                if(parsed.getProtocol().startsWith("http")) {
                    getTerramap().http().setMaxConcurrentRequests(url, this.getMaxConcurrentRequests());
                }
            } catch(IllegalArgumentException | MalformedURLException e) {
                getTerramap().logger().error("Failed to set max concurrent requests for host. Url :{}", url);
                getTerramap().logger().catching(e);
            }
        }
    }

    private static void checkUrlPattern(String pattern) {
        String url = pattern.replace("{z}", "0").replace("{x}", "0").replace("{y}", "0");
        try {
            new URL(url); // Checking if the URL is valid
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException(url + " is not a valid url pattern");
        }
    }

    private static class UrlRasterTile implements RasterTile {

        private final TilePosImmutable pos;
        private final String url;
        private Identifier texture = null;
        private CompletableFuture<byte[]> textureTask;

        public UrlRasterTile(String urlPattern, TilePosImmutable pos) {
            this.pos = pos;
            this.url = urlPattern
                    .replace("{x}", String.valueOf(this.getPosition().getX()))
                    .replace("{y}", String.valueOf(this.getPosition().getY()))
                    .replace("{z}", String.valueOf(this.getPosition().getZoom()));
        }

        public String getURL() {
            return this.url;
        }

        @Override
        public boolean isTextureAvailable() {
            if(texture != null) return true; // Don't try loading the texture if it has already been loaded
            try {
                this.tryLoadingTexture();
            } catch (Throwable e) {
                return false;
            }
            return this.texture != null;
        }

        @Override
        public Identifier getTexture() throws Throwable {
            if(this.texture == null) {
                if(this.textureTask == null) {
                    this.textureTask = getTerramap().http().get(this.getURL());
                } else this.tryLoadingTexture();
            }
            return this.texture;
        }

        private void tryLoadingTexture() throws Throwable {
            //TODO Do that fully async, DynamicTexture::new is expensive
            if(this.textureTask != null && this.textureTask.isDone()){
                if(this.textureTask.isCompletedExceptionally()) {
                    if(this.textureTask.isCancelled()) {
                        this.textureTask = null;
                    } else {
                        try {
                            this.textureTask.get(); // That will throw an exception
                        } catch(ExecutionException e) {
                            throw e.getCause();
                        }
                    }
                    return;
                }
                byte[] buf = this.textureTask.get();
                if(buf == null) throw new IOException("404 response");
                try (ByteArrayInputStream is = new ByteArrayInputStream(buf)) {
                    BufferedImage image = ImageIO.read(is);
                    if(image == null) throw new IOException("Failed to read image! url: " + this.getURL());
                    this.texture = getGameClient().guiDrawContext().loadDynamicTexture(image);
                }
            }
        }

        @Override
        public void cancelTextureLoading() {
            if(this.textureTask != null) {
                this.textureTask.cancel(true);
                this.textureTask = null;
            }
        }

        @Override
        public void unloadTexture() {
            this.cancelTextureLoading();
            if(this.texture != null) {
                getGameClient().guiDrawContext().unloadDynamicTexture(this.texture);
                this.texture = null;
            }
        }

        @Override
        public boolean equals(Object obj) {
            if(obj == this) return true;
            if(obj == null) return false;
            if(!(obj instanceof UrlRasterTile)) return false;
            UrlRasterTile other = (UrlRasterTile) obj;
            return other.url.equals(this.url);
        }

        @Override
        public TilePosImmutable getPosition() {
            return this.pos;
        }

        @Override
        public int hashCode() {
            return this.url.hashCode();
        }

    }
}