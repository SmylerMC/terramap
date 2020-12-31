package fr.thesmyler.terramap.gui.config;

import java.util.Map;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.screen.WindowedScreen;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.IntegerSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.OptionSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.config.TerramapConfigScreen.TileScalingOption;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AnimalMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MobMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.maps.TiledMap;
import net.minecraft.client.Minecraft;

//TODO Localize
public class HudConfigScreen extends Screen {
	
	private MapWidget minimap = new MapWidget(0, TerramapRemote.getRemote().getMapStyles().values().iterator().next(), MapContext.MINIMAP, TerramapConfig.tileScaling);
	private WindowedScreen minimapWindow = new WindowedScreen(BackgroundType.NONE, minimap, "Minimap", 15);
	private OptionSliderWidget<TileScalingOption> tileScalingSlider = new OptionSliderWidget<TileScalingOption>(10, TileScalingOption.values());
	private IntegerSliderWidget zoomSlider = new IntegerSliderWidget(11, 0, 20, 10);
	private OptionSliderWidget<String> styleSlider;
	private ToggleButtonWidget otherPlayers = new ToggleButtonWidget(10, false);
	private ToggleButtonWidget entities = new ToggleButtonWidget(10, false);
	private int lastWidth, lastHeight = -1; // Used to re-calculate the relative minimap position when the game's window is resized
	
	public HudConfigScreen() {
		super(BackgroundType.NONE);
		this.styleSlider = new OptionSliderWidget<String>(0, 0, 0, 10, TerramapRemote.getRemote().getMapStyles().keySet().toArray(new String[0]));
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
		this.styleSlider.setOnChange(v -> {
			Map<String, TiledMap> maps = TerramapRemote.getRemote().getMapStyles();
			TiledMap map = maps.get(v);
			if(map == null) map = maps.values().iterator().next();
			this.minimap.setBackground(map);
		});
		this.tileScalingSlider.setOnChange(v -> {
			if(v == TileScalingOption.AUTO) this.minimap.setTileScaling(SmyLibGui.getMinecraftGuiScale());
			else this.minimap.setTileScaling(v.value);
		});
		this.otherPlayers.setOnActivate(() -> this.minimap.trySetMarkersVisibility(OtherPlayerMarkerController.ID, true)).setOnDeactivate(() -> this.minimap.trySetMarkersVisibility(OtherPlayerMarkerController.ID, false));
		this.entities.setOnActivate(() -> this.minimap.trySetMarkersVisibility(AnimalMarkerController.ID, true).trySetMarkersVisibility(MobMarkerController.ID, true))
				   .setOnDeactivate(() -> this.minimap.trySetMarkersVisibility(AnimalMarkerController.ID, false).trySetMarkersVisibility(MobMarkerController.ID, false));
		this.reset();
	}
	
	@Override
	public void initScreen() {
		this.removeAllWidgets();
		if(this.lastHeight <= 0 || this.lastWidth <= 0) {
			this.recalcMinimapPos();
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
		this.addWidget(new TextButtonWidget(this.width/2 - 93, this.height - 20, 10, 64, "Cancel", this::close));
		this.addWidget(new TextButtonWidget(this.width/2 - 31, this.height - 20, 10, 64, "Reset", this::reset));
		this.addWidget(new TextButtonWidget(this.width/2 + 31, this.height - 20, 10, 62, "Save", this::saveAndClose));
		this.addWidget(this.tileScalingSlider.setX(this.width / 2 - 93).setY(this.height - 39).setWidth(94).setDisplayPrefix("Scaling: ")); 
		this.addWidget(this.zoomSlider.setWidth(94).setX(this.width/2 - 1).setY(this.height - 39).setDisplayPrefix("Zoom: "));
		this.addWidget(this.styleSlider.setWidth(186).setX(this.width/2 - 93).setY(this.height - 58));
		TextWidget playerText = new TextWidget("Players: ", 10, true, this.getFont());
		this.addWidget(playerText.setAnchorX(this.width/2 - 89).setAnchorY(this.height - 74).setBackgroundColor(0xB0000000).setPadding(3));
		this.addWidget(this.otherPlayers.setX(playerText.getX() + playerText.getWidth() + 1).setY(playerText.getY()));
		TextWidget entitiesText = new TextWidget("Entities: ", 10, true, this.getFont());
		this.addWidget(entitiesText.setAnchorX(this.width/2 + 89 - entitiesText.getWidth() - this.entities.getWidth() - 1).setAnchorY(playerText.getAnchorY()).setBackgroundColor(0xB0000000).setPadding(3));
		this.addWidget(this.entities.setX(this.width/2 + 92 - this.entities.getWidth()).setY(playerText.getY()));
		TextWidget explain = new TextWidget("Move and resize the minimap using the preview window.", this.width/2, this.height - 90, 10, TextAlignment.CENTER, this.getFont());
		this.addWidget(explain.setMaxWidth(200).setAnchorY(this.height - 80 - explain.getHeight()));
		this.lastHeight = this.height;
		this.lastWidth = this.width;
	}
	
	private void saveAndClose() {
		TerramapConfig.minimapZoomLevel = (int) this.zoomSlider.getValue();
		TerramapConfig.minimapShowEntities = this.entities.getState();
		TerramapConfig.minimapShowOtherPlayers = this.otherPlayers.getState();
		TerramapConfig.minimapStyle = this.styleSlider.getCurrentOption();
		TerramapConfig.minimapTileScaling = this.tileScalingSlider.getCurrentOption().value;
		TerramapConfig.minimapPosX = (float)this.minimapWindow.getX() / this.getWidth() * 100;
		TerramapConfig.minimapPosY = (float)this.minimapWindow.getY() / this.getHeight() * 100;
		TerramapConfig.minimapWidth = (float)this.minimapWindow.getWidth() / this.getWidth() * 100;
		TerramapConfig.minimapHeight = (float)this.minimapWindow.getHeight() / this.getHeight() * 100;
		TerramapConfig.sync();
		this.close();
	}
	
	private void close() {
		Minecraft.getMinecraft().displayGuiScreen(null);
	}
	
	private void reset() {
		this.zoomSlider.setValue(TerramapConfig.minimapZoomLevel);
		this.otherPlayers.setState(TerramapConfig.minimapShowOtherPlayers);
		this.entities.setState(TerramapConfig.minimapShowEntities);
		this.styleSlider.setCurrentOption(TerramapConfig.minimapStyle);
		this.tileScalingSlider.setCurrentOption(TileScalingOption.getFromValue(TerramapConfig.minimapTileScaling));
		this.recalcMinimapPos();
	}
	
	private void recalcMinimapPos() {
		this.minimapWindow.setX(Math.round(this.width * TerramapConfig.minimapPosX / 100));
		this.minimapWindow.setY(Math.round(this.height * TerramapConfig.minimapPosY / 100));
		this.minimapWindow.setWidth(Math.round(this.width * TerramapConfig.minimapWidth / 100));
		this.minimapWindow.setHeight(Math.round(this.height * TerramapConfig.minimapHeight / 100));
	}

}
