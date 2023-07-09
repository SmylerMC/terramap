package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.terramap.gui.widgets.map.layer.McChunksLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapLayerLibrary {

    public static final MapLayerLibrary INSTANCE = new MapLayerLibrary();

    public static final String INPUT_LAYER_ID = "terramap:layer";
    public static final String RASTER_LAYER_ID = "terramap:raster";
    public static final String CHUNKS_LAYER_ID = "terramap:chunks";


    static {
        INSTANCE.registerLayer(INPUT_LAYER_ID, InputLayer::new);
        INSTANCE.registerLayer(RASTER_LAYER_ID, RasterMapLayer::new);
        INSTANCE.registerLayer(CHUNKS_LAYER_ID, McChunksLayer::new);
    }

    private final Map<String, Supplier<MapLayer>> layers = new HashMap<>();

    public void registerLayer(String layerId, Supplier<MapLayer> constructor) {
        this.layers.put(layerId, constructor);
    }

    public Supplier<MapLayer> getLayerConstructor(String layerId) {
        return this.layers.get(layerId);
    }

}
