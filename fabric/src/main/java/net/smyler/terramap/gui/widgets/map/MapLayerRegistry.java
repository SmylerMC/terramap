package net.smyler.terramap.gui.widgets.map;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.smyler.terramap.gui.widgets.map.layer.OnlineRasterMapLayer;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;

public final class MapLayerRegistry {

    public static final MapLayerRegistry INSTANCE = new MapLayerRegistry();

    public static final String RASTER_LAYER_ID = "terramap:raster";


    static {
        INSTANCE.newRegistration(RASTER_LAYER_ID, OnlineRasterMapLayer::new)
                .showInNewLayerMenu("terramap.terramapscreen.newlayer.raster")
                .register();
    }

    private final Map<String, LayerRegistration<? extends MapLayer>> layers = new HashMap<>();
    private final Map<String, LayerRegistration<? extends MapLayer>> readOnlyLayers = unmodifiableMap(this.layers);

    public <T extends MapLayer> LayerRegistrationBuilder<T> newRegistration(String id, Supplier<T> layerFactory) {
        return new LayerRegistrationBuilder<>(id, layerFactory);
    }

    public LayerRegistration<? extends MapLayer> getRegistrations(String id){
        return this.layers.get(id);
    }

    public Map<String, LayerRegistration<? extends MapLayer>> getRegistrations() {
        return this.readOnlyLayers;
    }

    public static final class LayerRegistration<T extends MapLayer> {

        final String id;
        final Supplier<T> constructor;
        final String newLayerMenuTranslationKey;

        public LayerRegistration(String id, String newLayerMenuTranslationKey, Supplier<T> constructor) {
            this.id = id;
            this.newLayerMenuTranslationKey = newLayerMenuTranslationKey;
            this.constructor = constructor;
        }

        public String getId() {
            return this.id;
        }

        public Supplier<T> getConstructor() {
            return this.constructor;
        }

        public String getNewLayerMenuTranslationKey() {
            return this.newLayerMenuTranslationKey;
        }

        public boolean showsOnNewLayerMenu() {
            return this.newLayerMenuTranslationKey != null;
        }

    }

    public final class LayerRegistrationBuilder<T extends MapLayer> {
        private final String id;
        private String translationKey;
        private final Supplier<T> constructor;

        private LayerRegistrationBuilder(String id, Supplier<T> constructor) {
            requireNonNull(id, "layer type ID cannot be null");
            requireNonNull(constructor, "layer supplier cannot be null");
            this.id = id;
            this.constructor = constructor;
        }

        public LayerRegistrationBuilder<T> showInNewLayerMenu(String translationKey) {
            this.translationKey = translationKey;
            return this;
        }

        public void register() {
            MapLayerRegistry.this.layers.put(this.id, new LayerRegistration<>(
                    this.id,
                    this.translationKey,
                    this.constructor
            ));
        }

    }

}
