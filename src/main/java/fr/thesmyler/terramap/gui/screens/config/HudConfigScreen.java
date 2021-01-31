package fr.thesmyler.terramap.gui.screens.config;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.screen.WindowedScreen;
import fr.thesmyler.smylibgui.widgets.SlidingPanelWidget;
import fr.thesmyler.smylibgui.widgets.SlidingPanelWidget.PanelTarget;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.IntegerSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.OptionSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.screens.config.TerramapConfigScreen.TileScalingOption;
import fr.thesmyler.terramap.gui.widgets.RibbonCompassWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AnimalMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MobMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.maps.IRasterTiledMap;
import io.github.terra121.projection.GeographicProjection;
import io.github.terra121.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;

//TODO Localize
//FIXME Compass position changes
public class HudConfigScreen extends Screen {
	
	private MapWidget minimap = new MapWidget(0, TerramapRemote.getRemote().getMapStyles().values().iterator().next(), MapContext.MINIMAP, TerramapConfig.tileScaling);
	private WindowedScreen minimapWindow = new WindowedScreen(BackgroundType.NONE, this.minimap, "Minimap", 15);
	private CompassScreen compassScreen = new CompassScreen();
	private WindowedScreen compassWindow = new WindowedScreen(BackgroundType.NONE, this.compassScreen, "Compass", 16);
	private OptionSliderWidget<TileScalingOption> tileScalingSlider = new OptionSliderWidget<TileScalingOption>(10, TileScalingOption.values());
	private IntegerSliderWidget zoomSlider = new IntegerSliderWidget(11, 0, 20, 10);
	private OptionSliderWidget<MapStyleSliderEntry> styleSlider;
	private MapStyleSliderEntry[] mapStyles = new MapStyleSliderEntry[0];
	private ToggleButtonWidget otherPlayersButton = new ToggleButtonWidget(10, false);
	private ToggleButtonWidget entitiesButton = new ToggleButtonWidget(10, false);
	private ToggleButtonWidget minimapButton = new ToggleButtonWidget(10, false);
	private ToggleButtonWidget compassButton = new ToggleButtonWidget(10, false);
	private ToggleButtonWidget directionsButton = new ToggleButtonWidget(10, false); //TODO Implement
	private SlidingPanelWidget buttonPanel = new SlidingPanelWidget(20, 100);
	private SlidingPanelWidget settingsPanel = new SlidingPanelWidget(20, 100);
	private int lastWidth, lastHeight = -1; // Used to re-calculate the relative minimap position when the game's window is resized
	
