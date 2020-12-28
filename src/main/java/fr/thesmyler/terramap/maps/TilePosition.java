package fr.thesmyler.terramap.maps;

import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;

public abstract class TilePosition {

	protected int zoom;
	protected int xPosition;
	protected int yPosition;
	
	public TilePosition(int zoom, int x, int y) {
		if(zoom < 0 || zoom > WebMercatorUtils.MAX_ZOOM) throw new InvalidTilePositionException("Invalid zoom level: " + zoom);
		int mapSize = WebMercatorUtils.getDimensionsInTile(zoom);
		if(x < 0 || x >= mapSize) throw new InvalidTilePositionException("Invalid coordinate: " + x + " for zoom level " + zoom);
		if(y < 0 || y >= mapSize) throw new InvalidTilePositionException("Invalid coordinate: " + y + " for zoom level " + zoom);
		this.zoom = zoom;
		this.xPosition = x;
		this.yPosition = y;
	}
	
	public int getZoom() {
		return this.zoom;
	}
	
	public int getX() {
		return this.xPosition;
	}
	
	public int getY() {
		return this.yPosition;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + xPosition;
		result = prime * result + yPosition;
		result = prime * result + zoom;
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if(this == obj) return true;
		if(obj == null) return false;
		if(!(obj instanceof TilePosition)) return false;
		TilePosition other = (TilePosition) obj;
		if(xPosition != other.xPosition) return false;
		if(yPosition != other.yPosition) return false;
		if(zoom != other.zoom) return false;
		return true;
	}
	

	public static class InvalidTilePositionException extends RuntimeException{
		
		public InvalidTilePositionException(String msg) {
			super(msg);
		}
		
		public InvalidTilePositionException() {
			super();
		}

	}
	
}
