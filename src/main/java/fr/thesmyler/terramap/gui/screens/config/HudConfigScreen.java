package fr.thesmyler.terramap.gui.screens.config;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.SlidingPanelWidget;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.container.WindowedContainer;
import fr.thesmyler.smylibgui.container.SlidingPanelWidget.PanelTarget;
import fr.thesmyler.smylibgui.screen.BackgroundOption;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.IntegerSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.OptionSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.config.TerramapConfigScreen.TileScalingOption;
import fr.thesmyler.terramap.gui.widgets.RibbonCompassWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.McChunksLayer;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AnimalMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MobMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerDirectionsVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerNameVisibilityController;
import fr.thesmyler.terramap.maps.raster.IRasterTiledMap;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.TextComponentTranslation;


public class HudConfigScreen extends Screen {

    private MapWidget minimap = new MapWidget(0, TerramapClientContext.getContext().getMapStyles().values().iterator().next(), MapContext.MINIMAP, TerramapConfig.CLIENT.minimap.getEffectiveTileScaling());
    private WindowedContainer minimapWindow = new WindowedContainer(15, this.minimap, "");
    private CompassScreen compassScreen = new CompassScreen();
    private WindowedContainer compassWindow = new WindowedContainer(16, this.compassScreen, "");
    private OptionSliderWidget<TileScalingOption> tileScalingSlider = new OptionSliderWidget<TileScalingOption>(10, TileScalingOption.values());
    private IntegerSliderWidget zoomSlider = new IntegerSliderWidget(11, 0, 20, 10);
    private OptionSliderWidget<MapStyleSliderEntry> styleSlider;
    private MapStyleSliderEntry[] mapStyles = new MapStyleSliderEntry[0];
    private ToggleButtonWidget otherPlayersButton = new ToggleButtonWidget(10, false);
    private ToggleButtonWidget entitiesButton = new ToggleButtonWidget(10, false);
    private ToggleButtonWidget minimapButton = new ToggleButtonWidget(10, false);
    private ToggleButtonWidget compassButton = new ToggleButtonWidget(10, false);
    private ToggleButtonWidget directionsButton = new ToggleButtonWidget(10, false);
    private ToggleButtonWidget rotationButton = new ToggleButtonWidget(10, false);
    private ToggleButtonWidget chunksButton = new ToggleButtonWidget(10, false);
    private SlidingPanelWidget buttonPanel = new SlidingPanelWidget(20, 100);
    private SlidingPanelWidget settingsPanel = new SlidingPanelWidget(20, 100);
    private int lastWidth, lastHeight = -1; // Used to re-calculate the relative minimap position when the game's window is resized