	public HudConfigScreen() {
		super(BackgroundType.NONE);
		List<MapStyleSliderEntry> maps = new ArrayList<>();
		TerramapRemote.getRemote().getMapStyles().values().stream()
			.filter(m -> m.isAllowedOnMinimap())
			.forEachOrdered(m -> maps.add(new MapStyleSliderEntry(m)));
		this.mapStyles = maps.toArray(this.mapStyles);
		this.styleSlider = new OptionSliderWidget<>(0, 0, 15, 10, this.mapStyles);
		this.minimap.setInteractive(false);
		this.minimap.setCopyrightVisibility(false);
		this.minimap.setRightClickMenuEnabled(false);
		this.minimap.setScaleVisibility(false);
		this.minimap.scheduleAtUpdate(() -> {
			if(TerramapRemote.getRemote().getProjection() != null) {
				this.minimap.track(this.minimap.getMainPlayerMarker());
			}
		});
		this.zoomSlider.setOnChange(this.minimap::setZoom);
		this.styleSlider.setOnChange(map -> {
			this.minimap.setBackground(map.map);
			this.zoomSlider.setMin(map.map.getMinZoom());
			this.zoomSlider.setMax(map.map.getMaxZoom());

		});
		this.tileScalingSlider.setOnChange(v -> {
			if(v == TileScalingOption.AUTO) this.minimap.setTileScaling(SmyLibGui.getMinecraftGuiScale());
			else this.minimap.setTileScaling(v.value);
		});
		this.minimapButton.setOnActivate(() -> this.minimapWindow.setVisibility(true)).setOnDeactivate(() -> this.minimapWindow.setVisibility(false));
		this.compassButton.setOnActivate(() -> this.compassWindow.setVisibility(true)).setOnDeactivate(() -> this.compassWindow.setVisibility(false));
		this.otherPlayersButton.setOnActivate(() -> this.minimap.trySetMarkersVisibility(OtherPlayerMarkerController.ID, true)).setOnDeactivate(() -> this.minimap.trySetMarkersVisibility(OtherPlayerMarkerController.ID, false));
		this.entitiesButton.setOnActivate(() -> this.minimap.trySetMarkersVisibility(AnimalMarkerController.ID, true).trySetMarkersVisibility(MobMarkerController.ID, true))
				   .setOnDeactivate(() -> this.minimap.trySetMarkersVisibility(AnimalMarkerController.ID, false).trySetMarkersVisibility(MobMarkerController.ID, false));
		this.minimapWindow.setEnableTopBar(false);
		this.minimapWindow.setCenterDragColor(0x00000000);
		this.minimapWindow.setEnableCenterDrag(true);
		this.compassWindow.setHeight(this.compassScreen.getHeight());
		this.compassWindow.setAllowVerticalResize(false);
		this.compassWindow.setEnableTopBar(false);
		this.compassWindow.setEnableCenterDrag(true);
		this.compassWindow.setMinInnerHeight(1);
		this.compassWindow.trySetInnerDimensions(50, this.compassScreen.compass.getHeight());
		this.compassWindow.setCenterDragColor(0x00000000);
		this.reset();
	}
	
