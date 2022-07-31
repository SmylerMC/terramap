package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

public class MapLayerLibrary {

    public static final MapLayerLibrary INSTANCE = new MapLayerLibrary();

    static {
        INSTANCE.registerLayer("input", InputLayer::new);
        INSTANCE.registerLayer("raster", RasterMapLayer::new);
    }

    private final Map<String, Supplier<MapLayer>> layers = new HashMap<>();

    public void registerLayer(String layerId, Supplier<MapLayer> constructor) {
        this.layers.put(layerId, constructor);
    }

    public Supplier<MapLayer> getLayerConstructor(String layerId) {
        return this.layers.get(layerId);
    }

}
