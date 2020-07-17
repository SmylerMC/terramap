package fr.thesmyler.smylibgui.screen;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.smylibgui.Animation.AnimationState;
import fr.thesmyler.smylibgui.text.TextAlignment;
import fr.thesmyler.smylibgui.text.TextWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.terramap.gui.GuiTiledMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class TestScreen extends Screen {

	private GuiScreen parent;
	private Animation animation = new Animation(10000);
	private int counter = 0;
	private TextWidget fpsCounter;
	private TextWidget focus;
	private TextWidget hovered;
	private TextWidget colored;
	

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
		TextWidget feedback = new TextWidget("", this.width/2, this.getHeight() - 60, 10, TextAlignment.CENTER, this.getFont());
		this.fpsCounter = new TextWidget("FPS: ", 20, 40, 10, this.getFont());
		this.focus = new TextWidget("Focused: ", 20, 60, 10, this.getFont());
		this.hovered = new TextWidget("Hovered: ", 20, 80, 10, this.getFont());
		TextWidget counterStr = new TextWidget("FPS", 20, 160, 10, this.getFont());
		this.colored = new TextWidget("Color animated text", 20, 180, 10, this.getFont());
		this.addWidget(title);
		this.addWidget(fpsCounter);
		this.addWidget(focus);
		this.addWidget(hovered);
		this.addWidget(counterStr);
		this.addWidget(colored);
		this.addWidget(feedback);
		
		//Text field widgets
		this.addWidget(new TextFieldWidget(20, 100, 150, 1));
		this.addWidget(new TextFieldWidget(20, 130, 150, 1));
		
		this.addWidget(
			new TexturedButtonWidget(this.width/2 - 7, this.height/2 - 7, 0, 15, 15, 40, 0, GuiTiledMap.WIDGET_TEXTURES,
					()-> {
						feedback.setText("Clicked!");
						this.scheduleWithDelay(()->{feedback.setText("");}, 1000);
					},
					()-> {
						feedback.setText("Double Clicked!");
						this.scheduleWithDelay(()->{feedback.setText("");}, 1000);
					}
		));
		this.addWidget(
				new TexturedButtonWidget(this.width/2 - 7, this.height - 40, 0, 15, 15, 40, 0, GuiTiledMap.WIDGET_TEXTURES,
						()-> {Minecraft.getMinecraft().displayGuiScreen(this.parent);}
		));
		
		//Same as Javascript's setInterval
		this.scheduleAtInterval(() -> {counterStr.setText("Scheduled callback has been called " + this.counter++);}, 1000);
	}
	
	@Override
	public void onUpdate(Screen parent) {
		super.onUpdate(parent);
		this.animation.update();
		this.fpsCounter.setText("FPS: " + Minecraft.getDebugFPS());
		this.focus.setText("Focused: " + this.getFocusedWidget());
		this.hovered.setText("Hovered: " + this.hoveredWidget);
		this.colored.setColor(animation.rainbowColor());
	}

}
