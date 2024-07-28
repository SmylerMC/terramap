package net.smyler.terramap.gui.screens;

import java.util.*;
import java.util.function.Consumer;

import net.smyler.smylib.game.Key;
import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.gl.Scissor;
import net.smyler.smylib.gui.sprites.WarningWidget;
import net.smyler.smylib.gui.widgets.Widget;
import net.smyler.terramap.MapContext;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.TerramapClientContext;
import net.smyler.terramap.TerramapConfig;
import net.smyler.terramap.gui.widgets.map.*;
import net.smyler.terramap.gui.widgets.map.layer.OnlineRasterMapLayer;
import net.smyler.terramap.tilesets.raster.*;
import org.jetbrains.annotations.Nullable;

import net.smyler.smylib.Color;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.game.Translator;
import net.smyler.smylib.gui.Font;
import net.smyler.terramap.util.geo.GeoPointReadOnly;
import net.smyler.terramap.util.geo.WebMercatorUtil;

import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.smylib.gui.containers.ScrollableWidgetContainer;
import net.smyler.smylib.gui.containers.SlidingPanelWidget;
import net.smyler.smylib.gui.containers.SlidingPanelWidget.PanelTarget;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.gui.screen.BackgroundOption;
import net.smyler.smylib.gui.popups.MultiChoicePopup;
import net.smyler.smylib.gui.screen.Screen;
import net.smyler.smylib.gui.widgets.AbstractSolidWidget;
import net.smyler.smylib.gui.widgets.ScrollbarWidget;
import net.smyler.smylib.gui.widgets.ScrollbarWidget.ScrollbarOrientation;
import net.smyler.smylib.gui.widgets.SpriteWidget;
import net.smyler.smylib.gui.widgets.buttons.TextButtonWidget;
import net.smyler.smylib.gui.widgets.buttons.SpriteButtonWidget;
import net.smyler.smylib.gui.widgets.buttons.SpriteButtonWidget.ButtonSprites;
import net.smyler.smylib.gui.widgets.text.TextAlignment;
import net.smyler.smylib.gui.widgets.text.TextFieldWidget;
import net.smyler.smylib.gui.widgets.text.TextWidget;
import net.smyler.terramap.gui.widgets.CircularCompassWidget;
import net.smyler.terramap.gui.widgets.map.MapLayerRegistry.LayerRegistration;

import static net.smyler.terramap.util.geo.GeoServices.formatZoomLevelForDisplay;
import static net.smyler.smylib.Color.WHITE;
import static net.smyler.smylib.Color.YELLOW;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.game.Key.*;
import static net.smyler.smylib.math.Math.clamp;
import static java.util.stream.Collectors.toMap;
import static net.smyler.smylib.text.Formatting.*;
import static net.smyler.smylib.text.ImmutableText.ofPlainText;
import static net.smyler.smylib.text.ImmutableText.ofTranslation;


public class TerramapScreen extends Screen {

    private final Screen parent;

    // Main map area widgets
    private final MapWidget map;
    private final SpriteButtonWidget closeButton = new SpriteButtonWidget(50, ButtonSprites.CROSS);
    private final SpriteButtonWidget zoomInButton = new SpriteButtonWidget(50, ButtonSprites.PLUS);
    private final SpriteButtonWidget zoomOutButton = new SpriteButtonWidget(50, ButtonSprites.MINUS);
    private final SpriteButtonWidget centerButton = new SpriteButtonWidget(50, ButtonSprites.CENTER);
    private final SpriteButtonWidget styleButton = new SpriteButtonWidget(50, ButtonSprites.PAPER);
    private final CircularCompassWidget compass = new CircularCompassWidget(100, 100, 50, 100);
    private final SpriteWidget offsetWarning = new WarningWidget(0, 0, 50);

