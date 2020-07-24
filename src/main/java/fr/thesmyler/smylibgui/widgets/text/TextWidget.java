package fr.thesmyler.smylibgui.widgets.text;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;

public class TextWidget implements IWidget {

	private int anchorX, x, anchorY, y, z, width, height, color;
	private boolean shadow;
	private String text;
	private TextAlignment alignment;
	private FontRendererContainer font;
	
	public TextWidget(String text, int anchorX, int anchorY, int z, TextAlignment alignment, int color, boolean shadow, FontRendererContainer font) {
		this.text = text;
		this.anchorX = anchorX;
		this.anchorY = anchorY;
		this.z = z;
		this.alignment = alignment;
		this.color = color;
		this.shadow = shadow;
		this.font = font;
		this.updateCoords();
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, TextAlignment alignment, boolean shadow, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, TextAlignment alignment, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, int color, boolean shadow, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, boolean shadow, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, TextAlignment.RIGHT, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, TextAlignment alignment, boolean shadow, FontRendererContainer font) {
		this("", anchorX, anchorY, z, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, TextAlignment alignment, FontRendererContainer font) {
		this("", anchorX, anchorY, z, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, int color, boolean shadow, FontRendererContainer font) {
		this("", anchorX, anchorY, z, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, boolean shadow, FontRendererContainer font) {
		this("", anchorX, anchorY, z, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, FontRendererContainer font) {
		this("", anchorX, anchorY, z, TextAlignment.RIGHT, 0xFFFFFFFF, true, font);
	}
	
	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
//		this.font.drawString(this.getText(), x, y, this.getColor(), this.hasShadow()); //TODO Until shadows are fixed
		this.font.drawString(this.getText(), x, y, this.getColor(), false);
	}
	
	private void updateCoords() {
		this.y = this.anchorY;
		this.x = this.anchorX;
		this.width = this.font.getStringWidth(this.getText());
		this.height = this.font.FONT_HEIGHT;
		switch(this.alignment) {
		case RIGHT:
			break;
		case LEFT:
			this.x -= this.width;
			break;
		case CENTER:
			this.x -= this.width/2;
			break;
		}
	}
	
	@Override
	public int getX() {
		return this.x;
	}
	
	@Override
	public int getY() {
		return this.y;	
	}
	
	@Override
	public int getZ() {
		return this.z;
	}
	
	@Override
	public int getWidth() {
		return this.width;
	}
	
	@Override
	public int getHeight() {
		return this.height;
	}

	public int getAnchorX() {
		return anchorX;
	}

	public void setAnchorX(int anchorX) {
		this.anchorX = anchorX;
		this.updateCoords();
	}

	public int getAnchorY() {
		return anchorY;
	}

	public void setAnchorY(int anchorY) {
		this.anchorY = anchorY;
		this.updateCoords();
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public boolean hasShadow() {
		return shadow;
	}

	public void setShadow(boolean shadow) {
		this.shadow = shadow;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
		this.updateCoords();
	}

	public TextAlignment getAlignment() {
		return alignment;
	}

	public void setAlignment(TextAlignment alignment) {
		this.alignment = alignment;
		this.updateCoords();
	}	
	
}
