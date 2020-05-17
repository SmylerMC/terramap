/* IRL World Minecraft Mod
    Copyright (C) 2017  Smyler

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
	along with this program. If not, see <http://www.gnu.org/licenses/>.

	The author can be contacted at smyler@mail.com
*/
package fr.smyler.terramap.maps;

import java.io.IOException;
import java.util.LinkedList;

import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.maps.tiles.RasterWebTile;
import fr.smyler.terramap.maps.utils.WebMercatorUtils;

/**
 * @author SmylerMC
 *
 * //TODO class comment
 */
public class TiledMap<T extends RasterWebTile> {

	protected int zoomLevel;
	protected TileFactory<T> factory;
	protected LinkedList<T> tiles;
	protected int maxLoaded;
	
	protected boolean smartLoadEnable = false;
	
	//TODO javadoc
	public TiledMap(TileFactory<T> fact, int zoomLevel, int maxLoaded) {
		this.factory = fact;
		this.zoomLevel = zoomLevel;
		this.tiles = new LinkedList<T>();
		this.maxLoaded = maxLoaded;
	}
	
	//TODO javadoc
	public TiledMap(TileFactory<T> fact) {
		this(fact, 0, 20);
	}
	
	
	public int getZoomLevel() {
		return this.zoomLevel;
	}
	
	
	public void setZoomLevel(int zoom) {
		if(zoom < 0) {
			throw new IllegalArgumentException("The zoom level can't be negative!");
		}
		this.zoomLevel = zoom;
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
						TerramapMod.cacheManager.cacheAsync(this.getTile(tile.getX()+x, tile.getY()+y));
					} catch (RasterWebTile.InvalidTileCoordinatesException e) {}
				}
			}
			this.enableSmartLoading();
		}
		this.unloadToMaxLoad();
	}
	
	public T getTile(long x, long y, int zoom) {
		//TODO A better way to store our tiles, this will be very slow with large maps
		for(T tile: this.tiles)
			if(tile.getX() == x && tile.getY() == y && tile.getZoom() == zoom) {
				this.needTile(tile);
				return tile;
			}
		T tile = this.factory.getInstance(x, y, zoom);
		this.loadTile(tile);
		return tile;
	}
	
	public T getTile(long x, long y) {
		return this.getTile(x, y, this.zoomLevel);
	}
	
	
	public T getTileAt(long x, long y, int zoom) {
		long tileX = WebMercatorUtils.getTileXAt(x);
		long tileY = WebMercatorUtils.getTileYAt(y);
		return this.getTile(tileX, tileY, zoom);
	}
	
	//TODO JAvadoc
	public T getTileAt(long x, long y) {
		return this.getTileAt(x, y, this.zoomLevel);
	}
	
	public void unloadTile(T tile) {
		tile.unloadTexture();
	}
	
	
	public long getSizeInTiles(){
		return WebMercatorUtils.getDimensionsInTile(this.zoomLevel);
	}
	
	public long getSizeInPixels(){
		return WebMercatorUtils.getMapDimensionInPixel(this.zoomLevel);
	}
	
	public int[] getPixel(long x, long y) throws IOException {
		long tileX = WebMercatorUtils.getTileXAt(x);
		long tileY = WebMercatorUtils.getTileYAt(y);
		int tX = (int)(x % 256), tY = (int)(y % 256);
		return this.getTile(tileX, tileY).getPixel(tX, tY);
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