    public HudConfigScreen() {
        super(BackgroundOption.NONE);
        List<MapStyleSliderEntry> maps = new ArrayList<>();
        TerramapClientContext.getContext().getMapStyles().values().stream()
        .sorted(((Comparator<IRasterTiledMap>)IRasterTiledMap::compareTo).reversed())
        .filter(m -> m.isAllowedOnMinimap())
        .forEachOrdered(m -> maps.add(new MapStyleSliderEntry(m)));
        this.mapStyles = maps.toArray(this.mapStyles);
        this.styleSlider = new OptionSliderWidget<>(0, 0, 15, 10, this.mapStyles);
        this.minimap.setInteractive(false);
        this.minimap.setCopyrightVisibility(false);
        this.minimap.setRightClickMenuEnabled(false);
        this.minimap.setScaleVisibility(false);
        this.minimap.getVisibilityControllers().get(PlayerNameVisibilityController.ID).setVisibility(false);
        this.minimap.scheduleBeforeEachUpdate(() -> {
            if(TerramapClientContext.getContext().getProjection() != null) {
                this.minimap.track(this.minimap.getMainPlayerMarker());
            }
        });
        this.zoomSlider.setOnChange(this.minimap::setZoom);
        this.styleSlider.setOnChange(map -> {
            this.minimap.setBackground(map.map);
            this.zoomSlider.setMin(map.map.getMinZoom());
            this.zoomSlider.setMax(TerramapConfig.CLIENT.unlockZoom ? 25: map.map.getMaxZoom());
        });
        this.tileScalingSlider.setOnChange(v -> {
            if(v == TileScalingOption.AUTO) this.minimap.setTileScaling(SmyLibGui.getMinecraftGuiScale());
            else this.minimap.setTileScaling(v.value);
        });
        this.minimapButton.setOnChange(b -> this.minimapWindow.setVisibility(b));
        this.compassButton.setOnChange(b -> this.compassWindow.setVisibility(b));
        this.otherPlayersButton.setOnChange(b -> this.minimap.trySetFeatureVisibility(OtherPlayerMarkerController.ID, b));
        this.entitiesButton.setOnChange(b -> this.minimap.trySetFeatureVisibility(AnimalMarkerController.ID, b).trySetFeatureVisibility(MobMarkerController.ID, b));
        this.directionsButton.setOnChange(b -> this.minimap.trySetFeatureVisibility(PlayerDirectionsVisibilityController.ID, b));
        this.chunksButton.setOnChange(b -> this.minimap.trySetFeatureVisibility(McChunksLayer.ID, b));
        this.rotationButton.setOnChange(b -> {
            this.minimap.setTrackRotation(b);
            if(!b) this.minimap.setRotationWithAnimation(0);
        });
        this.minimapWindow.setEnableTopBar(false);
        this.minimapWindow.setCenterDragColor(Color.TRANSPARENT);
        this.minimapWindow.setEnableCenterDrag(true);
        this.compassWindow.setHeight(this.compassScreen.getHeight());
        this.compassWindow.setAllowVerticalResize(false);
        this.compassWindow.setEnableTopBar(false);
        this.compassWindow.setEnableCenterDrag(true);
        this.compassWindow.setMinInnerHeight(1);
        this.compassWindow.trySetInnerDimensions(50, this.compassScreen.compass.getHeight());
        this.compassWindow.setCenterDragColor(Color.TRANSPARENT);
        this.buttonPanel.setContourColor(Color.TRANSPARENT);
        this.settingsPanel.setContourColor(Color.TRANSPARENT);
        this.reset();
    }

