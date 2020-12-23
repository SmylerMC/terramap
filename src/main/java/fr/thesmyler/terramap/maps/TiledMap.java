package fr.thesmyler.terramap.maps;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.minecraft.util.text.ITextComponent;

//TODO Never unload bellow a certain zoom level
//TODO Set rate limit per host
public class TiledMap implements Comparable<TiledMap> {

	private String urlPattern;
	private LinkedList<WebTile> tileList; // Uses for ordered access when unloading
	private Map<TileCoordinates, WebTile> tileMap; // Used for unordered access
	private int maxLoaded;
	private int maxZoom = 19;
	private int minZoom = 0;
	private int displayPriority = 0;
	private boolean allowOnMinimap = true;

	private String id;
	private TiledMapProvider provider;
	private Map<String, String> names; // A map of language key => name
	private Map<String, String> copyrightJsons;
	private long version;
	private String comment;
	private static final ITextComponent FALLBACK_COPYRIGHT = ITextComponent.Serializer.jsonToComponent("{\"text\":\"The text component for this copyright notice was malformatted!\",\"color\":\"dark_red\"}");


	public TiledMap(String urlPattern, int minZoom, int maxZoom, int maxLoaded, String id, Map<String, String> names, Map<String, String> copyright, int displayPriority, boolean allowOnMinimap, TiledMapProvider provider, long version, String comment) {
		this.urlPattern = urlPattern;
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
	}

	protected void loadTile(WebTile tile) {
		this.tileList.add(0, tile);
		this.tileMap.put(new TileCoordinates(tile), tile);
		this.unloadToMaxLoad();
	}

	public WebTile getTile(int zoom, int x, int y) {
		WebTile tile = this.tileMap.get(new TileCoordinates(x, y, zoom));
		if(tile != null) {
			this.needTile(tile);
			return tile;
		}
		tile = new WebTile(this.urlPattern, zoom, x, y);
		this.loadTile(tile);
		return tile;
	}

	public WebTile getTileAt(int zoom, long x, long y) {
		int tileX = WebMercatorUtils.getTileXAt(x);
		int tileY = WebMercatorUtils.getTileYAt(y);
		return this.getTile(zoom, tileX, tileY);
	}

	public void unloadTile(WebTile tile) {
		tile.unloadTexture();
		//TODO Remove tile from list
	}


	public long getSizeInTiles(int zoomLevel){
		return WebMercatorUtils.getDimensionsInTile(zoomLevel);
	}

	public long getSizeInPixels(int zoomLevel){
		return WebMercatorUtils.getMapDimensionInPixel(zoomLevel);
	}

	/**
	 * @return The number of tiles currently loaded
	 */
	public int getLoadedCount() {
		return this.tileList.size();
	}

	public void needTile(WebTile tile) {
		if(this.tileList.contains(tile)) {
			this.tileList.remove(tile);
		}
		this.tileList.add(0, tile);
		this.tileMap.put(new TileCoordinates(tile), tile);
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
		while(this.tileList.size() > this.maxLoaded) {
			WebTile toUnload = this.tileList.removeLast();
			this.tileMap.remove(new TileCoordinates(toUnload));
			this.unloadTile(toUnload);
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
	
	private static class TileCoordinates {
		 int x, y, z;
		 TileCoordinates(int x, int y, int z) {
			 this.x = x;
			 this.y = y;
			 this.z = z;
		 }
		 TileCoordinates(WebTile tile) {
			 this(tile.getX(), tile.getY(), tile.getZoom());
		 }
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + x;
			result = prime * result + y;
			result = prime * result + z;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			TileCoordinates other = (TileCoordinates) obj;
			if (x != other.x)
				return false;
			if (y != other.y)
				return false;
			if (z != other.z)
				return false;
			return true;
		}
		 
		 
	}

}