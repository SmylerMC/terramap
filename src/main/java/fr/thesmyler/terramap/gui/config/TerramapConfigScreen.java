package fr.thesmyler.terramap.gui.config;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.OptionSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
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
	private ToggleButtonWidget unlockZoom = new ToggleButtonWidget(10, false);
	private TextFieldWidget cacheDirField;
	private OptionSliderWidget<TileScalingOption> tileScalingSlider = new OptionSliderWidget<>(10, TileScalingOption.values());
	private TextButtonWidget reloadMapStylesButton;
	private TextWidget pageText;
	//TODO max loaded tiles
	//TODO double click delay
	//TODO Warn when changing values that require the game to restart
	//TODO Localize
	
	public TerramapConfigScreen(GuiScreen parent) {
		super(Screen.BackgroundType.DIRT);
		this.parent = parent;
		this.cacheDirField = new TextFieldWidget(10, this.getFont());
		this.pageText = new TextWidget(10, this.getFont());
		this.reset();
	}
	
	@Override
	public void initScreen() {
		this.removeAllWidgets(); //Remove the widgets that were already there
		this.cancellAllScheduled(); //Cancel all callbacks that were already there
		this.addWidget(new TextWidget("Terramap config", this.width/2, 10, 5, TextAlignment.CENTER, this.getFont()));
		TextButtonWidget save = new TextButtonWidget(this.width/2 + 30, this.height - 30, 10, 100, "Save", this::saveAndClose);
		TextButtonWidget cancel = new TextButtonWidget(this.width/2 - 130, save.getY(), save.getZ(), save.getWidth(), "Cancel", this::close);
		TextButtonWidget reset = new TextButtonWidget(this.width/2 - 25, save.getY(), save.getZ(), 50, "Reset", this::reset);
		//TODO Page counter in the middle
		this.addWidget(save);
		this.addWidget(cancel);
		this.addWidget(reset);
		this.addWidget(this.next.setX(save.getX() + save.getWidth() + 5).setY(save.getY() + 2));
		this.addWidget(this.previous.setX(cancel.getX() - 20).setY(this.next.getY()));
		Screen main = new Screen(20, 20, 1, this.width - 40, this.height - 75, BackgroundType.NONE);
		Screen cache = new Screen(20, 20, 1, this.width - 40, this.height - 75, BackgroundType.NONE);
		this.pages = new Screen[] { main, cache};
		main.addWidget(new TextWidget("Map settings", main.width/2, 10, 5, TextAlignment.CENTER, this.getFont()));
		main.addWidget(this.tileScalingSlider.setX(0).setY(40).setWidth(150));
		main.addWidget(new TextWidget("Tile scaling", this.tileScalingSlider.getX() + this.tileScalingSlider.getWidth() + 7, tileScalingSlider.getY() + 7, 10, TextAlignment.RIGHT, this.getFont()));
		main.addWidget(this.unlockZoom.setX(this.tileScalingSlider.getX()).setY(this.tileScalingSlider.getY() + this.tileScalingSlider.getHeight() + 7));
		main.addWidget(new TextWidget("Unlock zoom", this.unlockZoom.getX() + this.unlockZoom.getWidth() + 7, this.unlockZoom.getY() + 4, 10, TextAlignment.RIGHT, this.getFont()));
		this.reloadMapStylesButton = new TextButtonWidget(this.unlockZoom.getX(), this.unlockZoom.getY() + this.unlockZoom.getHeight() + 7, 10, 150, "Reload map styles", MapStyleRegistry::loadFromConfigFile);
		main.addWidget(this.reloadMapStylesButton);
		main.addWidget(new TextWidget(MapStyleRegistry.getTiledMaps().size() + " loaded", this.reloadMapStylesButton.getX() + this.reloadMapStylesButton.getWidth() + 7, this.reloadMapStylesButton.getY() + 6, 10, TextAlignment.RIGHT, this.getFont()));
		TextButtonWidget hudButton = new TextButtonWidget(this.reloadMapStylesButton.getX(), this.reloadMapStylesButton.getY() + this.reloadMapStylesButton.getHeight() + 7, 10, 150, "Configure Minimaps");
		main.addWidget(hudButton);
		cache.addWidget(new TextWidget("Cache", cache.width/2, 10, 5, TextAlignment.CENTER, this.getFont()));
		this.addWidget(this.pages[this.currentSubScreen]);
		cache.addWidget(this.cacheDirField.setY(40).setWidth(150));
		cache.addWidget(new TextWidget("Cache directory", this.cacheDirField.getX() + this.cacheDirField.getWidth() + 7, this.cacheDirField.getY() + 6, 10, TextAlignment.RIGHT, this.getFont()));
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
		TerramapConfig.cachingDir = this.cacheDirField.getText();
		TerramapConfig.unlockZoom = this.unlockZoom.getState();
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
		this.unlockZoom.setState(TerramapConfig.unlockZoom);
		this.cacheDirField.setText(TerramapConfig.cachingDir);
	}
	
	private enum TileScalingOption {
		
		AUTO(0), POINT5(0.5), ONE(1), TWO(2), FOUR(4), HEIGHT(8);
		
		double value;
		
		TileScalingOption(double v) {
			this.value = v;
		}
		
		private static TileScalingOption getFromValue(double val) {
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
