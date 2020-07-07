package fr.thesmyler.smylibgui.screen;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.smylibgui.Animation.AnimationState;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.TextFieldWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.terramap.gui.GuiTiledMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class TestScreen extends Screen {

	private GuiScreen parent;
	private String testString = "";
	private Animation animation = new Animation(10000);

	public TestScreen(GuiScreen parent) {
		super(Screen.BackgroundType.DIRT);
		this.parent = parent;
	}

	@Override
	public void initScreen() {
		this.removeAllWidgets();
		MenuWidget rcm = new MenuWidget(50, this.getFont());
		MenuWidget animationMenu = new MenuWidget(1, this.getFont());
		MenuWidget here = new MenuWidget(1, this.getFont());
		MenuWidget is = new MenuWidget(1, this.getFont());
		MenuWidget a = new MenuWidget(1, this.getFont());
		MenuWidget very = new MenuWidget(1, this.getFont());
		MenuWidget nested = new MenuWidget(1, this.getFont());
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
		rcm.useAsRightClick();
		this.addWidget(rcm);
		this.addWidget(
			new TexturedButtonWidget(this.width/2 - 7, this.height/2 - 7, 0, 15, 15, 40, 0, GuiTiledMap.WIDGET_TEXTURES,
					()-> {
						this.testString = "Clicked!";
						this.scheduleWithDelay(()->{this.testString = "";}, 1000);
					},
					()-> {
						this.testString = "Double clicked!";
						this.scheduleWithDelay(()->{this.testString = "";}, 1000);
					}
		));
		this.addWidget(
				new TexturedButtonWidget(this.width/2 - 7, this.height - 40, 0, 15, 15, 40, 0, GuiTiledMap.WIDGET_TEXTURES,
						()-> {Minecraft.getMinecraft().displayGuiScreen(this.parent);}
		));
		this.animation  = new Animation(5000);
		this.animation.start(AnimationState.CONTINUOUS_ENTER);
		this.addWidget(new TextFieldWidget(20, 90, 150, 1));
		this.addWidget(new TextFieldWidget(20, 120, 150, 1));
	}
	
	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean screenHovered, boolean screenFocused, Screen parent) {
		super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
		this.drawCenteredString(this.fontRenderer, this.testString, this.width/2, this.getHeight() - 60, 0xFFFFFFFF);
		this.drawString(this.fontRenderer, "FPS: " + Minecraft.getDebugFPS(), 20, 20, 0xFFFFFFFF);
		this.drawString(this.fontRenderer, "Focused: " + this.getFocusedWidget(), 20, 50, 0xFFFFFFFF);
		this.drawString(this.fontRenderer, "Hovered: " + this.hoveredWidget, 20, 70, 0xFFFFFFFF);
		this.drawCenteredString(this.fontRenderer, "LOLCATS ARE THE BESTS!", this.width/2, this.getHeight() - 80, animation.rainbowColor());
		this.fontRenderer.drawString("Test!", this.width/2, this.getHeight() - 100, 0xFFFFFFFF, true);
	}
	
	@Override
	public void onUpdate(Screen parent) {
		super.onUpdate(parent);
		this.animation.update();
	}

}
