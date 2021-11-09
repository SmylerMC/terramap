package fr.thesmyler.terramap.maps.raster;

import fr.thesmyler.terramap.util.geo.TilePos;
import fr.thesmyler.terramap.util.geo.TilePosImmutable;
import fr.thesmyler.terramap.util.geo.WebMercatorBounds;

public interface IRasterTiledMap extends Comparable<IRasterTiledMap> {

    /**
     * Initializes the map.
     */
    void setup();

    /**
     * Gets a specific tile from this map.
     *
     * @paraam position - the position of the tile
     *
     * @return
     */
    IRasterTile getTile(TilePos position);

    @Deprecated
    default IRasterTile getTile(int zoom, int x, int y) {
        return this.getTile(new TilePosImmutable(zoom, x, y));
    }

    /**
     * @return the minimum zoom level that map supports, that's usually 0
     */
    int getMinZoom();

    /**
     * @return the maximum zoom level this map supports
     */
    int getMaxZoom();

    /**
     * @return the String id of this map
     */
    String getId();

    /**
     * Gets a name for this map, translated in the appropriate language,
     * or English if it isn't available.
     * 
     * @param localeKey - the language key to get the copyright for
     * @return the name of this map, translated to the appropriate language.
     */
    String getLocalizedName(String localeKey);

    /**
     * @return the comment from the map provider metadata
     */
    String getComment();

    /**
     * @return this map's provider
     */
    TiledMapProvider getProvider();

    /**
     * @return the version of this map's provider
     */
    long getProviderVersion();

    /**
     * @return an integer used to calculate the order in which map styles should be displayed. Higher means first.
     */
    int getDisplayPriority();

    /**
     * @return whether or not the map is allowed on the minimap
     */
    boolean isAllowedOnMinimap();

    /**
     * @return true if this map should be considered a debug map
     */
    boolean isDebug();
    
    /**
     * @param zoom the zoom level to consider
     * 
     * @return the bounds of this map style, or null if it has none (uses the default web mercator bounds
     */
    default WebMercatorBounds getBounds(int zoom) {
        return null;
    }


}
