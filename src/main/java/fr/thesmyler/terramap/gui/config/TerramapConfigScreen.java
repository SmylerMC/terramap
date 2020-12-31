package fr.thesmyler.terramap.gui.config;

import java.util.Set;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.IntegerSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.OptionSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.maps.MapStyleRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class TerramapConfigScreen extends Screen {
	
	private GuiScreen parent;
	private Screen[] pages;
	private int currentSubScreen = 0;
	private TexturedButtonWidget next = new TexturedButtonWidget(10, IncludedTexturedButtons.RIGHT, this::nextPage);
	private TexturedButtonWidget previous = new TexturedButtonWidget(10, IncludedTexturedButtons.LEFT, this::previousPage);
	private ToggleButtonWidget unlockZoomToggle = new ToggleButtonWidget(10, false);
	private ToggleButtonWidget saveUIStateToggle = new ToggleButtonWidget(10, false);
	private OptionSliderWidget<TileScalingOption> tileScalingSlider = new OptionSliderWidget<>(10, TileScalingOption.values());
	private IntegerSliderWidget doubleClickDelaySlider = new IntegerSliderWidget(10, TerramapConfig.DOUBLE_CLICK_DELAY_MIN, TerramapConfig.DOUBLE_CLICK_DELAY_MAX, TerramapConfig.DOUBLE_CLICK_DELAY_DEFAULT);
	private IntegerSliderWidget maxLoadedTilesSlider = new IntegerSliderWidget(10, TerramapConfig.TILE_LOAD_MIN, TerramapConfig.TILE_LOAD_MAX, TerramapConfig.TILE_LOAD_DEFAULT);
	private IntegerSliderWidget lowZoomLevelSlider = new IntegerSliderWidget(10, TerramapConfig.LOW_ZOOM_LEVEL_MIN, TerramapConfig.LOW_ZOOM_LEVEL_MAX, TerramapConfig.LOW_ZOOM_LEVEL_DEFAULT);
	private TextButtonWidget reloadMapStylesButton;
	private TextFieldWidget tpCommandField;
	private TextWidget pageText;
	//TODO Localize
	//TODO Tooltips
	
	public TerramapConfigScreen(GuiScreen parent) {
		super(Screen.BackgroundType.DIRT);
		this.parent = parent;
		this.pageText = new TextWidget(10, this.getFont());
		this.tpCommandField = new TextFieldWidget(10, this.getFont()).setWidth(200);
		this.reset();
	}
	
	@Override
	public void initScreen() {
		int inter = 9;
		this.removeAllWidgets(); //Remove the widgets that were already there
		this.cancellAllScheduled(); //Cancel all callbacks that were already there
		this.addWidget(new TextWidget("Terramap config", this.width/2, 10, 5, TextAlignment.CENTER, this.getFont()));
		TextButtonWidget save = new TextButtonWidget(this.width/2 + 30, this.height - 30, 10, 100, "Save", this::saveAndClose);
		TextButtonWidget cancel = new TextButtonWidget(this.width/2 - 130, save.getY(), save.getZ(), save.getWidth(), "Cancel", this::close);
		TextButtonWidget reset = new TextButtonWidget(this.width/2 - 25, save.getY(), save.getZ(), 50, "Reset", this::reset);
		this.addWidget(save);
		this.addWidget(cancel);
		this.addWidget(reset);
		this.addWidget(this.next.setX(save.getX() + save.getWidth() + 5).setY(save.getY() + 2));
		this.addWidget(this.previous.setX(cancel.getX() - 20).setY(this.next.getY()));
		Screen mapConfigScreen = new Screen(20, 20, 1, this.width - 40, this.height - 75, BackgroundType.NONE);
		Screen mapStylesConfigScreen = new Screen(20, 20, 1, this.width - 40, this.height - 75, BackgroundType.NONE);
		Screen otherConfigScreen = new Screen(20, 20, 1, this.width - 40, this.height - 75, BackgroundType.NONE);
		this.pages = new Screen[] { mapConfigScreen, mapStylesConfigScreen, otherConfigScreen};
		
		// Map settings
		mapConfigScreen.addWidget(new TextWidget("Map settings", mapConfigScreen.width/2, 20, 5, TextAlignment.CENTER, this.getFont()));
		TextWidget unlockZoomText = new TextWidget("Unlock zoom: ", 10, TextAlignment.RIGHT, this.getFont());
		unlockZoomText.setAnchorX((mapConfigScreen.width - unlockZoomText.getWidth() - this.unlockZoomToggle.getWidth())/2 - 71).setAnchorY(60);
		mapConfigScreen.addWidget(unlockZoomText);
		mapConfigScreen.addWidget(this.unlockZoomToggle.setX(unlockZoomText.getX() + unlockZoomText.getWidth() + 5).setY(unlockZoomText.getAnchorY() - 4));
		TextWidget saveUIStateText = new TextWidget("Save UI state: ", 10, TextAlignment.RIGHT, this.getFont());
		saveUIStateText.setAnchorX((mapConfigScreen.width - saveUIStateText.getWidth() - this.saveUIStateToggle.getWidth())/2 + 64).setAnchorY(unlockZoomText.getAnchorY());
		mapConfigScreen.addWidget(saveUIStateText);
		mapConfigScreen.addWidget(this.saveUIStateToggle.setX(saveUIStateText.getX() + saveUIStateText.getWidth() + 5).setY(saveUIStateText.getAnchorY() - 4));
		mapConfigScreen.addWidget(this.tileScalingSlider.setX(mapConfigScreen.width/2 - 130).setY(this.unlockZoomToggle.getY() + this.unlockZoomToggle.getHeight() + inter).setWidth(125).setDisplayPrefix("Tile scaling: "));
		mapConfigScreen.addWidget(this.doubleClickDelaySlider.setX(mapConfigScreen.width/2 + 5).setY(this.tileScalingSlider.getY()).setWidth(this.tileScalingSlider.getWidth()).setDisplayPrefix("Double click delay: "));
		mapConfigScreen.addWidget(this.maxLoadedTilesSlider.setX(mapConfigScreen.width/2 - 130).setY(this.tileScalingSlider.getY() + this.tileScalingSlider.getHeight() + inter).setWidth(125).setDisplayPrefix("Tile cache: "));
		mapConfigScreen.addWidget(this.lowZoomLevelSlider.setX(mapConfigScreen.width/2 + 5).setY(this.maxLoadedTilesSlider.getY()).setWidth(this.maxLoadedTilesSlider.getWidth()).setDisplayPrefix("Low zoom: "));
		TextButtonWidget hudButton = new TextButtonWidget(mapConfigScreen.getWidth() / 2 - 100, this.lowZoomLevelSlider.getY() + this.lowZoomLevelSlider.getHeight() + inter, 10, 200, "Configure Minimap", () -> Minecraft.getMinecraft().displayGuiScreen(new HudConfigScreen()));
		mapConfigScreen.addWidget(hudButton);
		
		// Map styles
		mapStylesConfigScreen.addWidget(new TextWidget("Map styles", mapStylesConfigScreen.width/2, 20, 5, TextAlignment.CENTER, this.getFont()));
		Set<String> baseIDs = MapStyleRegistry.getBaseMaps().keySet();
		Set<String> userIDs = MapStyleRegistry.getUserMaps().keySet();
		Set<String> serverIDs = TerramapRemote.getRemote().getServerMapStyles().keySet();
		Set<String> proxyIDs = TerramapRemote.getRemote().getProxyMapStyles().keySet();
		Set<String> resolved = TerramapRemote.getRemote().getMapStyles().keySet();
		TextWidget baseText = new TextWidget("Base (" + baseIDs.size() + ") : " + String.join(", ", baseIDs), mapStylesConfigScreen.width / 2, 40, 10, TextAlignment.CENTER, this.getFont());
		TextWidget proxyText = new TextWidget("Proxy (" + proxyIDs.size() + ") : " + String.join(", ", proxyIDs), mapStylesConfigScreen.width / 2, 57, 10, TextAlignment.CENTER, this.getFont());
		TextWidget serverText = new TextWidget("Server (" + serverIDs.size() + ") : " + String.join(", ", serverIDs), mapStylesConfigScreen.width / 2, 74, 10, TextAlignment.CENTER, this.getFont());
		TextWidget userText = new TextWidget("User (" + userIDs.size() + ") : " + String.join(", ", userIDs), mapStylesConfigScreen.width / 2, 91, 10, TextAlignment.CENTER, this.getFont());
		TextWidget effectiveText = new TextWidget("Effective styles (" + resolved.size() + ") : " + String.join(", ", resolved), mapStylesConfigScreen.width / 2, 108, 10, TextAlignment.CENTER, this.getFont());
		mapStylesConfigScreen.addWidget(baseText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(40));
		mapStylesConfigScreen.addWidget(proxyText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(baseText.getY() + baseText.getHeight() + inter));
		mapStylesConfigScreen.addWidget(serverText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(proxyText.getY() + proxyText.getHeight() + inter));
		mapStylesConfigScreen.addWidget(userText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(serverText.getY() + serverText.getHeight() + inter));
		mapStylesConfigScreen.addWidget(effectiveText.setMaxWidth(mapConfigScreen.getWidth()).setAnchorY(userText.getY() + userText.getHeight() + inter));
		this.reloadMapStylesButton = new TextButtonWidget(mapStylesConfigScreen.width / 2 - 100, (effectiveText.getY() + effectiveText.getHeight() + mapStylesConfigScreen.getHeight()) / 2 - 10, 10, 200, "Reload map styles", () -> {MapStyleRegistry.reload(); TerramapConfigScreen.this.initScreen();});
		mapStylesConfigScreen.addWidget(this.reloadMapStylesButton);
		
		// Other config screen
		otherConfigScreen.addWidget(new TextWidget("Other", mapStylesConfigScreen.width/2, 20, 5, TextAlignment.CENTER, this.getFont()));
		TextWidget tpCommandText = new TextWidget("Teleport command: ", 10, TextAlignment.RIGHT, this.getFont());
		otherConfigScreen.addWidget(tpCommandText.setAnchorX((otherConfigScreen.getWidth() - this.tpCommandField.getWidth() - tpCommandText.getWidth()) / 2).setAnchorY(60));
		otherConfigScreen.addWidget(this.tpCommandField.setX(tpCommandText.getX() + tpCommandText.getWidth() + inter).setY(tpCommandText.getY() - 7));

		// Footer
		this.addWidget(this.pages[this.currentSubScreen]);
		this.addWidget(this.pageText.setAnchorX(this.width/2).setAnchorY(this.height - 45).setAlignment(TextAlignment.CENTER));
		this.updateButtons();
	}
	
	private void nextPage() {
		this.removeWidget(this.pages[this.currentSubScreen]);
		this.currentSubScreen++;
		this.addWidget(this.pages[this.currentSubScreen]);
		this.updateButtons();
	}
	
	private void previousPage() {
		this.removeWidget(this.pages[this.currentSubScreen]);
		this.currentSubScreen--;
		this.addWidget(this.pages[this.currentSubScreen]);
		this.updateButtons();
	}
	
	private void updateButtons() {
		if(this.currentSubScreen <= 0) this.previous.disable();
		else this.previous.enable();
		if(this.currentSubScreen >= this.pages.length - 1) this.next.disable();
		else this.next.enable();
		this.pageText.setText("Page " + (this.currentSubScreen + 1) + " of " + this.pages.length);
	}
	
	private void saveAndClose() {
		if(this.tileScalingSlider.getCurrentOption() == TileScalingOption.AUTO) {
			TerramapConfig.autoTileScaling = true;
		} else {
			TerramapConfig.autoTileScaling = false;
			TerramapConfig.tileScaling = this.tileScalingSlider.getCurrentOption().value;
		}
		TerramapConfig.unlockZoom = this.unlockZoomToggle.getState();
		TerramapConfig.saveUiState = this.saveUIStateToggle.getState();
		TerramapConfig.doubleClickDelay = (int) this.doubleClickDelaySlider.getValue();
		TerramapConfig.maxTileLoad = (int) this.maxLoadedTilesSlider.getValue();
		TerramapConfig.lowZoomLevel = (int) this.lowZoomLevelSlider.getValue();
		TerramapConfig.tpllcmd = this.tpCommandField.getText();
		TerramapConfig.sync();
		this.close();
	}
	
	private void close() {
		Minecraft.getMinecraft().displayGuiScreen(this.parent);
	}
	
	private void reset() {
		if(TerramapConfig.autoTileScaling) {
			this.tileScalingSlider.setCurrentOption(TileScalingOption.AUTO);
		} else {
			this.tileScalingSlider.setCurrentOption(TileScalingOption.getFromValue(TerramapConfig.tileScaling));
		}
		this.unlockZoomToggle.setState(TerramapConfig.unlockZoom);
		this.saveUIStateToggle.setState(TerramapConfig.saveUiState);
		this.doubleClickDelaySlider.setValue(TerramapConfig.doubleClickDelay);
		this.maxLoadedTilesSlider.setValue(TerramapConfig.maxTileLoad);
		this.lowZoomLevelSlider.setValue(TerramapConfig.lowZoomLevel);
		this.tpCommandField.setText(TerramapConfig.tpllcmd);
	}
	
	@Override
	public void onKeyTyped(char typedChar, int keyCode, Screen parent) {
		switch(keyCode) {
		case Keyboard.KEY_ESCAPE:
			Minecraft.getMinecraft().displayGuiScreen(new ConfirmScreen());
			break;
		default: super.onKeyTyped(typedChar, keyCode, parent);
		}
	}
	
	private class ConfirmScreen extends Screen {
		public ConfirmScreen() {
			super(BackgroundType.DIRT);
		}
		@Override
		public void initScreen() {
			this.removeAllWidgets();
			this.cancellAllScheduled();
			TextWidget text = new TextWidget("Save configuration ?", this.width / 2, this.height / 2 - 20, 10, TextAlignment.CENTER, this.getFont());
			this.addWidget(text);
			this.addWidget(new TextButtonWidget(this.width / 2 - 125, text.getY() + text.getHeight() + 15, 10, 80, "No", TerramapConfigScreen.this::close));
			this.addWidget(new TextButtonWidget(this.width / 2 - 40, text.getY() + text.getHeight() + 15, 10, 80, "Cancel", () -> Minecraft.getMinecraft().displayGuiScreen(TerramapConfigScreen.this)));
			this.addWidget(new TextButtonWidget(this.width / 2 + 45, text.getY() + text.getHeight() + 15, 10, 80, "Yes",TerramapConfigScreen.this::saveAndClose));
		}
	}
	
	protected enum TileScalingOption {
		
		AUTO(0), POINT5(0.5), ONE(1), TWO(2), FOUR(4), HEIGHT(8);
		
		double value;
		
		TileScalingOption(double v) {
			this.value = v;
		}
		
		protected static TileScalingOption getFromValue(double val) {
			for(TileScalingOption o: TileScalingOption.values()) {
				if(o.value == val) return o;
			}
			return AUTO;
		}
		
		@Override
		public String toString() {
			if(this == AUTO) {
				return "Auto"; //TODO localize
			}
			return "" + this.value;
		}
	}

}
