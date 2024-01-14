package fr.thesmyler.terramap.gui.screens;

import java.util.*;
import java.util.function.Consumer;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.util.*;
import fr.thesmyler.terramap.gui.widgets.map.*;
import fr.thesmyler.terramap.gui.widgets.map.layer.OnlineRasterMapLayer;
import fr.thesmyler.terramap.maps.raster.CachingRasterTiledMap;
import fr.thesmyler.terramap.maps.raster.imp.UrlTiledMap;
import fr.thesmyler.terramap.util.geo.*;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.minecraft.util.text.*;
import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.ScrollableWidgetContainer;
import fr.thesmyler.smylibgui.container.SlidingPanelWidget;
import fr.thesmyler.smylibgui.container.SlidingPanelWidget.PanelTarget;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.screen.BackgroundOption;
import fr.thesmyler.smylibgui.screen.MultiChoicePopupScreen;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.AbstractSolidWidget;
import fr.thesmyler.smylibgui.widgets.ChatWidget;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.ScrollbarWidget;
import fr.thesmyler.smylibgui.widgets.ScrollbarWidget.ScrollbarOrientation;
import fr.thesmyler.smylibgui.widgets.WarningWidget;
import fr.thesmyler.smylibgui.widgets.buttons.AbstractButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.config.TerramapConfigScreen;
import fr.thesmyler.terramap.gui.widgets.CircularCompassWidget;
import fr.thesmyler.terramap.gui.widgets.map.layer.RasterMapLayer;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.FeatureVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.MainPlayerMarker;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.raster.IRasterTiledMap;
import fr.thesmyler.terramap.maps.raster.TiledMapProvider;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.profiler.Profiler.Result;
import net.minecraft.util.ITabCompleter;

import static fr.thesmyler.smylibgui.SmyLibGui.getGameContext;
import static fr.thesmyler.terramap.gui.widgets.map.MapLayerRegistry.LayerRegistration;
import static fr.thesmyler.terramap.util.geo.GeoServices.formatZoomLevelForDisplay;
import static fr.thesmyler.terramap.util.math.Math.clamp;
import static java.util.stream.Collectors.toMap;


public class TerramapScreen extends Screen implements ITabCompleter {

    private final GuiScreen parent;

    private final ChatWidget chat = new ChatWidget(1000);

    // Main map area widgets
    private final MapWidget map;
    private final TexturedButtonWidget closeButton = new TexturedButtonWidget(50, IncludedTexturedButtons.CROSS);
    private final TexturedButtonWidget zoomInButton = new TexturedButtonWidget(50, IncludedTexturedButtons.PLUS);
    private final TexturedButtonWidget zoomOutButton = new TexturedButtonWidget(50, IncludedTexturedButtons.MINUS);
    private final TexturedButtonWidget centerButton = new TexturedButtonWidget(50, IncludedTexturedButtons.CENTER);
    private final TexturedButtonWidget styleButton = new TexturedButtonWidget(50, IncludedTexturedButtons.PAPER);
    private final CircularCompassWidget compass = new CircularCompassWidget(100, 100, 50, 100);
    private final WarningWidget offsetWarning = new WarningWidget(0, 0, 50);

    // Info panel widgets
    private TextWidget zoomText;
    private final SlidingPanelWidget infoPanel = new SlidingPanelWidget(70, 200);
    private TexturedButtonWidget panelButton = new TexturedButtonWidget(230, 5, 10, IncludedTexturedButtons.RIGHT, this::toggleInfoPanel);
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

    public TerramapScreen(GuiScreen parent, SavedMainScreenState state) {
        super(BackgroundOption.OVERLAY);
        this.parent = parent;
        this.map = new MapWidget(10, MapContext.FULLSCREEN, TerramapConfig.CLIENT.getEffectiveTileScaling());
        this.backgroundStylePanelListContainer = new BackgroundStylePanelListContainer();
        this.infoPanel.setContourColor(Color.DARKER_GRAY.withAlpha(.5f));
        this.layerPanel.setContourColor(Color.DARKER_GRAY.withAlpha(.5f));
        this.layerListContainer = new LayerListContainer(0, 0, 0, 100, this.map);
        this.layerList = new ScrollableWidgetContainer(0, 0, 10, 10, 10, this.layerListContainer);
        TerramapClientContext.getContext().registerForUpdates(true);
        this.restore(state);
        this.map.getRightClickMenu().addEntry(
                SmyLibGui.getTranslator().format("terramap.mapwidget.rclickmenu.offset"),
                () -> this.map.getRasterBackgroundLayer().ifPresent(l -> new LayerRenderingOffsetPopup(l).show())
        );
    }

