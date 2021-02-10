package fr.thesmyler.terramap.maps;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.maps.utils.TilePos;
import fr.thesmyler.terramap.maps.utils.TilePosUnmutable;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;

/**
 * This class is in charge of keeping track of and loading the tiles used for rendering a specific map.
 * When tiles need to be unloaded, priority is given to keep those that were used recently loaded.
 * Tiles with zoom levels lower than a certain value will also never be unloaded, so that backup textures are always kept.
 * It also holds the metadata defined in the map config.
 * 
 * @author SmylerMC
 *
 * @param <T> The type of tile handled by this map
 */
//FIXME There is a problem when changing low zoom level
public abstract class CachingRasterTiledMap<T extends IRasterTile> implements IRasterTiledMap {

	private final LinkedList<T> tileList; // Uses for ordered access when unloading
	private final Map<TilePosUnmutable, T> tileMap; // Used for unordered access
	private int lowZoom = 0;
	private boolean useLowZoom = true;
	private int maxLoaded;
	private int baseLoad = 0;

	public CachingRasterTiledMap() {
		this.tileList = new LinkedList<>();
		this.tileMap = new HashMap<>();
		this.maxLoaded = TerramapConfig.CLIENT.maxTileLoad;
	}

	@Override
	public T getTile(TilePos position) {
		TilePosUnmutable pos = position.getUnmutable();
		T tile = this.tileMap.get(pos);
		if(tile != null) {
			this.needTile(tile);
			return tile;
		}
		tile = this.createNewTile(pos);
		this.loadTile(tile);
		return tile;
	}

	protected abstract T createNewTile(TilePosUnmutable pos);

	/**
	 * Loads a tile and registers it as last used. Doesn't load its texture.
	 * 
	 * @param tile - the tile to load
	 */
	protected void loadTile(T tile) {
		this.tileList.add(Math.min(this.baseLoad, this.tileList.size()), tile);
		this.tileMap.put(tile.getPosition(), tile);
		this.unloadToMaxLoad();
	}

	/**
	 * Unloads the given tile:
	 * unload it's texture from the GPU and stop any pending http request to get that texture, and forgets about it.
	 * 
	 * @param tile - the tile to unload
	 */
	public void unloadTile(T tile) {
		tile.unloadTexture();
		tile = this.tileMap.remove(tile.getPosition());
		if(tile != null) {
			tile.unloadTexture();
			this.tileList.remove(tile);
		}
	}

	/**
	 * @return The number of tiles currently loaded
	 */
	public int getLoadedCount() {
		return this.tileList.size();
	}

	private void needTile(T tile) {
		if(tile.getPosition().getZoom() <= this.lowZoom) return; // Those should stay where they are
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

	private void prepareLowTiles() {
		this.unloadAll();
		this.lowZoom = Math.min(3, TerramapConfig.CLIENT.lowZoomLevel); // We hard-code that here because we really don't want that to go above 3, 4 would already be 341 tiles
		if(this.useLowZoom) {
			for(int zoom=this.getMinZoom(); zoom<=Math.min(this.getMaxZoom(), this.lowZoom); zoom++) {
				int size = WebMercatorUtils.getDimensionsInTile(zoom);
				for(int x=0; x<size; x++) for(int y=0; y<size; y++) {
					try {
						this.getTile(zoom, x, y).getTexture();
					} catch (Throwable e) {
						TerramapMod.logger.error("Failed to load a low level texture for map: ", this.getId() + "-" + this.getProvider() + "v" + this.getProviderVersion() + " at " + " " + zoom + "/" + x + "/" + y);
						TerramapMod.logger.catching(e);
					}
				}
			}
		}
		this.baseLoad = this.tileList.size();
	}

	@Override
	public void setup() {
		if(this.maxLoaded != TerramapConfig.CLIENT.maxTileLoad) {
			this.maxLoaded = TerramapConfig.CLIENT.maxTileLoad;
			this.unloadToMaxLoad();
		}
		if(baseLoad <= 0 || this.lowZoom != TerramapConfig.CLIENT.lowZoomLevel) this.prepareLowTiles();
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
	 * @return the number of tiles at low zoom levels this map is keeping loaded
	 */
	public int getBaseLoad() {
		return this.baseLoad;
	}

	/**
	 * Unloads tiles until we are at the max number of loaded tiles
	 */
	public void unloadToMaxLoad() {
		while(this.tileList.size() > this.maxLoaded) {
			T toUnload = this.tileList.removeLast();
			this.tileMap.remove(toUnload.getPosition());
			this.unloadTile(toUnload);
		}
	}

	public boolean getUsesLowZoom() {
		return this.useLowZoom;
	}

	public void setUseLowZoom(boolean yesNo) {
		this.useLowZoom = yesNo;
	}
	
	@Override
	public int compareTo(IRasterTiledMap other) {
		return Integer.compare(this.getDisplayPriority(), other.getDisplayPriority());
	}
	
}