    @Override
    public void initGui() {
        WidgetContainer content = this.getContent();
        content.removeAllWidgets();
        this.buttonPanel.removeAllWidgets();
        this.settingsPanel.removeAllWidgets();
        if(this.lastHeight <= 0 || this.lastWidth <= 0) {
            this.recalcWidgetsPos();
        } else {
            this.minimapWindow.setX(this.minimapWindow.getX() * this.width / this.lastWidth);
            this.minimapWindow.setY(this.minimapWindow.getY() * this.height / this.lastHeight);
            this.minimapWindow.setWidth(this.minimapWindow.getWidth() / this.lastWidth * this.width);
            this.minimapWindow.setHeight(this.minimapWindow.getHeight() / this.lastHeight * this.height);
            double t = this.tileScalingSlider.getCurrentOption().value;
            if(t == 0) this.minimap.setTileScaling(SmyLibGui.getMinecraftGuiScale());
            else this.minimap.setTileScaling(t);
            this.compassWindow.setX(this.compassWindow.getX() * this.width / this.lastWidth);
            this.compassWindow.setY(this.compassWindow.getY() * this.height / this.lastHeight);
            this.compassWindow.setWidth(this.compassWindow.getWidth() / this.lastWidth * this.width);
        }
        this.minimapWindow.init();
        content.addWidget(this.minimapWindow);

        // Buttons
        this.buttonPanel.addWidget(new TextButtonWidget(3, 3, 10, 54, I18n.format("terramap.config.cancel"), this::close).setTooltip(I18n.format("terramap.config.cancel.tooltip")));
        this.buttonPanel.addWidget(new TextButtonWidget(58, 3, 10, 54, I18n.format("terramap.config.reset"), this::reset).setTooltip(I18n.format("terramap.config.reset.tooltip")));
        this.buttonPanel.addWidget(new TextButtonWidget(113, 3, 10, 54, I18n.format("terramap.config.save"), this::saveAndClose).setTooltip(I18n.format("terramap.config.save.tooltip")));
        this.buttonPanel.addWidget(new TexturedButtonWidget(168, 3, 10, IncludedTexturedButtons.OPTIONS_20, this::toggleSettingsPanel).setTooltip(I18n.format("terramap.config.options.tooltip")));

        // Boolean lines
        LinkedList<TextWidget> buttonsTexts = new LinkedList<>();
        buttonsTexts.add(new TextWidget(10, new TextComponentTranslation("terramap.hudconfig.minimap"), SmyLibGui.DEFAULT_FONT));
        buttonsTexts.add(new TextWidget(10, new TextComponentTranslation("terramap.hudconfig.compass"), SmyLibGui.DEFAULT_FONT));
        buttonsTexts.add(new TextWidget(10, new TextComponentTranslation("terramap.hudconfig.players"), SmyLibGui.DEFAULT_FONT));
        buttonsTexts.add(new TextWidget(10, new TextComponentTranslation("terramap.hudconfig.entities"), SmyLibGui.DEFAULT_FONT));
        buttonsTexts.add(new TextWidget(10, new TextComponentTranslation("terramap.hudconfig.directions"), SmyLibGui.DEFAULT_FONT));
        buttonsTexts.add(new TextWidget(10, new TextComponentTranslation("terramap.hudconfig.rotation"), SmyLibGui.DEFAULT_FONT));
        buttonsTexts.add(new TextWidget(10, new TextComponentTranslation("terramap.hudconfig.chunks"), SmyLibGui.DEFAULT_FONT));
        this.minimapButton.setTooltip(I18n.format("terramap.hudconfig.minimap.tooltip"));
        this.compassButton.setTooltip(I18n.format("terramap.hudconfig.compass.tooltip"));
        this.otherPlayersButton.setTooltip(I18n.format("terramap.hudconfig.players.tooltip"));
        this.entitiesButton.setTooltip(I18n.format("terramap.hudconfig.entities.tooltip"));
        this.directionsButton.setTooltip(I18n.format("terramap.hudconfig.directions.tooltip"));
        this.rotationButton.setTooltip(I18n.format("terramap.hudconfig.rotation.tooltip"));
        this.chunksButton.setTooltip(I18n.format("terramap.hudconfig.chunks.tooltip"));
        LinkedList<ToggleButtonWidget> buttons = new LinkedList<>();
        buttons.add(this.minimapButton);
        buttons.add(this.compassButton);
        buttons.add(this.otherPlayersButton);
        buttons.add(this.entitiesButton);
        buttons.add(this.directionsButton);
        buttons.add(this.rotationButton);
        buttons.add(this.chunksButton);
        float lineY = 3;
        float textButtonSpace = 3;
        float lineSpace = 4;
        ToggleButtonWidget lastButton = null;
        while(buttonsTexts.size() > 0) {
            float lineWidth = 0;
            int lineCount = 0;
            for(; lineCount < buttonsTexts.size(); lineCount++) {
                TextWidget text = buttonsTexts.get(lineCount);
                ToggleButtonWidget button = buttons.get(lineCount);
                float newWidth = lineWidth + text.getWidth() + textButtonSpace +button.getWidth();
                if(lineCount > 0 && newWidth > 0.75 * this.width) break;
                lineWidth = newWidth;
            }
            float padding = (this.width  - lineWidth) / (lineCount + 1);
            float x = padding;
            for(int i=0; i<lineCount; i++) {
                TextWidget text = buttonsTexts.pop();
                ToggleButtonWidget button = buttons.pop();
                text.setAnchorX(x).setAnchorY(lineY + 4);
                x += text.getWidth() + textButtonSpace;
                button.setX(x).setY(lineY);
                x += button.getWidth() + padding;
                lastButton = button;
                this.settingsPanel.addWidget(text);
                this.settingsPanel.addWidget(button);
            }
            lineY = lastButton.getY() + lastButton.getHeight() + lineSpace;
        }
        // Second line
        this.settingsPanel.addWidget(this.tileScalingSlider.setX(this.width / 2 - 153).setY(lastButton.getY() + lastButton.getHeight() + lineSpace).setWidth(100).setDisplayPrefix(I18n.format("terramap.hudconfig.scaling")).setTooltip(I18n.format("terramap.hudconfig.scaling.tooltip")));
        this.settingsPanel.addWidget(this.zoomSlider.setWidth(100).setX(this.width/2 - 50).setY(this.tileScalingSlider.getY()).setDisplayPrefix(I18n.format("terramap.hudconfig.zoom")).setTooltip(I18n.format("terramap.hudconfig.zoom.tooltip")));
        this.settingsPanel.addWidget(this.styleSlider.setWidth(100).setX(this.width/2 + 53).setY(this.tileScalingSlider.getY()).setTooltip(I18n.format("terramap.hudconfig.mapstyle.tooltip")));

        // Setup panels
        this.buttonPanel.setBackgroundColor(Color.DARKER_OVERLAY);
        this.buttonPanel.setSize(190, 25);
        this.settingsPanel.setWidth(this.width);
        this.settingsPanel.setHeight(this.styleSlider.getY() + this.styleSlider.getHeight() + 3);
        this.settingsPanel.setClosedX(0).setOpenX(0).setClosedY(this.height);
        this.buttonPanel.setClosedX((this.width - this.buttonPanel.getWidth()) / 2).setClosedY(this.height - this.buttonPanel.getHeight());
        this.buttonPanel.setOpenX(this.buttonPanel.getClosedX()).setOpenY(this.height - this.settingsPanel.getHeight() - this.buttonPanel.getHeight());
        this.settingsPanel.setOpenY(this.height - this.settingsPanel.getHeight());

        TextWidget explain = new TextWidget(this.width/2, this.height/2 - 100, 10, new TextComponentTranslation("terramap.hudconfig.explain"), TextAlignment.CENTER, SmyLibGui.DEFAULT_FONT);
        content.addWidget(explain.setMaxWidth(this.width * .8f).setAnchorY(this.height/2 - explain.getHeight() - 10));

        content.addWidget(this.buttonPanel);
        content.addWidget(this.settingsPanel);
        content.addWidget(this.compassWindow);
        this.lastHeight = this.height;
        this.lastWidth = this.width;
    }