    // Info panel widgets
    private TextWidget zoomText;
    private final SlidingPanelWidget infoPanel = new SlidingPanelWidget(70, 200);
    private SpriteButtonWidget panelButton = new SpriteButtonWidget(230, 5, 10, ButtonSprites.RIGHT, this::toggleInfoPanel);
    private TextWidget distortionText;
    private TextWidget debugText;
    private TextWidget playerGeoLocationText;
    private final TextFieldWidget searchBox = new TextFieldWidget(10);

    // Style panel
    private final SlidingPanelWidget stylePanel = new SlidingPanelWidget(80, 200);
    private final BackgroundStylePanelListContainer backgroundStylePanelListContainer;
    private final ScrollbarWidget styleScrollbar = new ScrollbarWidget(100, ScrollbarOrientation.VERTICAL);
    
    // Advanced layer settings panel
    private final SlidingPanelWidget layerPanel = new SlidingPanelWidget(70, 200);
    private final LayerListContainer layerListContainer;
    private final ScrollableWidgetContainer layerList;
    
    // UI states
    private boolean f1Mode = false;
    private boolean debugMode = false;

    public TerramapScreen(Screen parent) {
        super(BackgroundOption.OVERLAY);
        this.parent = parent;
        this.map = new MapWidget(10, MapContext.FULLSCREEN, TerramapConfig.CLIENT.getEffectiveTileScaling());
        OnlineRasterMapLayer layer = (OnlineRasterMapLayer) this.map.createLayer("terramap:raster");
        this.map.setLayerZ(layer, Integer.MIN_VALUE);
        RasterTileSet tileSet = Terramap.instance().rasterTileSetManager().getBaseMaps().values().stream().findAny().get();
        layer.setTiledMap(tileSet);
        this.backgroundStylePanelListContainer = new BackgroundStylePanelListContainer();
        this.infoPanel.setContourColor(Color.DARKER_GRAY.withAlpha(.5f));
        this.layerPanel.setContourColor(Color.DARKER_GRAY.withAlpha(.5f));
        this.layerListContainer = new LayerListContainer(0, 0, 0, 100, this.map);
        this.layerList = new ScrollableWidgetContainer(0, 0, 10, 10, 10, this.layerListContainer);
        this.map.getRightClickMenu().addEntry(
                getGameClient().translator().format("terramap.mapwidget.rclickmenu.offset")
        );
    }

