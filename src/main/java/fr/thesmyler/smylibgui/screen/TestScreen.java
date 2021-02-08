package fr.thesmyler.smylibgui.screen;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.smylibgui.Animation.AnimationState;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.buttons.OptionButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.FloatSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.IntegerSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.OptionSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextComponentWidget;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.text.ITextComponent;

public class TestScreen extends Screen {

	private GuiScreen parent;
	private Animation animation = new Animation(10000);
	private int counter = 0;
	private TextWidget fpsCounter;
	private TextWidget focus;
	private TextWidget hovered;
	private TextWidget colored;
	private TextFieldWidget textField;
	private TextButtonWidget testButton;
	private Screen[] subScreens;
	private int currentSubScreen = 0;
	
	private TexturedButtonWidget previous, next;
	
	public TestScreen(GuiScreen parent) {
		super(Screen.BackgroundType.DEFAULT);
		this.parent = parent;
		this.animation  = new Animation(5000); 	//We will use an animation to set the color of one of the displayed strings
		this.animation.start(AnimationState.CONTINUOUS_ENTER);
		this.next = new TexturedButtonWidget(10, IncludedTexturedButtons.RIGHT, this::nextPage);
		this.previous = new TexturedButtonWidget(10, IncludedTexturedButtons.LEFT, this::previousPage);
		
		this.fpsCounter = new TextWidget("FPS: 0", 10, this.getFont());
		this.focus = new TextWidget("Focused: null", 10, this.getFont());
		this.hovered = new TextWidget("Hovered: null", 10, this.getFont());
		this.textField = new TextFieldWidget(1, "Text field", this.getFont());
	}