	@Override
	public void initScreen() {
		this.removeAllWidgets();
		this.buttonPanel.removeAllWidgets();
		this.settingsPanel.removeAllWidgets();
		if(this.lastHeight <= 0 || this.lastWidth <= 0) {
			this.recalcWidgetsPos();
		} else {
			this.minimapWindow.setX(Math.round((float)this.minimapWindow.getX() * this.width / this.lastWidth));
			this.minimapWindow.setY(Math.round((float)this.minimapWindow.getY() * this.height / this.lastHeight));
			this.minimapWindow.setWidth(Math.round((float)this.minimapWindow.getWidth() / this.lastWidth * this.width));
			this.minimapWindow.setHeight(Math.round((float)this.minimapWindow.getHeight() / this.lastHeight * this.height));
			double t = this.tileScalingSlider.getCurrentOption().value;
			if(t == 0) this.minimap.setTileScaling(SmyLibGui.getMinecraftGuiScale());
			else this.minimap.setTileScaling(t);
		}
		this.minimapWindow.initScreen();
		this.addWidget(this.minimapWindow);
		
		// Buttons
		this.buttonPanel.addWidget(new TextButtonWidget(3, 3, 10, 54, "Cancel", this::close));
		this.buttonPanel.addWidget(new TextButtonWidget(58, 3, 10, 54, "Reset", this::reset));
		this.buttonPanel.addWidget(new TextButtonWidget(113, 3, 10, 54, "Save", this::saveAndClose));
		this.buttonPanel.addWidget(new TexturedButtonWidget(168, 3, 10, IncludedTexturedButtons.OPTIONS_20, this::toggleSettingsPanel));
		
		// Boolean lines
		LinkedList<TextWidget> buttonsTexts = new LinkedList<>();
		buttonsTexts.add(new TextWidget("Minimap: ", 10, true, this.getFont()));
		buttonsTexts.add(new TextWidget("Compass: ", 10, true, this.getFont()));
		buttonsTexts.add(new TextWidget("Players: ", 10, true, this.getFont()));
		buttonsTexts.add(new TextWidget("Entities: ", 10, true, this.getFont()));
		buttonsTexts.add(new TextWidget("Player Direction: ", 10, true, this.getFont()));
		LinkedList<ToggleButtonWidget> buttons = new LinkedList<>();
		buttons.add(this.minimapButton);
		buttons.add(this.compassButton);
		buttons.add(this.otherPlayersButton);
		buttons.add(this.entitiesButton);
		buttons.add(this.directionsButton);
		int lineY = 3;
		int textButtonSpace = 3;
		int lineSpace = 4;
		ToggleButtonWidget lastButton = null;
		while(buttonsTexts.size() > 0) {
			int lineWidth = 0;
			int lineCount = 0;
			for(; lineCount < buttonsTexts.size(); lineCount++) {
				TextWidget text = buttonsTexts.get(lineCount);
				ToggleButtonWidget button = buttons.get(lineCount);
				int newWidth = lineWidth + text.getWidth() + textButtonSpace +button.getWidth();
				if(lineCount > 0 && newWidth > 0.75 * this.width) break;
				lineWidth = newWidth;
			}
			int padding = (this.width  - lineWidth) / (lineCount + 1);
			int x = padding;
			for(int i=0; i<lineCount; i++) {
				TextWidget text = buttonsTexts.pop();
				ToggleButtonWidget button = buttons.pop();
				text.setAnchorX(x).setAnchorY(lineY + 3);
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
		this.settingsPanel.addWidget(this.tileScalingSlider.setX(this.width / 2 - 153).setY(lastButton.getY() + lastButton.getHeight() + lineSpace).setWidth(100).setDisplayPrefix("Scaling: ")); 
		this.settingsPanel.addWidget(this.zoomSlider.setWidth(100).setX(this.width/2 - 50).setY(this.tileScalingSlider.getY()).setDisplayPrefix("Zoom: "));
		this.settingsPanel.addWidget(this.styleSlider.setWidth(100).setX(this.width/2 + 53).setY(this.tileScalingSlider.getY()));
		
		// Setup panels
		this.buttonPanel.setBackgroundColor(0xB0000000);
		this.buttonPanel.setWidth(190).setHeight(25);
		this.settingsPanel.setWidth(this.width);
		this.settingsPanel.setHeight(this.styleSlider.getY() + this.styleSlider.getHeight() + 3);
		this.settingsPanel.setClosedX(0).setOpenX(0).setClosedY(this.height);
		this.buttonPanel.setClosedX((this.width - this.buttonPanel.getWidth()) / 2).setClosedY(this.height - this.buttonPanel.getHeight());	
		this.buttonPanel.setOpenX(this.buttonPanel.getClosedX()).setOpenY(this.height - this.settingsPanel.getHeight() - this.buttonPanel.getHeight());
		this.settingsPanel.setOpenY(this.height - this.settingsPanel.getHeight());
		
//		TextWidget explain = new TextWidget("Move and resize the minimap using the preview window.", this.width/2, this.height - 90, 10, TextAlignment.CENTER, this.getFont());
//		this.addWidget(explain.setMaxWidth(200).setAnchorY(this.height - 80 - explain.getHeight()));
		
		this.addWidget(this.buttonPanel);
		this.addWidget(this.settingsPanel);
		this.addWidget(this.compassWindow);
		this.lastHeight = this.height;
		this.lastWidth = this.width;
	}
	
	private void saveAndClose() {
		TerramapConfig.minimapEnable = this.minimapButton.getState();
		TerramapConfig.minimapZoomLevel = (int) this.zoomSlider.getValue();
		TerramapConfig.minimapShowEntities = this.entitiesButton.getState();
		TerramapConfig.minimapShowOtherPlayers = this.otherPlayersButton.getState();
		TerramapConfig.minimapStyle = this.styleSlider.getCurrentOption().map.getId();
		TerramapConfig.minimapTileScaling = this.tileScalingSlider.getCurrentOption().value;
		TerramapConfig.minimapPosX = (float)this.minimapWindow.getX() / this.getWidth() * 100;
		TerramapConfig.minimapPosY = (float)this.minimapWindow.getY() / this.getHeight() * 100;
		TerramapConfig.minimapWidth = (float)this.minimapWindow.getWidth() / this.getWidth() * 100;
		TerramapConfig.minimapHeight = (float)this.minimapWindow.getHeight() / this.getHeight() * 100;
		TerramapConfig.compassVisibility = this.compassButton.getState();
		TerramapConfig.compassX = (float)this.compassWindow.getX() / this.getWidth() * 100;
		TerramapConfig.compassY = (float)this.compassWindow.getY() / this.getHeight() * 100;
		TerramapConfig.compassWidth = (float)this.compassWindow.getWidth() / this.getWidth() * 100;
		TerramapConfig.sync();
		this.close();
	}
	
	private void close() {
		Minecraft.getMinecraft().displayGuiScreen(null);
	}
	
	private void reset() {
		this.minimapWindow.setVisibility(TerramapConfig.minimapEnable);
		this.compassWindow.setVisibility(TerramapConfig.compassVisibility);
		this.minimapButton.setState(TerramapConfig.minimapEnable);
		this.compassButton.setState(TerramapConfig.compassVisibility);
		this.zoomSlider.setValue(TerramapConfig.minimapZoomLevel);
		this.otherPlayersButton.setState(TerramapConfig.minimapShowOtherPlayers);
		this.minimap.trySetMarkersVisibility(OtherPlayerMarkerController.ID, TerramapConfig.minimapShowOtherPlayers);
		this.entitiesButton.setState(TerramapConfig.minimapShowEntities);
		this.minimap.trySetMarkersVisibility(AnimalMarkerController.ID, TerramapConfig.minimapShowEntities);
		this.minimap.trySetMarkersVisibility(MobMarkerController.ID, TerramapConfig.minimapShowEntities);
		for(MapStyleSliderEntry map: this.mapStyles) if(map.map.getId().equals(TerramapConfig.minimapStyle)) {
			this.styleSlider.setCurrentOption(map);
			break;
		}
		this.tileScalingSlider.setCurrentOption(TileScalingOption.getFromValue(TerramapConfig.minimapTileScaling));
		this.recalcWidgetsPos();
	}
	
	private void recalcWidgetsPos() {
		this.minimapWindow.setX(Math.round(this.width * TerramapConfig.minimapPosX / 100));
		this.minimapWindow.setY(Math.round(this.height * TerramapConfig.minimapPosY / 100));
		this.minimapWindow.setWidth(Math.round(this.width * TerramapConfig.minimapWidth / 100));
		this.minimapWindow.setHeight(Math.round(this.height * TerramapConfig.minimapHeight / 100));
		this.compassWindow.setX(Math.round(this.width * TerramapConfig.compassX / 100));
		this.compassWindow.setY(Math.round(this.height * TerramapConfig.compassY / 100));
		this.compassWindow.setWidth(Math.round(this.width * TerramapConfig.compassWidth / 100));
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
	
	private class CompassScreen extends Screen {
		
		RibbonCompassWidget compass = new RibbonCompassWidget(0, 0, 0, 30);
		
		CompassScreen() {
			super(BackgroundType.NONE);
			this.addWidget(this.compass);
			this.scheduleAtUpdate(() -> {
				GeographicProjection p = TerramapRemote.getRemote().getProjection();
				if(p != null) {
					double x = Minecraft.getMinecraft().player.posX;
					double z = Minecraft.getMinecraft().player.posZ;
					float a = Minecraft.getMinecraft().player.rotationYaw;
					try {
						compass.setAzimuth(p.azimuth(x, z, a));
					} catch (OutOfProjectionBoundsException e) {
					}
				}
			});
		}

		@Override
		public void draw(int x, int y, int mouseX, int mouseY, boolean screenHovered, boolean screenFocused, Screen parent) {
			this.compass.setWidth(this.width);
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
