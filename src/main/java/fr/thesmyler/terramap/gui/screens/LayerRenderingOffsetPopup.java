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
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RenderingDeltaPreviewLayer;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.GeoServices;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.Vec2d;
import net.minecraft.client.resources.I18n;
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
    private final TextButtonWidget doneButton;

    public LayerRenderingOffsetPopup(RasterMapLayer background, MapLayer layer) {
        super(300f, 150f);
        this.layer = layer;
        float interline = 20;
        float margin = 8;
        float spacing = 7;
        WidgetContainer content = this.getContent();
        TextWidget title = new TextWidget(content.getWidth() / 2, margin, 0, new TextComponentTranslation("terramap.popup.renderoffset.title"), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        content.addWidget(title);
        TextWidget xText = new TextWidget(margin, title.getY() + title.getHeight() + interline, 0, new TextComponentTranslation("terramap.popup.renderoffset.x"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        content.addWidget(xText);
        TextWidget yText = new TextWidget(margin, xText.getY() + xText.getHeight() + interline, 0, new TextComponentTranslation("terramap.popup.renderoffset.y"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        content.addWidget(yText);
        float inputsX = Math.max(xText.getX() + xText.getWidth(), yText.getX() + yText.getWidth()) + spacing;
        this.xInput = new TextFieldWidget(inputsX, xText.getY() - 6, 0, 70);
        content.addWidget(this.xInput);
        this.yInput = new TextFieldWidget(inputsX, yText.getY() - 6, 0, this.xInput.getWidth());
        content.addWidget(this.yInput);
        this.zoomText = new TextWidget(xText.getX(), this.yInput.getY() + this.yInput.getHeight() + 6, 0, TextAlignment.RIGHT, content.getFont());
        content.addWidget(this.zoomText);
        float mapSize = Math.min(
                content.getWidth() - (inputsX + this.yInput.getWidth() + spacing * 2) - margin,
                content.getHeight() - this.xInput.getY() - margin);
        this.map = new MapWidget(content.getWidth() - mapSize - margin, this.xInput.getY(), 0, mapSize, mapSize, background.getMap(), MapContext.PREVIEW, layer.getTileScaling());
        this.map.setCenterLocation(layer.getCenterLocation());
        this.map.getBackgroundLayer().setRenderingOffset(layer.getRenderingOffset());
        background = this.map.getBackgroundLayer();
        RenderingDeltaPreviewLayer previewLayer = new RenderingDeltaPreviewLayer(layer.getTileScaling(), layer.getCenterLocation());
        this.map.addOverlayLayer(previewLayer);
        MapLayer layerCopy = layer.copy();
        layerCopy.setAlpha(0.5f);
        layerCopy.setZ(-3);
        layerCopy.setRenderingOffset(Vec2d.NULL);
        this.map.addOverlayLayer(layerCopy);
        this.map.setScaleVisibility(false);
        this.map.setCopyrightVisibility(false);
        this.map.setRightClickMenuEnabled(false);
        this.map.setFocusedZoom(false);
        this.map.setAllowsQuickTp(false);
        this.map.setRotation(layer.getRotation());
        this.map.setZoom(layer.getZoom());
        this.resetMap();
        content.addWidget(this.map);
        content.scheduleBeforeEachUpdate(() -> this.map.setTileScaling(layer.getTileScaling()));
        content.scheduleAfterEachUpdate(this::updateMap);
        float midWidth = content.getWidth() - this.map.getWidth() - margin*3;
        TextButtonWidget resetButton = new TextButtonWidget(margin, this.yInput.getY() + this.xInput.getHeight() + interline, 0, (midWidth - spacing) / 2, I18n.format("terramap.popup.renderoffset.reset"), this::resetMap);
        content.addWidget(resetButton);
        TextButtonWidget set0Button = new TextButtonWidget(resetButton.getX() + resetButton.getWidth() + spacing, resetButton.getY(), 0, resetButton.getWidth(), I18n.format("terramap.popup.renderoffset.set0"), this::setOffsetToZero);
        content.addWidget(set0Button);
        TextButtonWidget cancelButton = new TextButtonWidget(resetButton.getX(), resetButton.getY() + resetButton.getHeight() + 4, 0, resetButton.getWidth(), I18n.format("terramap.popup.renderoffset.cancel"), () -> {
            this.close();
        });
        content.addWidget(cancelButton);
        this.doneButton = new TextButtonWidget(set0Button.getX(), cancelButton.getY(), 0, set0Button.getWidth(), I18n.format("terramap.popup.renderoffset.done"), this::applyAndClose);
        content.addWidget(this.doneButton);
        this.yInput.setOnChangeCallback(this::onTextFieldsChange);
        this.xInput.setOnChangeCallback(this::onTextFieldsChange);
    }
    
    public LayerRenderingOffsetPopup(RasterMapLayer background) {
        this((RasterMapLayer)background.copy(), background);
    }
    
    private void onTextFieldsChange(String unused) {
        boolean okX = false, okY = false;
        double dX = 0, dY = 0;
        double mapZoom = this.map.getZoom();
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
        if(okX && okY) this.setRenderedOffset(new Vec2d(dX, dY).downscale(256 * Math.pow(2d, mapZoom)));
        this.doneButton.setEnabled(okY && okX);
    }
    
    private void resetMap() {
        this.setRenderedOffset(this.layer.getRenderingOffset());
    }
    
    private void setRenderedOffset(Vec2d offset) {
        GeoPoint layerCenterLocation = this.layer.getCenterLocation();
        Vec2d layerCenterOnMap = WebMercatorUtil.fromGeo(layerCenterLocation, 0d).downscale(256d);
        Vec2d newCenter = layerCenterOnMap.add(offset);
        GeoPoint newGeoCenter = WebMercatorUtil.toGeo(newCenter.scale(256d), 0d);
        this.map.setCenterLocation(newGeoCenter);
    }
    
    private void updateMap() {
        IWidget focused = this.getContent().getFocusedWidget();
        if(focused == this.yInput || focused == this.xInput) return;
        double zoom = this.map.getZoom();
        Vec2d layerCenter = WebMercatorUtil.fromGeo(this.layer.getCenterLocation(), zoom);
        Vec2d previewCenter = WebMercatorUtil.fromGeo(this.map.getCenterLocation(), zoom);
        Vec2d delta = previewCenter.substract(layerCenter);
        this.xInput.setText(OFFSET_FORMATTER.format(delta.x));
        this.yInput.setText(OFFSET_FORMATTER.format(delta.y));
        this.map.getBackgroundLayer().setPixelRenderingOffset(delta.scale(-1d));
        this.zoomText.setText(new TextComponentTranslation("terramap.popup.renderoffset.zoom", GeoServices.formatZoomLevelForDisplay(zoom)));
    }
    
    private void setOffsetToZero() {
        this.map.setCenterLocation(this.layer.getCenterLocation());
    }
    
    private void applyAndClose() {
        double xOffset = Double.parseDouble(this.xInput.getText());
        double yOffset = Double.parseDouble(this.yInput.getText());
        this.layer.setRenderingOffset(new Vec2d(xOffset, yOffset).downscale(256 * Math.pow(2d, this.map.getZoom())));
        this.close();
    }
    
    public MapLayer getLayer() {
        return this.layer;
    }

}
