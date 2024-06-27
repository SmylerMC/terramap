package fr.thesmyler.terramap.maps.raster;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

import net.smyler.terramap.Terramap;
import net.smyler.terramap.util.geo.TilePos;
import net.smyler.terramap.util.geo.TilePosImmutable;
import net.smyler.terramap.util.geo.WebMercatorBounds;
import net.smyler.terramap.util.geo.WebMercatorUtil;
import net.smyler.terramap.util.geo.TilePos.InvalidTilePositionException;

/**
 * This class is in charge of keeping track of and loading the tiles used for rendering a specific map.
 * When tiles need to be unloaded, priority is given to keep those that were used recently loaded.
 * Tiles with zoom levels lower than a certain value will also never be unloaded, so that backup textures are always kept.
 * It also holds the metadata defined in the map config.
 * 
 * @author Smyler
 *
 * @param <T> The type of tile handled by this map
 */
public abstract class CachingRasterTiledMap<T extends RasterTile> implements RasterTiledMap {

    public static final int CACHE_SIZE = 512;
    public static final int LOW_ZOOM = 2;

    private final LinkedList<T> tileList; // Uses for ordered access when unloading
    private final Map<TilePosImmutable, T> tileMap; // Used for unordered access
    private boolean useLowZoom = true;
    private int baseLoad = 0;

    public CachingRasterTiledMap() {
        this.tileList = new LinkedList<>();
        this.tileMap = new HashMap<>();
    }

    @Override
    public T getTile(TilePos position) {
        TilePosImmutable pos = position.getImmutable();
        WebMercatorBounds b = this.getBounds(pos.getZoom());
        if(b != null && !b.contains(pos)) throw new InvalidTilePositionException();
        T tile = this.tileMap.get(pos);
        if(tile != null) {
            this.needTile(tile);
            return tile;
        }
        tile = this.createNewTile(pos);
        this.loadTile(tile);
        return tile;
    }

    protected abstract T createNewTile(TilePosImmutable pos);

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
     * unload its texture from the GPU and stop any pending http request to get that texture, and forgets about it.
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
        if(tile.getPosition().getZoom() <= LOW_ZOOM) {
            return; // Those should stay where they are
        }
        this.tileList.remove(tile);
        if(this.tileList.size() >= this.baseLoad) {
            this.tileList.add(this.baseLoad, tile);
        } else {
            this.tileList.add(tile);
        }
        this.tileMap.put(tile.getPosition(), tile);
    }

    private void prepareLowTiles() {
        this.unloadAll();
        if(this.useLowZoom) {
            for(int zoom=this.getMinZoom(); zoom<=Math.min(this.getMaxZoom(), LOW_ZOOM); zoom++) {
                int size = WebMercatorUtil.getDimensionsInTile(zoom);
                for(int x=0; x<size; x++) for(int y=0; y<size; y++) {
                    try {
                        this.getTile(zoom, x, y).getTexture();
                    } catch (Throwable e) {
                        Terramap.instance().logger().error(
                                "Failed to load a low level texture for map: {}-{}v{} at {}/{}/{}",
                                this.getId(), this.getProvider(), this.getProviderVersion(), zoom, x, y
                        );
                        Terramap.instance().logger().catching(e);
                    }
                }
            }
        }
        this.baseLoad = this.tileList.size();
    }

    @Override
    public void setup() {
        this.unloadToMaxLoad();
        if(this.baseLoad <= 0){
            this.prepareLowTiles();
        }
    }

    /**
     * Unloads all tiles, after this operation, this map will be as if it was just instantiated.
     */
    public void unloadAll() {
        while(!this.tileList.isEmpty()) {
            T toUnload = this.tileList.removeLast();
            this.tileMap.remove(toUnload.getPosition());
            this.unloadTile(toUnload);
        }
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
        while(this.tileList.size() > CACHE_SIZE) {
            T toUnload = this.tileList.removeLast();
            this.tileMap.remove(toUnload.getPosition());
            this.unloadTile(toUnload);
        }
    }

    public void setUsesLowZoom(boolean yesNo) {
        this.useLowZoom = yesNo;
    }

    @Override
    public int compareTo(RasterTiledMap other) {
        return Integer.compare(this.getDisplayPriority(), other.getDisplayPriority());
    }

}
