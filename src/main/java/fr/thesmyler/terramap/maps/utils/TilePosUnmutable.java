package fr.thesmyler.terramap.maps.utils;

public class TilePosUnmutable extends TilePos {

	public TilePosUnmutable(int zoom, int x, int y) {
		super(zoom, x, y);
	}
	
	public TilePosUnmutable(TilePos pos) {
		super(pos);
	}

	@Override
	public TilePos getCopy() {
		return new TilePosUnmutable(this);
	}

	@Override
	public TilePosMutable getMutable() {
		return new TilePosMutable(this);
	}

	@Override
	public TilePosUnmutable getUnmutable() {
		return this;
	}

}
