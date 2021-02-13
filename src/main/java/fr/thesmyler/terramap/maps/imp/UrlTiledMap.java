package fr.thesmyler.terramap.maps.imp;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.maps.CachingRasterTiledMap;
import fr.thesmyler.terramap.maps.MapStylesLibrary;
import fr.thesmyler.terramap.maps.TiledMapProvider;
import fr.thesmyler.terramap.maps.utils.TilePosUnmutable;
import fr.thesmyler.terramap.network.SP2CMapStylePacket;
import net.buildtheearth.terraplusplus.util.http.Http;
import net.minecraft.util.text.ITextComponent;

/**
 * Instances are usually created in {@link MapStylesLibrary} and {@link SP2CMapStylePacket}.
 * 
 * @author SmylerMC
 *
 */
public class UrlTiledMap extends CachingRasterTiledMap<UrlRasterTile> {

	private final String[] urlPatterns;
	private final int maxZoom;
	private final int minZoom;
	private final int displayPriority;
	private final boolean allowOnMinimap;

	private final String id;
	private final TiledMapProvider provider;
	private final Map<String, String> names; // A map of language key => name
	private final Map<String, String> copyrightJsons;
	private final long version;
	private final String comment;
	private final int maxConcurrentRequests; // How many concurrent http connections are allowed by this map provider. This should be two by default, as that's what OSM requires
	private boolean debug;
	
	private static final ITextComponent FALLBACK_COPYRIGHT = ITextComponent.Serializer.jsonToComponent("{\"text\":\"The text component for this copyright notice was malformatted!\",\"color\":\"dark_red\"}");

	public UrlTiledMap(
			String[] urlPatterns,
			int minZoom,
			int maxZoom,
			String id,
			Map<String, String> names,
			Map<String, String> copyright,
			int displayPriority,
			boolean allowOnMinimap,
			TiledMapProvider provider,
			long version,
			String comment,
			int maxConcurrentDownloads,
			boolean debug) {
		Preconditions.checkArgument(urlPatterns.length > 0, "At least one url pattern needed");
		Preconditions.checkArgument(minZoom >= 0, "Zoom level must be at least 0");
		Preconditions.checkArgument(maxZoom >= 0, "Zoom level must be at most 25");
		Preconditions.checkArgument(!Strings.isNullOrEmpty(id), "A valid map id needs to be provided");
		Preconditions.checkArgument(names != null, "Valid map names needs to be provided");
		Preconditions.checkArgument(copyright != null, "Valid map coprights needs to be provided");
		Preconditions.checkArgument(provider != null, "Av alid map provider needs to be provided");
		Preconditions.checkArgument(version >= 0, "Map version number must be positive");
		Preconditions.checkArgument(comment != null, "A valid map comment needs to be provided");
		Preconditions.checkArgument(maxConcurrentDownloads > 0 ,"Max concurent downloads must be at least 1");
		for(String pattern: urlPatterns) {
			String url = pattern.replace("{z}", "0").replace("{x}", "0").replace("{y}", "0");
			try {
				@SuppressWarnings("unused")
				URL u = new URL(url);
			} catch (MalformedURLException e) {
				throw new IllegalArgumentException(url + " is not a valid url pattern");
			}
		}
		this.urlPatterns = urlPatterns;
		this.maxZoom = maxZoom;
		this.minZoom = minZoom;
		this.id = id;
		this.copyrightJsons = copyright;
		this.names = names;
		this.provider = provider;
		this.version = version;
		this.comment = comment;
		this.allowOnMinimap = allowOnMinimap;
		this.displayPriority = displayPriority;
		this.maxConcurrentRequests = maxConcurrentDownloads;
		this.debug = debug;
	}
	
	/**
	 * Initializes this map by loading all tiles bellow a certain zoom level specified in {@link TerramapConfig}, and starts loading their textures, if it hasn't been done yet.
	 * Also makes sure the cache follows the mod's config
	 */
	@Override
	public void setup() {
		for(String urlPattern: this.getUrlPatterns()) {
			String url = urlPattern.replace("{z}", "0").replace("{x}", "0").replace("{y}", "0");
			try {
				Http.setMaximumConcurrentRequestsTo(url, this.getMaxConcurrentRequests());
			} catch(IllegalArgumentException e) {
				TerramapMod.logger.error("Failed to set max concurrent requests for host. Url :" + url);
			}
		}
		super.setup();
	}
	
	@Override
	protected UrlRasterTile createNewTile(TilePosUnmutable pos) {
		String pat = this.urlPatterns[(pos.getZoom() + pos.getX() + pos.getY()) % this.urlPatterns.length];
		return new UrlRasterTile(pat, pos);
	}

	@Override
	public boolean isDebug() {
		return this.debug;
	}

	/**
	 * 
	 * @return the minimum zoom level that map supports, that's usually 0
	 */
	@Override
	public int getMinZoom() {
		return this.minZoom;
	}

	/**
	 * @return the maximum zoom level this map supports
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
	 * @return a copyright as a {@link ITextComponent}, translated to the appropriate language.
	 */
	@Override
	public ITextComponent getCopyright(String localeKey) {
		String result = this.copyrightJsons.getOrDefault(localeKey, this.copyrightJsons.get("en_us"));
		if(result == null) {
			return FALLBACK_COPYRIGHT;
		} else {
			try {
				return ITextComponent.Serializer.jsonToComponent(result);
			} catch (Exception e) {
				TerramapMod.logger.error("Copyright notice json failed to be parsing!");
				TerramapMod.logger.catching(e);
				return FALLBACK_COPYRIGHT;
			}
		}
	}

	/**
	 * @return the language key => copyright json value map for this map
	 */
	public Map<String, String> getUnlocalizedCopyrights() {
		return this.copyrightJsons;
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
	public TiledMapProvider getProvider() {
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
	 * @return Whether or not this map can be used on the minimap
	 */
	@Override
	public boolean isAllowedOnMinimap() {
		return this.allowOnMinimap;
	}

}