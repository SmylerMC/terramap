package fr.thesmyler.terramap.gui.screens;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.RenderUtil;
import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.AbstractWidget;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.Scrollbar;
import fr.thesmyler.smylibgui.widgets.SlidingPanelWidget;
import fr.thesmyler.smylibgui.widgets.SlidingPanelWidget.PanelTarget;
import fr.thesmyler.smylibgui.widgets.buttons.AbstractButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.text.FontRendererContainer;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextComponentWidget;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.config.TerramapConfigScreen;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.FeatureVisibilityController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.MainPlayerMarker;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.CachingRasterTiledMap;
import fr.thesmyler.terramap.maps.IRasterTiledMap;
import fr.thesmyler.terramap.maps.TiledMapProvider;
import fr.thesmyler.terramap.maps.imp.UrlTiledMap;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TerramapScreen extends Screen {

	private GuiScreen parent;

	// Main map area widgets
	private MapWidget map; 
	private TexturedButtonWidget closeButton = new TexturedButtonWidget(50, IncludedTexturedButtons.CROSS);
	private TexturedButtonWidget zoomInButton = new TexturedButtonWidget(50, IncludedTexturedButtons.PLUS);
	private TexturedButtonWidget zoomOutButton = new TexturedButtonWidget(50, IncludedTexturedButtons.MINUS);
	private TexturedButtonWidget centerButton = new TexturedButtonWidget(50, IncludedTexturedButtons.CENTER);
	private TexturedButtonWidget styleButton = new TexturedButtonWidget(50, IncludedTexturedButtons.PAPER);
	
	// Info panel widgets
	private TextWidget zoomText;
	private SlidingPanelWidget infoPanel = new SlidingPanelWidget(70, 200);
	private TexturedButtonWidget panelButton = new TexturedButtonWidget(220, 5, 10, IncludedTexturedButtons.RIGHT, this::toggleInfoPannel);
	private TextWidget mouseGeoLocationText;
	private TextWidget mouseMCLocationText;
	private TextWidget distortionText;
	private TextWidget debugText;
	private TextWidget playerGeoLocationText;
	private TextFieldWidget searchBox = new TextFieldWidget(10, new FontRendererContainer(Minecraft.getMinecraft().fontRenderer));
	
	// Style panel
	private SlidingPanelWidget stylePanel = new SlidingPanelWidget(80, 200); 
	private Scrollbar styleScrollbar = new Scrollbar(100);

	// Screen states
	private boolean f1Mode = false;
	private boolean debugMode = false;
	
	private Map<String, IRasterTiledMap> backgrounds;

	public TerramapScreen(GuiScreen parent, Map<String, IRasterTiledMap> maps, TerramapScreenSavedState state) {
		this.parent = parent;
		this.backgrounds = maps;
		Collection<IRasterTiledMap> tiledMaps = this.backgrounds.values();
		IRasterTiledMap bg = tiledMaps.toArray(new IRasterTiledMap[0])[0];
		this.map = new MapWidget(10, this.backgrounds.getOrDefault("osm", bg), MapContext.FULLSCREEN, TerramapConfig.CLIENT.getEffectiveTileScaling());
		if(state != null) this.resumeFromSavedState(TerramapClientContext.getContext().getSavedScreenState());
		TerramapClientContext.getContext().registerForUpdates(true);
	}
	
	public TerramapScreen(GuiScreen parent, Map<String, IRasterTiledMap> maps) {
		this(parent, maps, null);
	}

	@Override
	public void initScreen() {
		this.removeAllWidgets();
		this.map.setX(0).setY(0).setWidth(this.getWidth()).setHeight(this.getHeight());
		this.map.setTileScaling(TerramapConfig.CLIENT.getEffectiveTileScaling());
		this.addWidget(this.map);

		// Map control buttons
		this.closeButton.setX(this.width - this.closeButton.getWidth() - 5).setY(5);
		this.closeButton.setOnClick(() -> {
			Minecraft.getMinecraft().displayGuiScreen(this.parent);
		});
		this.closeButton.setTooltip(I18n.format("terramap.terramapscreen.buttons.close.tooltip"));
		this.closeButton.enable();
		this.addWidget(this.closeButton);
		this.zoomInButton.setX(this.closeButton.getX()).setY(this.closeButton.getY() + closeButton.getHeight() + 15);
		this.zoomInButton.setOnClick(() -> this.map.zoom(1));
		this.zoomInButton.setTooltip(I18n.format("terramap.terramapscreen.buttons.zoomin.tooltip"));
		this.zoomInButton.enable();
		this.addWidget(this.zoomInButton);
		this.zoomText = new TextWidget(49, this.getFont());
		this.zoomText.setAnchorX(this.zoomInButton.getX() + this.zoomInButton.getWidth() / 2 + 1).setAnchorY(this.zoomInButton.getY() +  this.zoomInButton.getHeight() + 2);
		this.zoomText.setAlignment(TextAlignment.CENTER).setBackgroundColor(0xA0000000).setPadding(3);
		this.zoomText.setVisibility(!this.f1Mode);
		this.addWidget(this.zoomText);
		this.zoomOutButton.setX(this.zoomInButton.getX()).setY(this.zoomText.getY() + zoomText.getHeight() + 2);
		this.zoomOutButton.setOnClick(() -> this.map.zoom(-1));
		this.zoomOutButton.setTooltip(I18n.format("terramap.terramapscreen.buttons.zoomout.tooltip"));
		this.zoomOutButton.enable();
		this.addWidget(this.zoomOutButton);
		this.centerButton.setX(this.zoomOutButton.getX()).setY(this.zoomOutButton.getY() + this.zoomOutButton.getHeight() + 15);
		this.centerButton.setOnClick(() -> map.track(this.map.getMainPlayerMarker()));
		this.centerButton.enable();
		this.centerButton.setTooltip(I18n.format("terramap.terramapscreen.buttons.track.tooltip"));
		this.addWidget(this.centerButton);
		this.styleButton.setX(this.centerButton.getX()).setY(this.centerButton.getY() + this.centerButton.getHeight() + 5);
		this.styleButton.setOnClick(() -> this.stylePanel.open());
		this.styleButton.setTooltip(I18n.format("terramap.terramapscreen.buttons.style.tooltip"));
		this.styleButton.enable();
		this.addWidget(this.styleButton);
		this.debugText = new TextWidget(49, this.getFont());
		this.debugText.setAnchorX(3).setAnchorY(0);
		this.debugText.setAlignment(TextAlignment.RIGHT).setBackgroundColor(0xC0000000).setPadding(3);
		this.debugText.setVisibility(this.debugMode);
		this.addWidget(this.debugText);

		// Info panel
		this.infoPanel.removeAllWidgets();
		this.infoPanel.setWidth(240).setHeight(this.getHeight());
		this.infoPanel.setOpenX(0).setOpenY(0).setClosedX(-infoPanel.getWidth() + 25).setClosedY(0);
		this.panelButton.setTooltip(I18n.format("terramap.terramapscreen.buttons.info.tooltip"));
		this.infoPanel.addWidget(panelButton);
		TexturedButtonWidget openConfigButton = new TexturedButtonWidget(this.panelButton.getX(), this.panelButton.getY() + this.panelButton.getHeight() + 3, 100, IncludedTexturedButtons.WRENCH, this::openConfig);
		openConfigButton.setTooltip(I18n.format("terramap.terramapscreen.buttons.config.tooltip"));
		this.infoPanel.addWidget(openConfigButton);
		this.mouseGeoLocationText = new TextWidget(49, this.getFont());
		this.mouseGeoLocationText.setAnchorX(5).setAnchorY(5).setAlignment(TextAlignment.RIGHT);
		this.infoPanel.addWidget(this.mouseGeoLocationText);
		this.mouseMCLocationText = new TextWidget(49, this.getFont());
		this.mouseMCLocationText.setAnchorX(5).setAnchorY(this.mouseGeoLocationText.getAnchorY() + this.getFont().FONT_HEIGHT + 5).setAlignment(TextAlignment.RIGHT);
		this.infoPanel.addWidget(this.mouseMCLocationText);
		this.playerGeoLocationText = new TextWidget(49, this.getFont());
		this.playerGeoLocationText = new TextWidget(49, this.getFont());
		this.playerGeoLocationText.setAnchorX(5).setAnchorY(this.mouseMCLocationText.getAnchorY() + this.getFont().FONT_HEIGHT + 5).setAlignment(TextAlignment.RIGHT);
		this.infoPanel.addWidget(this.playerGeoLocationText);
		this.distortionText = new TextWidget(49, this.getFont());
		this.distortionText.setAnchorX(5).setAnchorY(this.playerGeoLocationText.getAnchorY() + this.getFont().FONT_HEIGHT + 5).setAlignment(TextAlignment.RIGHT);
		this.infoPanel.addWidget(this.distortionText);
		int y = this.distortionText.getY() + this.distortionText.getHeight() + 3;
		int lineHeight = 0;
		int x = 5;
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
		this.searchBox.setText(I18n.format("terramap.terramapscreen.search.wip")).disable();
		this.searchBox.setOnPressEnterCallback(this::search);
		this.infoPanel.addWidget(this.searchBox);
		TexturedButtonWidget searchButton = new TexturedButtonWidget(50, IncludedTexturedButtons.SEARCH);
		searchButton.setX(this.searchBox.getX() + this.searchBox.getWidth() + 2).setY(this.searchBox.getY() - 1);
		searchButton.setOnClick(() -> this.search(this.searchBox.getText()));
		//		searchButton.enable();
		this.infoPanel.addWidget(searchButton);
		this.infoPanel.setHeight(this.searchBox.getY() + this.searchBox.getHeight() + 5);
		this.addWidget(this.infoPanel);

		// Style panel
		this.stylePanel.setWidth(200).setHeight(this.getHeight());
		this.stylePanel.setClosedX(this.getWidth() + 1).setClosedY(0).setOpenX(this.getWidth() - this.stylePanel.getWidth()).setOpenY(0);
		this.stylePanel.setCloseOnClickOther(false);
		this.stylePanel.removeAllWidgets();
		this.styleScrollbar.setX(this.stylePanel.width - 15).setY(0).setHeight(this.getHeight());
		this.stylePanel.addWidget(this.styleScrollbar);
		StyleScreen s = new StyleScreen();
		this.styleScrollbar.setViewPort((double) this.height / (s.getHeight() - 10));
		if(this.styleScrollbar.getViewPort() >= 1) this.styleScrollbar.setProgress(0);
		this.stylePanel.addWidget(s);
		this.addWidget(this.stylePanel);

		if(!TerramapClientContext.getContext().isInstalledOnServer() && TerramapClientContext.getContext().getProjection() == null && TerramapClientContext.getContext().isOnEarthWorld()) {
			String warning = "";
			for(int i=1; I18n.hasKey("terramap.terramapscreen.projection_warning.line" + i); i++) {
				if(warning.length() > 0) warning += "\n";
				warning += I18n.format("terramap.terramapscreen.projection_warning.line" + i);
			}
			ITextComponent c = new TextComponentString(warning);
			Style style = new Style();
			style.setColor(TextFormatting.YELLOW);
			c.setStyle(style);
			TextComponentWidget warningWidget = new TextComponentWidget(150, 0, 1000, 300, c, TextAlignment.CENTER, 0xFFFFFFFF, true, this.getFont());
			warningWidget.setBackgroundColor(0xA0000000).setPadding(5).setAnchorY(this.height - warningWidget.getHeight());
			this.addWidget(warningWidget);
		}

		TerramapClientContext.getContext().setupMaps();
	}

	@Override
	public void onUpdate(Screen parent) {
		super.onUpdate(parent);

		GeographicProjection projection = TerramapClientContext.getContext().getProjection();

		this.zoomInButton.setEnabled(this.map.getZoom() < this.map.getMaxZoom());
		this.zoomOutButton.setEnabled(this.map.getZoom() > this.map.getMinZoom());
		this.zoomText.setText("" + Math.round(this.map.getZoom()));
		this.centerButton.setEnabled(!(this.map.getTracking() instanceof MainPlayerMarker));

		double mouseLat = this.map.getMouseLatitude();
		double mouseLon = this.map.getMouseLongitude();
		String formatX = "-";
		String formatZ = "-";
		String formatScale = "-"; 
		String formatOrientation = "-";
		if(Math.abs(mouseLat) > WebMercatorUtils.LIMIT_LATITUDE) {
			this.distortionText.setText(I18n.format("terramap.terramapscreen.information.distortion", "-", "-"));
			this.mouseGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.mouse_geo", "-", "-"));
			this.mouseMCLocationText.setText(I18n.format("terramap.terramapscreen.information.mouse_mc", "-", "-"));
		} else {
			String displayLat = GeoServices.formatGeoCoordForDisplay(mouseLat);
			String displayLon = GeoServices.formatGeoCoordForDisplay(mouseLon);
			this.mouseGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.mouse_geo", displayLat, displayLon));
			if(projection != null) {
				try {
					double[] pos = projection.fromGeo(mouseLon, mouseLat);
					formatX = "" + Math.round(pos[0]);
					formatZ = "" + Math.round(pos[1]);
				} catch(OutOfProjectionBoundsException e) {}
				this.mouseMCLocationText.setText(I18n.format("terramap.terramapscreen.information.mouse_mc", formatX, formatZ));
				try {
					try {
						double[] dist = projection.tissot(mouseLon, mouseLat);
						formatScale = "" + GeoServices.formatGeoCoordForDisplay(Math.sqrt(Math.abs(dist[0])));
						formatOrientation = "" + GeoServices.formatGeoCoordForDisplay(Math.toDegrees(dist[1]));
					} catch(OutOfProjectionBoundsException e) {}
					this.distortionText.setText(I18n.format("terramap.terramapscreen.information.distortion", formatScale, formatOrientation));
				} catch(NoSuchMethodError e) {
					this.distortionText.setText(I18n.format("terramap.terramapscreen.information.outdatedterra121"));
					this.distortionText.setBaseColor(0xFFFF0000);
				}
			} else {
				this.distortionText.setText(I18n.format("terramap.terramapscreen.information.distortion", "-", "-"));
				this.mouseMCLocationText.setText(I18n.format("terramap.terramapscreen.information.mouse_mc", "-", "-"));
			}
		}

		if(this.map.isTracking()) {
			Marker marker = this.map.getTracking();
			double markerLon = marker.getLongitude();
			double markerLat = marker.getLatitude();
			String markerName = marker.getDisplayName().getFormattedText();
			if(!Double.isFinite(markerLon) || !Double.isFinite(markerLat)) {
				this.playerGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.trackedoutsidemap", markerName));
			} else {
				String trackFormatLon = GeoServices.formatGeoCoordForDisplay(markerLon);
				String trackFormatLat = GeoServices.formatGeoCoordForDisplay(markerLat);
				this.playerGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.tracked", markerName, trackFormatLat, trackFormatLon));
			}
		} else if(this.map.getMainPlayerMarker() != null){
			Marker marker = this.map.getMainPlayerMarker();
			double markerLong = marker.getLongitude();
			double markerLat = marker.getLatitude();
			if(Double.isNaN(markerLong) || Double.isNaN(markerLat)) {
				this.playerGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.playerout"));
			} else {
				String formatedLon = GeoServices.formatGeoCoordForDisplay(marker.getLongitude());
				String formatedLat = GeoServices.formatGeoCoordForDisplay(marker.getLatitude());
				this.playerGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.playergeo", formatedLat, formatedLon));
			}
		} else {
			this.playerGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.noplayer"));
		}

		if(this.debugMode) {
			String dbText = "";
			TerramapClientContext srv = TerramapClientContext.getContext();
			dbText += "FPS: " + Minecraft.getDebugFPS();
			dbText += "\nClient: " + TerramapMod.getVersion();
			dbText += "\nServer: " + srv.getServerVersion();
			dbText += "\nSledgehammer: " + srv.getSledgehammerVersion();
			String proj = null;
			if(srv != null && srv.getGeneratorSettings() != null) {
				proj = srv.getGeneratorSettings().projection().toString();
			}
			dbText += "\nProjection: " + proj;
			dbText += "\nMap id: " + this.map.getBackgroundStyle().getId();
			dbText += "\nMap provider: " + this.map.getBackgroundStyle().getProvider() + " v" + this.map.getBackgroundStyle().getProviderVersion();
			if(this.map.getBackgroundStyle() instanceof CachingRasterTiledMap) {
				CachingRasterTiledMap<?> cachingMap = (CachingRasterTiledMap<?>) this.map.getBackgroundStyle();
				dbText += "\nLoaded tiles: " + cachingMap.getBaseLoad() + "/" + cachingMap.getLoadedCount() + "/" + cachingMap.getMaxLoad();
				if(cachingMap instanceof UrlTiledMap) {
					UrlTiledMap urlMap = (UrlTiledMap) cachingMap;
					String[] urls = urlMap.getUrlPatterns();
					dbText += "\nMap urls (" + urls.length + "): " + urls[(int) ((System.currentTimeMillis()/3000) % urls.length)];
				}
			}
			this.debugText.setText(dbText);
		}
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean screenHovered, boolean screenFocused, @Nullable Screen parent) {
		this.debugText.setAnchorY(this.getHeight() - this.debugText.getHeight());
		super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
	}

	private void toggleInfoPannel() {
		int x = this.panelButton.getX();
		int y = this.panelButton.getY();
		int z = this.panelButton.getZ();
		TexturedButtonWidget newButton;
		if(this.infoPanel.getTarget().equals(PanelTarget.OPENED)) {
			this.infoPanel.close();
			newButton = new TexturedButtonWidget(x, y, z, IncludedTexturedButtons.RIGHT, this::toggleInfoPannel);
		} else {
			this.infoPanel.open();
			newButton = new TexturedButtonWidget(x, y, z, IncludedTexturedButtons.LEFT, this::toggleInfoPannel);
		}
		newButton.setTooltip(this.panelButton.getTooltipText());
		this.infoPanel.removeWidget(this.panelButton);
		this.panelButton = newButton;
		this.infoPanel.addWidget(this.panelButton);
	}

	@Override
	public void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {
		if(this.getFocusedWidget() == null || !this.getFocusedWidget().equals(this.searchBox)) {
			if(keyCode == KeyBindings.TOGGLE_DEBUG.getKeyCode()) this.setDebugMode(!this.debugMode);
			if(keyCode == Keyboard.KEY_F1) this.setF1Mode(!this.f1Mode);
			if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode() || keyCode == Keyboard.KEY_UP) this.map.moveMap(0, 10);
			if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode() || keyCode == Keyboard.KEY_DOWN) this.map.moveMap(0, -10);
			if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode() || keyCode == Keyboard.KEY_RIGHT) this.map.moveMap(-10, 0);
			if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode() || keyCode == Keyboard.KEY_LEFT) this.map.moveMap(10, 0);
			if(keyCode == KeyBindings.ZOOM_IN.getKeyCode()) this.zoomInButton.getOnClick().run();
			if(keyCode == KeyBindings.ZOOM_OUT.getKeyCode()) this.zoomOutButton.getOnClick().run();
			if(keyCode == KeyBindings.OPEN_MAP.getKeyCode() || keyCode == Keyboard.KEY_ESCAPE) Minecraft.getMinecraft().displayGuiScreen(this.parent);
		} else {
			super.onKeyTyped(typedChar, keyCode, parent);
		}
	}

	public TerramapScreenSavedState saveToState() {
		String tracking = null;
		Marker trackingMarker = this.map.getTracking();
		if(trackingMarker != null) {
			tracking = trackingMarker.getIdentifier();
		}
		Map<String, Boolean> visibility = new HashMap<>();
		Map<String, FeatureVisibilityController> visibilityControllers = this.map.getVisibilityControllers();
		for(String key: visibilityControllers.keySet()) {
			visibility.put(key, visibilityControllers.get(key).getVisibility());
		}

		return new TerramapScreenSavedState(
				this.map.getZoom(),
				this.map.getCenterLongitude(),
				this.map.getCenterLatitude(),
				this.map.getBackgroundStyle().getId(),
				this.infoPanel.getTarget().equals(PanelTarget.OPENED),
				TerramapConfig.CLIENT.saveUiState ? this.debugMode : false,
				TerramapConfig.CLIENT.saveUiState ? this.f1Mode : false,
				visibility,
				tracking
			);
	}

	public void resumeFromSavedState(TerramapScreenSavedState state) {
		this.map.setBackground(this.backgrounds.getOrDefault(state.mapStyle, this.map.getBackgroundStyle()));
		this.map.setZoom(state.zoomLevel);
		this.map.setCenterLongitude(state.centerLongitude);
		this.map.setCenterLatitude(state.centerLatitude);
		this.map.restoreTracking(state.trackedMarker);
		this.infoPanel.setStateNoAnimation(state.infoPannel);
		TexturedButtonWidget newButton;
		if(this.infoPanel.getTarget().equals(PanelTarget.OPENED)) {
			int x = this.panelButton.getX();
			int y = this.panelButton.getY();
			int z = this.panelButton.getZ();
			newButton = new TexturedButtonWidget(x, y, z, IncludedTexturedButtons.LEFT, this::toggleInfoPannel);
			newButton.setTooltip(this.panelButton.getTooltipText());
			this.infoPanel.removeWidget(this.panelButton);
			this.panelButton = newButton;
		}
		this.infoPanel.addWidget(this.panelButton);
		this.setF1Mode(state.f1);
		this.setDebugMode(state.debug);
		for(FeatureVisibilityController c: this.map.getVisibilityControllers().values()) {
			if(state.visibilitySettings.containsKey(c.getSaveName())) c.setVisibility(state.visibilitySettings.get(c.getSaveName()));
		}
	}

	@Override
	public boolean doesGuiPauseGame() {
		/**
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

	private class StyleScreen extends Screen {

		int mapWidth = 175;
		int mapHeight = 100;

		StyleScreen() {
			super(0, 0, 0, 0, 0, BackgroundType.NONE);
			IWidget lw = null;
			for(TiledMapProvider provider: TiledMapProvider.values()) {
				Exception e = provider.getLastError();
				if(e == null) continue;
				int x = 0;
				int y = 0;
				if(lw != null) {
					y = lw.getY() + lw.getHeight() + 5;
				}
				FailedMapLoadingNotice w = new FailedMapLoadingNotice(x, y, 50, mapWidth, mapHeight, provider, e);
				this.addWidget(w);
				lw = w;
			}
			ArrayList<IRasterTiledMap> maps = new ArrayList<>(TerramapScreen.this.backgrounds.values());
			Collections.sort(maps, (m1, m2) -> Integer.compare(m2.getDisplayPriority(), m1.getDisplayPriority()));
			for(IRasterTiledMap map: maps) {
				MapWidget w = new MapPreview(50, map);
				w.setWidth(mapWidth).setHeight(mapHeight);
				if(lw == null) {
					w.setX(0).setY(0);
				} else {
					w.setX(0).setY(lw.getY() + lw.getHeight() + 5);
				}
				this.addWidget(w);
				lw = w;
			}
			this.height = lw.getY() + lw.getHeight() + 10;
			this.width = this.mapWidth;
		}

		@Override
		public void onUpdate(Screen parent) {
			for(IWidget w: this.widgets) {
				if(w instanceof MapPreview) {
					MapPreview map = (MapPreview)w;
					map.setZoom(TerramapScreen.this.map.getZoom());
					map.setCenterPosition(TerramapScreen.this.map.getCenterPosition());
				}
			}
			super.onUpdate(parent);
		}

		@Override
		public boolean onMouseWheeled(int mouseX, int mouseY, int amount, Screen parent) {
			if(TerramapScreen.this.styleScrollbar.getViewPort() < 1) {
				if(amount > 0) TerramapScreen.this.styleScrollbar.scrollUp();
				else TerramapScreen.this.styleScrollbar.scrollDown();
			}
			return super.onMouseWheeled(mouseX, mouseY, amount, parent);
		}

		@Override
		public int getX() {
			return 5;
		}

		@Override
		public int getY() {
			return 5 - (int) Math.round((this.height - TerramapScreen.this.getHeight()) * TerramapScreen.this.styleScrollbar.getProgress());
		}

	}
	
	private class FailedMapLoadingNotice extends AbstractWidget {

		private TiledMapProvider provider;
		private Exception exception;
		
		public FailedMapLoadingNotice(int x, int y, int z, int width, int height, TiledMapProvider provider, Exception e) {
			super(x, y, z, width, height);
			this.provider = provider;
			this.exception = e;
		}

		@Override
		public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
			boolean wasScissor = RenderUtil.isScissorEnabled();
			RenderUtil.setScissorState(true);
			RenderUtil.pushScissorPos();
			RenderUtil.scissor(x, y, this.width, this.height);
			int yellow = 0xFFFFCC00;
			int gray = 0xFF808080;
			Gui.drawRect(x, y, x + this.width, y + this.height, yellow);
			Gui.drawRect(x + 4, y + 4, x + this.width - 4, y + this.height - 4, gray);
			parent.drawCenteredString(parent.getFont().font, I18n.format("terramap.terramapscreen.mapstylefailed.title"), x + this.width / 2, y + 8, yellow);
			parent.getFont().drawString(I18n.format("terramap.terramapscreen.mapstylefailed.provider", this.provider), x + 8, y + 16 + parent.getFont().FONT_HEIGHT, 0xFFFFFFFF);
			parent.getFont().drawSplitString(I18n.format("terramap.terramapscreen.mapstylefailed.exception", this.exception), x + 8, y + 24 + parent.getFont().FONT_HEIGHT*2, this.width - 16, 0xFFFFFFFF);
			RenderUtil.popScissorPos();
			RenderUtil.setScissorState(wasScissor);
		}
		
	}

	@Override
	public void onGuiClosed() {
		TerramapClientContext.getContext().setSavedScreenState(this.saveToState()); //TODO Also save if minecraft is closed from the OS
		TerramapClientContext.getContext().saveSettings();
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
		List<FeatureVisibilityController> l = new ArrayList<>();
		l.addAll(this.map.getVisibilityControllers().values());
		return l;
	}

	private class MapPreview extends MapWidget {

		public MapPreview(int z, IRasterTiledMap map) {
			super(z, map, MapContext.PREVIEW, TerramapScreen.this.map.getTileScaling());
			this.setInteractive(false);
			this.setRightClickMenuEnabled(false);
			this.setCopyrightVisibility(false);
			this.setScaleVisibility(false);
		}

		@Override
		public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
			super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
			int color = 0xFF808080;
			int textColor = hovered? 0xFF90A0FF: 0xFFFFFFFF;
			String text = this.background.getMap().getLocalizedName(SmyLibGui.getLanguage());
			GuiScreen.drawRect(x, y, x + this.width, y + 4, color);
			GuiScreen.drawRect(x, y + this.height - parent.getFont().FONT_HEIGHT - 4, x + this.width, y + this.height, color);
			GuiScreen.drawRect(x, y, x + 4, y + this.height, color);
			GuiScreen.drawRect(x + this.width - 4, y, x + this.width, y + this.height, color);
			parent.getFont().drawCenteredString(x + this.width/2, y + this.height - parent.getFont().FONT_HEIGHT - 2, text, textColor, true);

		}

		@Override
		public boolean onClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
			if(mouseButton == 0) {
				TerramapScreen.this.map.setBackground(this.background.getMap());
				TerramapScreen.this.stylePanel.close();
			}
			return false;
		}

		@Override
		public String getTooltipText() {
			return this.background.getMap().getId();
		}

		@Override
		public long getTooltipDelay() {
			return 0;
		}

	}

}
