package fr.thesmyler.terramap.util.geo;

import fr.thesmyler.terramap.util.Immutable;
import fr.thesmyler.terramap.util.Mutable;

/**
 * A Web-Mercator tile position.
 * Zoom levels go from 0 to 30, and x and  positions go from 0 to 2^z where z is the zoom level.
 * 
 * @author SmylerMC
 *
 */
public abstract class TilePos implements Mutable<TilePosImmutable>, Immutable<TilePosMutable> {

    protected int zoom;
    protected int xPosition;
    protected int yPosition;

    /**
     * Default TilePos constructor.
     * Implementing classes should implement a similar constructor.
     * 
     * @param zoom - the zoom level for this tile, between 0 and 30
     * @param x - the x position for this tile, between 0 and 2^z where z is the zoom level
     * @param y - the y position for this tile, between 0 and 2^z where z is the zoom level
     * 
     * @throws InvalidTilePositionException if the given argument do not meet their respective criteria
     */
    public TilePos(int zoom, int x, int y) {
        if(zoom < 0 || zoom > WebMercatorUtil.MAX_ZOOM) throw new InvalidTilePositionException("Invalid zoom level: " + zoom);
        int mapSize = WebMercatorUtil.getDimensionsInTile(zoom);
        if(x < 0 || x >= mapSize) throw new InvalidTilePositionException("Invalid coordinate: " + x + " for zoom level " + zoom);
        if(y < 0 || y >= mapSize) throw new InvalidTilePositionException("Invalid coordinate: " + y + " for zoom level " + zoom);
        this.zoom = zoom;
        this.xPosition = x;
        this.yPosition = y;
    }

    /**
     * TilePos constructor.
     * Implementing classes should implement a similar constructor.
     * 
     * @param other - another tile to copy
     */
    public TilePos(TilePos other) {
        this(other.getZoom(), other.getX(), other.getY());
    }

    /**
     * @return this tile's zoom level, between 0 and 30
     * @see #getX()
     * @see #getY()
     */
    public int getZoom() {
        return this.zoom;
    }

    /**
     * @return this tile's X position, between 0 and 2^z, where z is the zoom level
     * @see #getY()
     * @see #getZoom()
     */
    public int getX() {
        return this.xPosition;
    }

    /**
     * @return this tile's Y position, between 0 and 2^z, where z is the zoom level
     * @see #getX()
     * @see #getZoom()
     */
    public int getY() {
        return this.yPosition;
    }

    /**
     * @return a mutable deep copy of this tile position
     * @see #getImmutableCopy()
     * @see #getCopy()
     */
    public TilePosMutable getMutableCopy() {
        return new TilePosMutable(this.zoom, this.xPosition, this.yPosition);
    }

    /**
     * @return an immutable deep copy of this tile position
     * @see #getMutableCopy()
     * @see #getCopy()
     */
    public TilePosImmutable getImmutableCopy() {
        return new TilePosImmutable(this.zoom, this.xPosition, this.yPosition);
    }

    /**
     * @return a deep copy of this tile position
     * @see #getMutableCopy()
     * @see #getImmutableCopy()
     */
    public abstract TilePos getCopy();

    /**
     * @return this if this is mutable, or a mutable copy if it isn't
     */
    public abstract TilePosMutable getMutable();

    /**
     * @return this if this is immutable, or an immutable copy if it isn't
     */
    public abstract TilePosImmutable getImmutable();

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj instanceof TilePos) {
            TilePos o = (TilePos) obj;
            return this.zoom == o.zoom && this.xPosition == o.xPosition && this.yPosition == o.yPosition;
        }
        return false;
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

    public static class InvalidTilePositionException extends RuntimeException{

        public InvalidTilePositionException(String msg) {
            super(msg);
        }

        public InvalidTilePositionException() {
            super();
        }

    }

}
