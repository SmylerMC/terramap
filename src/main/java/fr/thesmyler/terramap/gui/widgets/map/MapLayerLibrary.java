package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.terramap.gui.widgets.map.layer.McChunksLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RenderingDeltaPreviewLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapLayerLibrary {

    public static final MapLayerLibrary INSTANCE = new MapLayerLibrary();

    public static final String RASTER_LAYER_ID = "terramap:raster";
    public static final String CHUNKS_LAYER_ID = "terramap:chunks";
    public static final String OFFSET_PREVIEW = "terramap:offset_preview";


    static {
        INSTANCE.registerLayer(RASTER_LAYER_ID, RasterMapLayer::new);
        INSTANCE.registerLayer(CHUNKS_LAYER_ID, McChunksLayer::new);
        INSTANCE.registerLayer(OFFSET_PREVIEW, RenderingDeltaPreviewLayer::new);
    }

    private final Map<String, Supplier<MapLayer>> layers = new HashMap<>();

    public void registerLayer(String layerId, Supplier<MapLayer> constructor) {
        this.layers.put(layerId, constructor);
    }

    public Supplier<MapLayer> getLayerConstructor(String layerId) {
        return this.layers.get(layerId);
    }

}