    private void saveAndClose() {
        TerramapConfig.CLIENT.minimap.enable = this.minimapButton.getState();
        TerramapConfig.CLIENT.minimap.zoomLevel = (int) this.zoomSlider.getValue();
        TerramapConfig.CLIENT.minimap.showEntities = this.entitiesButton.getState();
        TerramapConfig.CLIENT.minimap.showOtherPlayers = this.otherPlayersButton.getState();
        TerramapConfig.CLIENT.minimap.style = this.styleSlider.getCurrentOption().map.getId();
        TerramapConfig.CLIENT.minimap.tileScaling = this.tileScalingSlider.getCurrentOption().value;
        TerramapConfig.CLIENT.minimap.posX = this.minimapWindow.getX() / this.width * 100;
        TerramapConfig.CLIENT.minimap.posY = this.minimapWindow.getY() / this.height * 100;
        TerramapConfig.CLIENT.minimap.width = this.minimapWindow.getWidth() / this.width * 100;
        TerramapConfig.CLIENT.minimap.height = this.minimapWindow.getHeight() / this.height * 100;
        TerramapConfig.CLIENT.minimap.playerDirections = this.directionsButton.getState();
        TerramapConfig.CLIENT.minimap.playerRotation = this.rotationButton.getState();
        TerramapConfig.CLIENT.minimap.chunksRender = this.chunksButton.getState();
        TerramapConfig.CLIENT.compass.enable = this.compassButton.getState();
        TerramapConfig.CLIENT.compass.posX = this.compassWindow.getX() / this.width * 100;
        TerramapConfig.CLIENT.compass.posY = this.compassWindow.getY() / this.height * 100;
        TerramapConfig.CLIENT.compass.width = this.compassWindow.getWidth() / this.width * 100;
        TerramapConfig.sync();
        this.close();
    }

    private void close() {
        Minecraft.getMinecraft().displayGuiScreen(null);
    }