	@Override
	public void initScreen() { //Called at normal gui init, when screen opens or resizes
		this.removeAllWidgets(); //Remove the widgets that were already there
		this.cancellAllScheduled(); //Cancel all callbacks that were already there
		
		//Main screen
		Screen textScreen = new Screen(20, 50, 1, this.width - 40, this.height - 70, BackgroundType.NONE);
		Screen buttonScreen = new Screen(20, 50, 1, this.width - 40, this.height - 70, BackgroundType.NONE);
		Screen sliderScreen = new Screen(20, 50, 1, this.width - 40, this.height - 70, BackgroundType.NONE);
		Screen menuScreen = new Screen(20, 50, 1, this.width - 40, this.height - 70, BackgroundType.NONE);
		this.subScreens = new Screen[] { textScreen, buttonScreen, sliderScreen, menuScreen};

		TextWidget title = new TextWidget("SmyguiLib demo test screen", this.width/2, 20, 10, TextAlignment.CENTER, this.getFont());
		this.addWidget(title);
		this.addWidget(new TexturedButtonWidget(this.width - 20, 5, 10, IncludedTexturedButtons.CROSS, () -> {Minecraft.getMinecraft().displayGuiScreen(this.parent);}));
		this.addWidget(next.setX(this.width - 20).setY(this.height - 20));
		this.addWidget(previous.setX(5).setY(this.height - 20));
		this.addWidget(
				new TextButtonWidget(13, 13, 10, 100, "Reset screen",
						()-> {Minecraft.getMinecraft().displayGuiScreen(new TestScreen(this.parent));}
				)
		);

		// === Text related stuff and general features examples === //
		this.hovered = new TextWidget("Hovered: null", 0, 50, 10, this.getFont());
		
		TextWidget counterStr = new TextWidget(0, 100, 10, this.getFont());
		this.colored = new TextWidget("Color animated text", 0, 120, 10, this.getFont());
		this.colored.setColor(animation.rainbowColor());
		textScreen.addWidget(fpsCounter.setAnchorX(0).setAnchorY(10));
		textScreen.addWidget(focus.setAnchorX(0).setAnchorY(30));
		textScreen.addWidget(hovered);
		textScreen.addWidget(this.textField.setX(0).setY(70).setWidth(150).setOnPressEnterCallback(s -> {this.textField.setText("You pressed enter :)"); return true;}));
		textScreen.addWidget(counterStr);
		textScreen.addWidget(colored);
		ITextComponent compo = ITextComponent.Serializer.jsonToComponent("[\"\",{\"text\":\"This is red, with a hover event,\",\"color\":\"dark_red\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"I said it's red\"}},{\"text\":\" \"},{\"text\":\"and this is green with an other hover event.\",\"color\":\"dark_green\",\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Don't you trust me? This is green!\"}},{\"text\":\"\\n\"},{\"text\":\"And this is blue, with a click event!\",\"color\":\"dark_blue\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://example.com\"}},{\"text\":\"\\n\"},{\"text\":\"And finally, this is \",\"color\":\"white\"},{\"text\":\"black\",\"strikethrough\":true,\"color\":\"white\"},{\"text\":\" white, \",\"color\":\"white\"},{\"text\":\"with\",\"underlined\":true,\"color\":\"white\"},{\"text\":\" various\",\"italic\":true,\"color\":\"white\"},{\"text\":\" styles \",\"bold\":true,\"color\":\"white\"},{\"text\":\"and I bet you can't read that.\",\"obfuscated\":true,\"color\":\"white\"}]");
		textScreen.addWidget(new TextComponentWidget(textScreen.width/2, 140, 1, compo, TextAlignment.CENTER, this.getFont()).setMaxWidth(textScreen.getWidth()).setBackgroundColor(0x80000000).setPadding(10));
		
		// === Button screen: examples on how to use button widgets === //
		
		this.testButton = new TextButtonWidget(0, 0, 1, 150, "Click me!",
				() -> {
					this.testButton.setText("Nice, double click me now!");
				},
				() -> {
					this.testButton.setText("I'm done now :(");
					this.testButton.disable();
				}
				);
		buttonScreen.addWidget(testButton);
		buttonScreen.addWidget(new TexturedButtonWidget(0, 30, 1, IncludedTexturedButtons.BLANK_15, null));
		buttonScreen.addWidget(new TexturedButtonWidget(30, 30, 1, IncludedTexturedButtons.CROSS, null));
		buttonScreen.addWidget(new TexturedButtonWidget(30, 30, 1, IncludedTexturedButtons.PLUS, null));
		buttonScreen.addWidget(new TexturedButtonWidget(60, 30, 1, IncludedTexturedButtons.MINUS, null));
		buttonScreen.addWidget(new TexturedButtonWidget(90, 30, 1, IncludedTexturedButtons.LEFT, null));
		buttonScreen.addWidget(new TexturedButtonWidget(120, 30, 1, IncludedTexturedButtons.UP, null));
		buttonScreen.addWidget(new TexturedButtonWidget(150, 30, 1, IncludedTexturedButtons.DOWN, null));
		buttonScreen.addWidget(new TexturedButtonWidget(180, 30, 1, IncludedTexturedButtons.RIGHT, null));
		ToggleButtonWidget tb1 = new ToggleButtonWidget(0, 60, 1, true);
		buttonScreen.addWidget(tb1);
		buttonScreen.addWidget(new ToggleButtonWidget(30, 60, 1, true, b -> tb1.setEnabled(b)));
		buttonScreen.addWidget(new OptionButtonWidget<String>(0, 90, 2, 150, new String[] {"Option 1", "Option 2", "Option 3", "Option 4"}));

		
		// === Slider screen: examples on how to use slider widgets === //
		
		sliderScreen.addWidget(new IntegerSliderWidget(0, 0, 1, 150, 0, 100, 50));
		sliderScreen.addWidget(new FloatSliderWidget(0, 30, 1, 150, 0, 1, 0.5));
		sliderScreen.addWidget(new OptionSliderWidget<String>(0, 60, 1, 150, new String[] {"Option 1", "Option 2", "Option 3", "Option 4"}));

		
		// === Menu screen: example on how to use menu widgets === //
		
		MenuWidget rcm = new MenuWidget(50, this.getFont()); //This will be used as our right click menu, the following are it's sub menus
		MenuWidget animationMenu = new MenuWidget(1, this.getFont());
		MenuWidget here = new MenuWidget(50, this.getFont());
		MenuWidget is = new MenuWidget(50, this.getFont());
		MenuWidget a = new MenuWidget(50, this.getFont());
		MenuWidget very = new MenuWidget(50, this.getFont());
		MenuWidget nested = new MenuWidget(50, this.getFont());
		animationMenu.addEntry("Show", () -> {animation.start(AnimationState.ENTER);});
		animationMenu.addEntry("Hide", () -> {animation.start(AnimationState.LEAVE);});
		animationMenu.addEntry("Flash", () -> {animation.start(AnimationState.FLASH);});
		animationMenu.addEntry("Continuous", ()-> {animation.start(AnimationState.CONTINUOUS_ENTER);});
		animationMenu.addEntry("Continuous backward", () -> {animation.start(AnimationState.CONTINUOUS_LEAVE);});
		animationMenu.addEntry("Back and forth", () -> {animation.start(AnimationState.BACK_AND_FORTH);});
		animationMenu.addEntry("Stop", () -> {animation.start(AnimationState.STOPPED);});
		rcm.addEntry("Close", () -> {
			Minecraft.getMinecraft().displayGuiScreen(this.parent);
		});
		rcm.addEntry("Disabled Entry");
		rcm.addEntry("Here", here);
		here.addEntry("is", is);
		is.addEntry("a", a);
		a.addEntry("very", very);
		very.addEntry("nested", nested);
		nested.addEntry("menu");
		rcm.addSeparator();
		rcm.addEntry("Animation", animationMenu);
		rcm.useAsRightClick(); //Calling this tells the menu to open whenever it's parent screen is right clicked
		menuScreen.addWidget(new TextWidget("Please right click anywhere", menuScreen.getWidth() / 2, menuScreen.getHeight() / 2, 1, TextAlignment.CENTER, true, this.getFont()));
		menuScreen.addWidget(rcm);
		
		
		// ==== Getting everything ready and setting up scheduled tasks === //
		
		this.addWidget(subScreens[this.currentSubScreen]); //A screen is also a widget, that allows for a lot of flexibility
		
		//Same as Javascript's setInterval
		this.scheduleAtInterval(() -> {counterStr.setText("Scheduled callback called " + this.counter++);}, 1000);
		this.scheduleAtUpdate(() -> { //Called at every update
			this.animation.update();
			this.fpsCounter.setText("FPS: " + Minecraft.getDebugFPS());
			this.focus.setText("Focused: " + this.getFocusedWidget());
			this.hovered.setText("Hovered: " + this.getHoveredWidget());
			this.colored.setColor(animation.rainbowColor());
		});
		this.updateButtons();
	}
	
	private void nextPage() {
		this.removeWidget(this.subScreens[this.currentSubScreen]);
		this.currentSubScreen++;
		this.addWidget(this.subScreens[this.currentSubScreen]);
		this.updateButtons();
	}
	
	private void previousPage() {
		this.removeWidget(this.subScreens[this.currentSubScreen]);
		this.currentSubScreen--;
		this.addWidget(this.subScreens[this.currentSubScreen]);
		this.updateButtons();
	}
	
	private void updateButtons() {
		if(this.currentSubScreen <= 0) this.previous.disable();
		else this.previous.enable();
		if(this.currentSubScreen >= this.subScreens.length - 1) this.next.disable();
		else this.next.enable();
	}

}
