package fr.thesmyler.terramap.maps.utils;

import com.google.common.base.Preconditions;

/**
 * Tile position that supports thread safe mutations
 * 
 * @author SmylerMC
 *
 */
public class TilePosMutable extends TilePos {

	/**
	 * Default constructor
	 * 
	 * @param zoom
	 * @param x
	 * @param y
	 */
	public TilePosMutable(int zoom, int x, int y) {
		super(zoom, x, y);
	}
	
	/**
	 * Initializes this tile position as a copy of the one given as argument
	 * 
	 * @param pos
	 */
	public TilePosMutable(TilePos pos) {
		super(pos);
	}
	
	/**
	 * Initializes this tile as all zeros
	 */
	public TilePosMutable() {
		super(0, 0, 0);
	}
	
	/**
	 * Set this position
	 * @param zoom - new zoom level between 0 and 30
	 * @param x - new x position between 0 and 2^z where z is the zoom level
	 * @param y - new y position between 0 and 2^z where z is the zoom level
	 * @throws IllegalArgumentException if the given position is not valid
	 */
	public void setPosition(int zoom, int x, int y) {
		this.setZoom(zoom);
		this.setX(x);
		this.setY(y);
	}
	
	/**
	 * Change this position, ensure it stays valid
	 * @param zoom - new zoom level between 0 and 30
	 * @param x - new x position between 0 and 2^z where z is the zoom level
	 * @param y - new y position between 0 and 2^z where z is the zoom level
	 */
	public void setPositionSafe(int zoom, int x, int y) {
		this.setZoomSafe(zoom);
		this.setXSafe(x);
		this.setYSafe(y);
	}
	
	/**
	 * Set this tile's zoom level and updates the X and Y position to reflect the change
	 * @param zoom - the zoom level to set, between 0 and 30
	 * @throws IllegalArgumentException if the given zoom level is not valid
	 */
	public void setZoom(int zoom) {
		Preconditions.checkArgument(WebMercatorUtils.isValidTilePosition(zoom, this.xPosition, this.yPosition), "Invalid zoom level " + zoom);
		synchronized(this) {
			int delta = zoom - this.zoom;
			this.zoom = zoom;
			if(delta > 0) {
				int fact = 1<<delta;
				this.xPosition *= fact;
				this.yPosition *= fact;
			} else {
				int fact = 1<<(-delta);
				this.xPosition /= fact;
				this.yPosition /= fact;
			}
		}
	}
	
	/**
	 * Set this tile's zoom level, clamping the given value between 0 and 30
	 * @param zoom - the zoom level to set, between 0 and 30
	 */
	public void setZoomSafe(int zoom) {
		synchronized(this) {
			int newZoom = Math.max(0, Math.min(WebMercatorUtils.MAX_ZOOM, zoom));
			int delta = newZoom - this.zoom;
			this.zoom = newZoom;
			if(delta > 0) {
				int fact = 1<<delta;
				this.xPosition *= fact;
				this.yPosition *= fact;
			} else {
				int fact = 1<<(-delta);
				this.xPosition /= fact;
				this.yPosition /= fact;
			}
		}
	}
	
	/**
	 * Set this tile's X position
	 * @param x - the X position to set, between 0 and 2^z, where z is the zoom level
	 * @throws IllegalArgumentException if the given X position is not in the acceptable range
	 */
	public void setX(int x) {
		Preconditions.checkArgument(WebMercatorUtils.isValidTilePosition(zoom, x, this.yPosition), "Invalid x value " + x + " for zoom level " + this.zoom);
		synchronized(this) {
			this.xPosition = x;
		}
	}
	
	/**
	 * Set this tile's X position, clamping the value between 0 and 2^z where z is the zoom level
	 * @param x - the X position to set
	 */
	public void setXSafe(int x) {
		synchronized (this) {
			this.xPosition = Math.floorMod(x, WebMercatorUtils.getDimensionsInTile(this.zoom));
		}
	}
	
	/**
	 * Set this tile's Y position
	 * @param y - the Y position to set, between 0 and 2^z, where z is the zoom level
	 * @throws IllegalAccessException if the given Y position is not in the acceptable range
	 */
	public void setY(int y) {
		Preconditions.checkArgument(WebMercatorUtils.isValidTilePosition(zoom, this.xPosition, y), "Invalid y value " + y + " for zoom level " + this.zoom);
		synchronized(this) {
			this.yPosition = y;
		}
	}
	
	/**
	 * Set this tile's Y position, clamping the value between 0 and 2^z where z is the zoom level
	 * @param y - the Y position to set
	 */
	public void setYSafe(int y) {
		synchronized (this) {
			this.yPosition = Math.max(0, Math.min(WebMercatorUtils.getDimensionsInTile(this.zoom) - 1, y));
		}
	}
	
	/**
	 * Increments this tile's zoom level
	 * @param i amount to increment
	 * @return the new zoom value
	 * @throws IllegalArgumentException if the new zoom value wouldn't be in the accepted range
	 */
	public int incrementZoom(int i) {
		this.setZoom(this.zoom + i);
		return this.zoom;
	}
	
	/**
	 * Increments this tile zoom level, clamping the value in the acceptable range
	 * @param i - the amount to increment
	 * @return the new zoom level value
	 */
	public int incrementZoomSafe(int i) {
		this.setZoomSafe(this.zoom + i);
		return this.zoom;
	}
	
	/**
	 * Increments this tile's X position
	 * @param i - the amount to increment
	 * @return the new X position value
	 * @throws IllegalArgumentException if the new X value wouldn't be valid
	 */
	public int incrementX(int i) {
		this.setX(this.xPosition + i);
		return this.xPosition;
	}
	
	/**
	 * Increments the tile's X position, clamping the value in the acceptable range
	 * @param i - the amount to increment
	 * @return the new X position
	 */
	public int incrementXSafe(int i) {
		this.setXSafe(this.xPosition + i);
		return this.xPosition;
	}
	
	/**
	 * Increments this tile's Y position
	 * @param i - the amount to increment
	 * @return the new Y position value
	 * @throws IllegalArgumentException if the new Y value wouldn't be valid
	 */
	public int incrementY(int i) {
		this.setY(this.yPosition + i);
		return this.yPosition;
	}
	
	/**
	 * Increments the tile's Y position, clamping the value in the acceptable range
	 * @param i - the amount to increment
	 * @return the new Y position
	 */
	public int incrementYSafe(int i) {
		this.setYSafe(this.yPosition + i);
		return this.yPosition;
	}

	@Override
	public TilePos getCopy() {
		return new TilePosMutable(this);
	}

	@Override
	public TilePosMutable getMutable() {
		return this;
	}

	@Override
	public TilePosUnmutable getUnmutable() {
		return new TilePosUnmutable(this);
	}

}
