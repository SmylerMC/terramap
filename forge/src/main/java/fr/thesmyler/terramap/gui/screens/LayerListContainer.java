package fr.thesmyler.terramap.gui.screens;

import java.util.ArrayList;
import java.util.List;

import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Animation;
import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.FloatSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.gui.screens.config.LayerConfigurationPopup;
import fr.thesmyler.terramap.gui.widgets.map.InputLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

import javax.annotation.Nullable;

import static fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons.*;
import static java.util.Comparator.comparing;
import static net.smyler.smylib.SmyLib.getGameClient;

//TODO localize
class LayerListContainer extends FlexibleWidgetContainer {

    private final MapWidget map;
    private static final long SWAP_ANIMATION_DURATION = 100;

    private boolean cancelNextInit = false;

    public LayerListContainer(float x, float y, int z, float width, MapWidget map) {
        super(x, y, z, width, 10);
        this.map = map;
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
        layers.sort(comparing(MapLayer::getZ).reversed());
        float ly = 5f;

        LayerEntry previous = null;
        for(MapLayer layer: layers) {
            if (layer instanceof InputLayer) continue; // We don't want the user to have to deal with the input layer
            LayerEntry entry;
            if (layer.getZ() == Integer.MIN_VALUE && layer instanceof RasterMapLayer) {
                entry = new BackgroundLayerEntry(ly, (RasterMapLayer) layer);
            } else {
                entry = new GenericLayerEntry(ly, layer);
            }
            if (previous != null) {
                entry.setPrevious(previous);
                previous.setNext(entry);
            }
            this.addWidget(entry);
            ly += entry.getHeight() + 5f;
            previous = entry;
        }
        this.cancelNextInit = true;
        this.setHeight(ly); // That would init again
        super.init();
    }

    private void swapLayers(LayerEntry entry1, LayerEntry entry2) {
        entry1.animateSwap(entry2.getY());
        entry2.animateSwap(entry1.getY());
        LayerListContainer.this.scheduleBeforeUpdate(() -> {
            int layer2z = entry1.layer.getZ();
            this.map.setLayerZ(entry1.layer, entry2.layer.getZ());
            this.map.setLayerZ(entry2.layer, layer2z);
        }, SWAP_ANIMATION_DURATION / 2);
        LayerListContainer.this.scheduleBeforeUpdate(LayerListContainer.this::init, SWAP_ANIMATION_DURATION);
    }

    private abstract class LayerEntry extends FlexibleWidgetContainer {

        final MapLayer layer;
        private float startY;
        private float destinationY;
        private Animation animation;

        protected LayerEntry previous;
        protected LayerEntry next;

        public LayerEntry(MapLayer layer, float y, float height) {
            super(5, y, 0, LayerListContainer.this.getWidth() - 10, height);
            this.layer = layer;
            this.startY = this.destinationY = y;
        }

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, WidgetContainer parent) {
            RenderUtil.drawRectWithContour(x, y, x + this.getWidth(), y + this.getHeight(), Color.LIGHT_OVERLAY , 1f, Color.DARK_GRAY);
            super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
        }

        void animateSwap(float destinationY) {
            this.startY = this.getY();
            this.destinationY = destinationY;
            this.animation = new Animation(SWAP_ANIMATION_DURATION);
            this.animation.start(Animation.AnimationState.ENTER);
        }

        @Override
        public void onUpdate(float mouseX, float mouseY, @Nullable WidgetContainer parent) {
            if (this.animation != null) {
                this.animation.update();
                this.setY(this.animation.blend(this.destinationY, this.startY));
            }
            super.onUpdate(mouseX, mouseY, parent);
        }

        protected void setPrevious(LayerEntry entry) {
            this.previous = entry;
        }

