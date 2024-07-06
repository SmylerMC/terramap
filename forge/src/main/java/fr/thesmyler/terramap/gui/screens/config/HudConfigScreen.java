package fr.thesmyler.terramap.gui.screens.config;

import java.util.*;

import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.smylib.gui.containers.SlidingPanelWidget;
import net.smyler.smylib.gui.containers.WidgetContainer;
import fr.thesmyler.smylibgui.container.WindowedContainer;
import net.smyler.smylib.gui.containers.SlidingPanelWidget.PanelTarget;
import net.smyler.smylib.gui.screen.BackgroundOption;
import net.smyler.smylib.gui.screen.Screen;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.widgets.buttons.TextButtonWidget;
import net.smyler.smylib.gui.widgets.buttons.SpriteButtonWidget;
import net.smyler.smylib.gui.widgets.buttons.SpriteButtonWidget.ButtonSprites;
import net.smyler.smylib.gui.widgets.buttons.ToggleButtonWidget;
import net.smyler.smylib.gui.widgets.sliders.IntegerSliderWidget;
import net.smyler.smylib.gui.widgets.sliders.OptionSliderWidget;
import net.smyler.smylib.gui.widgets.text.TextAlignment;
import net.smyler.smylib.gui.widgets.text.TextWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.config.TerramapConfigScreen.TileScalingOption;
import net.smyler.terramap.gui.widgets.RibbonCompassWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapController;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.McChunksLayer;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AnimalMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MobMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerDirectionsVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.PlayerNameVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import net.smyler.terramap.tilesets.raster.RasterTileSet;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.game.Translator;
import net.smyler.smylib.gui.UiDrawContext;

import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.text.ImmutableText.ofTranslation;

public class HudConfigScreen extends Screen {

    private final MapWidget minimap = new MapWidget(0, MapContext.MINIMAP, TerramapConfig.CLIENT.minimap.getEffectiveTileScaling());
    private final WindowedContainer minimapWindow = new WindowedContainer(15, this.minimap, "");
    private final CompassScreen compassScreen = new CompassScreen();
    private final WindowedContainer compassWindow = new WindowedContainer(16, this.compassScreen, "");
    private final OptionSliderWidget<TileScalingOption> tileScalingSlider = new OptionSliderWidget<>(10, TileScalingOption.values());
    private final IntegerSliderWidget zoomSlider = new IntegerSliderWidget(11, 0, 20, 10);
    private final OptionSliderWidget<RasterTileSetSliderEntry> styleSlider;
    private RasterTileSetSliderEntry[] tileSets = new RasterTileSetSliderEntry[0];
    private final ToggleButtonWidget otherPlayersButton = new ToggleButtonWidget(10, false);
    private final ToggleButtonWidget entitiesButton = new ToggleButtonWidget(10, false);
    private final ToggleButtonWidget minimapButton = new ToggleButtonWidget(10, false);
    private final ToggleButtonWidget compassButton = new ToggleButtonWidget(10, false);
    private final ToggleButtonWidget directionsButton = new ToggleButtonWidget(10, false);
    private final ToggleButtonWidget rotationButton = new ToggleButtonWidget(10, false);
    private final ToggleButtonWidget chunksButton = new ToggleButtonWidget(10, false);
    private final SlidingPanelWidget buttonPanel = new SlidingPanelWidget(20, 100);
    private final SlidingPanelWidget settingsPanel = new SlidingPanelWidget(20, 100);
    private float lastWidth, lastHeight = -1; // Used to re-calculate the relative minimap position when the game's window is resized