    @Override
    public void init() {
        GameClient game = getGameClient();
        Translator translator = game.translator();

        float width = this.getWidth();
        float height = this.getHeight();

        this.removeAllWidgets();
        this.map.setPosition(0, 0);
        this.map.setSize(width, height);
        this.map.setTileScaling(TerramapConfig.CLIENT.getEffectiveTileScaling());

        this.addWidget(this.map);

        // Map control buttons
        this.closeButton.setX(width - this.closeButton.getWidth() - 5).setY(5);
        this.closeButton.setOnClick(() -> game.displayScreen(this.parent));
        this.closeButton.setTooltip(translator.format("terramap.terramapscreen.buttons.close.tooltip"));
        this.closeButton.enable();
        this.addWidget(this.closeButton);
        this.compass.setOnClick(() -> this.map.getController().setRotation(0f, true));
        this.compass.setFadeAwayOnZero(true);
        this.compass.setTooltip(translator.format("terramap.terramapscreen.buttons.compass.tooltip"));
        this.compass.setX(this.closeButton.getX());
        this.compass.setY(this.closeButton.getY() + this.closeButton.getHeight() + 5);
        this.compass.setSize(this.closeButton.getWidth());
        this.addWidget(this.compass);
        this.zoomInButton.setX(this.compass.getX()).setY(this.compass.getY() + this.compass.getHeight() + 5);
        this.zoomInButton.setOnClick(() -> {
            this.map.getController().setZoomStaticLocation(this.map.getController().getCenterLocation());
            this.map.getController().zoom(1, true);
        });
        this.zoomInButton.setTooltip(translator.format("terramap.terramapscreen.buttons.zoomin.tooltip"));
        this.zoomInButton.enable();
        this.addWidget(this.zoomInButton);
        this.zoomText = new TextWidget(49, game.defaultFont());
        this.zoomText.setAnchorX(this.zoomInButton.getX() + this.zoomInButton.getWidth() / 2 + 1).setAnchorY(this.zoomInButton.getY() +  this.zoomInButton.getHeight() + 2);
        this.zoomText.setAlignment(TextAlignment.CENTER).setBackgroundColor(Color.DARKER_OVERLAY).setPadding(3);
        this.zoomText.setVisibility(!this.f1Mode);
        this.addWidget(this.zoomText);
        this.zoomOutButton.setX(this.zoomInButton.getX()).setY(this.zoomText.getY() + zoomText.getHeight() + 2);
        this.zoomOutButton.setOnClick(() -> {
            this.map.getController().setZoomStaticLocation(this.map.getController().getCenterLocation());
            this.map.getController().zoom(-1, true);
        });
        this.zoomOutButton.setTooltip(translator.format("terramap.terramapscreen.buttons.zoomout.tooltip"));
        this.zoomOutButton.enable();
        this.addWidget(this.zoomOutButton);
        this.centerButton.setX(this.zoomOutButton.getX()).setY(this.zoomOutButton.getY() + this.zoomOutButton.getHeight() + 15);
        this.centerButton.setTooltip(translator.format("terramap.terramapscreen.buttons.track.tooltip"));
        this.addWidget(this.centerButton);
        this.styleButton.setX(this.centerButton.getX()).setY(this.centerButton.getY() + this.centerButton.getHeight() + 5);
        this.styleButton.setOnClick(this.stylePanel::open);
        this.styleButton.setTooltip(translator.format("terramap.terramapscreen.buttons.style.tooltip"));
        this.styleButton.enable();
        this.addWidget(this.styleButton);
        this.offsetWarning.setTooltip(translator.format("terramap.terramapscreen.warning.offset.tooltip"));
        this.addWidget(this.offsetWarning.setPosition(this.styleButton.getX(), this.styleButton.getY() + this.styleButton.getHeight() + 5));
        this.debugText = new TextWidget(49, game.smallestFont());
        this.debugText.setAnchorX(3).setAnchorY(0);
        this.debugText.setAlignment(TextAlignment.RIGHT).setBackgroundColor(Color.DARKER_OVERLAY).setPadding(3);
        this.debugText.setVisibility(this.debugMode);
        this.addWidget(this.debugText);

        // Info panel
        Font infoFont = this.getFont();
        this.infoPanel.removeAllWidgets();
        this.infoPanel.setSize(250, height);
        this.infoPanel.setOpenX(0).setOpenY(0).setClosedX(-this.infoPanel.getWidth() + 25).setClosedY(0);
        this.panelButton.setTooltip(translator.format("terramap.terramapscreen.buttons.info.tooltip"));
        this.infoPanel.addWidget(this.panelButton);
        SpriteButtonWidget openConfigButton = new SpriteButtonWidget(this.panelButton.getX(), this.panelButton.getY() + this.panelButton.getHeight() + 3, 100, ButtonSprites.WRENCH);
        openConfigButton.setTooltip(translator.format("terramap.terramapscreen.buttons.config.tooltip"));
        this.infoPanel.addWidget(openConfigButton);
        SpriteButtonWidget openLayerListButton = new SpriteButtonWidget(
                openConfigButton.getX(), openConfigButton.getY() + openConfigButton.getHeight() + 3, 100,
                ButtonSprites.PAPER, () -> {
                    if(this.layerPanel.getTarget() == PanelTarget.CLOSED) {
                        this.layerPanel.open();
                        if(this.infoPanel.getTarget() == PanelTarget.CLOSED) this.toggleInfoPanel();
                    }
                });
        this.infoPanel.addWidget(openLayerListButton);
        this.playerGeoLocationText = new TextWidget(49, infoFont);
        this.playerGeoLocationText = new TextWidget(49, infoFont);
        this.playerGeoLocationText.setAnchorX(5).setAnchorY(5).setAlignment(TextAlignment.RIGHT);
        this.infoPanel.addWidget(this.playerGeoLocationText);
        this.distortionText = new TextWidget(49, infoFont);
        this.distortionText.setAnchorX(5).setAnchorY(this.playerGeoLocationText.getAnchorY() + infoFont.height() + 5).setAlignment(TextAlignment.RIGHT);
        this.infoPanel.addWidget(this.distortionText);
        float y = this.distortionText.getY() + this.distortionText.getHeight() + 3;
        float lineHeight = 0;
        this.searchBox.setX(5).setY(y + lineHeight + 4).setWidth(186);
        this.searchBox.enableRightClickMenu();
        this.searchBox.setText(translator.format("terramap.terramapscreen.search.wip")).disable();
        this.searchBox.setOnPressEnterCallback(this::search);
        this.infoPanel.addWidget(this.searchBox);
        SpriteButtonWidget searchButton = new SpriteButtonWidget(50, ButtonSprites.SEARCH);
        searchButton.setX(this.searchBox.getX() + this.searchBox.getWidth() + 2).setY(this.searchBox.getY() - 1);
        searchButton.setOnClick(() -> this.search(this.searchBox.getText()));
        //searchButton.enable();
        this.infoPanel.addWidget(searchButton);
        this.infoPanel.setHeight(this.searchBox.getY() + this.searchBox.getHeight() + 5);
        this.addWidget(this.infoPanel);

        // Layers panel
        this.layerPanel.removeAllWidgets();
        this.layerPanel.cancelAllScheduled();
        this.layerPanel.setSize(
                this.infoPanel.getWidth(),
                height - this.infoPanel.getHeight());
        this.layerPanel.setClosedX(-this.layerPanel.getWidth()).setClosedY(this.infoPanel.getHeight());
        this.layerPanel.setOpenX(0).setOpenY(this.layerPanel.getClosedY());
        this.layerPanel.addWidget(new TextWidget(
                this.layerPanel.getWidth() / 2, 7, 1,
                ofTranslation("terramap.terramapscreen.layerscreen.title"), TextAlignment.CENTER,
                this.getFont()));
        this.layerPanel.addWidget(new SpriteButtonWidget(
                this.layerPanel.getWidth() - 20, 5, 1,
                ButtonSprites.CROSS, this.layerPanel::close));
        this.layerList.setPosition(5f, 25f);
        this.layerList.setSize(this.layerPanel.getWidth() - 10, this.layerPanel.getHeight() - 55f);
        this.layerList.setContourColor(Color.DARK_OVERLAY).setContourSize(1f);
        this.layerListContainer.setWidth(this.layerList.getWidth() - 15);
        this.layerListContainer.setPosition(0, 0);
        this.layerPanel.addWidget(this.layerList);
        int buttonWidth = (int) ((this.layerList.getWidth() - 10) / 3);
        this.layerPanel.addWidget(
                new TextButtonWidget(
                        15f + buttonWidth*2, this.layerPanel.getHeight() - 25, 1,
                        buttonWidth,
                        translator.format("terramap.terramapscreen.layerscreen.new"),
                        this::openInitialNewLayerSelector).setEnabled(false));
        this.addWidget(this.layerPanel);

        // Style panel
        this.stylePanel.setSize(200, height);
        this.stylePanel.setClosedX(width + 1).setClosedY(0).setOpenX(width - this.stylePanel.getWidth()).setOpenY(0);
        this.stylePanel.setCloseOnClickOther(false);
        this.stylePanel.removeAllWidgets();
        this.styleScrollbar.setPosition(this.stylePanel.getWidth() - 15f, 0);
        this.styleScrollbar.setLength(height);
        this.stylePanel.addWidget(this.styleScrollbar);
        this.styleScrollbar.setViewPort(height / (this.backgroundStylePanelListContainer.getHeight() - 10f));
        if(this.styleScrollbar.getViewPort() >= 1) this.styleScrollbar.setProgress(0);
        this.stylePanel.addWidget(this.backgroundStylePanelListContainer);
        this.addWidget(this.stylePanel);

    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        super.onUpdate(mouseX, mouseY, parent);

        MapController controller = this.map.getController();
        this.setZoomRestrictions();
        this.zoomText.setText(ofPlainText(formatZoomLevelForDisplay(controller.getZoom())));

        this.compass.setAzimuth(controller.getRotation());

        GeoPointReadOnly mouseLocation = this.map.getMouseLocation();
        String formatScale = "-";
        String formatOrientation = "-";
        if(!WebMercatorUtil.PROJECTION_BOUNDS.contains(mouseLocation)) {
            this.distortionText.setText(ofTranslation("terramap.terramapscreen.information.distortion", "-", "-"));
        }

        this.playerGeoLocationText.setText(ofTranslation("terramap.terramapscreen.information.noplayer"));

        this.offsetWarning.setVisibility(this.map.getRasterBackgroundLayer().map(MapLayer::hasRenderingOffset).orElse(false));

        if(this.debugMode) {
            StringBuilder debugBuilder = new StringBuilder();
            Locale locale = Locale.US;
            debugBuilder.append(String.format(locale, "FPS: %s", getGameClient().currentFPS()));
            debugBuilder.append(String.format(locale, "\nClient: %s", Terramap.instance().version()));
            this.map.getRasterBackgroundLayer().ifPresent(layer -> {
                RasterTileSet backgroundStyle = layer.getTiledMap();
                debugBuilder.append(String.format(locale, "\nMap id: %s", backgroundStyle.getId()));
                debugBuilder.append(String.format(locale, "\nMap provider: %sv%s", backgroundStyle.getProvider(), backgroundStyle.getProviderVersion()));
                if (backgroundStyle instanceof CachingRasterTileSet) {
                    CachingRasterTileSet cachingMap = (CachingRasterTileSet) backgroundStyle;
                    debugBuilder.append(String.format(locale, "\nLoaded tiles: %d/%d/%d", cachingMap.getBaseLoad(), cachingMap.getLoadedCount(), CachingRasterTileSet.CACHE_SIZE));
                    if (cachingMap instanceof UrlRasterTileSet) {
                        UrlRasterTileSet urlMap = (UrlRasterTileSet) cachingMap;
                        String[] urls = urlMap.getUrlPatterns();
                        int showingIndex = (int) ((System.currentTimeMillis() / 3000) % urls.length);
                        debugBuilder.append(String.format(locale, "\nMap urls (%d) %s", urls.length, urls[showingIndex]));
                    }
                }
            });
            debugBuilder.append(String.format(locale, "\nScaling: %.2f/%s", this.map.getTileScaling(), getGameClient().scaleFactor()));
            debugBuilder.append("\n\n");
            debugBuilder.append("Locations: ")
                    .append(RED).append("center ")
                    .append(BLUE).append("center target ")
                    .append(GREEN).append("zoom target ")
                    .append(GOLD).append("rotation target ")
                    .append(RESET);
            debugBuilder.append('\n');
            this.debugText.setText(ofPlainText(debugBuilder.toString()));
            this.debugText.setAnchorY(this.getHeight() - this.debugText.getHeight());
        }
    }

