package fr.thesmyler.smylibgui.screen;

import java.io.IOException;

import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.widgets.IWidget;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;

public class Screen extends GuiScreen {
	
	private final WidgetContainer container = new Container();
	
	private long startHoverTime;
	private IWidget lastHoveredWidget;
	private float lastRenderMouseX, lastRenderMouseY;
	
	private final InputProcessor processor;
	
	private BackgroundOption background;
	
	public Screen(BackgroundOption background) {
		this.background = background;
		this.processor = new InputProcessor(this.container);
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
				this.drawHoveringText(hoveredWidget.getTooltipText(), mouseX, mouseY);
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
		this.processor.processMouseEvent();
	}
	
	@Override
	protected void keyTyped(char typedChar, int keyCode) {
		this.container.onKeyTyped(typedChar, keyCode, null);
	}
	
	
	@Deprecated @Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		this.warnNotToCall();
		this.container.onClick(mouseX, mouseY, mouseButton, null);
	}
	
	@Deprecated @Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		this.warnNotToCall();
		this.container.onMouseReleased(mouseX, mouseY, mouseButton, null);
	}
	
	@Deprecated @Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
		this.warnNotToCall();
	}
	
	private void warnNotToCall() {
		if(SmyLibGui.logger == null) return;
		SmyLibGui.logger.warn("Something called SmyLibGui's ScreenGui native vanilla input handling methods. This could cause weird behavior, call the IWidget floating point variants instead!");
		StackTraceElement[] lines = Thread.currentThread().getStackTrace();
		for(int i=1; i<lines.length; i++) SmyLibGui.logger.warn(lines[i]);
	}
	
	public void drawHoveringText(String text, double x, double y) {
		// This is a workaround for vanilla not allowing double coordinates
		GlStateManager.pushMatrix();
		GlStateManager.translate(x, y, 0);
		this.drawHoveringText(text, 0, 0);
		GlStateManager.popMatrix();
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
