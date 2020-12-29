package fr.thesmyler.smylibgui.widgets.text;

import net.minecraft.util.text.TextComponentString;

public class TextWidget extends TextComponentWidget {
	
	public TextWidget(String text, int anchorX, int anchorY, int z, int maxWidth, TextAlignment alignment, int color, boolean shadow, FontRendererContainer font) {
		super(anchorX, anchorY, z, maxWidth, new TextComponentString(text), alignment, color, shadow, font);
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, TextAlignment alignment, boolean shadow, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, Integer.MAX_VALUE, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, TextAlignment alignment, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, Integer.MAX_VALUE, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, int color, boolean shadow, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, Integer.MAX_VALUE, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, boolean shadow, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, Integer.MAX_VALUE, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, int anchorX, int anchorY, int z, FontRendererContainer font) {
		this(text, anchorX, anchorY, z, Integer.MAX_VALUE, TextAlignment.RIGHT, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, TextAlignment alignment, boolean shadow, FontRendererContainer font) {
		this("", anchorX, anchorY, z, Integer.MAX_VALUE, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, TextAlignment alignment, FontRendererContainer font) {
		this("", anchorX, anchorY, z, Integer.MAX_VALUE, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, int color, boolean shadow, FontRendererContainer font) {
		this("", anchorX, anchorY, z, Integer.MAX_VALUE, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, boolean shadow, FontRendererContainer font) {
		this("", anchorX, anchorY, z, Integer.MAX_VALUE, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(int anchorX, int anchorY, int z, FontRendererContainer font) {
		this("", anchorX, anchorY, z, Integer.MAX_VALUE, TextAlignment.RIGHT, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(String text, int z, TextAlignment alignment, int color, boolean shadow, FontRendererContainer font) {
		this(text, 0, 0, z, Integer.MAX_VALUE, alignment, color, shadow, font);
	}
	
	public TextWidget(String text, int z, TextAlignment alignment, boolean shadow, FontRendererContainer font) {
		this(text, z, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, int z, TextAlignment alignment, FontRendererContainer font) {
		this(text, z, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(String text, int z, int color, boolean shadow, FontRendererContainer font) {
		this(text, z, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(String text, int z, boolean shadow, FontRendererContainer font) {
		this(text, z, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(String text, int z, FontRendererContainer font) {
		this(text, z, TextAlignment.RIGHT, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(int z, TextAlignment alignment, boolean shadow, FontRendererContainer font) {
		this("", z, alignment, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(int z, TextAlignment alignment, FontRendererContainer font) {
		this("", z, alignment, 0xFFFFFFFF, true, font);
	}
	
	public TextWidget(int z, int color, boolean shadow, FontRendererContainer font) {
		this("", z, TextAlignment.RIGHT, color, shadow, font);
	}
	
	public TextWidget(int z, boolean shadow, FontRendererContainer font) {
		this("", z, TextAlignment.RIGHT, 0xFFFFFFFF, shadow, font);
	}
	
	public TextWidget(int z, FontRendererContainer font) {
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
	public TextWidget setAnchorX(int anchorX) {
		super.setAnchorX(anchorX);
		return this;
	}
	
	@Override
	public TextWidget setAnchorY(int anchorY) {
		super.setAnchorY(anchorY);
		return this;
	}
	
	@Override
	public TextWidget setMaxWidth(int maxWidth) {
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
