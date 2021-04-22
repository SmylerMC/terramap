package fr.thesmyler.smylibgui.widgets.text;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.Font;
import fr.thesmyler.smylibgui.RenderUtil;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;

public class TextComponentWidget implements IWidget {

	protected ITextComponent component;
	protected ITextComponent[] lines;
	protected float anchorX, x, anchorY, y;
	protected int z;
	protected float width, height, maxWidth;
	protected boolean visible = true;
	protected int baseColor;
	protected boolean shadow;
	protected int backgroundColor = 0x00000000;
	protected float padding = 0;
	protected Font font;
	protected ITextComponent hovered;
	protected TextAlignment alignment;

	public TextComponentWidget(float x, float y, int z, float maxWidth, ITextComponent component, TextAlignment alignment, int baseColor, boolean shadow, Font font) {
		this.anchorX = x;
		this.anchorY = y;
		this.z = z;
		this.component = component;
		this.font = font;
		this.alignment = alignment;
		this.maxWidth = maxWidth;
		this.baseColor = baseColor;
		this.shadow = shadow;
		this.updateCoords();
	}
	
	public TextComponentWidget(float x, float y, int z, ITextComponent component, TextAlignment alignment, Font font) {
		this(x, y, z, Float.MAX_VALUE, component, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextComponentWidget(float x, float y, int z, ITextComponent component, Font font) {
		this(x, y, z, component, TextAlignment.RIGHT, font);
	}
	
	public TextComponentWidget(int z, ITextComponent component, Font font) {
		this(0, 0, z, component, font);
	}

	@Override
	public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, Screen parent) {
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		float w = this.getWidth();
		float h = this.getHeight();
		RenderUtil.drawRect(x, y, x + w, y + h, this.backgroundColor);
		float drawY = y + this.padding;
		for(ITextComponent line: this.lines) {
			String ft = line.getFormattedText();
			float lineWidth = this.font.getStringWidth(ft);
			float lx = x + this.anchorX - this.x;
			switch(this.alignment) {
			case RIGHT:
				break;
			case LEFT:
				lx -= lineWidth;
				break;
			case CENTER:
				lx -= lineWidth/2;
				break;
			}
			this.font.drawString(ft, lx, drawY, this.baseColor, this.shadow);
			drawY += this.font.height() + this.padding;
		}
		this.hovered = this.getComponentUnder(mouseX - x, mouseY - y);
	}

	protected void updateCoords() {
		this.lines = this.font.splitText(this.component, this.maxWidth, true, false).toArray(new ITextComponent[] {});
		this.height = this.lines.length * (this.font.height() + this.padding) + this.padding ;
		float w = 0;
		for(ITextComponent line: this.lines) {
			String ft = line.getFormattedText();
			w = Math.max(w, this.font.getStringWidth(ft));
		}
		this.width = w + this.padding * 2;
		this.x = this.anchorX;
		switch(this.alignment) {
		case RIGHT:
			this.x -= this.padding;
			break;
		case LEFT:
			this.x -= this.width - this.padding;
			break;
		case CENTER:
			this.x -= this.width/2;
			break;
		}
		this.y = this.anchorY;
	}

	protected ITextComponent getComponentUnder(float x, float y) {
		if(x < this.padding || x > this.width - this.padding) return null;
		int lineIndex = (int) Math.floor((float)(y - this.padding) / (this.font.height() + this.padding));
		if(lineIndex < 0 || lineIndex >= this.lines.length) return null;
		if(y - this.padding - lineIndex*(this.font.height() + this.padding) > this.font.height()) return null;
		ITextComponent line = this.lines[lineIndex];
		float pos = this.padding;
		float lineWidth = this.font.getStringWidth(line.getFormattedText());
		switch(this.alignment) {
		case RIGHT:
			break;
		case LEFT:
			pos = this.width - lineWidth;
			break;
		case CENTER:
			pos = (this.width - lineWidth) / 2;
			break;
		}
		for(ITextComponent child: line.getSiblings()) {
			pos += this.font.getStringWidth(child.getFormattedText());
			if(pos >= x) return child;
		}
		return null;
	}

	@Override
	public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable Screen parent) {
		ITextComponent clicked = this.getComponentUnder(mouseX, mouseY);
		if(clicked != null) {
			Minecraft.getMinecraft().currentScreen.handleComponentClick(clicked);
		}
		parent.setFocus(null); //We don't want to retain focus
		return false;
	}
	
	public ITextComponent getComponent() {
		return this.component;
	}
	
	public TextComponentWidget setComponent(ITextComponent component) {
		this.component = component;
		this.updateCoords();
		return this;
	}
	
	@Override
	public float getX() {
		return this.x;
	}
	
	public float getAnchorX() {
		return this.anchorX;
	}
	
	public TextComponentWidget setAnchorX(float x) {
		this.anchorX = x;
		this.updateCoords();
		return this;
	}

	@Override
	public float getY() {
		return this.y;
	}
	
	public float getAnchorY() {
		return this.anchorY;
	}
	
	public TextComponentWidget setAnchorY(float y) {
		this.anchorY = y;
		this.updateCoords();
		return this;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public float getWidth() {
		return this.width;
	}
	
	public float getMaxWidth() {
		return this.maxWidth;
	}
	
	public TextComponentWidget setMaxWidth(float maxWidth) {
		this.maxWidth = maxWidth;
		this.updateCoords();
		return this;
	}

	@Override
	public float getHeight() {
		return this.height;
	}
	
	public int getBaseColor() {
		return this.baseColor;
	}
	
	public TextComponentWidget setBaseColor(int color) {
		this.baseColor = color;
		return this;
	}
	
	public boolean hasShadow() {
		return this.shadow;
	}
	
	public TextComponentWidget setShadow(boolean shadow) {
		this.shadow = shadow;
		return this;
	}
	
	public TextAlignment getAlignment() {
		return this.alignment;
	}
	
	public TextComponentWidget setAlignment(TextAlignment alignment) {
		this.alignment = alignment;
		this.updateCoords();
		return this;
	}

	@Override
	public long getTooltipDelay() {
		return 0;
	}

	@Override
	public String getTooltipText() {
		try {
			//TODO Adapt to non text tooltips
			return this.hovered.getStyle().getHoverEvent().getValue().getFormattedText();
		} catch(NullPointerException e) {
			return null;
		}
	}
	
	public int getBackgroundColor() {
		return this.backgroundColor;
	}
	
	public TextComponentWidget setBackgroundColor(int color) {
		this.backgroundColor = color;
		return this;
	}

	public float getPadding() {
		return padding;
	}

	public TextComponentWidget setPadding(float padding) {
		this.padding = padding;
		this.updateCoords();
		return this;
	}
	
	@Override
	public boolean isVisible(Screen parent) {
		return this.visible;
	}
	
	public TextComponentWidget setVisibility(boolean yesNo) {
		this.visible = yesNo;
		return this;
	}
	
	public TextComponentWidget show() {
		return this.setVisibility(true);
	}
	
	public TextComponentWidget hide() {
		return this.setVisibility(false);
	}

}