    private void setZoomRestrictions() {
        MapController controller = this.map.getController();
        Optional<OnlineRasterMapLayer> backgroundLayer = this.map.getRasterBackgroundLayer();
        double minZoom = 0;
        double maxZoom = 25;
        double zoom = controller.getZoom();
        if (backgroundLayer.isPresent()) {
            RasterTileSet style = backgroundLayer.get().getTiledMap();
            minZoom = style.getMinZoom();
            if (!TerramapConfig.CLIENT.unlockZoom) {
                maxZoom = style.getMaxZoom();
            }
        }
        controller.setMinZoom(minZoom);
        controller.setMaxZoom(maxZoom);
        if (zoom < minZoom || zoom > maxZoom) {
            controller.setZoom(clamp(zoom, minZoom, maxZoom), true);
        }
        this.zoomInButton.setEnabled(controller.getZoom() < controller.getMaxZoom());
        this.zoomOutButton.setEnabled(controller.getZoom() > controller.getMinZoom());
    }

    private void toggleInfoPanel() {
        float x = this.panelButton.getX();
        float y = this.panelButton.getY();
        int z = this.panelButton.getZ();
        SpriteButtonWidget newButton;
        if(this.infoPanel.getTarget().equals(PanelTarget.OPENED)) {
            this.infoPanel.close();
            if(this.layerPanel.getTarget() == PanelTarget.OPENED) this.layerPanel.close();
            newButton = new SpriteButtonWidget(x, y, z, ButtonSprites.RIGHT, this::toggleInfoPanel);
        } else {
            this.infoPanel.open();
            newButton = new SpriteButtonWidget(x, y, z, ButtonSprites.LEFT, this::toggleInfoPanel);
        }
        newButton.setTooltip(this.panelButton.getTooltipText());
        this.infoPanel.removeWidget(this.panelButton);
        this.panelButton = newButton;
        this.infoPanel.addWidget(this.panelButton);
    }

