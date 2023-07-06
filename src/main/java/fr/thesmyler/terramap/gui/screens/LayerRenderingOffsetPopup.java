package fr.thesmyler.terramap.gui.screens;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.screen.PopupScreen;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.gui.widgets.map.MapController;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RenderingDeltaPreviewLayer;
import fr.thesmyler.terramap.util.geo.GeoPointMutable;
import fr.thesmyler.terramap.util.geo.GeoServices;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.Vec2d;
import fr.thesmyler.terramap.util.math.Vec2dImmutable;
import fr.thesmyler.terramap.util.math.Vec2dMutable;
import net.minecraft.util.text.TextComponentTranslation;

public class LayerRenderingOffsetPopup extends PopupScreen {
    
    private static final DecimalFormat OFFSET_FORMATTER = new DecimalFormat();
    
    static {
        OFFSET_FORMATTER.setMaximumFractionDigits(2);
        OFFSET_FORMATTER.setGroupingUsed(false);
        OFFSET_FORMATTER.setDecimalFormatSymbols(new DecimalFormatSymbols(Locale.US));
    }
    
    private final MapLayer layer;
    private final TextFieldWidget xInput;
    private final TextFieldWidget yInput;
    private final TextWidget zoomText;
    private final MapWidget map;
    private final MapController mapController;
    private final TextButtonWidget doneButton;

    // Used in calculations
    private final Vec2dMutable updateMapLayerCenter = new Vec2dMutable();
    private final Vec2dMutable updateMapDelta = new Vec2dMutable();
    private final Vec2dMutable offsetCalculator = new Vec2dMutable();
    private final GeoPointMutable layerCenterLocation = new GeoPointMutable();

    public LayerRenderingOffsetPopup(RasterMapLayer background, MapLayer layer) {
        super(300f, 150f);
        this.layer = layer;
        float interline = 20;
        float margin = 8;
        float spacing = 7;
        WidgetContainer content = this.getContent();
        TextWidget title = new TextWidget(
                content.getWidth() / 2, margin, 0,
                new TextComponentTranslation("terramap.popup.renderoffset.title"),
                TextAlignment.CENTER,
                SmyLibGui.getDefaultFont());
        content.addWidget(title);
        TextWidget xText = new TextWidget(
                margin, title.getY() + title.getHeight() + interline, 0,
                new TextComponentTranslation("terramap.popup.renderoffset.x"),
                TextAlignment.RIGHT,
                SmyLibGui.getDefaultFont());
        content.addWidget(xText);
        TextWidget yText = new TextWidget(
                margin, xText.getY() + xText.getHeight() + interline, 0,
                new TextComponentTranslation("terramap.popup.renderoffset.y"),
                TextAlignment.RIGHT,
                SmyLibGui.getDefaultFont());
        content.addWidget(yText);
        float inputsX = Math.max(xText.getX() + xText.getWidth(), yText.getX() + yText.getWidth()) + spacing;
        this.xInput = new TextFieldWidget(inputsX, xText.getY() - 6, 0, 70);
        content.addWidget(this.xInput);
        this.yInput = new TextFieldWidget(inputsX, yText.getY() - 6, 0, this.xInput.getWidth());
        content.addWidget(this.yInput);
        this.zoomText = new TextWidget(
                xText.getX(), this.yInput.getY() + this.yInput.getHeight() + 6, 0,
                TextAlignment.RIGHT,
                content.getFont());
        content.addWidget(this.zoomText);
        float mapSize = Math.min(
                content.getWidth() - (inputsX + this.yInput.getWidth() + spacing * 2) - margin,
                content.getHeight() - this.xInput.getY() - margin);
        this.map = new MapWidget(content.getWidth() - mapSize - margin, this.xInput.getY(), 0,
                mapSize, mapSize,
                background.getTiledMap(),
                MapContext.PREVIEW,
                layer.getMap().getTileScaling());
        this.mapController = this.map.getController();
        this.mapController.moveLocationToCenter(layer.getMap().getController().getCenterLocation(), false);
        this.map.getBackgroundLayer().setRenderingOffset(layer.getRenderingOffset());
        RenderingDeltaPreviewLayer previewLayer = new RenderingDeltaPreviewLayer(this.map, layer.getMap().getController().getCenterLocation());
        this.map.addOverlayLayer(previewLayer);
        MapLayer layerCopy = layer.copy(this.map);
        layerCopy.setAlpha(0.5f);
        layerCopy.setZ(-3);
        layerCopy.setRenderingOffset(Vec2dImmutable.NULL);
        this.map.addOverlayLayer(layerCopy);
        this.map.setScaleVisibility(false);
        this.map.setCopyrightVisibility(false);
        this.map.setRightClickMenuEnabled(false);
        this.map.setFocusedZoom(false);
        this.map.setAllowsQuickTp(false);
        this.mapController.setRotation(layer.getMap().getController().getRotation(), false);
        this.mapController.setZoom(layer.getMap().getController().getZoom(), false);
        this.mapController.moveLocationToCenter(layer.getMap().getController().getCenterLocation(), false);
        this.resetMap();
        content.addWidget(this.map);
        content.scheduleBeforeEachUpdate(() -> this.map.setTileScaling(layer.getMap().getTileScaling()));
        content.scheduleAfterEachUpdate(this::updateMap);
        float midWidth = content.getWidth() - this.map.getWidth() - margin*3;
        TextButtonWidget resetButton = new TextButtonWidget(
                margin, this.yInput.getY() + this.xInput.getHeight() + interline, 0,
                (midWidth - spacing) / 2,
                SmyLibGui.getTranslator().format("terramap.popup.renderoffset.reset"), this::resetMap);
        content.addWidget(resetButton);
        TextButtonWidget set0Button = new TextButtonWidget(
                resetButton.getX() + resetButton.getWidth() + spacing, resetButton.getY(), 0,
                resetButton.getWidth(),
                SmyLibGui.getTranslator().format("terramap.popup.renderoffset.set0"), this::setOffsetToZero);
        content.addWidget(set0Button);
        TextButtonWidget cancelButton = new TextButtonWidget(
                resetButton.getX(), resetButton.getY() + resetButton.getHeight() + 4, 0,
                resetButton.getWidth(),
                SmyLibGui.getTranslator().format("terramap.popup.renderoffset.cancel"), this::close);
        content.addWidget(cancelButton);
        this.doneButton = new TextButtonWidget(set0Button.getX(), cancelButton.getY(), 0,
                set0Button.getWidth(),
                SmyLibGui.getTranslator().format("terramap.popup.renderoffset.done"), this::applyAndClose);
        content.addWidget(this.doneButton);
        this.yInput.setOnChangeCallback(this::onTextFieldsChange);
        this.xInput.setOnChangeCallback(this::onTextFieldsChange);
    }
    
