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
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.map.layer.RenderingDeltaPreviewLayer;
import fr.thesmyler.terramap.util.GeoServices;
import fr.thesmyler.terramap.util.Vec2d;
import net.minecraft.util.text.TextComponentTranslation;

//TODO Localize
public class LayerRenderingOffsetPopup extends PopupScreen {
    
    private final RasterMapLayer layer;
    private final TextFieldWidget lonInput;
    private final TextFieldWidget latInput;
    private final MapWidget map;
    private final TextButtonWidget doneButton;

    public LayerRenderingOffsetPopup(RasterMapLayer layer) {
        super(300f, 150f);
        this.layer = layer;
        float interline = 20;
        float margin = 8;
        float spacing = 7;
        WidgetContainer content = this.getContent();
        TextWidget title = new TextWidget(content.getWidth() / 2, margin, 0, new TextComponentTranslation("Set layer rendering offset"), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        content.addWidget(title);
        TextWidget latText = new TextWidget(margin, title.getY() + title.getHeight() + interline, 0, new TextComponentTranslation("Latitude (°):"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        content.addWidget(latText);
        TextWidget lonText = new TextWidget(margin, latText.getY() + latText.getHeight() + interline, 0, new TextComponentTranslation("Longitude (°):"), TextAlignment.RIGHT, SmyLibGui.DEFAULT_FONT);
        content.addWidget(lonText);
        float inputsX = Math.max(latText.getX() + latText.getWidth(), lonText.getX() + lonText.getWidth()) + spacing;
        this.latInput = new TextFieldWidget(inputsX, latText.getY() - 6, 0, 70);
        content.addWidget(this.latInput);
        this.lonInput = new TextFieldWidget(inputsX, lonText.getY() - 6, 0, this.latInput.getWidth());
        content.addWidget(this.lonInput);
        float mapSize = Math.min(
                content.getWidth() - (inputsX + this.lonInput.getWidth() + spacing * 2) - margin,
                content.getHeight() - this.latInput.getY() - margin);
        this.map = new MapWidget(content.getWidth() - mapSize - margin, latInput.getY(), 0, mapSize, mapSize, layer.getMap(), MapContext.PREVIEW, layer.getTileScaling());
        RenderingDeltaPreviewLayer previewLayer = new RenderingDeltaPreviewLayer(layer.getTileScaling(), layer.getCenterLongitude(), layer.getCenterLatitude());
        this.map.addOverlayLayer(previewLayer);
        this.map.setScaleVisibility(false);
        this.map.setCopyrightVisibility(false);
        this.map.setRightClickMenuEnabled(false);
        this.map.setFocusedZoom(false);
        this.map.setAllowsQuickTp(false);
        this.map.setCenterPosition(layer.getCenterLongitude() + layer.getRenderDeltaLongitude(), layer.getCenterLatitude() + layer.getRenderDeltaLatitude());
        this.map.setRotation(layer.getRotation());
        this.map.setZoom(layer.getZoom());
        content.addWidget(this.map);
        content.scheduleAtUpdate(() -> this.map.setTileScaling(layer.getTileScaling()));
        content.scheduleAtUpdate(() -> {
            IWidget focused = content.getFocusedWidget();
            if(focused == this.latInput || focused == this.lonInput) return;
            Vec2d layerCenter = new Vec2d(layer.getCenterLongitude(), layer.getCenterLatitude());
            Vec2d previewCenter = new Vec2d(this.map.getCenterLongitude(), this.map.getCenterLatitude());
            Vec2d delta = previewCenter.add(layerCenter.scale(-1));
            this.lonInput.setText(GeoServices.formatGeoCoordForDisplay(delta.x));
            this.latInput.setText(GeoServices.formatGeoCoordForDisplay(delta.y));
        });
        float midWidth = content.getWidth() - this.map.getWidth() - margin*3;
        TextButtonWidget resetButton = new TextButtonWidget(margin, this.lonInput.getY() + this.lonInput.getHeight() + interline, 0, (midWidth - spacing) / 2, "Reset", () -> {
            this.map.setCenterPosition(layer.getCenterLongitude() + layer.getRenderDeltaLongitude(), layer.getCenterLatitude() + layer.getRenderDeltaLatitude());
        });
        content.addWidget(resetButton);
        TextButtonWidget set0Button = new TextButtonWidget(resetButton.getX() + resetButton.getWidth() + spacing, resetButton.getY(), 0, resetButton.getWidth(), "Set to 0", () -> {
            this.map.setCenterPosition(layer.getCenterLongitude(), layer.getCenterLatitude());
        });
        content.addWidget(set0Button);
        TextButtonWidget cancelButton = new TextButtonWidget(resetButton.getX(), resetButton.getY() + resetButton.getHeight() + 4, 0, resetButton.getWidth(), "Cancel", () -> {
            this.close();
        });
        content.addWidget(cancelButton);
        this.doneButton = new TextButtonWidget(set0Button.getX(), cancelButton.getY(), 0, set0Button.getWidth(), "Done", () -> {
            layer.setRenderDeltaLongitude(Double.parseDouble(this.lonInput.getText()));
            layer.setRenderDeltaLatitude(Double.parseDouble(this.latInput.getText()));
            this.close();
        });
        content.addWidget(this.doneButton);
        this.latInput.setOnChangeCallback(this::onTextFieldsChange);
        this.lonInput.setOnChangeCallback(this::onTextFieldsChange);
    }
    
    private void onTextFieldsChange(String unused) {
        boolean okLon = false, okLat = false;
        try {
            double dlon = Double.parseDouble(this.lonInput.getText());
            if(Math.abs(dlon) > 180d) throw new NumberFormatException();
            okLon = true;
            this.lonInput.setEnabledTextColor(Color.WHITE);
            this.lonInput.setFocusedTextColor(Color.WHITE);
            this.map.setCenterLongitude(this.layer.getCenterLongitude() + dlon);
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
            this.map.setCenterLatitude(this.layer.getCenterLatitude() + dlat);
        } catch(NumberFormatException e) {
            this.latInput.setEnabledTextColor(Color.RED);
            this.latInput.setFocusedTextColor(Color.RED);
        }
        this.doneButton.setEnabled(okLat && okLon);
    }

}
