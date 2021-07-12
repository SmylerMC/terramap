package fr.thesmyler.terramap.gui.screens;

import java.util.Arrays;
import java.util.List;

import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.WarningWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.sliders.FloatSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import net.minecraft.util.text.TextComponentString;

class OverlayList extends FlexibleWidgetContainer {
    
    private MapWidget map;
    
    private boolean cancelNextInit = false;

    public OverlayList(float x, float y, int z, float width, MapWidget map) {
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
        this.cancellAllScheduled();
        List<MapLayer> layers = Arrays.asList(this.map.getOverlayLayers());
        layers.sort((l1, l2) -> Integer.compare(l2.getZ(), l1.getZ()));
        float ly = 5f;

        for(MapLayer layer: layers) {
            OverlayEntry entry = new GenericOverlayEntry(ly, layer);
            this.addWidget(entry);
            ly += entry.getHeight() + 5f;
        }
        BackgroundOverlayEntry bgEntry = new BackgroundOverlayEntry(ly, map.getBackgroundLayer());
        this.addWidget(bgEntry);
        this.cancelNextInit = true;
        this.setHeight(ly + bgEntry.getHeight() + 5);
    }
    
    private void swapLayers(MapLayer layer1, MapLayer layer2) {
        this.map.removeOverlayLayer(layer1);
        this.map.removeOverlayLayer(layer2);
        int swap = layer1.getZ();
        layer1.setZ(layer2.getZ());
        layer2.setZ(swap);
        this.map.addOverlayLayer(layer1);
        this.map.addOverlayLayer(layer2);
        OverlayList.this.scheduleBeforeNextUpdate(OverlayList.this::init);
    }
    
    private abstract class OverlayEntry extends FlexibleWidgetContainer {

        public OverlayEntry(float y, float height) {
            super(5, y, 0, OverlayList.this.getWidth() - 10, height);
            this.setDoScissor(false);
        }

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, WidgetContainer parent) {
            RenderUtil.drawRectWithContour(x, y, x + this.getWidth(), y + this.getHeight(), Color.LIGHT_OVERLAY , 1f, Color.DARK_GRAY);
            super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
        }
        
    }
    
    private class BackgroundOverlayEntry extends OverlayEntry {

        public BackgroundOverlayEntry(float y, RasterMapLayer layer) {
            super(y, 20);
            TextWidget name = new TextWidget(5, 7, 0, new TextComponentString(layer.name()), TextAlignment.RIGHT, this.getFont());
            TextWidget type = new TextWidget(5, 23, 0, new TextComponentString("Raster background"), TextAlignment.RIGHT, this.getFont());
            type.setBaseColor(Color.MEDIUM_GRAY);
            this.addWidget(name);
            this.addWidget(type);
            this.addWidget(new TexturedButtonWidget(this.getWidth() - 18, 3, 0, IncludedTexturedButtons.WRENCH));
            if(layer.getRenderingOffset().normSquared()  != 0d) {
                WarningWidget warn = new WarningWidget(this.getWidth() - 37, 3, 0);
                warn.setTooltip("A rendering offset is set for this layer");
                this.addWidget(warn);
            }
            this.setHeight(37);
        }
        
    }
    
    private class GenericOverlayEntry extends OverlayEntry {
        
        MapLayer layer;

        public GenericOverlayEntry(float y, MapLayer layer) {
            super(y, 20);
            this.layer = layer;
            TextWidget name = new TextWidget(5, 7, 0, new TextComponentString(layer.name()), TextAlignment.RIGHT, this.getFont());
            TextWidget type = new TextWidget(5, 23, 0, new TextComponentString(layer.description()), TextAlignment.RIGHT, this.getFont());
            type.setBaseColor(Color.MEDIUM_GRAY);
            this.addWidget(name);
            this.addWidget(type);
            TexturedButtonWidget remove = new TexturedButtonWidget(this.getWidth() - 38, 3, 0, IncludedTexturedButtons.TRASH, this::remove);
            this.addWidget(new TexturedButtonWidget(this.getWidth() - 18, 3, 0, IncludedTexturedButtons.UP, this::moveUp));
            this.addWidget(new TexturedButtonWidget(this.getWidth() - 18, 19, 0, IncludedTexturedButtons.DOWN, this::moveDown));
            this.addWidget(remove.setEnabled(layer.isUserOverlay()));
            this.addWidget(new TexturedButtonWidget(this.getWidth() - 54, 3, 0, IncludedTexturedButtons.WRENCH));
            this.addWidget(new TexturedButtonWidget(this.getWidth() - 70, 3, 0, IncludedTexturedButtons.OFFSET, () -> {
                OverlayList.this.scheduleBeforeNextUpdate(() -> new LayerRenderingOffsetPopup(OverlayList.this.map.getBackgroundLayer(), layer).show());
            }));
            if(layer.getRenderingOffset().normSquared()  != 0d) {
                WarningWidget warn = new WarningWidget(this.getWidth() - 86, 3, 0);
                warn.setTooltip("A rendering offset is set for this layer");
                this.addWidget(warn);
            }
            FloatSliderWidget alphaSlider = new FloatSliderWidget(this.getWidth() - 86f, 19f, -1, 63f, 15f, 0d, 1d, layer.getAlpha());
            alphaSlider.setDisplayPrefix("Alpha: ");
            alphaSlider.setOnChange(d -> this.layer.setAlpha(d.floatValue()));
            this.addWidget(alphaSlider);
            this.setHeight(37);
        }
        
        public void remove() {
            OverlayList.this.scheduleBeforeNextUpdate(() -> {
                OverlayList.this.map.removeOverlayLayer(this.layer);
                OverlayList.this.init();
            });
        }
        
        
        void moveUp() {
            List<MapLayer> layers = Arrays.asList(OverlayList.this.map.getOverlayLayers());
            layers.sort((l1, l2) -> Integer.compare(l2.getZ(), l1.getZ()));
            int i = layers.indexOf(this.layer);
            if(i > 0) {
                MapLayer other = layers.get(i - 1);
                OverlayList.this.swapLayers(this.layer, other);
            }
        }
        
        void moveDown() {
            List<MapLayer> layers = Arrays.asList(OverlayList.this.map.getOverlayLayers());
            layers.sort((l1, l2) -> Integer.compare(l2.getZ(), l1.getZ()));
            int i = layers.indexOf(this.layer);
            if(i < layers.size() - 1) {
                MapLayer other = layers.get(i + 1);
                OverlayList.this.swapLayers(this.layer, other);
            }
        }
        
    }

}
