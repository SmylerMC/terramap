package fr.thesmyler.terramap.gui.screens;

import java.util.ArrayList;
import java.util.List;

import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.FloatSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.gui.widgets.map.InputLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import net.minecraft.util.text.TextComponentString;

import static fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons.*;
import static java.util.Comparator.comparing;

//TODO localize
class LayerListContainer extends FlexibleWidgetContainer {
    
    private final MapWidget map;
    
    private boolean cancelNextInit = false;

    public LayerListContainer(float x, float y, int z, float width, MapWidget map) {
        super(x, y, z, width, 10);
        this.map = map;
        this.setDoScissor(false);
    }
    
    @Override
    public void init() {
        if(this.cancelNextInit) {
            this.cancelNextInit = false;
            return;
        }
        this.removeAllWidgets();
        this.cancelAllScheduled();
        List<MapLayer> layers = new ArrayList<>(this.map.getLayers());
        layers.sort((l1, l2) -> Integer.compare(l2.getZ(), l1.getZ()));
        float ly = 5f;

        for(MapLayer layer: layers) {
            if (layer instanceof InputLayer) continue; // We don't want the user to have to deal with the input layer
            LayerEntry entry;
            if (layer.getZ() == Integer.MIN_VALUE && layer instanceof RasterMapLayer) {
                entry = new BackgroundLayerEntry(ly, (RasterMapLayer) layer);
            } else {
                entry = new GenericLayerEntry(ly, layer);
            }
            this.addWidget(entry);
            ly += entry.getHeight() + 5f;
        }
        this.cancelNextInit = true;
        this.setHeight(ly);
    }
    
    private void swapLayers(MapLayer layer1, MapLayer layer2) {
        int layer2z = layer1.getZ();
        this.map.setLayerZ(layer1, layer2.getZ());
        this.map.setLayerZ(layer2, layer2z);
        LayerListContainer.this.scheduleBeforeNextUpdate(LayerListContainer.this::init);
    }
    
    private abstract class LayerEntry extends FlexibleWidgetContainer {

