package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.container.FlexibleWidgetContainer;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.math.MathHelper;

//TODO Use a texture
public class Scrollbar extends FlexibleWidgetContainer {
	
	private static final Color BAR_BG_COLOR = Color.DARKER_OVERLAY;
	private static final Color BAR_BORDER_COLOR = Color.BLACK;
	private static final Color DRAG_BG_COLOR = Color.DARK_GRAY;
	private static final Color DRAG_BG_COLOR_HOVER = Color.SELECTION;
	private static final Color DRAG_BORDER_COLOR = Color.MEDIUM_GRAY;
	
	protected TexturedButtonWidget upButton = new TexturedButtonWidget(1, IncludedTexturedButtons.UP);
	protected TexturedButtonWidget downButton = new TexturedButtonWidget(1, IncludedTexturedButtons.DOWN);
	protected Draggable drag = new Draggable();
	protected double progress = 0;
	protected double viewPort = 0.1;
	
	public Scrollbar(float x, float y, int z, float height) {
		super(x, y, z, 15, height);
		this.downButton.setY(this.getHeight() - this.downButton.getHeight());
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
	public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
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
	public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
		return this.onClick(mouseX, mouseY, mouseButton, parent);
	}

	@Override
	public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, WidgetContainer parent) {
		float width = this.getWidth();
		float height = this.getHeight();
		RenderUtil.drawRect(x, y, x + width, y + height, BAR_BG_COLOR);
		RenderUtil.drawRect(x, y, x + 1, y + height, BAR_BORDER_COLOR);
		RenderUtil.drawRect(x + width - 1, y, x + width, y + height, BAR_BORDER_COLOR);
		super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
	}

	public void scrollUp() {
		this.progress = Math.max(0, this.progress - this.viewPort * 0.5);
	}
	
	public void scrollDown() {
		this.progress = Math.min(1, this.progress + this.viewPort * 0.5);
	}
	
	@Override
	public void setHeight(float height) {
		this.setSize(this.getWidth(), height);
		this.downButton.setY(this.getHeight() - this.downButton.getHeight());
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
	public boolean isVisible(WidgetContainer parent) {
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
			float h = Scrollbar.this.getHeight() - Scrollbar.this.upButton.getHeight() - Scrollbar.this.downButton.getHeight() - selfH;
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
			return (float) (Math.min(Scrollbar.this.viewPort, 1) * (Scrollbar.this.getHeight() - Scrollbar.this.upButton.getHeight() - Scrollbar.this.downButton.getHeight()));
		}

		@Override
		public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int button, WidgetContainer parent, long dt) {
			float selfH = this.getHeight();
			float h = Scrollbar.this.getHeight() - Scrollbar.this.upButton.getHeight() - Scrollbar.this.downButton.getHeight() - selfH;
			float frac = this.getY() + dY - Scrollbar.this.upButton.getHeight();
			Scrollbar.this.progress = MathHelper.clamp((double)frac / h, 0, 1);
		}
		
		@Override
		public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
			Color bgcolor = hovered || focused ? DRAG_BG_COLOR_HOVER: DRAG_BG_COLOR;
			float height = this.getHeight();
			RenderUtil.drawRect(x, y, x + this.getWidth(), y + height, bgcolor);
			
			RenderUtil.drawRect(x, y, x + this.getWidth(), y + 1, DRAG_BORDER_COLOR);
			RenderUtil.drawRect(x, y + height - 1, x + this.getWidth(), y + height, DRAG_BORDER_COLOR);
			RenderUtil.drawRect(x, y, x + 1, y + height, DRAG_BORDER_COLOR);
			RenderUtil.drawRect(x + this.getWidth() - 1, y, x + this.getWidth(), y  + height, DRAG_BORDER_COLOR);
			
		}
		
	}

}