    private void reset() {
        this.minimapWindow.setVisibility(TerramapConfig.CLIENT.minimap.enable);
        this.compassWindow.setVisibility(TerramapConfig.CLIENT.compass.enable);
        this.minimapButton.setState(TerramapConfig.CLIENT.minimap.enable);
        this.compassButton.setState(TerramapConfig.CLIENT.compass.enable);
        this.zoomSlider.setValue(Math.round(TerramapConfig.CLIENT.minimap.zoomLevel));
        this.otherPlayersButton.setState(TerramapConfig.CLIENT.minimap.showOtherPlayers);
        this.minimap.trySetFeatureVisibility(OtherPlayerMarkerController.ID, TerramapConfig.CLIENT.minimap.showOtherPlayers);
        this.entitiesButton.setState(TerramapConfig.CLIENT.minimap.showEntities);
        this.minimap.trySetFeatureVisibility(AnimalMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        this.minimap.trySetFeatureVisibility(MobMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        this.minimap.trySetFeatureVisibility(PlayerDirectionsVisibilityController.ID, TerramapConfig.CLIENT.minimap.playerDirections);
        this.minimap.trySetFeatureVisibility(McChunksLayer.ID, TerramapConfig.CLIENT.minimap.chunksRender);
        this.minimap.setTrackRotation(TerramapConfig.CLIENT.minimap.playerRotation);
        if(!TerramapConfig.CLIENT.minimap.playerRotation) this.minimap.setRotation(0f);
        for(MapStyleSliderEntry map: this.mapStyles) if(map.map.getId().equals(TerramapConfig.CLIENT.minimap.style)) {
            this.styleSlider.setCurrentOption(map);
            break;
        }
        this.tileScalingSlider.setCurrentOption(TileScalingOption.getFromValue(TerramapConfig.CLIENT.minimap.tileScaling));
        this.directionsButton.setState(TerramapConfig.CLIENT.minimap.playerDirections);
        this.rotationButton.setState(TerramapConfig.CLIENT.minimap.playerRotation);
        this.chunksButton.setState(TerramapConfig.CLIENT.minimap.chunksRender);
        this.recalcWidgetsPos();
    }

    private void recalcWidgetsPos() {
        this.minimapWindow.setX(this.width * TerramapConfig.CLIENT.minimap.posX / 100);
        this.minimapWindow.setY(this.height * TerramapConfig.CLIENT.minimap.posY / 100);
        this.minimapWindow.setWidth(this.width * TerramapConfig.CLIENT.minimap.width / 100);
        this.minimapWindow.setHeight(this.height * TerramapConfig.CLIENT.minimap.height / 100);
        this.compassWindow.setX(this.width * TerramapConfig.CLIENT.compass.posX / 100);
        this.compassWindow.setY(this.height * TerramapConfig.CLIENT.compass.posY / 100);
        this.compassWindow.setWidth(this.width * TerramapConfig.CLIENT.compass.width / 100);
    }

    public void toggleSettingsPanel() {
        if(this.buttonPanel.getTarget().equals(PanelTarget.CLOSED)) {
            this.buttonPanel.open();
            this.settingsPanel.open();
        } else {
            this.buttonPanel.close();
            this.settingsPanel.close();
        }
    }

    private class CompassScreen extends FlexibleWidgetContainer {

        RibbonCompassWidget compass = new RibbonCompassWidget(0, 0, 0, 30);

        CompassScreen() {
            super(0, 0, 0, 30, 30);
            this.addWidget(this.compass);
            this.setSize(this.compass.getWidth(), this.compass.getHeight());
            this.scheduleBeforeEachUpdate(() -> {
                GeographicProjection p = TerramapClientContext.getContext().getProjection();
                if(p != null) {
                    double x = Minecraft.getMinecraft().player.posX;
                    double z = Minecraft.getMinecraft().player.posZ;
                    float a = Minecraft.getMinecraft().player.rotationYaw;
                    try {
                        compass.setAzimuth(p.azimuth(x, z, a));
                    } catch (OutOfProjectionBoundsException silenced) {}
                }
            });
        }

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, WidgetContainer parent) {
            this.compass.setWidth(this.getWidth());
            super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
        }


    }

    private class MapStyleSliderEntry {
        private IRasterTiledMap map;
        private MapStyleSliderEntry(IRasterTiledMap map) {
            this.map = map;
        }
        @Override
        public String toString() {
            return this.map.getLocalizedName(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode());
        }
    }

}
