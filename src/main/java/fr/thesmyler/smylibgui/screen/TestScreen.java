package fr.thesmyler.smylibgui.screen;

import java.io.IOException;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.smylibgui.Animation.AnimationState;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.buttons.OptionButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.smylibgui.widgets.sliders.FloatSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.IntegerSliderWidget;
import fr.thesmyler.smylibgui.widgets.sliders.OptionSliderWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.GuiTextField;

public class TestScreen extends Screen {

	private GuiScreen parent;
	private Animation animation = new Animation(10000);
	private int counter = 0;
	private TextWidget fpsCounter;
	private TextWidget focus;
	private TextWidget hovered;
	private TextWidget colored;
	private TextButtonWidget testButton;
	
	private GuiTextField testGuiTextField; //TODO TEST CODE


	public TestScreen(GuiScreen parent) {
		super(Screen.BackgroundType.DIRT);
		this.parent = parent;
	}

	@Override
	public void initScreen() { //Called at normal gui init, when screen opens or resizes
		this.removeAllWidgets(); //Remove the widgets that were already there
		this.cancellAllScheduled(); //Cancell all callbacks that were already there

		MenuWidget rcm = new MenuWidget(50, this.getFont()); //This will be used as our right click menu, the following are it's sub menus
		MenuWidget animationMenu = new MenuWidget(1, this.getFont());
		MenuWidget here = new MenuWidget(50, this.getFont());
		MenuWidget is = new MenuWidget(50, this.getFont());
		MenuWidget a = new MenuWidget(50, this.getFont());
		MenuWidget very = new MenuWidget(50, this.getFont());
		MenuWidget nested = new MenuWidget(50, this.getFont());
		animationMenu.addEntry("Show", ()-> {animation.start(AnimationState.ENTER);});
		animationMenu.addEntry("Hide", ()-> {animation.start(AnimationState.LEAVE);});
		animationMenu.addEntry("Continuous", ()-> {animation.start(AnimationState.CONTINUOUS_ENTER);});
		animationMenu.addEntry("Continuous backward", ()-> {animation.start(AnimationState.CONTINUOUS_LEAVE);});
		animationMenu.addEntry("Back and forth", ()-> {animation.start(AnimationState.BACK_AND_FORTH);});
		animationMenu.addEntry("Stop", ()-> {animation.start(AnimationState.STOPPED);});
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
		this.addWidget(rcm);

		//We will use an animation to set the color of one of the displayed strings
		this.animation  = new Animation(5000);
		this.animation.start(AnimationState.CONTINUOUS_ENTER);

		TextWidget title = new TextWidget("SmyguiLib demo test screen", this.width/2, 20, 10, TextAlignment.CENTER, this.getFont());
		TextWidget feedback = new TextWidget(this.width/2, this.getHeight() - 60, 10, TextAlignment.CENTER, this.getFont());
		this.fpsCounter = new TextWidget("FPS: 0", this.width/2 - 170, 40, 10, this.getFont());
		this.focus = new TextWidget("Focused: null", this.width/2 - 170, 60, 10, this.getFont());
		this.hovered = new TextWidget("Hovered: null", this.width/2 - 170, 80, 10, this.getFont());
		TextWidget counterStr = new TextWidget(this.width/2 - 170, 160, 10, this.getFont());
		this.colored = new TextWidget("Color animated text", this.width/2 - 170, 180, 10, this.getFont());
		this.colored.setColor(animation.rainbowColor());
		this.addWidget(title);
		this.addWidget(fpsCounter);
		this.addWidget(focus);
		this.addWidget(hovered);
		this.addWidget(counterStr);
		this.addWidget(colored);
		this.addWidget(feedback);

		//Text field widgets
		this.addWidget(new TextFieldWidget(this.width/2 - 170, 100, 150, 1));

		this.testButton = new TextButtonWidget(this.width/2 - 170, 130, 150, "Click me!", 1,
				() -> {
					this.testButton.setText("Nice, double click me now!");
				},
				() -> {
					this.testButton.setText("I'm done now :(");
					this.testButton.disable();
				}
				);
		this.addWidget(testButton);
//		this.addWidget(
//				new TexturedButtonWidget(this.width - 20, 5, 0, 15, 15, 40, 0, GuiTiledMap.WIDGET_TEXTURES));
		this.addWidget(
				new TextButtonWidget(this.width/2 - 50, this.height - 40, 100, "Reset screen", 1,
						()-> {Minecraft.getMinecraft().displayGuiScreen(this.parent);}
						));

		this.addWidget(new IntegerSliderWidget(this.width/2 + 20, 130, 1, 150, 0, 100, 50));
		this.addWidget(new FloatSliderWidget(this.width/2 + 20, 160, 1, 150, 0, 1, 0.5));
		this.addWidget(new OptionSliderWidget<String>(this.width/2 + 20, 190, 1, 150, new String[] {"Option 1", "Option 2", "Option 3", "Option 4"}));
		ToggleButtonWidget tb1 = new ToggleButtonWidget(this.width/2 - 170, 200, 1, true);
		this.addWidget(tb1);
		this.addWidget(new ToggleButtonWidget(this.width/2 - 140, 200, 1, true, () -> {tb1.enable();}, () -> {tb1.disable();}));
		this.addWidget(new OptionButtonWidget<String>(this.width/2 + 20, 220, 150,  2, new String[] {"Option 1", "Option 2", "Option 3", "Option 4"}));

		
		//Same as Javascript's setInterval
		this.scheduleAtInterval(() -> {counterStr.setText("Scheduled callback called " + this.counter++);}, 1000);
		this.testGuiTextField = new GuiTextField(2, this.fontRenderer, this.width/2 + 20, 100, 150, 20);
	}

	@Override
	public void onUpdate(Screen parent) {
		super.onUpdate(parent);
		this.animation.update();
		this.fpsCounter.setText("FPS: " + Minecraft.getDebugFPS());
		this.focus.setText("Focused: " + this.getFocusedWidget());
		this.hovered.setText("Hovered: " + this.hoveredWidget);
		this.colored.setColor(animation.rainbowColor());
		this.testGuiTextField.updateCursorCounter();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		super.keyTyped(typedChar, keyCode);
        this.testGuiTextField.textboxKeyTyped(typedChar, keyCode);
	}
	
	
    @Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        this.testGuiTextField.mouseClicked(mouseX, mouseY, mouseButton);
    }
    
    @Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
    	super.drawScreen(mouseX, mouseY, partialTicks);
        this.testGuiTextField.drawTextBox();
    }

}