        protected void setNext(LayerEntry entry) {
            this.next = entry;
        }

    }
    
    private class BackgroundLayerEntry extends LayerEntry {

        public BackgroundLayerEntry(float y, RasterMapLayer layer) {
            super(layer, y, 20);
            TextWidget name = new TextWidget(5, 7, 0, new TextComponentString(layer.name()), TextAlignment.RIGHT, this.getFont());
            TextWidget type = new TextWidget(5, 23, 0, new TextComponentTranslation("terramap.terramapscreen.layerscreen.raster_background.type"), TextAlignment.RIGHT, this.getFont());
            type.setBaseColor(Color.MEDIUM_GRAY);
            this.addWidget(name);
            this.addWidget(type);
            TexturedButtonWidget settingsButton = new TexturedButtonWidget(this.getWidth() - 18, 3, 0, WRENCH);
            if (layer.isConfigurable()) {
                settingsButton.setOnClick(() -> new LayerConfigurationPopup(layer).show());
                settingsButton.enable();
            }
            this.addWidget(settingsButton);
            TexturedButtonWidget offsetButton = new TexturedButtonWidget(this.getWidth() - 37, 3, 0,
                    layer.hasRenderingOffset() ? OFFSET_WARNING: OFFSET,
                    () -> LayerListContainer.this.scheduleBeforeNextUpdate(() -> new LayerRenderingOffsetPopup(layer).show())
            );
            offsetButton.setTooltip(getGameClient().getTranslator().format(
                    layer.hasRenderingOffset() ?
                    "terramap.terramapscreen.layerscreen.raster_background.offset":
                    "terramap.terramapscreen.layerscreen.raster_background.no_offset"
            ));
            this.addWidget(offsetButton);
            this.setHeight(37);
        }

    }
    
    private class GenericLayerEntry extends LayerEntry {
        
        final MapLayer layer;
        final FloatSliderWidget alphaSlider;

        private final TexturedButtonWidget nextButton = new TexturedButtonWidget(this.getWidth() - 18, 19, 0, DOWN, this::moveDown);
        private final TexturedButtonWidget previousButton = new TexturedButtonWidget(this.getWidth() - 18, 3, 0, UP, this::moveUp);

        public GenericLayerEntry(float y, MapLayer layer) {
            super(layer, y, 20);
            this.layer = layer;
            TextWidget name = new TextWidget(5, 7, 0, new TextComponentString(layer.name()), TextAlignment.RIGHT, this.getFont());
            TextWidget type = new TextWidget(5, 23, 0, new TextComponentString(layer.description()), TextAlignment.RIGHT, this.getFont());
            type.setBaseColor(Color.MEDIUM_GRAY);
            this.addWidget(name);
            this.addWidget(type);
            TexturedButtonWidget remove = new TexturedButtonWidget(this.getWidth() - 38, 3, 0, TRASH, this::remove);
            this.addWidget(this.previousButton);
            this.addWidget(this.nextButton);
            this.addWidget(remove.setEnabled(layer.isUserLayer()));
            TexturedButtonWidget settingsButton = new TexturedButtonWidget(this.getWidth() - 54, 3, 0, WRENCH);
            if (layer.isConfigurable()) {
                settingsButton.setOnClick(() -> new LayerConfigurationPopup(layer).show());
                settingsButton.enable();
            }
            this.addWidget(settingsButton);
            TexturedButtonWidget offsetButton = new TexturedButtonWidget(this.getWidth() - 70, 3, 0,
                layer.hasRenderingOffset() ? OFFSET_WARNING: OFFSET,
                () -> {
                    MapLayer lowestLayer = LayerListContainer.this.map.getLayers().stream().min(comparing(MapLayer::getZ)).orElse(layer);
                    LayerListContainer.this.scheduleBeforeNextUpdate(() -> new LayerRenderingOffsetPopup(lowestLayer, layer).show());
                }
            );
            offsetButton.setTooltip(getGameClient().getTranslator().format(
                    layer.hasRenderingOffset() ? "terramap.terramapscreen.layerscreen.generic.offset": "terramap.terramapscreen.layerscreen.generic.no_offset"
            ));
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
            this.alphaSlider.setDisplayPrefix(getGameClient().getTranslator().format("terramap.terramapscreen.layerscreen.generic.alpha"));
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
            if (this.previous != null) {
                this.animateSwap(this.previous.getY());
                this.previous.animateSwap(this.getY());
                LayerListContainer.this.swapLayers(this, this.previous);
            }
        }

        void moveDown() {
            if (this.next != null) {
                this.animateSwap(this.next.getY());
                this.next.animateSwap(this.getY());
                LayerListContainer.this.swapLayers(this, this.next);
            }
        }

        void toggleVisibility(boolean visibility) {
            this.alphaSlider.setEnabled(visibility);
            this.layer.setVisibility(visibility);
        }

        @Override
        protected void setPrevious(LayerEntry entry) {
            super.setPrevious(entry);
            this.previousButton.setEnabled(entry instanceof GenericLayerEntry);
        }

        @Override
        protected void setNext(LayerEntry entry) {
            super.setNext(entry);
            this.nextButton.setEnabled(entry instanceof GenericLayerEntry);
        }

    }

}
