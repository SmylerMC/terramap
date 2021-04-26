package fr.thesmyler.smylibgui.screen;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.config.TerramapConfig;
import net.minecraft.client.gui.GuiScreen;

//TODO Customizable background
public class Screen extends GuiScreen {
	
	private final WidgetContainer container = new Container();
	
	private int touchContactsCount = 0;
	private final boolean[] mouseButtonsPressed = new boolean[Mouse.getButtonCount()];
	private final float[] lastClickX = new float[Mouse.getButtonCount()];
	private final float[] lastClickY = new float[Mouse.getButtonCount()];
	private final long[] lastClickTime = new long[Mouse.getButtonCount()];
	private int lastClickedButton = -1;
	
	private long startHoverTime;
	private IWidget lastHoveredWidget;
	private float lastRenderMouseX, lastRenderMouseY;
	
	private BackgroundOption background;
	
	public Screen(BackgroundOption background) {
		this.background = background;
	}
	
	public WidgetContainer getContent() {
		return this.container;
	}
	
	@Override
	public void drawScreen(int nopX, int nopY, float partialTicks) {
		this.drawBackground();
		super.drawScreen(nopX, nopY, partialTicks);
        float mouseX = (float)Mouse.getX() * this.width / this.mc.displayWidth;
        float mouseY = this.height - (float)Mouse.getY() * this.height / this.mc.displayHeight - 1;
        this.onUpdate();
		this.container.onUpdate(null);
		this.container.draw(0, 0, mouseX, mouseY, true, true, null);
		IWidget hoveredWidget = this.container.getHoveredWidget();
		boolean mouseMoved = mouseX != this.lastRenderMouseX && mouseY != this.lastRenderMouseY;
		if(mouseMoved || (hoveredWidget != null && !hoveredWidget.equals(this.lastHoveredWidget))) {
			this.startHoverTime = System.currentTimeMillis();
		}
		if(
				hoveredWidget != null
				&& hoveredWidget.getTooltipText() != null
				&& !hoveredWidget.getTooltipText().isEmpty()
				&& this.startHoverTime + hoveredWidget.getTooltipDelay() <= System.currentTimeMillis()
			) {
				//this.drawHoveringText(w.getTooltipText(), mouseX, mouseY); TODO floating point version
				this.drawHoveringText(hoveredWidget.getTooltipText(), Math.round(mouseX), Math.round(mouseY));
		}
		this.lastHoveredWidget = hoveredWidget;
	}
	
	public void onUpdate() {}
	
	@Override
	public void initGui() {
		super.initGui();
		this.container.init();
	}
	
	@Override
	public void handleMouseInput() throws IOException {
		// We override that so we can make float math instead of being stuck with ints
        float mouseX = (float)Mouse.getEventX() * this.width / this.mc.displayWidth;
        float mouseY = this.height - (float)Mouse.getEventY() * this.height / this.mc.displayHeight - 1;
        int mouseButton = Mouse.getEventButton();
        long ctime = System.currentTimeMillis();

        if(Mouse.getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.touchContactsCount++ > 0) return;
            this.mouseButtonsPressed[mouseButton] = true;
    		if(ctime - this.lastClickTime[mouseButton] <= TerramapConfig.CLIENT.doubleClickDelay && this.lastClickX[mouseButton] == mouseX && this.lastClickY[mouseButton] == mouseY) {
    			this.container.onDoubleClick(mouseX, mouseY, mouseButton, null);
    		} else {
    			this.container.onClick(mouseX, mouseY, mouseButton, null);
//    			this.lastClickTime[mouseButton] = ctime; //TODO Why was it done only here
    		}
            this.lastClickedButton = mouseButton;
            this.lastClickTime[mouseButton] = ctime;
            this.lastClickX[mouseButton] = mouseX;
            this.lastClickY[mouseButton] = mouseY;
        } else if(mouseButton >= 0) {
            if(this.mc.gameSettings.touchscreen && --this.touchContactsCount > 0) return;
            this.mouseButtonsPressed[mouseButton] = false;
            this.lastClickedButton = -1;
            this.container.onMouseReleased(mouseX, mouseY, mouseButton, null);
        } else if(this.lastClickedButton >= 0 && this.mouseButtonsPressed[this.lastClickedButton]) {
    		float dX = mouseX - this.lastClickX[this.lastClickedButton];
    		float dY = mouseY - this.lastClickY[this.lastClickedButton];
            this.lastClickX[this.lastClickedButton] = mouseX;
            this.lastClickY[this.lastClickedButton] = mouseY;
    		this.container.onMouseDragged(mouseX, mouseY, dX, dY, this.lastClickedButton, null); //TODO Pass time since last as well
        }

		int scroll = Mouse.getDWheel();
		if(scroll != 0) this.container.onMouseWheeled(mouseX, mouseY, scroll, null);
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		this.container.onKeyTyped(typedChar, keyCode, null);
	}
	
	
	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		this.warnNotToCall();
		this.container.onClick(mouseX, mouseY, mouseButton, null);
	}
	
	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		this.warnNotToCall();
		this.container.onMouseReleased(mouseX, mouseY, mouseButton, null);
	}
	
	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
		this.warnNotToCall();
		int dX = Math.round(mouseX - this.lastClickX[button]);
		int dY = Math.round(mouseY - this.lastClickY[button]);
		this.container.onMouseDragged(mouseX, mouseY, dX, dY, button, null);
	}
	
	private void warnNotToCall() {
		if(SmyLibGui.logger == null) return;
		SmyLibGui.logger.warn("Something called SmyLibGui's ScreenGui native vanilla input handling methods. This could cause weird behavior, call the IWidget floating point variants instead!");
		StackTraceElement[] lines = Thread.currentThread().getStackTrace();
		for(int i=1; i<lines.length; i++) SmyLibGui.logger.warn(lines[i]);
	}
	
	private void drawBackground() {
		switch(this.background) {
		case NONE:
			break;
		case DEFAULT:
			this.drawDefaultBackground();
			break;
		case DIRT:
			this.drawBackground(0);
			break;
		case OVERLAY:
			this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
			break;
		}
	}
	
	private class Container extends WidgetContainer {

		public Container() {
			super(Integer.MAX_VALUE);
		}

		@Override
		public float getX() {
			return 0;
		}

		@Override
		public float getY() {
			return 0;
		}

		@Override
		public float getWidth() {
			return Screen.this.width;
		}

		@Override
		public float getHeight() {
			return Screen.this.height;
		}
		
	}

}
