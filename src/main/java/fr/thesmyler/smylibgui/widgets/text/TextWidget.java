package fr.thesmyler.smylibgui.widgets.text;

import fr.thesmyler.smylibgui.Font;
import net.minecraft.util.text.TextComponentString;

public class TextWidget extends TextComponentWidget {
	
	public TextWidget(String text, float anchorX, float anchorY, int z, float maxWidth, TextAlignment alignment, int color, boolean shadow, Font font) {
		super(anchorX, anchorY, z, maxWidth, new TextComponentString(text), alignment, color, shadow, font);
	}
	
	public TextWidget(String text, float anchorX, float anchorY, int z, TextAlignment alignment, boolean shadow, Font font) {
		this(text, anchorX, anchorY, z, Float.MAX_VALUE, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, float anchorX, float anchorY, int z, TextAlignment alignment, Font font) {
		this(text, anchorX, anchorY, z, Float.MAX_VALUE, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(String text, float anchorX, float anchorY, int z, int color, boolean shadow, Font font) {
		this(text, anchorX, anchorY, z, Float.MAX_VALUE, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(String text, float anchorX, float anchorY, int z, boolean shadow, Font font) {
		this(text, anchorX, anchorY, z, Float.MAX_VALUE, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, float anchorX, float anchorY, int z, Font font) {
		this(text, anchorX, anchorY, z, Float.MAX_VALUE, TextAlignment.RIGHT, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(float anchorX, float anchorY, int z, TextAlignment alignment, boolean shadow, Font font) {
		this("", anchorX, anchorY, z, Float.MAX_VALUE, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(float anchorX, float anchorY, int z, TextAlignment alignment, Font font) {
		this("", anchorX, anchorY, z, Float.MAX_VALUE, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(float anchorX, float anchorY, int z, int color, boolean shadow, Font font) {
		this("", anchorX, anchorY, z, Float.MAX_VALUE, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(float anchorX, float anchorY, int z, boolean shadow, Font font) {
		this("", anchorX, anchorY, z, Float.MAX_VALUE, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(float anchorX, float anchorY, int z, Font font) {
		this("", anchorX, anchorY, z, Float.MAX_VALUE, TextAlignment.RIGHT, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(String text, int z, TextAlignment alignment, int color, boolean shadow, Font font) {
		this(text, 0, 0, z, Float.MAX_VALUE, alignment, color, shadow, font);
	}
	
	public TextWidget(String text, int z, TextAlignment alignment, boolean shadow, Font font) {
		this(text, z, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, int z, TextAlignment alignment, Font font) {
		this(text, z, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(String text, int z, int color, boolean shadow, Font font) {
		this(text, z, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(String text, int z, boolean shadow, Font font) {
		this(text, z, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, int z, Font font) {
		this(text, z, TextAlignment.RIGHT, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(int z, TextAlignment alignment, boolean shadow, Font font) {
		this("", z, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(int z, TextAlignment alignment, Font font) {
		this("", z, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(int z, int color, boolean shadow, Font font) {
		this("", z, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(int z, boolean shadow, Font font) {
		this("", z, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(int z, Font font) {
		this("", z, TextAlignment.RIGHT, 0xFFFFFFFF, true, font);
	}

	public int getColor() {
		return this.getBaseColor();
	}

	/**
	 * Warper for setBaseColor
	 * 
	 * @param color
	 * @return this
	 */
	public TextWidget setColor(int color) {
		this.setBaseColor(color);
		return this;
	}

	public String getText() {
		return this.getComponent().getFormattedText();
	}

	public TextWidget setText(String text) {
		this.setComponent(new TextComponentString(text));
		return this;
	}
	
	@Override
	public TextWidget setAnchorX(float anchorX) {
		super.setAnchorX(anchorX);
		return this;
	}
	
	@Override
	public TextWidget setAnchorY(float anchorY) {
		super.setAnchorY(anchorY);
		return this;
	}
	
	@Override
	public TextWidget setMaxWidth(float maxWidth) {
		super.setMaxWidth(maxWidth);
		return this;
	}

	@Override
	public TextWidget setAlignment(TextAlignment alignment) {
		super.setAlignment(alignment);
		return this;
	}
	
	@Override
	public TextComponentWidget setShadow(boolean shadow) {
		super.setShadow(shadow);
		return this;
	}
	
	
}
