package fr.thesmyler.terramap.gui;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.Scrollbar;
import fr.thesmyler.smylibgui.widgets.SlidingPanelWidget;
import fr.thesmyler.smylibgui.widgets.SlidingPanelWidget.PanelTarget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.text.FontRendererContainer;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextComponentWidget;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.markers.MainPlayerMarker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.TiledMap;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public class TerramapScreen extends Screen {

	private GuiScreen parent;
	private Map<String, TiledMap> backgrounds;

	// Widgets
	private MapWidget map; 
	private TexturedButtonWidget closeButton = new TexturedButtonWidget(50, IncludedTexturedButtons.CROSS);
	private TexturedButtonWidget zoomInButton = new TexturedButtonWidget(50, IncludedTexturedButtons.PLUS);
	private TexturedButtonWidget zoomOutButton = new TexturedButtonWidget(50, IncludedTexturedButtons.MINUS);
	private TexturedButtonWidget centerButton = new TexturedButtonWidget(50, IncludedTexturedButtons.CENTER);
	private TexturedButtonWidget styleButton = new TexturedButtonWidget(50, IncludedTexturedButtons.PAPER);
	private TextWidget zoomText;
	private SlidingPanelWidget infoPanel = new SlidingPanelWidget(70, 200);
	private TexturedButtonWidget panelButton = new TexturedButtonWidget(220, 5, 10, IncludedTexturedButtons.RIGHT, this::toggleInfoPannel);
	private TextWidget mouseGeoLocationText;
	private TextWidget mouseMCLocationText;
	private TextWidget distortionText;
	private TextWidget debugText;
	private TextWidget playerGeoLocationText;
	private TextFieldWidget searchBox = new TextFieldWidget(10, new FontRendererContainer(Minecraft.getMinecraft().fontRenderer));
	private SlidingPanelWidget stylePanel = new SlidingPanelWidget(80, 200); 
	private Scrollbar styleScrollbar = new Scrollbar(100);
	
	private boolean f1Mode = false;
	private boolean debugMode = false;


	public TerramapScreen(GuiScreen parent, Map<String, TiledMap> maps) {
		this.parent = parent;
		this.backgrounds = maps;
		Collection<TiledMap> tiledMaps = this.backgrounds.values();
		TiledMap bg = tiledMaps.toArray(new TiledMap[0])[0];
		this.map = new MapWidget(10, this.backgrounds.getOrDefault("osm", bg), MapContext.FULLSCREEN, TerramapConfig.getEffectiveTileScaling());
		TerramapScreenSavedState state = TerramapRemote.getRemote().getSavedScreenState();
		if(state != null) this.resumeFromSavedState(TerramapRemote.getRemote().getSavedScreenState());
		TerramapRemote.getRemote().registerForUpdates(true);
	}

	@Override
	public void initScreen() {

		this.removeAllWidgets();
		this.map.setX(0).setY(0).setWidth(this.getWidth()).setHeight(this.getHeight());
		this.map.setTileScaling(TerramapConfig.getEffectiveTileScaling());
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

		// Info pannel
		this.infoPanel.removeAllWidgets();
		this.infoPanel.setWidth(240).setHeight(this.getHeight());
		this.infoPanel.setOpenX(0).setOpenY(0).setClosedX(-infoPanel.getWidth() + 25).setClosedY(0);
		this.panelButton.setTooltip(I18n.format("terramap.terramapscreen.buttons.info.tooltip"));
		this.infoPanel.addWidget(panelButton);
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
		for(MarkerController<?> controller: this.map.getMarkerControllers()) {
			if(!controller.showToggleButton()) continue;
			ToggleButtonWidget button = controller.getToggleButton();
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
		this.searchBox.setX(5).setY(y + lineHeight + 4).setWidth(187);
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
		
		if(!TerramapRemote.getRemote().isInstalledOnServer() && TerramapRemote.getRemote().getProjection() == null) {
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
	}

	@Override
	public void onUpdate(Screen parent) {
		super.onUpdate(parent);
		
		GeographicProjection projection = TerramapRemote.getRemote().getProjection();
		
		this.zoomInButton.setEnabled(this.map.getZoom() < this.map.getMaxZoom());
		this.zoomOutButton.setEnabled(this.map.getZoom() > this.map.getMinZoom());
		this.zoomText.setText("" + Math.round(this.map.getZoom()));
		this.centerButton.setEnabled(!(this.map.getTracking() instanceof MainPlayerMarker));

		double mouseLat = this.map.getMouseLatitude();
		double mouseLon = this.map.getMouseLongitude();
		String displayLat = GeoServices.formatGeoCoordForDisplay(mouseLat);
		String displayLon = GeoServices.formatGeoCoordForDisplay(mouseLon);
		String formatX = "-";
		String formatZ = "-";
		String formatScale = "-"; 
		String formatOrientation = "-";
		if(Math.abs(mouseLat) > WebMercatorUtils.LIMIT_LATITUDE) {
			displayLat = "-";
			displayLon = "-";
		} else if(projection != null) {
			double[] pos = projection.fromGeo(mouseLon, mouseLat);
			formatX = "" + Math.round(pos[0]);
			formatZ = "" + Math.round(pos[1]);
			double[] dist = projection.tissot(mouseLon, mouseLat, 0.0000001f);
			formatScale = "" + GeoServices.formatGeoCoordForDisplay(Math.sqrt(Math.abs(dist[0])));
			formatOrientation = "" + GeoServices.formatGeoCoordForDisplay(dist[1]*180.0/Math.PI);
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
				this.playerGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.playergeo", formatedLon, formatedLat));
			}
		} else {
			this.playerGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.noplayer"));
		}

		this.mouseGeoLocationText.setText(I18n.format("terramap.terramapscreen.information.mouse_geo", displayLat, displayLon));
		this.mouseMCLocationText.setText(I18n.format("terramap.terramapscreen.information.mouse_mc", formatX, formatZ));
		this.distortionText.setText(I18n.format("terramap.terramapscreen.information.distortion", formatScale, formatOrientation));
		
		if(this.debugMode) {
			String dbText = "";
			TerramapRemote srv = TerramapRemote.getRemote();
			dbText += "FPS: " + Minecraft.getDebugFPS();
			dbText += "\nClient: " + TerramapMod.getVersion();
			dbText += "\nServer: " + srv.getServerVersion();
			dbText += "\nSledgehammer: " + srv.getSledgehammerVersion();
			String proj = null;
			String orientation = null;
			if(srv != null && srv.getGeneratorSettings() != null) {
				proj = srv.getGeneratorSettings().settings.projection;
				orientation = srv.getGeneratorSettings().settings.orentation.name();
			}
			dbText += "\nProjection: " + proj;
			dbText += "\nOrientation: " + orientation;
			dbText += "\nCache queue: " + TerramapMod.cacheManager.getQueueSize();
			dbText += "\nMap id: " + this.map.getBackgroundStyle().getId();
			dbText += "\nMap provider: " + this.map.getBackgroundStyle().getProvider() + " v" + this.map.getBackgroundStyle().getProviderVersion();
			dbText += "\nMap url: " + this.map.getBackgroundStyle().getUrlPattern();
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
		if(keyCode == KeyBindings.TOGGLE_DEBUG.getKeyCode()) this.setDebugMode(!this.debugMode);
		if(keyCode == Keyboard.KEY_F1) this.setF1Mode(!this.f1Mode);
		if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindForward.getKeyCode() || keyCode == Keyboard.KEY_UP) this.map.moveMap(0, 10);
		if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindBack.getKeyCode() || keyCode == Keyboard.KEY_DOWN) this.map.moveMap(0, -10);
		if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindRight.getKeyCode() || keyCode == Keyboard.KEY_RIGHT) this.map.moveMap(-10, 0);
		if(keyCode == Minecraft.getMinecraft().gameSettings.keyBindLeft.getKeyCode() || keyCode == Keyboard.KEY_LEFT) this.map.moveMap(10, 0);
		if(keyCode == KeyBindings.OPEN_MAP.getKeyCode() || keyCode == Keyboard.KEY_ESCAPE) Minecraft.getMinecraft().displayGuiScreen(this.parent);
		super.onKeyTyped(typedChar, keyCode, parent);
	}
	
	public TerramapScreenSavedState saveToState() {
		String tracking = null;
		Marker trackingMarker = this.map.getTracking();
		if(trackingMarker != null) {
			tracking = trackingMarker.getIdentifier();
		}
		return new TerramapScreenSavedState(
				this.map.getZoom(),
				this.map.getCenterLongitude(),
				this.map.getCenterLatitude(),
				this.map.getBackgroundStyle().getId(),
				this.infoPanel.getTarget().equals(PanelTarget.OPENED),
				this.debugMode,
				this.f1Mode,
				this.map.getMarkersVisibility(), tracking);
	}
	
	public void resumeFromSavedState(TerramapScreenSavedState state) {
		this.map.setZoom(state.zoomLevel);
		this.map.setCenterLongitude(state.centerLongitude);
		this.map.setCenterLatitude(state.centerLatitude);
		this.map.restoreTracking(state.trackedMarker);
		this.map.setMarkersVisibility(state.markerSettings);
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
		this.map.setBackground(this.backgrounds.getOrDefault(state.mapStyle, this.map.getBackgroundStyle()));
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

		int mapsSize = 175;

		StyleScreen() {
			super(0, 0, 0, 0, 0, BackgroundType.NONE);
			MapWidget lw = null;
			ArrayList<TiledMap> maps = new ArrayList<TiledMap>(TerramapScreen.this.backgrounds.values());
			Collections.sort(maps, Collections.reverseOrder());
			for(TiledMap map: maps) {
				MapWidget w = new MapPreview(50, map);
				w.setWidth(mapsSize).setHeight(mapsSize);
				if(lw == null) {
					w.setX(0).setY(0);
				} else {
					w.setX(0).setY(lw.getY() + lw.getHeight() + 5);
				}
				this.addWidget(w);
				lw = w;
			}
			this.height = lw.getY() + lw.getHeight() + 10;
			this.width = this.mapsSize;
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
	
	@Override
	public void onGuiClosed() {
		TerramapRemote.getRemote().setSavedScreenState(this.saveToState()); //TODO Also save if minecraft is closed from the OS
		TerramapRemote.getRemote().saveSettings();
		TerramapRemote.getRemote().registerForUpdates(false);

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

	private class MapPreview extends MapWidget {

		public MapPreview(int z, TiledMap map) {
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
			int textColor = hovered? 0xFFC0C0FF: 0xFFFFFFFF;
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