    @Override
    public void initGui() {
        WidgetContainer content = this.getContent();
        content.removeAllWidgets();
        this.map.setPosition(0, 0);
        this.map.setSize(this.width, this.height);
        this.map.setTileScaling(TerramapConfig.CLIENT.getEffectiveTileScaling());

        content.addWidget(this.map);

        // Map control buttons
        this.closeButton.setX(this.width - this.closeButton.getWidth() - 5).setY(5);
        this.closeButton.setOnClick(() -> Minecraft.getMinecraft().displayGuiScreen(this.parent));
        this.closeButton.setTooltip(SmyLibGui.getTranslator().format("terramap.terramapscreen.buttons.close.tooltip"));
        this.closeButton.enable();
        content.addWidget(this.closeButton);
        this.compass.setOnClick(() -> this.map.getController().setRotation(0f, true));
        this.compass.setFadeAwayOnZero(true);
        this.compass.setTooltip(SmyLibGui.getTranslator().format("terramap.terramapscreen.buttons.compass.tooltip"));
        this.compass.setX(this.closeButton.getX());
        this.compass.setY(this.closeButton.getY() + this.closeButton.getHeight() + 5);
        this.compass.setSize(this.closeButton.getWidth());
        content.addWidget(this.compass);
        this.zoomInButton.setX(this.compass.getX()).setY(this.compass.getY() + this.compass.getHeight() + 5);
        this.zoomInButton.setOnClick(() -> {
            this.map.getController().setZoomStaticLocation(this.map.getController().getCenterLocation());
            this.map.getController().zoom(1, true);
        });
        this.zoomInButton.setTooltip(SmyLibGui.getTranslator().format("terramap.terramapscreen.buttons.zoomin.tooltip"));
        this.zoomInButton.enable();
        content.addWidget(this.zoomInButton);
        this.zoomText = new TextWidget(49, SmyLibGui.getDefaultFont());
        this.zoomText.setAnchorX(this.zoomInButton.getX() + this.zoomInButton.getWidth() / 2 + 1).setAnchorY(this.zoomInButton.getY() +  this.zoomInButton.getHeight() + 2);
        this.zoomText.setAlignment(TextAlignment.CENTER).setBackgroundColor(Color.DARKER_OVERLAY).setPadding(3);
        this.zoomText.setVisibility(!this.f1Mode);
        content.addWidget(this.zoomText);
        this.zoomOutButton.setX(this.zoomInButton.getX()).setY(this.zoomText.getY() + zoomText.getHeight() + 2);
        this.zoomOutButton.setOnClick(() -> {
            this.map.getController().setZoomStaticLocation(this.map.getController().getCenterLocation());
            this.map.getController().zoom(-1, true);
        });
        this.zoomOutButton.setTooltip(SmyLibGui.getTranslator().format("terramap.terramapscreen.buttons.zoomout.tooltip"));
        this.zoomOutButton.enable();
        content.addWidget(this.zoomOutButton);
        this.centerButton.setX(this.zoomOutButton.getX()).setY(this.zoomOutButton.getY() + this.zoomOutButton.getHeight() + 15);
        this.centerButton.setOnClick(() -> this.map.getController().track(this.map.getMainPlayerMarker()));
        this.centerButton.enable();
        this.centerButton.setTooltip(SmyLibGui.getTranslator().format("terramap.terramapscreen.buttons.track.tooltip"));
        content.addWidget(this.centerButton);
        this.styleButton.setX(this.centerButton.getX()).setY(this.centerButton.getY() + this.centerButton.getHeight() + 5);
        this.styleButton.setOnClick(this.stylePanel::open);
        this.styleButton.setTooltip(SmyLibGui.getTranslator().format("terramap.terramapscreen.buttons.style.tooltip"));
        this.styleButton.enable();
        content.addWidget(this.styleButton);
        this.offsetWarning.setTooltip(SmyLibGui.getTranslator().format("terramap.terramapscreen.warning.offset.tooltip"));
        content.addWidget(this.offsetWarning.setPosition(this.styleButton.getX(), this.styleButton.getY() + this.styleButton.getHeight() + 5));
        this.debugText = new TextWidget(49, Util.getSmallestFont());
        this.debugText.setAnchorX(3).setAnchorY(0);
        this.debugText.setAlignment(TextAlignment.RIGHT).setBackgroundColor(Color.DARKER_OVERLAY).setPadding(3);
        this.debugText.setVisibility(this.debugMode);
        content.addWidget(this.debugText);

        // Info panel
        Font infoFont = content.getFont();
        this.infoPanel.removeAllWidgets();
        this.infoPanel.setSize(250, this.height);
        this.infoPanel.setOpenX(0).setOpenY(0).setClosedX(-this.infoPanel.getWidth() + 25).setClosedY(0);
        this.panelButton.setTooltip(SmyLibGui.getTranslator().format("terramap.terramapscreen.buttons.info.tooltip"));
        this.infoPanel.addWidget(this.panelButton);
        TexturedButtonWidget openConfigButton = new TexturedButtonWidget(this.panelButton.getX(), this.panelButton.getY() + this.panelButton.getHeight() + 3, 100, IncludedTexturedButtons.WRENCH, this::openConfig);
        openConfigButton.setTooltip(SmyLibGui.getTranslator().format("terramap.terramapscreen.buttons.config.tooltip"));
        this.infoPanel.addWidget(openConfigButton);
        TexturedButtonWidget openLayerListButton = new TexturedButtonWidget(
                openConfigButton.getX(), openConfigButton.getY() + openConfigButton.getHeight() + 3, 100,
                IncludedTexturedButtons.PAPER, () -> {
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
        float x = 5;
        for(FeatureVisibilityController provider: this.getButtonProviders()) {
            if(!provider.showButton()) continue;
            AbstractButtonWidget button = provider.getButton();
            if(button == null) continue;
            if(x + button.getWidth() > this.infoPanel.getWidth() - 20) {
                x = 5;
                y += lineHeight + 3;
                lineHeight = 0;
            }
            lineHeight = Math.max(lineHeight, button.getHeight());
            button.setX(x);
            x += button.getWidth() + 3;
            button.setY(y);
            this.infoPanel.addWidget(button);
        }
        this.searchBox.setX(5).setY(y + lineHeight + 4).setWidth(186);
        this.searchBox.enableRightClickMenu();
        this.searchBox.setText(SmyLibGui.getTranslator().format("terramap.terramapscreen.search.wip")).disable();
        this.searchBox.setOnPressEnterCallback(this::search);
        this.infoPanel.addWidget(this.searchBox);
        TexturedButtonWidget searchButton = new TexturedButtonWidget(50, IncludedTexturedButtons.SEARCH);
        searchButton.setX(this.searchBox.getX() + this.searchBox.getWidth() + 2).setY(this.searchBox.getY() - 1);
        searchButton.setOnClick(() -> this.search(this.searchBox.getText()));
        //searchButton.enable();
        this.infoPanel.addWidget(searchButton);
        this.infoPanel.setHeight(this.searchBox.getY() + this.searchBox.getHeight() + 5);
        content.addWidget(this.infoPanel);

        // Layers panel
        this.layerPanel.removeAllWidgets();
        this.layerPanel.cancelAllScheduled();
        this.layerPanel.setSize(
                this.infoPanel.getWidth(),
                this.height - this.infoPanel.getHeight());
        this.layerPanel.setClosedX(-this.layerPanel.getWidth()).setClosedY(this.infoPanel.getHeight());
        this.layerPanel.setOpenX(0).setOpenY(this.layerPanel.getClosedY());
        this.layerPanel.addWidget(new TextWidget(
                this.layerPanel.getWidth() / 2, 7, 1,
                new TextComponentTranslation("terramap.terramapscreen.layerscreen.title"), TextAlignment.CENTER,
                content.getFont()));
        this.layerPanel.addWidget(new TexturedButtonWidget(
                this.layerPanel.getWidth() - 20, 5, 1,
                IncludedTexturedButtons.CROSS, this.layerPanel::close));
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
                        SmyLibGui.getTranslator().format("terramap.terramapscreen.layerscreen.new"),
                        this::openInitialNewLayerSelector));
        content.addWidget(this.layerPanel);

        // Style panel
        this.stylePanel.setSize(200, this.height);
        this.stylePanel.setClosedX(this.width + 1).setClosedY(0).setOpenX(this.width - this.stylePanel.getWidth()).setOpenY(0);
        this.stylePanel.setCloseOnClickOther(false);
        this.stylePanel.removeAllWidgets();
        this.styleScrollbar.setPosition(this.stylePanel.getWidth() - 15f, 0);
        this.styleScrollbar.setLength(this.height);
        this.stylePanel.addWidget(this.styleScrollbar);
        this.styleScrollbar.setViewPort(this.height / (this.backgroundStylePanelListContainer.getHeight() - 10f));
        if(this.styleScrollbar.getViewPort() >= 1) this.styleScrollbar.setProgress(0);
        this.stylePanel.addWidget(this.backgroundStylePanelListContainer);
        content.addWidget(this.stylePanel);

        if(TerramapConfig.CLIENT.chatOnMap) content.addWidget(this.chat);

        if(!TerramapClientContext.getContext().isInstalledOnServer() && TerramapClientContext.getContext().getProjection() == null && TerramapClientContext.getContext().isOnEarthWorld()) {
            StringBuilder warningBuilder = new StringBuilder();
            for(int i=1; SmyLibGui.getTranslator().hasKey("terramap.terramapscreen.projection_warning.line" + i); i++) {
                if(warningBuilder.length() > 0) warningBuilder.append('\n');
                warningBuilder.append(SmyLibGui.getTranslator().format("terramap.terramapscreen.projection_warning.line" + i));
            }
            ITextComponent c = new TextComponentString(warningBuilder.toString());
            Style style = new Style();
            style.setColor(TextFormatting.YELLOW);
            c.setStyle(style);
            TextWidget warningWidget = new TextWidget(150, 0, 1000, 300, c, TextAlignment.CENTER, Color.WHITE, true, SmyLibGui.getDefaultFont());
            warningWidget.setBackgroundColor(Color.DARKER_OVERLAY).setPadding(5).setAnchorY(this.height - warningWidget.getHeight());
            content.addWidget(warningWidget);
        }

        TerramapClientContext.getContext().setupMaps();
    }