    @Override
    public void onKeyTyped(char typedChar, Key key, WidgetContainer parent) {
        if(this.getFocusedWidget() == null || (!this.getFocusedWidget().equals(this.searchBox))) {
            MapController controller = this.map.getController();
            if(key == KEY_P) this.setDebugMode(!this.debugMode);
            if(key == KEY_F1) this.setF1Mode(!this.f1Mode);
            if(key == KEY_Z || key == KEY_UP) controller.moveMap(0, 30, true);
            if(key == KEY_S || key == KEY_DOWN) controller.moveMap(0, -30, true);
            if(key == KEY_D || key == KEY_RIGHT) controller.moveMap(-30, 0, true);
            if(key == KEY_Q || key == KEY_LEFT) controller.moveMap(30, 0, true);
            if(key == KEY_V) this.zoomInButton.getOnClick().run();
            if(key == KEY_B) this.zoomOutButton.getOnClick().run();
            if(key == KEY_M || key == KEY_ESCAPE) getGameClient().displayScreen(this.parent);
        } else {
            super.onKeyTyped(typedChar, key, parent);
        }
    }

    @Override
    public boolean shouldPauseGame() {
        /*
         * We cannot "pause the game" in single player.
         * It stops the integrated server from processing client packets, including the player movements packets.
         * But sending a tpll/tp command to a paused server actually processes the command and updates the player position.
         * That means that once the server resumes, it will try to process player movement packets from before the player was teleported,
         * and will check a MASSIVE area for collision.
         * 
         * There may be a potential DOS here?
         */
        return false;
    }