    public LayerRenderingOffsetPopup(RasterMapLayer background) {
        this((RasterMapLayer)background.copy(background.getMap()), background);
    }
    
    private void onTextFieldsChange(String unused) {
        IWidget focused = this.getContent().getFocusedWidget();
        if (focused != this.xInput && focused != this.yInput) return; // We don't want an infinite loop !
        boolean okX = false, okY = false;
        double dX = 0, dY = 0;
        double mapZoom = this.map.getController().getZoom();
        try {
            dX = Double.parseDouble(this.xInput.getText());
            if(!Double.isFinite(dX)) throw new NumberFormatException();
            okX = true;
            this.xInput.setEnabledTextColor(Color.WHITE);
            this.xInput.setFocusedTextColor(Color.WHITE);
        } catch(NumberFormatException e) {
            this.xInput.setEnabledTextColor(Color.RED);
            this.xInput.setFocusedTextColor(Color.RED);
        }
        try {
            dY = Double.parseDouble(this.yInput.getText());
            if(!Double.isFinite(dY) || dY > 256*Math.pow(2d, mapZoom))
                throw new NumberFormatException();
            okY = true;
            this.yInput.setEnabledTextColor(Color.WHITE);
            this.yInput.setFocusedTextColor(Color.WHITE);
        } catch(NumberFormatException e) {
            this.yInput.setEnabledTextColor(Color.RED);
            this.yInput.setFocusedTextColor(Color.RED);
        }
        if(okX && okY) this.setRenderedOffset(new Vec2dImmutable(dX, dY).downscale(256 * Math.pow(2d, mapZoom)));
        this.doneButton.setEnabled(okY && okX);
    }
    
    private void resetMap() {
        this.setRenderedOffset(this.layer.getRenderingOffset());
    }
    
    private void setRenderedOffset(Vec2d<?> offset) {
        WebMercatorUtil.fromGeo(this.offsetCalculator, this.layer.getMap().getController().getCenterLocation(), 0d);
        this.offsetCalculator.downscale(256d).add(offset).scale(256d);
        WebMercatorUtil.toGeo(this.layerCenterLocation, this.offsetCalculator, 0d);
        this.map.getController().moveLocationToCenter(this.layerCenterLocation, false);
    }
    
    private void updateMap() {
        IWidget focused = this.getContent().getFocusedWidget();
        double zoom = this.mapController.getZoom();
        WebMercatorUtil.fromGeo(this.updateMapLayerCenter, this.layer.getMap().getController().getCenterLocation(), zoom);
        WebMercatorUtil.fromGeo(this.updateMapDelta, this.mapController.getCenterLocation(), zoom);
        this.updateMapDelta.subtract(this.updateMapLayerCenter);
        if(focused != this.yInput && focused != this.xInput) {
            this.xInput.setText(OFFSET_FORMATTER.format(this.updateMapDelta.x()));
            this.yInput.setText(OFFSET_FORMATTER.format(this.updateMapDelta.y()));
        }
        this.map.getBackgroundLayer().setPixelRenderingOffset(this.updateMapDelta.scale(-1d));
        if (!this.map.getBackgroundLayer().hasRenderingOffset()) this.map.getBackgroundLayer().setPixelRenderingOffset(Vec2dImmutable.UNIT_X);
        this.zoomText.setText(new TextComponentTranslation("terramap.popup.renderoffset.zoom", GeoServices.formatZoomLevelForDisplay(zoom)));
    }
    
    private void setOffsetToZero() {
        this.mapController.moveLocationToCenter(this.layer.getMap().getController().getCenterLocation(), true);
    }
    
    private void applyAndClose() {
        double xOffset = Double.parseDouble(this.xInput.getText());
        double yOffset = Double.parseDouble(this.yInput.getText());
        this.layer.setRenderingOffset(new Vec2dImmutable(xOffset, yOffset).downscale(256 * Math.pow(2d, this.mapController.getZoom())));
        this.close();
    }
    
    public MapLayer getLayer() {
        return this.layer;
    }

}