    public void saveToState(SavedMainScreenState state) {
        state.map = this.map.save();
        state.f1 = TerramapConfig.CLIENT.saveUiState && this.f1Mode;
        state.debug = TerramapConfig.CLIENT.saveUiState && this.debugMode;
        state.infoPanel = this.infoPanel.getTarget().equals(PanelTarget.OPENED);
        state.layerPanel = this.layerPanel.getTarget().equals(PanelTarget.OPENED);
    }

    private void restore(SavedMainScreenState state) {
        this.map.restore(state.map);
        if (TerramapConfig.CLIENT.saveUiState) {
            this.f1Mode = state.f1;
            this.debugMode = state.debug;
        }
        this.infoPanel.setStateNoAnimation(state.infoPanel);
        if(this.infoPanel.getTarget().equals(PanelTarget.OPENED)) {
            float x = this.panelButton.getX();
            float y = this.panelButton.getY();
            int z = this.panelButton.getZ();
            TexturedButtonWidget newButton = new TexturedButtonWidget(x, y, z, IncludedTexturedButtons.LEFT, this::toggleInfoPanel);
            newButton.setTooltip(this.panelButton.getTooltipText());
            this.infoPanel.removeWidget(this.panelButton);
            this.panelButton = newButton;
            this.infoPanel.addWidget(this.panelButton);
        }
        this.layerPanel.setStateNoAnimation(state.layerPanel);

        this.setZoomRestrictions();
    }