    private boolean search(String text) {
        Terramap.instance().logger().info("Geo search: {}", text);
        //TODO Search
        return true; // Let the search box loose focus
    }
    
    private MapLayer addMapLayer(String type) {
        int z = this.map.getLayers().stream()
                .filter(l -> !(l instanceof InputLayer))
                .map(MapLayer::getZ)
                .max(Integer::compareTo)
                .orElse(Integer.MIN_VALUE) + 1;
        MapLayer layer = this.map.createLayer(type);
        this.map.setLayerZ(layer, z);
        layer.setIsUserLayer(true);
        this.layerListContainer.init();
        return layer;
    }
    
    private void openInitialNewLayerSelector() {
        Map<String, Runnable> options = MapLayerRegistry.INSTANCE.getRegistrations().values().stream()
                        .filter(MapLayerRegistry.LayerRegistration::showsOnNewLayerMenu)
                        .collect(toMap(LayerRegistration::getNewLayerMenuTranslationKey, l -> () -> this.addMapLayer(l.getId())));
        this.scheduleBeforeNextUpdate(() ->
            getGameClient().displayPopup(new MultiChoicePopup("terramap.terramapscreen.layerscreen.newlayer", options))
        );
    }

    private class BackgroundStylePanelListContainer extends FlexibleWidgetContainer {

