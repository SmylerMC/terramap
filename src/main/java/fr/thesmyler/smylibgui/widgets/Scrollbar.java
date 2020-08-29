package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.util.math.MathHelper;

public class Scrollbar extends Screen {
	
	private static final int BAR_BG_COLOR = 0xF0111111;
	private static final int BAR_BORDER_COLOR = 0xFF606060;
	private static final int DRAG_BG_COLOR = 0xFFE0E0FF;
	private static final int DRAG_BG_COLOR_HOVER = 0xFFA0A0A0;
	private static final int DRAG_BORDER_COLOR = 0xFFE0E0E0;
	
	protected TexturedButtonWidget upButton = new TexturedButtonWidget(1, IncludedTexturedButtons.UP);
	protected TexturedButtonWidget downButton = new TexturedButtonWidget(1, IncludedTexturedButtons.DOWN);
	protected Draggable drag = new Draggable();
	protected double progress = 0.5;
	protected double viewPort = 0.1;
	
	public Scrollbar(int x, int y, int z, int height) {
		super(x, y, z, 15, height, BackgroundType.NONE);
		this.downButton.setY(this.height - this.downButton.getHeight());
		this.upButton.setOnClick(this::scrollUp);
		this.downButton.setOnClick(this::scrollDown);
		this.upButton.enable();
		this.downButton.enable();
		this.addWidget(this.upButton).addWidget(this.downButton);
		this.addWidget(drag);
	}
	
	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		int i = 4;
		int y = this.drag.getY();
		if(mouseY > y + this.drag.getHeight() && mouseY < this.getHeight() - this.downButton.getHeight()) {
			for(; i>0; i--)this.scrollDown();
		} else if(mouseY < y && mouseY > this.upButton.getHeight()){
			for(; i>0; i--)this.scrollUp();
		}
		super.onClick(mouseX, mouseY, mouseButton, parent);
		return false;
	}
	
	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		return this.onClick(mouseX, mouseY, mouseButton, parent);
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean screenHovered, boolean screenFocused, Screen parent) {
		GuiScreen.drawRect(x, y, x + this.width, y + this.height, BAR_BG_COLOR);
		GuiScreen.drawRect(x, y, x + 1, y + this.height, BAR_BORDER_COLOR);
		GuiScreen.drawRect(x + this.width - 1, y, x + this.width, y + this.height, BAR_BORDER_COLOR);
		super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
	}

	public void scrollUp() {
		this.progress = Math.max(0, this.progress - this.viewPort * 0.5);
	}
	
	public void scrollDown() {
		this.progress = Math.min(1, this.progress + this.viewPort * 0.5);
	}
	
	public Scrollbar setY(int y) {
		this.y = y;
		return this;
	}
	
	public Scrollbar setHeight(int height) {
		this.height = height;
		return this;
	}
	
	public double getProgress() {
		return this.progress;
	}
	
	public Scrollbar setProgress(double progress) {
		this.progress = MathHelper.clamp(progress, 0, 1);
		return this;
	}
	
	public double getViewPort() {
		return this.viewPort;
	}
	
	public Scrollbar setViewPort(double viewPort) {
		this.viewPort = viewPort;
		return this;
	}

	private class Draggable implements IWidget {

		@Override
		public int getX() {
			return 1;
		}

		@Override
		public int getY() {
			int selfH = this.getHeight();
			int h = Scrollbar.this.height - Scrollbar.this.upButton.getHeight() - Scrollbar.this.downButton.getHeight() - selfH;
			return Scrollbar.this.upButton.getHeight() + (int) Math.round(h*Scrollbar.this.progress);
		}

		@Override
		public int getZ() {
			return 1;
		}

		@Override
		public int getWidth() {
			return 13;
		}

		@Override
		public int getHeight() {
			return (int) Math.round(Scrollbar.this.viewPort * (Scrollbar.this.height - Scrollbar.this.upButton.getHeight() - Scrollbar.this.downButton.getHeight()));
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int button, Screen parent) {
			int selfH = this.getHeight();
			int h = Scrollbar.this.height - Scrollbar.this.upButton.getHeight() - Scrollbar.this.downButton.getHeight() - selfH;
			int frac = this.getY() + dY - Scrollbar.this.upButton.getHeight();
			Scrollbar.this.progress = MathHelper.clamp((double)frac / h, 0, 1);
		}
		
		@Override
		public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
			int bgcolor = hovered ? DRAG_BG_COLOR: DRAG_BG_COLOR_HOVER;
			int height = this.getHeight();
			GuiScreen.drawRect(x, y, x + this.getWidth(), y + height, bgcolor);
			
			GuiScreen.drawRect(x, y, x + this.getWidth(), y + 1, DRAG_BORDER_COLOR);
			GuiScreen.drawRect(x, y + height - 1, x + this.getWidth(), y + height, DRAG_BORDER_COLOR);
			GuiScreen.drawRect(x, y, x + 1, y + height, DRAG_BORDER_COLOR);
			GuiScreen.drawRect(x + this.getWidth() - 1, y, x + this.getWidth(), y  + height, DRAG_BORDER_COLOR);
			
		}
		
	}

}
