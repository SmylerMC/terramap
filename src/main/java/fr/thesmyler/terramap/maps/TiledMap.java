package fr.thesmyler.terramap.maps;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import com.google.common.base.Preconditions;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import fr.thesmyler.terramap.network.SP2CMapStylePacket;
import net.minecraft.util.text.ITextComponent;

/**
 * This class is in charge of keeping track of and loading the tiles used for rendering a specific map.
 * When tiles need to be unloaded, priority is given to keep those that were used recently loaded.
 * Tiles with zoom levels lower than a certain value will also never be unloaded, so that backup textures are always kept.
 * It also holds the metadata defined in the map config.
 * Instances are usually created in {@link MapStyleRegistry} and {@link SP2CMapStylePacket}.
 * 
 * @author SmylerMC
 *
 */
public class TiledMap implements Comparable<TiledMap> {

	private final String[] urlPatterns;
	private final LinkedList<WebTile> tileList; // Uses for ordered access when unloading
	private final Map<UnmutableTilePosition, WebTile> tileMap; // Used for unordered access
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
	private int maxLoaded;
	private int baseLoad = 0;
	private int lowZoom = 0;
	
	private static final ITextComponent FALLBACK_COPYRIGHT = ITextComponent.Serializer.jsonToComponent("{\"text\":\"The text component for this copyright notice was malformatted!\",\"color\":\"dark_red\"}");

	public TiledMap(
			String[] urlPatterns,
			int minZoom,
			int maxZoom,
			int maxLoaded,
			String id,
			Map<String, String> names,
			Map<String, String> copyright,
			int displayPriority,
			boolean allowOnMinimap,
			TiledMapProvider provider,
			long version,
			String comment,
			int maxConcurrentDownloads) {
		Preconditions.checkArgument(urlPatterns.length > 0, "At least one url pattern needed");
		this.urlPatterns = urlPatterns;
		this.tileList = new LinkedList<>();
		this.tileMap = new HashMap<>();
		this.maxLoaded = maxLoaded;
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
	}
	
	/**
	 * Initializes this map by loading all tiles bellow a certain zoom level specified in {@link TerramapConfig}, and starts loading their textures, if it hasn't been done yet.
	 * Does nothing otherwise.
	 */
	public void prepareLowTiles() {
		if(baseLoad > 0 && this.lowZoom == TerramapConfig.lowZoomLevel) return;
		this.unloadAll();
		this.lowZoom = Math.min(3, TerramapConfig.lowZoomLevel); // We hard-code that here because we really don't want that to go above 3, 4 would already be 341 tiles
		for(int zoom=0; zoom<=this.lowZoom; zoom++) {
			int size = WebMercatorUtils.getDimensionsInTile(zoom);
			for(int x=0; x<size; x++) for(int y=0; y<size; y++) {
				try {
					this.getTile(zoom, x, y).getTexture();
				} catch (IOException | InterruptedException | ExecutionException e) {
					TerramapMod.logger.error("Failed to load a low level texture for map: ", this.id + "-" + this.provider + "v" + this.version + " at " + " " + zoom + "/" + x + "/" + y);
					TerramapMod.logger.catching(e);
				}
			}
		}
		this.baseLoad = this.tileList.size();
	}

	/**
	 * Loads a tile and registers it as last used. Doesn't load its texture.
	 * 
	 * @param tile - the tile to load
	 */
	protected void loadTile(WebTile tile) {
		this.tileList.add(Math.min(this.baseLoad, this.tileList.size()), tile);
		this.tileMap.put(tile.getPosition(), tile);
		this.unloadToMaxLoad();
	}

	/**
	 * Gets a specific tile from this map.
	 * Registers it as last used
	 * 
	 * @param zoom - the zoom level of the tile
	 * @param x - x coordinate of the tile
	 * @param y - y coordinate of the tile
	 * @return a {@link WebTile}
	 */
	public WebTile getTile(int zoom, int x, int y) {
		UnmutableTilePosition pos = new UnmutableTilePosition(zoom, x, y);
		WebTile tile = this.tileMap.get(pos);
		if(tile != null) {
			this.needTile(tile);
			return tile;
		}
		String pat = this.urlPatterns[(zoom + x + y) % this.urlPatterns.length];
		tile = new WebTile(pat, pos);
		this.loadTile(tile);
		return tile;
	}