        final float mapWidth = 175;
        final float mapHeight = 100;
        
        final List<MapPreview> maps = new ArrayList<>();

        BackgroundStylePanelListContainer() {
            super(0, 0, 0, 0, 0);
            Widget lw = null;
            for(RasterTileSetProvider provider: RasterTileSetProvider.values()) {
                Throwable e = provider.getLastError();
                if(e == null) continue;
                float x = 0;
                float y = 0;
                if(lw != null) {
                    y = lw.getY() + lw.getHeight() + 5;
                }
                FailedMapLoadingNotice w = new FailedMapLoadingNotice(x, y, 50, mapWidth, mapHeight, provider, e);
                this.addWidget(w);
                lw = w;
            }
            ArrayList<RasterTileSet> maps = new ArrayList<>(TerramapClientContext.getContext().getRasterTileSets().values());
            maps.sort((m1, m2) -> Integer.compare(m2.getDisplayPriority(), m1.getDisplayPriority()));
            for(RasterTileSet map: maps) {
                MapPreview w = new MapPreview(50, map, m -> {
                    TerramapScreen.this.map.getRasterBackgroundLayer().ifPresent(l -> {
                        l.setTiledMap(m.previewLayer.getTiledMap());
                        l.setRenderingOffset(m.previewLayer.getRenderingOffset());
                    });
                    TerramapScreen.this.stylePanel.close();
                    TerramapScreen.this.layerListContainer.init();
                });
                w.setWidth(mapWidth);
                w.setHeight(mapHeight);
                if(lw == null) {
                    w.setPosition(0, 0);
                } else {
                    w.setPosition(0, lw.getY() + lw.getHeight() + 5);
                }
                this.addWidget(w);
                lw = w;
                this.maps.add(w);
            }
            float height = lw != null ? lw.getY() + lw.getY() + 10f : 0f;
            this.setSize(this.mapWidth, height);
        }

        @Override
        public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
            TerramapScreen.this.map.getRasterBackgroundLayer().ifPresent(bg -> {
                final MapController thisController = TerramapScreen.this.map.getController();
                for (MapPreview map : this.maps) {
                    MapController controller = map.getController();
                    controller.setZoom(thisController.getZoom(), false);
                    controller.moveLocationToCenter(thisController.getCenterLocation(), false);
                    map.setTileScaling(TerramapScreen.this.map.getTileScaling());
                    if (map.previewLayer.getTiledMap().getId().equals(bg.getTiledMap().getId())) {
                        map.previewLayer.setRenderingOffset(bg.getRenderingOffset());
                    }
                }
            });
            super.onUpdate(mouseX, mouseY, parent);
        }

        @Override
        public boolean onMouseWheeled(float mouseX, float mouseY, int amount, WidgetContainer parent) {
            if(TerramapScreen.this.styleScrollbar.getViewPort() < 1) {
                if(amount > 0) TerramapScreen.this.styleScrollbar.scrollBackward();
                else TerramapScreen.this.styleScrollbar.scrollForward();
            }
            return super.onMouseWheeled(mouseX, mouseY, amount, parent);
        }

        @Override
        public float getX() {
            return 5;
        }

