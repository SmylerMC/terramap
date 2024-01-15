package fr.thesmyler.terramap.util.geo;

public class TilePosImmutable extends TilePos {

    public TilePosImmutable(int zoom, int x, int y) {
        super(zoom, x, y);
    }

    public TilePosImmutable(TilePos pos) {
        super(pos);
    }

    @Override
    public TilePos getCopy() {
        return new TilePosImmutable(this);
    }

    @Override
    public TilePosMutable getMutable() {
        return new TilePosMutable(this);
    }

    @Override
    public TilePosImmutable getImmutable() {
        return this;
    }

}