        public LayerEntry(float y, float height) {
            super(5, y, 0, LayerListContainer.this.getWidth() - 10, height);
            this.setDoScissor(false);
        }

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, WidgetContainer parent) {
            RenderUtil.drawRectWithContour(x, y, x + this.getWidth(), y + this.getHeight(), Color.LIGHT_OVERLAY , 1f, Color.DARK_GRAY);
            super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
        }
        
    }
    
    private class BackgroundLayerEntry extends LayerEntry {

        public BackgroundLayerEntry(float y, RasterMapLayer layer) {
            super(y, 20);
            TextWidget name = new TextWidget(5, 7, 0, new TextComponentString(layer.name()), TextAlignment.RIGHT, this.getFont());
            TextWidget type = new TextWidget(5, 23, 0, new TextComponentString("Raster background"), TextAlignment.RIGHT, this.getFont());
            type.setBaseColor(Color.MEDIUM_GRAY);
            this.addWidget(name);
            this.addWidget(type);
            this.addWidget(new TexturedButtonWidget(this.getWidth() - 18, 3, 0, WRENCH));
            TexturedButtonWidget offsetButton = new TexturedButtonWidget(this.getWidth() - 37, 3, 0,
                    layer.hasRenderingOffset() ? OFFSET_WARNING: OFFSET,
                    () -> {
                        LayerListContainer.this.scheduleBeforeNextUpdate(() -> new LayerRenderingOffsetPopup(layer).show());
                    }
            );
            offsetButton.setTooltip(layer.hasRenderingOffset() ? "Background has a rendering offset": "Set background rendering offset");
            this.addWidget(offsetButton);
            this.setHeight(37);
        }

    }
    
    private class GenericLayerEntry extends LayerEntry {
        
        final MapLayer layer;
        final FloatSliderWidget alphaSlider;

        public GenericLayerEntry(float y, MapLayer layer) {
            super(y, 20);
            this.layer = layer;
            TextWidget name = new TextWidget(5, 7, 0, new TextComponentString(layer.name()), TextAlignment.RIGHT, this.getFont());
            TextWidget type = new TextWidget(5, 23, 0, new TextComponentString(layer.description()), TextAlignment.RIGHT, this.getFont());
            type.setBaseColor(Color.MEDIUM_GRAY);
            this.addWidget(name);
            this.addWidget(type);
            TexturedButtonWidget remove = new TexturedButtonWidget(this.getWidth() - 38, 3, 0, TRASH, this::remove);
            this.addWidget(new TexturedButtonWidget(this.getWidth() - 18, 3, 0, UP, this::moveUp));
            this.addWidget(new TexturedButtonWidget(this.getWidth() - 18, 19, 0, DOWN, this::moveDown));
            this.addWidget(remove.setEnabled(layer.isUserLayer()));
            this.addWidget(new TexturedButtonWidget(this.getWidth() - 54, 3, 0, WRENCH));
            TexturedButtonWidget offsetButton = new TexturedButtonWidget(this.getWidth() - 70, 3, 0,
                layer.hasRenderingOffset() ? OFFSET_WARNING: OFFSET,
                () -> {
                    MapLayer lowestLayer = LayerListContainer.this.map.getLayers().stream().min(comparing(MapLayer::getZ)).orElse(layer);
                    LayerListContainer.this.scheduleBeforeNextUpdate(() -> new LayerRenderingOffsetPopup(lowestLayer, layer).show());
                }
            );
            offsetButton.setTooltip(layer.hasRenderingOffset() ? "A rendering offset is set for this layer": "Set layer render offset");
            this.addWidget(offsetButton);
            this.addWidget(new ToggleButtonWidget(
                    this.getWidth() - 86f, 3f, 0, 15f, 15f,
                    100, 164, // On enabled
                    100, 179, // Off enabled
                    100, 224, // On disabled
                    100, 239, // Off disabled
                    100, 194, // On focused
                    100, 209, // Off focused
                    layer.isVisible(),
                    this::toggleVisibility
            ));

            this.alphaSlider = new FloatSliderWidget(this.getWidth() - 86f, 19f, -1, 63f, 15f, 0d, 1d, layer.getAlpha());
            this.alphaSlider.setDisplayPrefix("Alpha: ");
            this.alphaSlider.setOnChange(d -> this.layer.setAlpha(d.floatValue()));
            this.alphaSlider.setEnabled(this.layer.isVisible());
            this.addWidget(alphaSlider);
            this.setHeight(37);
        }
        
        public void remove() {
            LayerListContainer.this.scheduleBeforeNextUpdate(() -> {
                LayerListContainer.this.map.removeLayer(this.layer);
                LayerListContainer.this.init();
            });
        }
        
        
        void moveUp() {
            List<MapLayer> layers = new ArrayList<>(LayerListContainer.this.map.getLayers());
            layers.sort((l1, l2) -> Integer.compare(l2.getZ(), l1.getZ()));
            int i = layers.indexOf(this.layer);
            if(i > 0) {
                MapLayer other = layers.get(i - 1);
                if (other.getZ() == Integer.MIN_VALUE) return; // Do not move the background
                LayerListContainer.this.swapLayers(this.layer, other);
            }
        }

        void moveDown() {
            List<MapLayer> layers = new ArrayList<>(LayerListContainer.this.map.getLayers());
            layers.sort((l1, l2) -> Integer.compare(l2.getZ(), l1.getZ()));
            int i = layers.indexOf(this.layer);
            if(i < layers.size() - 1) {
                MapLayer other = layers.get(i + 1);
                if (other.getZ() == Integer.MIN_VALUE) return; // Do not move the background
                LayerListContainer.this.swapLayers(this.layer, other);
            }
        }

        void toggleVisibility(boolean visibility) {
            this.alphaSlider.setEnabled(visibility);
            this.layer.setVisibility(visibility);
        }

    }

}