	/**
	 * Get the tile at a specific position
	 * 
	 * @param zoom - the zoom level to consider
	 * @param x - x coordinate on the map, in pixels
	 * @param y - y coordinate on the map, in pixels
	 * @return the {@link WebTile} that contains that position at that zoom level
	 */
	public WebTile getTileAt(int zoom, long x, long y) {
		int tileX = WebMercatorUtils.getTileXAt(x);
		int tileY = WebMercatorUtils.getTileYAt(y);
		return this.getTile(zoom, tileX, tileY);
	}

	/**
	 * Unloads the given tile:
	 * unload it's texture from the GPU and stop any pending http request to get that texture, and forgets about it.
	 * 
	 * @param tile - the tile to unload
	 */
	public void unloadTile(WebTile tile) {
		tile.unloadTexture();
		tile = this.tileMap.remove(tile.getPosition());
		if(tile != null) {
			tile.unloadTexture();
			this.tileList.remove(tile);
		}
	}

	/**
	 * The size of this map in tiles at a given zoom level
	 * 
	 * @param zoomLevel
	 * @return 2^zoom
	 */
	public long getSizeInTiles(int zoomLevel){
		return WebMercatorUtils.getDimensionsInTile(zoomLevel);
	}

	/**
	 * @return The number of tiles currently loaded
	 */
	public int getLoadedCount() {
		return this.tileList.size();
	}

	private void needTile(WebTile tile) {
		if(tile.getZoom() <= this.lowZoom) return; // Those should stay where they are
		if(this.tileList.contains(tile)) {
			this.tileList.remove(tile);
		}
		if(this.tileList.size() >= this.baseLoad) {
			this.tileList.add(this.baseLoad, tile);
		} else {
			this.tileList.add(tile);
		}
		this.tileMap.put(tile.getPosition(), tile);
	}
	
	/**
	 * 
	 * @return the maximum number of tiles to keep loaded
	 */
	public int getMaxLoad() {
		return this.maxLoaded;
	}

	/**
	 * Set the maximum number of tiles to keep loaded
	 * 
	 * @param maxLoad
	 */
	public void setMaxLoad(int maxLoad) {
		this.maxLoaded = maxLoad;
	}

	/**
	 * Unloads tiles until we are at the max number of loaded tiles
	 */
	public void unloadToMaxLoad() {
		while(this.tileList.size() > this.maxLoaded) {
			WebTile toUnload = this.tileList.removeLast();
			this.tileMap.remove(toUnload.getPosition());
			this.unloadTile(toUnload);
		}
	}

	/**
	 * Unloads all tiles, after this operation, this map will be as it it was just instantiated.
	 */
	public void unloadAll() {
		int i = this.maxLoaded;
		this.maxLoaded = 0;
		this.unloadToMaxLoad();
		this.maxLoaded = i;
		this.baseLoad = 0;
	}

	/**
	 * 
	 * @return the minimum zoom level that map supports, that's usually 0
	 */
	public int getMinZoom() {
		return this.minZoom;
	}

	/**
	 * @return the maximum zoom level this map supports
	 */
	public int getMaxZoom() {
		return this.maxZoom;
	}

	/**
	 * @return the String id of this map
	 */
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
	public String getComment() {
		return this.comment;
	}

	/**
	 * 
	 * @return this mapt's provider
	 */
	public TiledMapProvider getProvider() {
		return this.provider;
	}

	/**
	 * 
	 * @return the version of this map's provider
	 */
	public long getProviderVersion() {
		return this.version;
	}

	/**
	 * @return an integer used to calculate the order in which map styles should be displayed. Higher means first.
	 */
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
	 * @return the number of tiles at low zoom levels this map is keeping loaded
	 */
	public int getBaseLoad() {
		return this.baseLoad;
	}

	@Override
	public int compareTo(TiledMap o) {
		if(o == null) return 1;
		if(this.displayPriority > o.displayPriority) return 1;
		else if(this.displayPriority == o.displayPriority) return 0;
		else return -1;
	}

	/**
	 * @return Whether or not this map can be used on the minimap
	 */
	public boolean isAllowedOnMinimap() {
		return this.allowOnMinimap;
	}

}