package fr.thesmyler.terramap.gui.screens;

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
import fr.thesmyler.terramap.util.Vec2d;
import fr.thesmyler.terramap.util.geo.GeoServices;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;

//FIXME This is broken as angular rendering offset are being deprecated
public class LayerRenderingOffsetPopup extends PopupScreen {
    
    private final MapLayer layer;
    private final TextFieldWidget lonInput;
    private final TextFieldWidget latInput;
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
        TextWidget latText = new TextWidget(margin, title.getY() + title.getHeight() + interline, 0, new TextComponentTranslation("terramap.popup.renderoffset.latitude"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        content.addWidget(latText);
        TextWidget lonText = new TextWidget(margin, latText.getY() + latText.getHeight() + interline, 0, new TextComponentTranslation("terramap.popup.renderoffset.longitude"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        content.addWidget(lonText);
        float inputsX = Math.max(latText.getX() + latText.getWidth(), lonText.getX() + lonText.getWidth()) + spacing;
        this.latInput = new TextFieldWidget(inputsX, latText.getY() - 6, 0, 70);
        content.addWidget(this.latInput);
        this.lonInput = new TextFieldWidget(inputsX, lonText.getY() - 6, 0, this.latInput.getWidth());
        content.addWidget(this.lonInput);
        float mapSize = Math.min(
                content.getWidth() - (inputsX + this.lonInput.getWidth() + spacing * 2) - margin,
                content.getHeight() - this.latInput.getY() - margin);
        this.map = new MapWidget(content.getWidth() - mapSize - margin, latInput.getY(), 0, mapSize, mapSize, background.getMap(), MapContext.PREVIEW, layer.getTileScaling());
        this.map.setCenterLocation(layer.getCenterLocation());
//        this.map.setCenterPosition(layer.getCenterLongitude() + layer.getRenderDeltaLongitude(), layer.getCenterLatitude() + layer.getRenderDeltaLatitude());
        background = this.map.getBackgroundLayer();
        background.setRenderDeltaLongitude(background.getRenderDeltaLongitude());
        background.setRenderDeltaLatitude(background.getRenderDeltaLatitude());
        RenderingDeltaPreviewLayer previewLayer = new RenderingDeltaPreviewLayer(layer.getTileScaling(), layer.getCenterLocation());
        this.map.addOverlayLayer(previewLayer);
        MapLayer layerCopy = layer.copy();
        layerCopy.setAlpha(0.5f);
        layerCopy.setZ(-3);
        this.map.addOverlayLayer(layerCopy);
        this.map.setScaleVisibility(false);
        this.map.setCopyrightVisibility(false);
        this.map.setRightClickMenuEnabled(false);
        this.map.setFocusedZoom(false);
        this.map.setAllowsQuickTp(false);
        this.map.setRotation(layer.getRotation());
        this.map.setZoom(layer.getZoom());
        content.addWidget(this.map);
        content.scheduleAtUpdate(() -> this.map.setTileScaling(layer.getTileScaling()));
        content.scheduleAtUpdate(() -> {
            IWidget focused = content.getFocusedWidget();
            if(focused == this.latInput || focused == this.lonInput) return;
//            Vec2d layerCenter = new Vec2d(layer.getCenterLongitude(), layer.getCenterLatitude());
//            Vec2d previewCenter = new Vec2d(this.map.getCenterLongitude(), this.map.getCenterLatitude());
//            Vec2d delta = previewCenter.add(layerCenter.scale(-1));
            Vec2d delta = Vec2d.NULL;
            this.lonInput.setText(GeoServices.formatGeoCoordForDisplay(delta.x));
            this.latInput.setText(GeoServices.formatGeoCoordForDisplay(delta.y));
            this.map.getBackgroundLayer().setRenderDeltaLongitude(-delta.x);
            this.map.getBackgroundLayer().setRenderDeltaLatitude(-delta.y);
            
        });
        float midWidth = content.getWidth() - this.map.getWidth() - margin*3;
        TextButtonWidget resetButton = new TextButtonWidget(margin, this.lonInput.getY() + this.lonInput.getHeight() + interline, 0, (midWidth - spacing) / 2, I18n.format("terramap.popup.renderoffset.reset"), () -> {
//            this.map.setCenterPosition(layer.getCenterLongitude() + layer.getRenderDeltaLongitude(), layer.getCenterLatitude() + layer.getRenderDeltaLatitude());
        });
        content.addWidget(resetButton);
        TextButtonWidget set0Button = new TextButtonWidget(resetButton.getX() + resetButton.getWidth() + spacing, resetButton.getY(), 0, resetButton.getWidth(), I18n.format("terramap.popup.renderoffset.set0"), () -> {
//            this.map.setCenterPosition(layer.getCenterLongitude(), layer.getCenterLatitude());
        });
        content.addWidget(set0Button);
        TextButtonWidget cancelButton = new TextButtonWidget(resetButton.getX(), resetButton.getY() + resetButton.getHeight() + 4, 0, resetButton.getWidth(), I18n.format("terramap.popup.renderoffset.cancel"), () -> {
            this.close();
        });
        content.addWidget(cancelButton);
        this.doneButton = new TextButtonWidget(set0Button.getX(), cancelButton.getY(), 0, set0Button.getWidth(), I18n.format("terramap.popup.renderoffset.done"), () -> {
            layer.setRenderDeltaLongitude(Double.parseDouble(this.lonInput.getText()));
            layer.setRenderDeltaLatitude(Double.parseDouble(this.latInput.getText()));
            this.close();
        });
        content.addWidget(this.doneButton);
        this.latInput.setOnChangeCallback(this::onTextFieldsChange);
        this.lonInput.setOnChangeCallback(this::onTextFieldsChange);
    }
    
    public LayerRenderingOffsetPopup(RasterMapLayer background) {
        this((RasterMapLayer)background.copy(), background);
    }
    
    private void onTextFieldsChange(String unused) {
        boolean okLon = false, okLat = false;
        try {
            double dlon = Double.parseDouble(this.lonInput.getText());
            if(Math.abs(dlon) > 180d) throw new NumberFormatException();
            okLon = true;
            this.lonInput.setEnabledTextColor(Color.WHITE);
            this.lonInput.setFocusedTextColor(Color.WHITE);
//            this.map.setCenterLongitude(this.layer.getCenterLongitude() + dlon);
        } catch(NumberFormatException e) {
            this.lonInput.setEnabledTextColor(Color.RED);
            this.lonInput.setFocusedTextColor(Color.RED);
        }
        try {
            double dlat = Double.parseDouble(this.latInput.getText());
            if(Math.abs(dlat) > 90d) throw new NumberFormatException();
            okLat = true;
            this.latInput.setEnabledTextColor(Color.WHITE);
            this.latInput.setFocusedTextColor(Color.WHITE);
//            this.map.setCenterLatitude(this.layer.getCenterLatitude() + dlat);
        } catch(NumberFormatException e) {
            this.latInput.setEnabledTextColor(Color.RED);
            this.latInput.setFocusedTextColor(Color.RED);
        }
        this.doneButton.setEnabled(okLat && okLon);
    }
    
    public MapLayer getLayer() {
        return this.layer;
    }

}
