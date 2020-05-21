package fr.smyler.terramap.maps;

import java.io.IOException;
import java.util.LinkedList;

import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.maps.utils.WebMercatorUtils;


public class TiledMap<T extends RasterWebTile> {

	protected TileFactory<T> factory;
	protected LinkedList<T> tiles;
	protected int maxLoaded;
	protected int maxZoom = 19;
	protected int minZoom = 0;
	
	protected String name;
	protected String copyright;
	
	protected boolean smartLoadEnable = false;
	
	public TiledMap(TileFactory<T> fact, int minZoom, int maxZoom, int maxLoaded, String name, String copyright) {
		this.factory = fact;
		this.tiles = new LinkedList<T>();
		this.maxLoaded = maxLoaded;
		this.maxZoom = maxZoom;
		this.minZoom = minZoom;
		this.name = name;
		this.copyright = copyright;
	}
	
	public TiledMap(TileFactory<T> fact) {
		this(fact, 0, 19, 120, "", "");
	}	
	
	protected void loadTile(T tile) {
		this.tiles.add(0, tile);
		if(this.isSmartLoadingEnabled()) {
			TerramapMod.cacheManager.cacheAsync(tile);
			this.disableSmartLoading();
			for(int x=-1; x<=1; x++) {
				for(int y=-1; y<=1; y++) {
					if(x == 0 && y == 0) continue;
					try {
						TerramapMod.cacheManager.cacheAsync(this.getTile(tile.getZoom(), tile.getX()+x, tile.getY()+y));
					} catch (RasterWebTile.InvalidTileCoordinatesException e) {}
				}
			}
			this.enableSmartLoading();
		}
		this.unloadToMaxLoad();
	}
	
	public T getTile(int zoom, long x, long y) {
		for(T tile: this.tiles)
			if(tile.getX() == x && tile.getY() == y && tile.getZoom() == zoom) {
				this.needTile(tile);
				return tile;
			}
		T tile = this.factory.getInstance(zoom, x, y);
		this.loadTile(tile);
		return tile;
	}
	
	
	public T getTileAt(int zoom, long x, long y) {
		long tileX = WebMercatorUtils.getTileXAt(x);
		long tileY = WebMercatorUtils.getTileYAt(y);
		return this.getTile(zoom, tileX, tileY);
	}
	
	public void unloadTile(T tile) {
		tile.unloadTexture();
	}
	
	
	public long getSizeInTiles(int zoomLevel){
		return WebMercatorUtils.getDimensionsInTile(zoomLevel);
	}
	
	public long getSizeInPixels(int zoomLevel){
		return WebMercatorUtils.getMapDimensionInPixel(zoomLevel);
	}
	
	public int[] getPixel(int zoom, long x, long y) throws IOException {
		long tileX = WebMercatorUtils.getTileXAt(x);
		long tileY = WebMercatorUtils.getTileYAt(y);
		int tX = (int)(x % 256), tY = (int)(y % 256);
		return this.getTile(zoom, tileX, tileY).getPixel(tX, tY);
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
	
	public void needTile(T tile) {
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
	
}