        @Override
        public float getY() {
            return 5 - (this.getHeight() - TerramapScreen.this.getHeight()) * TerramapScreen.this.styleScrollbar.getProgress();
        }

    }

    private static class FailedMapLoadingNotice extends AbstractSolidWidget {

        private final RasterTileSetProvider provider;
        private final Throwable exception;

        public FailedMapLoadingNotice(float x, float y, int z, float width, float height, RasterTileSetProvider provider, Throwable e) {
            super(x, y, z, width, height);
            this.provider = provider;
            this.exception = e;
        }

        @Override
        public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            float width = this.getWidth();
            float height = this.getHeight();
            Translator translator = getGameClient().translator();
            Scissor scissor = context.scissor();
            scissor.push();
            scissor.setEnabled(true);
            scissor.cropSection(x, y, width, height);
            context.drawRectangle(x, y, x + width, y + height, YELLOW);
            context.drawRectangle(x + 4, y + 4, x + width - 4, y + height - 4, Color.DARK_GRAY);
            Font font = parent.getFont();
            font.drawCentered(x + width / 2, y + 8, translator.format("terramap.terramapscreen.mapstylefailed.title"), YELLOW, false);
            font.draw(x + 8, y + 16 + parent.getFont().height(), translator.format("terramap.terramapscreen.mapstylefailed.provider", this.provider), Color.WHITE, false);
            String[] lines = font.wrapToWidth(translator.format("terramap.terramapscreen.mapstylefailed.exception", this.exception), width - 16);
            font.drawLines(x + 8, y + 24 + font.height() * 2, WHITE, false, lines);
            scissor.pop();
        }

    }

    public void setF1Mode(boolean yesNo) {
        this.f1Mode = yesNo;
        this.infoPanel.setVisibility(!yesNo);
        this.stylePanel.setVisibility(!yesNo);
        this.layerPanel.setVisibility(!yesNo);
        this.closeButton.setVisibility(!yesNo);
        this.zoomInButton.setVisibility(!yesNo);
        this.zoomOutButton.setVisibility(!yesNo);
        this.styleButton.setVisibility(!yesNo);
        this.centerButton.setVisibility(!yesNo);
        if(this.zoomText != null) this.zoomText.setVisibility(!yesNo);
        this.map.setScaleVisibility(!yesNo);
    }

    public void setDebugMode(boolean yesNo) {
        this.debugMode = yesNo;
        if(this.debugText != null) this.debugText.setVisibility(yesNo);
        this.map.setDebugMode(yesNo);
    }

    private class MapPreview extends MapWidget {

        final Consumer<MapPreview> onClick;
        final OnlineRasterMapLayer previewLayer;

        public MapPreview(int z, RasterTileSet map, Consumer<MapPreview> onClick) {
            super(z, MapContext.PREVIEW, TerramapScreen.this.map.getTileScaling());
            this.setInteractive(false);
            this.setRightClickMenuEnabled(false);
            this.setCopyrightVisibility(false);
            this.setScaleVisibility(false);
            this.onClick = onClick;
            this.previewLayer = (OnlineRasterMapLayer) this.createLayer(MapLayerRegistry.RASTER_LAYER_ID);
            this.previewLayer.setTiledMap(map);
        }

        @Override
        public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            super.draw(context, x, y, mouseX, mouseY, hovered, focused, parent);
            Color textColor = hovered? Color.SELECTION: Color.WHITE;
            String text = this.previewLayer.getTiledMap().getLocalizedName(getGameClient().translator().language());
            float width = this.getWidth();
            float height = this.getHeight();
            context.drawRectangle(x, y, x + width, y + 4, Color.DARK_GRAY);
            context.drawRectangle(x, y + height - parent.getFont().height() - 4, x + width, y + height, Color.DARK_GRAY);
            context.drawRectangle(x, y, x + 4, y + height, Color.DARK_GRAY);
            context.drawRectangle(x + width - 4, y, x + width, y + height, Color.DARK_GRAY);
            parent.getFont().drawCentered(x + width/2, y + height - parent.getFont().height() - 2, text, textColor, true);

        }

        @Override
        public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
            if(mouseButton == 0) {
                this.onClick.accept(this);
            }
            return false;
        }

        @Override
        public String getTooltipText() {
            return this.previewLayer.getTiledMap().getId();
        }

        @Override
        public long getTooltipDelay() {
            return 0;
        }

    }

}
