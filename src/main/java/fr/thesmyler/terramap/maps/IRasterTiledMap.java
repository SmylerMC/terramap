package fr.thesmyler.terramap.maps;

import fr.thesmyler.terramap.util.TilePos;
import fr.thesmyler.terramap.util.TilePosUnmutable;
import fr.thesmyler.terramap.util.WebMercatorBounds;

public interface IRasterTiledMap extends Comparable<IRasterTiledMap> {

    /**
     * Initializes the map.
     */
    public void setup();

    /**
     * Gets a specific tile from this map.
     * 
     * @param zoom
     * @param x
     * @param y
     * @return
     */
    public IRasterTile getTile(TilePos position);

    public default IRasterTile getTile(int zoom, int x, int y) {
        return this.getTile(new TilePosUnmutable(zoom, x, y));
    }

    /**
     * @return the minimum zoom level that map supports, that's usually 0
     */
    public int getMinZoom();

    /**
     * @return the maximum zoom level this map supports
     */
    public int getMaxZoom();

    /**
     * @return the String id of this map
     */
    public String getId();

    /**
     * Gets a name for this map, translated in the appropriate language,
     * or English if it isn't available.
     * 
     * @param localeKey - the language key to get the copyright for
     * @return the name of this map, translated to the appropriate language.
     */
    public String getLocalizedName(String localeKey);

    /**
     * @return the comment from the map provider metadata
     */
    public String getComment();

    /**
     * @return this map's provider
     */
    public TiledMapProvider getProvider();

    /**
     * @return the version of this map's provider
     */
    public long getProviderVersion();

    /**
     * @return an integer used to calculate the order in which map styles should be displayed. Higher means first.
     */
    public int getDisplayPriority();

    /**
     * @return whether or not the map is allowed on the minimap
     */
    public boolean isAllowedOnMinimap();

    /**
     * @return true if this map should be considered a debug map
     */
    public boolean isDebug();
    
    /**
     * @param zoom the zoom level to consider
     * 
     * @return the bounds of this map style, or null if it has none (uses the default web mercator bounds
     */
    public default WebMercatorBounds getBounds(int zoom) {
        return null;
    }


}