    @Override
    public void onUpdate() {

        GeographicProjection projection = TerramapClientContext.getContext().getProjection();

        MapController controller = this.map.getController();
        this.setZoomRestrictions();
        this.zoomText.setText(new TextComponentString(formatZoomLevelForDisplay(controller.getZoom())));
        this.centerButton.setEnabled(!(controller.getTrackedMarker() instanceof MainPlayerMarker));

        this.compass.setAzimuth(controller.getRotation());

        GeoPointReadOnly mouseLocation = this.map.getMouseLocation();
        String formatScale = "-";
        String formatOrientation = "-";
        if(!WebMercatorUtil.PROJECTION_BOUNDS.contains(mouseLocation)) {
            this.distortionText.setText(new TextComponentTranslation("terramap.terramapscreen.information.distortion", "-", "-"));
        } else {
            if(projection != null) {
                try {
                    double[] dist = projection.tissot(mouseLocation.longitude(), mouseLocation.latitude());
                    formatScale = GeoServices.formatGeoCoordForDisplay(Math.sqrt(Math.abs(dist[0])));
                    formatOrientation = GeoServices.formatGeoCoordForDisplay(Math.toDegrees(dist[1]));
                } catch (OutOfProjectionBoundsException ignored) {
                }
                this.distortionText.setText(new TextComponentTranslation("terramap.terramapscreen.information.distortion", formatScale, formatOrientation));
            } else {
                this.distortionText.setText(new TextComponentTranslation("terramap.terramapscreen.information.distortion", "-", "-"));
            }
        }

        if(controller.isTracking()) {
            Marker marker = controller.getTrackedMarker();
            GeoPoint<?> markerLocation = marker.getLocation();
            String markerName = marker.getDisplayName().getFormattedText();
            if(markerLocation == null) {
                this.playerGeoLocationText.setText(new TextComponentTranslation("terramap.terramapscreen.information.trackedoutsidemap", markerName));
            } else {
                String trackFormatLon = GeoServices.formatGeoCoordForDisplay(markerLocation.longitude());
                String trackFormatLat = GeoServices.formatGeoCoordForDisplay(markerLocation.latitude());
                this.playerGeoLocationText.setText(new TextComponentTranslation("terramap.terramapscreen.information.tracked", markerName, trackFormatLat, trackFormatLon));
            }
        } else if(this.map.getMainPlayerMarker() != null){
            Marker marker = this.map.getMainPlayerMarker();
            GeoPoint<?> markerLocation = marker.getLocation();
            if(markerLocation == null) {
                this.playerGeoLocationText.setText(new TextComponentTranslation("terramap.terramapscreen.information.playerout"));
            } else {
                String formatedLon = GeoServices.formatGeoCoordForDisplay(markerLocation.longitude());
                String formatedLat = GeoServices.formatGeoCoordForDisplay(markerLocation.latitude());
                this.playerGeoLocationText.setText(new TextComponentTranslation("terramap.terramapscreen.information.playergeo", formatedLat, formatedLon));
            }
        } else {
            this.playerGeoLocationText.setText(new TextComponentTranslation("terramap.terramapscreen.information.noplayer"));
        }

        this.offsetWarning.setVisibility(this.map.getRasterBackgroundLayer().map(MapLayer::hasRenderingOffset).orElse(false));

        if(this.debugMode) {
            StringBuilder debugBuilder = new StringBuilder();
            Locale locale = Locale.US;
            TerramapClientContext srv = TerramapClientContext.getContext();
            EarthGeneratorSettings generationSettings = srv.getGeneratorSettings();
            debugBuilder.append(String.format(locale, "FPS: %s", Minecraft.getDebugFPS()));
            debugBuilder.append(String.format(locale, "\nClient: %s", TerramapMod.getVersion()));
            debugBuilder.append(String.format(locale, "\nServer: %s", srv.getServerVersion()));
            debugBuilder.append(String.format(locale, "\nSledgehammer: %s", srv.getSledgehammerVersion()));
            debugBuilder.append(String.format(locale, "\nProjection: %s", generationSettings != null ? generationSettings.projection() : null));
            this.map.getRasterBackgroundLayer().ifPresent(layer -> {
                IRasterTiledMap backgroundStyle = layer.getTiledMap();
                debugBuilder.append(String.format(locale, "\nMap id: %s", backgroundStyle.getId()));
                debugBuilder.append(String.format(locale, "\nMap provider: %sv%s", backgroundStyle.getProvider(), backgroundStyle.getProviderVersion()));
                if (backgroundStyle instanceof CachingRasterTiledMap) {
                    CachingRasterTiledMap<?> cachingMap = (CachingRasterTiledMap<?>) backgroundStyle;
                    debugBuilder.append(String.format(locale, "\nLoaded tiles: %d/%d/%d", cachingMap.getBaseLoad(), cachingMap.getLoadedCount(), cachingMap.getMaxLoad()));
                    if (cachingMap instanceof UrlTiledMap) {
                        UrlTiledMap urlMap = (UrlTiledMap) cachingMap;
                        String[] urls = urlMap.getUrlPatterns();
                        int showingIndex = (int) ((System.currentTimeMillis() / 3000) % urls.length);
                        debugBuilder.append(String.format(locale, "\nMap urls (%d) %s", urls.length, urls[showingIndex]));
                    }
                }
            });
            debugBuilder.append(String.format(locale, "\nScaling: %.2f/%s", this.map.getTileScaling(), getGameContext().getScaleFactor()));
            debugBuilder.append("\n\n");
            debugBuilder.append("Locations: ")
                    .append(TextFormatting.RED).append("center ")
                    .append(TextFormatting.BLUE).append("center target ")
                    .append(TextFormatting.GREEN).append("zoom target ")
                    .append(TextFormatting.GOLD).append("rotation target ")
                    .append(TextFormatting.RESET);
            debugBuilder.append('\n');
            this.buildProfilingResult(debugBuilder, "", "");
            this.debugText.setText(new TextComponentString(debugBuilder.toString()));
            this.debugText.setAnchorY(this.height - this.debugText.getHeight());
        }
    }

