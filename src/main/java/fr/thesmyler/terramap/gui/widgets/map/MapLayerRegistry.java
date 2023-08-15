package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.terramap.gui.widgets.map.layer.McChunksLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RenderingDeltaPreviewLayer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

public final class MapLayerRegistry {

    public static final MapLayerRegistry INSTANCE = new MapLayerRegistry();

    public static final String RASTER_LAYER_ID = "terramap:raster";
    public static final String CHUNKS_LAYER_ID = "terramap:chunks";
    public static final String OFFSET_PREVIEW = "terramap:offset_preview";


    static {
        INSTANCE.newRegistration(RASTER_LAYER_ID, RasterMapLayer::new).register();
        INSTANCE.newRegistration(CHUNKS_LAYER_ID, McChunksLayer::new).register();
        INSTANCE.newRegistration(OFFSET_PREVIEW, RenderingDeltaPreviewLayer::new).register();
    }

    private final Map<String, LayerRegistration<? extends MapLayer>> layers = new HashMap<>();

    public <T extends MapLayer> LayerRegistrationBuilder<T> newRegistration(String id, Supplier<T> layerFactory) {
        return new LayerRegistrationBuilder<>(id, layerFactory);
    }

    public LayerRegistration<? extends MapLayer> getRegistration(String id){
        return this.layers.get(id);
    }

    public static final class LayerRegistration<T extends MapLayer> {

        final String id;
        final Supplier<T> constructor;

        public LayerRegistration(String id, Supplier<T> constructor) {
            this.id = id;
            this.constructor = constructor;
        }

    }

    public final class LayerRegistrationBuilder<T extends MapLayer> {
        private final String id;
        private final Supplier<T> constructor;

        private LayerRegistrationBuilder(String id, Supplier<T> constructor) {
            requireNonNull(id, "layer type ID cannot be null");
            requireNonNull(constructor, "layer supplier cannot be null");
            this.id = id;
            this.constructor = constructor;
        }

        public void register() {
            MapLayerRegistry.this.layers.put(this.id, new LayerRegistration<>(
                    this.id,
                    this.constructor
            ));
        }

    }

}
