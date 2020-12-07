package fr.thesmyler.terramap.maps;

import java.util.LinkedList;
import java.util.Map;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.minecraft.util.text.ITextComponent;


public class TiledMap implements Comparable<TiledMap> {

	protected String urlPattern;
	protected LinkedList<WebTile> tiles;
	protected int maxLoaded;
	protected int maxZoom = 19;
	protected int minZoom = 0;
	protected int displayPriority = 0;
	protected boolean allowOnMinimap = true;

	protected String id;
	protected TiledMapProvider provider;
	protected Map<String, String> names; // A map of language key => name
	protected Map<String, String> copyrightJsons;
	protected long version;
	protected String comment;
	private static final ITextComponent FALLBACK_COPYRIGHT = ITextComponent.Serializer.jsonToComponent("{\"text\":\"The text component for this copyright notice was malformatted!\",\"color\":\"dark_red\"}");

	protected boolean smartLoadEnable = false;

	public TiledMap(String urlPattern, int minZoom, int maxZoom, int maxLoaded, String id, Map<String, String> names, Map<String, String> copyright, int displayPriority, boolean allowOnMinimap, TiledMapProvider provider, long version, String comment) {
		this.urlPattern = urlPattern;
		this.tiles = new LinkedList<WebTile>();
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
	}

	protected void loadTile(WebTile tile) {
		this.tiles.add(0, tile);
		if(this.isSmartLoadingEnabled()) {
			TerramapMod.cacheManager.cacheAsync(tile);
			this.disableSmartLoading();
			for(int x=-1; x<=1; x++) {
				for(int y=-1; y<=1; y++) {
					if(x == 0 && y == 0) continue;
					try {
						TerramapMod.cacheManager.cacheAsync(this.getTile(tile.getZoom(), tile.getX()+x, tile.getY()+y));
					} catch (WebTile.InvalidTileCoordinatesException e) {}
				}
			}
			this.enableSmartLoading();
		}
		this.unloadToMaxLoad();
	}

	public WebTile getTile(int zoom, long x, long y) {
		for(WebTile tile: this.tiles)
			if(tile.getX() == x && tile.getY() == y && tile.getZoom() == zoom) {
				this.needTile(tile);
				return tile;
			}
		WebTile tile = new WebTile(this.urlPattern, zoom, x, y);
		this.loadTile(tile);
		return tile;
	}


	public WebTile getTileAt(int zoom, long x, long y) {
		long tileX = WebMercatorUtils.getTileXAt(x);
		long tileY = WebMercatorUtils.getTileYAt(y);
		return this.getTile(zoom, tileX, tileY);
	}

	public void unloadTile(WebTile tile) {
		tile.unloadTexture();
	}


	public long getSizeInTiles(int zoomLevel){
		return WebMercatorUtils.getDimensionsInTile(zoomLevel);
	}

	public void enableSmartLoading() {
		this.smartLoadEnable = true;
	}

	public void disableSmartLoading() {
		this.smartLoadEnable = false;
	}

	public boolean isSmartLoadingEnabled() {
		return this.smartLoadEnable;
	}

	/**
	 * @return The number of tiles currently loaded
	 */
	public int getLoadedCount() {
		return this.tiles.size();
	}

	public void needTile(WebTile tile) {
		if(this.tiles.contains(tile)) {
			this.tiles.remove(tile);
		}
		this.tiles.add(0, tile);
	}

	public int getMaxLoad() {
		return this.maxLoaded;
	}

	public void setMaxLoad(int maxLoad) {
		this.maxLoaded = maxLoad;
	}

	/**
	 * Unloads tiles until we are at the max number of loaded tiles
	 */
	public void unloadToMaxLoad() {
		while(this.tiles.size() > this.maxLoaded) {
			this.unloadTile(this.tiles.removeLast());
		}
	}

	public void unloadAll() {
		int i = this.maxLoaded;
		this.maxLoaded = 0;
		this.unloadToMaxLoad();
		this.maxLoaded = i;
	}

	public int getMinZoom() {
		return this.minZoom;
	}

	public int getMaxZoom() {
		return this.maxZoom;
	}

	public String getId() {
		return this.id;
	}

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
	
	public Map<String, String> getUnlocalizedCopyrights() {
		return this.copyrightJsons;
	}

	public String getLocalizedName(String localeKey) {
		String result = this.names.getOrDefault(localeKey, this.names.get("en_us"));
		if(result != null) {
			return result;
		} else {
			return this.id;
		}
	}
	
	public Map<String, String> getUnlocalizedNames() {
		return this.names;
	}
	
	public String getUrlPattern() {
		return this.urlPattern;
	}
	
	public String getComment() {
		return this.comment;
	}
	
	public TiledMapProvider getProvider() {
		return this.provider;
	}
	
	public long getProviderVersion() {
		return this.version;
	}
	
	public int getDisplayPriority() {
		return this.displayPriority;
	}

	@Override
	public int compareTo(TiledMap o) {
		if(o == null) return 1;
		if(this.displayPriority > o.displayPriority) return 1;
		else if(this.displayPriority == o.displayPriority) return 0;
		else return -1;
	}
	
	public boolean isAllowedOnMinimap() {
		return this.allowOnMinimap;
	}

}