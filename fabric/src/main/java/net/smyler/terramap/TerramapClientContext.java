package net.smyler.terramap;

import net.smyler.terramap.tilesets.raster.RasterTileSet;
import net.smyler.terramap.tilesets.raster.RasterTileSetManager;

import java.util.HashMap;
import java.util.Map;

public class TerramapClientContext {

    private static final TerramapClientContext INSTANCE = new TerramapClientContext();

    public Map<String, RasterTileSet> getRasterTileSets() {
        Map<String, RasterTileSet> sets = new HashMap<>();
        RasterTileSetManager manager = Terramap.instance().rasterTileSetManager();
        sets.putAll(manager.getBaseMaps());
        sets.putAll(manager.getUserMaps());
        return sets;
    }

    public static TerramapClientContext getContext() {
        return INSTANCE;
    }

}
