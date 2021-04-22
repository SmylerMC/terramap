package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.RenderUtil;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

public class Scrollbar extends Screen {
	
	private static final int BAR_BG_COLOR = 0xF0111111;
	private static final int BAR_BORDER_COLOR = 0xFF000000;
	private static final int DRAG_BG_COLOR = 0xFF646464;
	private static final int DRAG_BG_COLOR_HOVER = 0xFF7E88BF;
	private static final int DRAG_BORDER_COLOR = 0xFFAAAAAA;
	
	protected TexturedButtonWidget upButton = new TexturedButtonWidget(1, IncludedTexturedButtons.UP);
	protected TexturedButtonWidget downButton = new TexturedButtonWidget(1, IncludedTexturedButtons.DOWN);
	protected Draggable drag = new Draggable();
	protected double progress = 0;
	protected double viewPort = 0.1;
	
	public Scrollbar(float x, float y, int z, float height) {
		super(x, y, z, 15, height, BackgroundType.NONE);
		this.downButton.setY(this.height - this.downButton.getHeight());
		this.upButton.setOnClick(this::scrollUp);
		this.downButton.setOnClick(this::scrollDown);
		this.upButton.enable();
		this.downButton.enable();
		this.addWidget(this.upButton).addWidget(this.downButton);
		this.addWidget(drag);
	}
	
	public Scrollbar(int z) {
		this(0, 0, z, 50);
	}
	
	@Override
	public boolean onClick(float mouseX, float mouseY, int mouseButton, Screen parent) {
		Minecraft.getMinecraft().getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
		float i = 4;
		float y = this.drag.getY();
		if(mouseY > y + this.drag.getHeight() && mouseY < this.getHeight() - this.downButton.getHeight()) {
			for(; i>0; i--)this.scrollDown();
		} else if(mouseY < y && mouseY > this.upButton.getHeight()){
			for(; i>0; i--)this.scrollUp();
		}
		super.onClick(mouseX, mouseY, mouseButton, parent);
		return false;
	}
	
	@Override
	public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, Screen parent) {
		return this.onClick(mouseX, mouseY, mouseButton, parent);
	}

	@Override
	public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, Screen parent) {
		RenderUtil.drawRect(x, y, x + this.width, y + this.height, BAR_BG_COLOR);
		RenderUtil.drawRect(x, y, x + 1, y + this.height, BAR_BORDER_COLOR);
		RenderUtil.drawRect(x + this.width - 1, y, x + this.width, y + this.height, BAR_BORDER_COLOR);
		super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
	}

	public void scrollUp() {
		this.progress = Math.max(0, this.progress - this.viewPort * 0.5);
	}
	
	public void scrollDown() {
		this.progress = Math.min(1, this.progress + this.viewPort * 0.5);
	}
	
	public Scrollbar setX(float x) {
		this.x = x;
		return this;
	}
	
	public Scrollbar setY(float y) {
		this.y = y;
		return this;
	}
	
	public Scrollbar setHeight(float height) {
		//TODO Better handling of floats
		this.height = Math.round(height);
		this.downButton.setY(this.height - this.downButton.getHeight());
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
	
	@Override
	public boolean isVisible(Screen parent) {
		return this.viewPort < 1;
	}

	private class Draggable implements IWidget {

		@Override
		public float getX() {
			return 1;
		}

		@Override
		public float getY() {
			float selfH = this.getHeight();
			float h = Scrollbar.this.height - Scrollbar.this.upButton.getHeight() - Scrollbar.this.downButton.getHeight() - selfH;
			return Scrollbar.this.upButton.getHeight() + (float)(h*Scrollbar.this.progress);
		}

		@Override
		public int getZ() {
			return 1;
		}

		@Override
		public float getWidth() {
			return 13;
		}

		@Override
		public float getHeight() {
			return (float) (Math.min(Scrollbar.this.viewPort, 1) * (Scrollbar.this.height - Scrollbar.this.upButton.getHeight() - Scrollbar.this.downButton.getHeight()));
		}

		@Override
		public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int button, Screen parent) {
			float selfH = this.getHeight();
			float h = Scrollbar.this.height - Scrollbar.this.upButton.getHeight() - Scrollbar.this.downButton.getHeight() - selfH;
			float frac = this.getY() + dY - Scrollbar.this.upButton.getHeight();
			Scrollbar.this.progress = MathHelper.clamp((double)frac / h, 0, 1);
		}
		
		@Override
		public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, Screen parent) {
			int bgcolor = hovered || focused ? DRAG_BG_COLOR_HOVER: DRAG_BG_COLOR;
			float height = this.getHeight();
			RenderUtil.drawRect(x, y, x + this.getWidth(), y + height, bgcolor);
			
			RenderUtil.drawRect(x, y, x + this.getWidth(), y + 1, DRAG_BORDER_COLOR);
			RenderUtil.drawRect(x, y + height - 1, x + this.getWidth(), y + height, DRAG_BORDER_COLOR);
			RenderUtil.drawRect(x, y, x + 1, y + height, DRAG_BORDER_COLOR);
			RenderUtil.drawRect(x + this.getWidth() - 1, y, x + this.getWidth(), y  + height, DRAG_BORDER_COLOR);
			
		}
		
	}

}