    private void setZoomRestrictions() {
        MapController controller = this.map.getController();
        Optional<OnlineRasterMapLayer> backgroundLayer = this.map.getRasterBackgroundLayer();
        double minZoom = 0;
        double maxZoom = 25;
        double zoom = controller.getZoom();
        if (backgroundLayer.isPresent()) {
            IRasterTiledMap style = backgroundLayer.get().getTiledMap();
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

    private void buildProfilingResult(StringBuilder builder, String sectionName, String padding) {
        List<Result> results = this.map.getProfiler().getProfilingData(sectionName);
        for(Result result: results) {
            String name = result.profilerName;
            if("".equals(name) || name.equals(sectionName) || (sectionName + ".").equals(name)) continue;
            long use = Math.round(result.usePercentage);
            if("unspecified".equals(name) && use >= 100) continue;
            builder.append('\n').append(padding).append(String.format(Locale.US, "%1$s: %2$d%%", name, use));
            this.buildProfilingResult(builder, name, padding + "  ");
        }
    }

    private void toggleInfoPanel() {
        float x = this.panelButton.getX();
        float y = this.panelButton.getY();
        int z = this.panelButton.getZ();
        TexturedButtonWidget newButton;
        if(this.infoPanel.getTarget().equals(PanelTarget.OPENED)) {
            this.infoPanel.close();
            if(this.layerPanel.getTarget() == PanelTarget.OPENED) this.layerPanel.close();
            newButton = new TexturedButtonWidget(x, y, z, IncludedTexturedButtons.RIGHT, this::toggleInfoPanel);
        } else {
            this.infoPanel.open();
            newButton = new TexturedButtonWidget(x, y, z, IncludedTexturedButtons.LEFT, this::toggleInfoPanel);
        }
        newButton.setTooltip(this.panelButton.getTooltipText());
        this.infoPanel.removeWidget(this.panelButton);
        this.panelButton = newButton;
        this.infoPanel.addWidget(this.panelButton);
    }

    @Override
    public void keyTyped(char typedChar, int keyCode) {
        if(this.getContent().getFocusedWidget() == null || (!this.getContent().getFocusedWidget().equals(this.searchBox) && !this.chat.isOpen())) {
            MapController controller = this.map.getController();
            if(keyCode == KeyBindings.TOGGLE_DEBUG.getKeyCode()) this.setDebugMode(!this.debugMode);
            if(keyCode == Keyboard.KEY_F1) this.setF1Mode(!this.f1Mode);
            if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode() || keyCode == Keyboard.KEY_UP) controller.moveMap(0, 30, true);
            if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode() || keyCode == Keyboard.KEY_DOWN) controller.moveMap(0, -30, true);
            if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode() || keyCode == Keyboard.KEY_RIGHT) controller.moveMap(-30, 0, true);
            if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode() || keyCode == Keyboard.KEY_LEFT) controller.moveMap(30, 0, true);
            if(keyCode == KeyBindings.ZOOM_IN.getKeyCode()) this.zoomInButton.getOnClick().run();
            if(keyCode == KeyBindings.ZOOM_OUT.getKeyCode()) this.zoomOutButton.getOnClick().run();
            if(keyCode == KeyBindings.OPEN_MAP.getKeyCode() || keyCode == Keyboard.KEY_ESCAPE) Minecraft.getMinecraft().displayGuiScreen(this.parent);
            if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindChat.getKeyCode()) {
                this.map.stopPassiveInputs();
                this.chat.setOpen(!this.chat.isOpen());
            }
        } else {
            super.keyTyped(typedChar, keyCode);
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
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
        TerramapMod.logger.info("Geo search: " + text);
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
        this.getContent().scheduleBeforeNextUpdate(() -> 
            new MultiChoicePopupScreen("Choose a type for the new  layer", options).show()
        );
    }

    private class BackgroundStylePanelListContainer extends FlexibleWidgetContainer {

        final float mapWidth = 175;
        final float mapHeight = 100;
        
        final List<MapPreview> maps = new ArrayList<>();

        BackgroundStylePanelListContainer() {
            super(0, 0, 0, 0, 0);
            IWidget lw = null;
            for(TiledMapProvider provider: TiledMapProvider.values()) {
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
            ArrayList<IRasterTiledMap> maps = new ArrayList<>(TerramapClientContext.getContext().getMapStyles().values());
            maps.sort((m1, m2) -> Integer.compare(m2.getDisplayPriority(), m1.getDisplayPriority()));
            for(IRasterTiledMap map: maps) {
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
            return 5 - (this.getHeight() - TerramapScreen.this.height) * TerramapScreen.this.styleScrollbar.getProgress();
        }

    }

    private static class FailedMapLoadingNotice extends AbstractSolidWidget {

        private final TiledMapProvider provider;
        private final Throwable exception;

        public FailedMapLoadingNotice(float x, float y, int z, float width, float height, TiledMapProvider provider, Throwable e) {
            super(x, y, z, width, height);
            this.provider = provider;
            this.exception = e;
        }

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            Scissor.push();
            Scissor.setScissorState(true);
            Scissor.scissorIntersecting(x, y, this.width, this.height);
            RenderUtil.drawRect(x, y, x + this.width, y + this.height, Color.YELLOW);
            RenderUtil.drawRect(x + 4, y + 4, x + this.width - 4, y + this.height - 4, Color.DARK_GRAY);
            parent.getFont().drawCenteredString(x + this.width / 2, y + 8, SmyLibGui.getTranslator().format("terramap.terramapscreen.mapstylefailed.title"), Color.YELLOW, false);
            parent.getFont().drawString(x + 8, y + 16 + parent.getFont().height(), SmyLibGui.getTranslator().format("terramap.terramapscreen.mapstylefailed.provider", this.provider), Color.WHITE, false);
            parent.getFont().drawSplitString(x + 8, y + 24 + parent.getFont().height()*2, SmyLibGui.getTranslator().format("terramap.terramapscreen.mapstylefailed.exception", this.exception), this.width - 16, Color.WHITE, false);
            Scissor.pop();
        }

    }

    @Override
    public void onGuiClosed() {
        //TODO Also save if minecraft is closed from the OS
        this.saveToState(TerramapClientContext.getContext().getSavedState().mainScreen);
        TerramapClientContext.getContext().saveState();
        TerramapClientContext.getContext().registerForUpdates(false);
    }

    public void setF1Mode(boolean yesNo) {
        this.f1Mode = yesNo;
        this.infoPanel.setVisibility(!yesNo);
        this.stylePanel.setVisibility(!yesNo);
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

    private void openConfig() {
        Minecraft.getMinecraft().displayGuiScreen(new TerramapConfigScreen(this));
    }

    private Collection<FeatureVisibilityController> getButtonProviders() {
        return Collections.unmodifiableCollection(this.map.getVisibilityControllers().values());
    }

    private class MapPreview extends MapWidget {

        final Consumer<MapPreview> onClick;
        final OnlineRasterMapLayer previewLayer;

        public MapPreview(int z, IRasterTiledMap map, Consumer<MapPreview> onClick) {
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
        public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
            Color textColor = hovered? Color.SELECTION: Color.WHITE;
            String text = this.previewLayer.getTiledMap().getLocalizedName(SmyLibGui.getGameContext().getLanguage());
            float width = this.getWidth();
            float height = this.getHeight();
            RenderUtil.drawRect(x, y, x + width, y + 4, Color.DARK_GRAY);
            RenderUtil.drawRect(x, y + height - parent.getFont().height() - 4, x + width, y + height, Color.DARK_GRAY);
            RenderUtil.drawRect(x, y, x + 4, y + height, Color.DARK_GRAY);
            RenderUtil.drawRect(x + width - 4, y, x + width, y + height, Color.DARK_GRAY);
            parent.getFont().drawCenteredString(x + width/2, y + height - parent.getFont().height() - 2, text, textColor, true);

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

    @Override
    public void setCompletions(String... newCompletions) {
        this.chat.setCompletions(newCompletions);
    }

}
