package fr.thesmyler.terramap.maps;

public class MutableTilePosition extends TilePosition {

	public MutableTilePosition(int zoom, int x, int y) {
		super(zoom, x, y);
	}
	
	public void setZoom(int zoom) {
		this.zoom = zoom;
	}
	
	public void setX(int x) {
		this.xPosition = x;
	}
	
	public void setY(int y) {
		this.yPosition = y;
	}

}