    public HudConfigScreen() {
        super(BackgroundOption.NONE);
        final MapController controller = this.minimap.getController();
        List<RasterTileSetSliderEntry> maps = new ArrayList<>();
        TerramapClientContext.getContext().getRasterTileSets().values().stream()
            .sorted(((Comparator<RasterTileSet>) RasterTileSet::compareTo).reversed())
            .filter(RasterTileSet::isAllowedOnMinimap)
            .forEachOrdered(m -> maps.add(new RasterTileSetSliderEntry(m)));
        this.tileSets = maps.toArray(this.tileSets);
        this.styleSlider = new OptionSliderWidget<>(0, 0, 15, 10, this.tileSets);
        this.minimap.setInteractive(false);
        this.minimap.setCopyrightVisibility(false);
        this.minimap.setRightClickMenuEnabled(false);
        this.minimap.setScaleVisibility(false);
        this.minimap.getVisibilityControllers().get(PlayerNameVisibilityController.ID).setVisibility(false);
        this.minimap.restore(TerramapClientContext.getContext().getSavedState().minimap);
        this.minimap.scheduleBeforeEachUpdate(() -> {
            GeographicProjection projection = TerramapClientContext.getContext().getProjection();
            Marker marker = this.minimap.getMainPlayerMarker();
            if (projection != null && marker != null) {
                controller.track(marker);
            }
        });
        this.zoomSlider.setOnChange(z -> controller.setZoom(z, true));
        this.styleSlider.setOnChange(map -> this.minimap.getRasterBackgroundLayer().ifPresent(b -> {
            b.setTiledMap(map.map);
            this.zoomSlider.setMin(map.map.getMinZoom());
            this.zoomSlider.setMax(map.map.getMaxZoom());
        }));
        this.tileScalingSlider.setOnChange(v -> {
            if(v == TileScalingOption.AUTO) this.minimap.setTileScaling(getGameClient().scaleFactor());
            else this.minimap.setTileScaling(v.value);
        });
        this.minimapButton.setOnChange(this.minimapWindow::setVisibility);
        this.compassButton.setOnChange(this.compassWindow::setVisibility);
        this.otherPlayersButton.setOnChange(b -> this.minimap.trySetFeatureVisibility(OtherPlayerMarkerController.ID, b));
        this.entitiesButton.setOnChange(b -> {
            this.minimap.trySetFeatureVisibility(AnimalMarkerController.ID, b);
            this.minimap.trySetFeatureVisibility(MobMarkerController.ID, b);
        });
        this.directionsButton.setOnChange(b -> this.minimap.trySetFeatureVisibility(PlayerDirectionsVisibilityController.ID, b));
        this.chunksButton.setOnChange(b -> this.minimap.trySetFeatureVisibility(McChunksLayer.ID, b));
        this.rotationButton.setOnChange(b -> {
            controller.setTracksRotation(b);
            if(!b) controller.setRotation(0, true);
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
    public void init() {
        GameClient game = getGameClient();
        Translator translator = game.translator();
        this.removeAllWidgets();

        float width = this.getWidth();
        float height = this.getHeight();

        this.buttonPanel.removeAllWidgets();
        this.settingsPanel.removeAllWidgets();
        if(this.lastHeight <= 0 || this.lastWidth <= 0) {
            this.recalculateWidgetsPositions();
        } else {
            this.minimapWindow.setX(this.minimapWindow.getX() * width / this.lastWidth);
            this.minimapWindow.setY(this.minimapWindow.getY() * height / this.lastHeight);
            this.minimapWindow.setWidth(this.minimapWindow.getWidth() / this.lastWidth * width);
            this.minimapWindow.setHeight(this.minimapWindow.getHeight() / this.lastHeight * height);
            double t = this.tileScalingSlider.getCurrentOption().value;
            if(t == 0) this.minimap.setTileScaling(game.scaleFactor());
            else this.minimap.setTileScaling(t);
            this.compassWindow.setX(this.compassWindow.getX() * width / this.lastWidth);
            this.compassWindow.setY(this.compassWindow.getY() * height / this.lastHeight);
            this.compassWindow.setWidth(this.compassWindow.getWidth() / this.lastWidth * width);
        }
        this.minimapWindow.init();
        this.addWidget(this.minimapWindow);

        // Buttons
        this.buttonPanel.addWidget(
                new TextButtonWidget(
                        3, 3, 10,
                        54,
                        translator.format("terramap.config.cancel"),
                        this::close)
                        .setTooltip(game.translator().format("terramap.config.cancel.tooltip")));
        this.buttonPanel.addWidget(
                new TextButtonWidget(
                        58, 3, 10,
                        54,
                        game.translator().format("terramap.config.reset"),
                        this::reset)
                        .setTooltip(translator.format("terramap.config.reset.tooltip")));
        this.buttonPanel.addWidget(
                new TextButtonWidget(
                        113, 3, 10,
                        54,
                        translator.format("terramap.config.save"),
                        this::saveAndClose)
                        .setTooltip(translator.format("terramap.config.save.tooltip")));
        this.buttonPanel.addWidget(
                new SpriteButtonWidget(
                        168, 3, 10,
                        ButtonSprites.BURGER_20,
                        this::toggleSettingsPanel)
                        .setTooltip(translator.format("terramap.config.options.tooltip")));

        // Boolean lines
        LinkedList<TextWidget> buttonsTexts = new LinkedList<>();
        buttonsTexts.add(new TextWidget(10, ofTranslation("terramap.hudconfig.minimap"), game.defaultFont()));
        buttonsTexts.add(new TextWidget(10, ofTranslation("terramap.hudconfig.compass"), game.defaultFont()));
        buttonsTexts.add(new TextWidget(10, ofTranslation("terramap.hudconfig.players"), game.defaultFont()));
        buttonsTexts.add(new TextWidget(10, ofTranslation("terramap.hudconfig.entities"), game.defaultFont()));
        buttonsTexts.add(new TextWidget(10, ofTranslation("terramap.hudconfig.directions"), game.defaultFont()));
        buttonsTexts.add(new TextWidget(10, ofTranslation("terramap.hudconfig.rotation"), game.defaultFont()));
        buttonsTexts.add(new TextWidget(10, ofTranslation("terramap.hudconfig.chunks"), game.defaultFont()));
        this.minimapButton.setTooltip(translator.format("terramap.hudconfig.minimap.tooltip"));
        this.compassButton.setTooltip(translator.format("terramap.hudconfig.compass.tooltip"));
        this.otherPlayersButton.setTooltip(translator.format("terramap.hudconfig.players.tooltip"));
        this.entitiesButton.setTooltip(translator.format("terramap.hudconfig.entities.tooltip"));
        this.directionsButton.setTooltip(translator.format("terramap.hudconfig.directions.tooltip"));
        this.rotationButton.setTooltip(translator.format("terramap.hudconfig.rotation.tooltip"));
        this.chunksButton.setTooltip(translator.format("terramap.hudconfig.chunks.tooltip"));
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
                if(lineCount > 0 && newWidth > 0.75 * width) break;
                lineWidth = newWidth;
            }
            float padding = (width  - lineWidth) / (lineCount + 1);
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
            lineY = lastButton != null ? lastButton.getY() + lastButton.getHeight() + lineSpace: lineSpace;
        }
        // Second line
        this.settingsPanel.addWidget(this.tileScalingSlider
                .setX(width / 2f - 153).setY(lastButton != null ? lastButton.getY() + lastButton.getHeight() + lineSpace: lineSpace)
                .setWidth(100)
                .setDisplayPrefix(translator.format("terramap.hudconfig.scaling"))
                .setTooltip(translator.format("terramap.hudconfig.scaling.tooltip")));
        this.settingsPanel.addWidget(this.zoomSlider
                .setWidth(100)
                .setX(width / 2f - 50f).setY(this.tileScalingSlider.getY())
                .setDisplayPrefix(translator.format("terramap.hudconfig.zoom"))
                .setTooltip(translator.format("terramap.hudconfig.zoom.tooltip")));
        this.settingsPanel.addWidget(this.styleSlider
                .setWidth(100)
                .setX(width / 2f + 53f).setY(this.tileScalingSlider.getY())
                .setTooltip(translator.format("terramap.hudconfig.mapstyle.tooltip")));
        this.styleSlider.setEnabled(this.minimap.getRasterBackgroundLayer().isPresent());

        // Setup panels
        this.buttonPanel.setBackgroundColor(Color.DARKER_OVERLAY);
        this.buttonPanel.setSize(190, 25);
        this.settingsPanel.setWidth(width);
        this.settingsPanel.setHeight(this.styleSlider.getY() + this.styleSlider.getHeight() + 3);
        this.settingsPanel.setClosedX(0).setOpenX(0).setClosedY(height);
        this.buttonPanel.setClosedX((width - this.buttonPanel.getWidth()) / 2).setClosedY(height - this.buttonPanel.getHeight());
        this.buttonPanel.setOpenX(this.buttonPanel.getClosedX()).setOpenY(height - this.settingsPanel.getHeight() - this.buttonPanel.getHeight());
        this.settingsPanel.setOpenY(height - this.settingsPanel.getHeight());

        TextWidget explain = new TextWidget(width / 2f, height / 2f - 100f, 10, ofTranslation("terramap.hudconfig.explain"), TextAlignment.CENTER, game.defaultFont());
        this.addWidget(explain.setMaxWidth(width * .8f).setAnchorY(height / 2f - explain.getHeight() - 10f));

        this.addWidget(this.buttonPanel);
        this.addWidget(this.settingsPanel);
        this.addWidget(this.compassWindow);
        this.lastHeight = height;
        this.lastWidth = width;
    }

    private void saveAndClose() {
        float width = this.getWidth();
        float height = this.getHeight();
        TerramapConfig.CLIENT.minimap.enable = this.minimapButton.getState();
        TerramapConfig.CLIENT.minimap.zoomLevel = (int) this.zoomSlider.getValue();
        TerramapConfig.CLIENT.minimap.showEntities = this.entitiesButton.getState();
        TerramapConfig.CLIENT.minimap.showOtherPlayers = this.otherPlayersButton.getState();
        TerramapConfig.CLIENT.minimap.style = this.styleSlider.getCurrentOption().map.getId();
        TerramapConfig.CLIENT.minimap.tileScaling = this.tileScalingSlider.getCurrentOption().value;
        TerramapConfig.CLIENT.minimap.posX = this.minimapWindow.getX() / width * 100;
        TerramapConfig.CLIENT.minimap.posY = this.minimapWindow.getY() / height * 100;
        TerramapConfig.CLIENT.minimap.width = this.minimapWindow.getWidth() / width * 100;
        TerramapConfig.CLIENT.minimap.height = this.minimapWindow.getHeight() / height * 100;
        TerramapConfig.CLIENT.minimap.playerDirections = this.directionsButton.getState();
        TerramapConfig.CLIENT.minimap.playerRotation = this.rotationButton.getState();
        TerramapConfig.CLIENT.minimap.chunksRender = this.chunksButton.getState();
        TerramapConfig.CLIENT.compass.enable = this.compassButton.getState();
        TerramapConfig.CLIENT.compass.posX = this.compassWindow.getX() / width * 100;
        TerramapConfig.CLIENT.compass.posY = this.compassWindow.getY() / height * 100;
        TerramapConfig.CLIENT.compass.width = this.compassWindow.getWidth() / width * 100;
        TerramapConfig.sync();
        this.close();
    }

    private void close() {
        getGameClient().displayScreen(null);
    }

    private void reset() {
        this.minimapWindow.setVisibility(TerramapConfig.CLIENT.minimap.enable);
        this.compassWindow.setVisibility(TerramapConfig.CLIENT.compass.enable);
        this.minimapButton.setState(TerramapConfig.CLIENT.minimap.enable);
        this.compassButton.setState(TerramapConfig.CLIENT.compass.enable);
        this.minimap.getController().setZoom(TerramapConfig.CLIENT.minimap.zoomLevel, false);
        this.zoomSlider.setValue(Math.round(TerramapConfig.CLIENT.minimap.zoomLevel));
        this.otherPlayersButton.setState(TerramapConfig.CLIENT.minimap.showOtherPlayers);
        this.minimap.trySetFeatureVisibility(OtherPlayerMarkerController.ID, TerramapConfig.CLIENT.minimap.showOtherPlayers);
        this.entitiesButton.setState(TerramapConfig.CLIENT.minimap.showEntities);
        this.minimap.trySetFeatureVisibility(AnimalMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        this.minimap.trySetFeatureVisibility(MobMarkerController.ID, TerramapConfig.CLIENT.minimap.showEntities);
        this.minimap.trySetFeatureVisibility(PlayerDirectionsVisibilityController.ID, TerramapConfig.CLIENT.minimap.playerDirections);
        this.minimap.trySetFeatureVisibility(McChunksLayer.ID, TerramapConfig.CLIENT.minimap.chunksRender);
        MapController minimapController = this.minimap.getController();
        minimapController.setTracksRotation(TerramapConfig.CLIENT.minimap.playerRotation);
        if(!TerramapConfig.CLIENT.minimap.playerRotation) minimapController.setRotation(0f, false);
        for(RasterTileSetSliderEntry map: this.tileSets) if(map.map.getId().equals(TerramapConfig.CLIENT.minimap.style)) {
            this.styleSlider.setCurrentOption(map);
            break;
        }
        this.tileScalingSlider.setCurrentOption(TileScalingOption.getFromValue(TerramapConfig.CLIENT.minimap.tileScaling));
        this.directionsButton.setState(TerramapConfig.CLIENT.minimap.playerDirections);
        this.rotationButton.setState(TerramapConfig.CLIENT.minimap.playerRotation);
        this.chunksButton.setState(TerramapConfig.CLIENT.minimap.chunksRender);
        this.recalculateWidgetsPositions();
    }

    private void recalculateWidgetsPositions() {
        float width = this.getWidth();
        float height = this.getHeight();
        this.minimapWindow.setX(width * TerramapConfig.CLIENT.minimap.posX / 100);
        this.minimapWindow.setY(height * TerramapConfig.CLIENT.minimap.posY / 100);
        this.minimapWindow.setWidth(width * TerramapConfig.CLIENT.minimap.width / 100);
        this.minimapWindow.setHeight(height * TerramapConfig.CLIENT.minimap.height / 100);
        this.compassWindow.setX(width * TerramapConfig.CLIENT.compass.posX / 100);
        this.compassWindow.setY(height * TerramapConfig.CLIENT.compass.posY / 100);
        this.compassWindow.setWidth(width * TerramapConfig.CLIENT.compass.width / 100);
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

    private static class CompassScreen extends FlexibleWidgetContainer {

        final RibbonCompassWidget compass = new RibbonCompassWidget(0, 0, 0, 30);

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
                    } catch (OutOfProjectionBoundsException ignored) {}
                }
            });
        }

        @Override
        public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, WidgetContainer parent) {
            this.compass.setWidth(this.getWidth());
            super.draw(context, x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
        }


    }

    private static class RasterTileSetSliderEntry {
        private final RasterTileSet map;
        private RasterTileSetSliderEntry(RasterTileSet map) {
            this.map = map;
        }
        @Override
        public String toString() {
            return this.map.getLocalizedName(Minecraft.getMinecraft().getLanguageManager().getCurrentLanguage().getLanguageCode());
        }
    }

}
