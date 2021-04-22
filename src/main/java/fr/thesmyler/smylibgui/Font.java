package fr.thesmyler.smylibgui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiUtilRenderComponents;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public class Font {

	private static final String SRG_renderStringAtPos = "func_78255_a";
	private static Method renderStringAtPos;
	private static final String SRG_bidiReorder = "func_147647_b";
	private static Method bidiReorder;
	private static final String SRG_resetStyles = "func_78265_b";
	private static Method resetStyles;
	private static final String SRG_trimStringNewline = "func_78273_d";
	private static Method trimStringNewline;
	private static final String SRG_posX = "field_78295_j";
	private static Field posX;
	private static final String SRG_posY = "field_78296_k";
	private static Field posY;
	private static final String SRG_red = "field_78291_n";
	private static Field red;
	private static final String SRG_green = "field_78306_p";
	private static Field green;
	private static final String SRG_blue = "field_78292_o";
	private static Field blue;
	private static final String SRG_alpha = "field_78305_q";
	private static Field alpha;
	private static final String SRG_textColor = "field_78304_r";
	private static Field textColor;
	private static final String SRG_randomStyle = "field_78303_s";
	private static Field randomStyle;
	private static final String SRG_boldStyle = "field_78302_t";
	private static Field boldStyle;
	private static final String SRG_strikethroughStyle = "field_78299_w";
	private static Field strikethroughStyle;
	private static final String SRG_underlineStyle = "field_78300_v";
	private static Field underlineStyle;
	private static final String SRG_italicStyle = "field_78301_u";
	private static Field italicStyle;
	private static final String SRG_colorCode = "field_78285_g";
	private static Field colorCode;
	
	public float height() {
		//TODO variable size
		return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(String text, float x, float y, int color, boolean dropShadow) {
		try {
			this.enableAlpha();
			GlStateManager.enableBlend();
			this.resetStyles();
			int i;
			if (dropShadow) {
				i = this.renderString(text, x + 1.0F, y + 1.0F, color, true);
				i = Math.max(i, this.renderString(text, x, y, color, false));
			} else {
				i = this.renderString(text, x, y, color, false);
			}
			return i;
		} catch (Exception e) {
			if(SmyLibGui.debug) {
				SmyLibGui.logger.error("Failed to use custom drawString in custom font renderer!");
				SmyLibGui.logger.catching(e);
			}
			return this.getFont().drawString(text, x, y, color, dropShadow);
		}
	}

	public void drawCenteredString(float x, float y, String str, int color, boolean shadow) {
		int w = this.getStringWidth(str);
		this.drawString(str, x - w/2, y, color, shadow);
	}

	/**
	 * Splits and draws a String with wordwrap
	 */
	public void drawSplitString(String str, float x, float y, float wrapWidth, int textColor) {
		try {
			this.resetStyles();
			this.setTextColor(textColor);
			String s = this.trimStringNewline(str);
			this.renderSplitString(s, x, y, wrapWidth, false);
		} catch (Exception e) {
			if(SmyLibGui.debug) {
				SmyLibGui.logger.error("Failed to use custom drawSplitString in custom font renderer!");
				SmyLibGui.logger.catching(e);
			}
			this.drawSplitString(str, x, y, wrapWidth, textColor);
		}
	}

	protected int renderString(String text, float x, float y, int color, boolean dropShadow) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if (text == null) {
			return 0;
		} else {
			String t = text;
			if (this.getFont().getBidiFlag()) {
				t = this.bidiReorder(text);
			}

			int shadowedColor = color;

			if (dropShadow) {
				shadowedColor = (color & 16579836) >> 2 | color & -16777216;
			}

			float red = (float)(shadowedColor >> 16 & 255) / 255.0F;
			float green = (float)(shadowedColor >> 8 & 255) / 255.0F;
			float blue = (float)(shadowedColor & 255) / 255.0F;
			float alpha = (float)(shadowedColor >> 24 & 255) / 255.0F;
			this.setRed(red);
			this.setGreen(green);
			this.setBlue(blue);
			this.setAlpha(alpha);
			this.setColor(red, green, blue, alpha);
			this.setPosX(x);
			this.setPosY(y);
			this.renderStringAtPos(t, dropShadow);
			return (int)this.getPosX();
		}
	}

	/**
	 * Perform actual work of rendering a multi-line string with wordwrap and with darker drop shadow color if flag is
	 * set
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	protected void renderSplitString(String str, float x, float y, float wrapWidth, boolean addShadow) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		float t = y;
		//TODO better handling of float widths
		for (String s : this.listFormattedStringToWidth(str, Math.round(wrapWidth))) {
			this.renderStringAligned(s, x, t, wrapWidth, this.getTextColor(), addShadow);
			t += this.getFont().FONT_HEIGHT;
		}
	}

	/**
	 * Render string either left or right aligned depending on bidiFlag
	 * @throws InvocationTargetException 
	 * @throws IllegalArgumentException 
	 * @throws IllegalAccessException 
	 */
	protected int renderStringAligned(String text, float x, float y, float width, int color, boolean dropShadow) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		float t = x;
		if (this.getFont().getBidiFlag()) {
			float i = this.getStringWidth(this.bidiReorder(text));
			t = t + width - i;
		}

		return this.renderString(text, t, y, color, dropShadow);
	}
	
	public List<ITextComponent> splitText(ITextComponent textComponent, float maxTextLenght, boolean keepTrailingBlank, boolean forceTextColor) {
		//TODO Better float handling
		return GuiUtilRenderComponents.splitText(textComponent, Math.round(maxTextLenght), this.getFont(), keepTrailingBlank, forceTextColor);

	}

	protected void renderStringAtPos(String text, boolean shadow) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(renderStringAtPos == null) 
			renderStringAtPos = ObfuscationReflectionHelper.findMethod(FontRenderer.class, SRG_renderStringAtPos, Void.TYPE, String.class, Boolean.TYPE);
		renderStringAtPos.invoke(this.getFont(), text, shadow);
	}

	protected String trimStringNewline(String t) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(trimStringNewline == null)
			trimStringNewline = ObfuscationReflectionHelper.findMethod(FontRenderer.class, SRG_trimStringNewline, String.class, String.class);
		return (String) trimStringNewline.invoke(this.getFont(), t);
	}

	protected void resetStyles() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(resetStyles == null)
			resetStyles = ObfuscationReflectionHelper.findMethod(FontRenderer.class, SRG_resetStyles, Void.TYPE);
		resetStyles.invoke(this.getFont());
	}

	protected String bidiReorder(String text) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		if(bidiReorder == null)
			bidiReorder = ObfuscationReflectionHelper.findMethod(FontRenderer.class, SRG_bidiReorder, String.class, String.class);
		return (String) bidiReorder.invoke(this.getFont(), text);
	}

	protected int getTextColor() throws IllegalArgumentException, IllegalAccessException {
		if(textColor == null) textColor = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_textColor);
		return textColor.getInt(this.getFont());
	}

	protected void setTextColor(int color) throws IllegalArgumentException, IllegalAccessException {
		if(textColor == null) textColor = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_textColor);
		textColor.setInt(this.getFont(), color);
	}

	protected float getRed() throws IllegalArgumentException, IllegalAccessException {
		if(red == null) red = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_red);
		return red.getFloat(this.getFont());
	}

	protected void setRed(float value) throws IllegalArgumentException, IllegalAccessException {
		if(red == null) red = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_red);
		red.setFloat(this.getFont(), value);
	}

	protected float getGreen() throws IllegalArgumentException, IllegalAccessException {
		if(green == null) green = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_green);
		return green.getFloat(this.getFont());
	}

	protected void setGreen(float value) throws IllegalArgumentException, IllegalAccessException {
		if(green == null) green = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_green);
		green.setFloat(this.getFont(), value);
	}

	protected float getBlue() throws IllegalArgumentException, IllegalAccessException {
		if(blue == null) blue = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_blue);
		return blue.getFloat(this.getFont());
	}

	protected void setBlue(float value) throws IllegalArgumentException, IllegalAccessException {
		if(blue == null) blue = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_blue);
		blue.setFloat(this.getFont(), value);
	}

	protected float getAlpha() throws IllegalArgumentException, IllegalAccessException {
		if(alpha == null) alpha = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_alpha);
		return alpha.getFloat(this.getFont());
	}

	protected void setAlpha(float value) throws IllegalArgumentException, IllegalAccessException {
		if(alpha == null) alpha = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_alpha);
		alpha.setFloat(this.getFont(), value);
	}

	protected float getPosX() throws IllegalArgumentException, IllegalAccessException {
		if(posX == null) posX = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_posX);
		return posX.getFloat(this.getFont());
	}

	protected void setPosX(float value) throws IllegalArgumentException, IllegalAccessException {
		if(posX == null) posX = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_posX);
		posX.set(this.getFont(), value);
	}

	protected float getPosY() throws IllegalArgumentException, IllegalAccessException {
		if(posY == null) posY = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_posY);
		return posY.getFloat(this.getFont());
	}

	protected void setPosY(float value) throws IllegalArgumentException, IllegalAccessException {
		if(posY == null) posY = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_posY);
		posY.set(this.getFont(), value);
	}

	protected boolean getBoldStyle() throws IllegalArgumentException, IllegalAccessException {
		if(boldStyle == null) boldStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_boldStyle);
		return boldStyle.getBoolean(this.getFont());
	}

	protected void setBoldStyle(boolean yesNo) throws IllegalArgumentException, IllegalAccessException {
		if(boldStyle == null) boldStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_boldStyle);
		boldStyle.setBoolean(this.getFont(), yesNo);
	}

	protected boolean getUnderlineStyle() throws IllegalArgumentException, IllegalAccessException {
		if(underlineStyle == null) underlineStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_underlineStyle);
		return underlineStyle.getBoolean(this.getFont());
	}

	protected void setUnderlineStyle(boolean yesNo) throws IllegalArgumentException, IllegalAccessException {
		if(underlineStyle == null) underlineStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_underlineStyle);
		underlineStyle.setBoolean(this.getFont(), yesNo);
	}

	protected boolean getStrikethroughStyle() throws IllegalArgumentException, IllegalAccessException {
		if(strikethroughStyle == null) strikethroughStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_strikethroughStyle);
		return strikethroughStyle.getBoolean(this.getFont());
		}

	protected void setStrikethroughStyle(boolean yesNo) throws IllegalArgumentException, IllegalAccessException {
		if(strikethroughStyle == null) strikethroughStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_strikethroughStyle);
		strikethroughStyle.setBoolean(this.getFont(), yesNo);
	}

	protected boolean getItalicStyle() throws IllegalArgumentException, IllegalAccessException {
		if(italicStyle == null) italicStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_italicStyle);
		return italicStyle.getBoolean(this.getFont());
	}

	protected void setItalicStyle(boolean yesNo) throws IllegalArgumentException, IllegalAccessException {
		if(italicStyle == null) italicStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_italicStyle);
		italicStyle.setBoolean(this.getFont(), yesNo);
	}

	protected boolean getRandomStyle() throws IllegalArgumentException, IllegalAccessException {
		if(randomStyle == null) randomStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_randomStyle);
		return randomStyle.getBoolean(this.getFont());
	}

	protected void setRandomStyle(boolean yesNo) throws IllegalArgumentException, IllegalAccessException {
		if(randomStyle == null) randomStyle = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_randomStyle);
		randomStyle.setBoolean(this.getFont(), yesNo);
	}

	protected int[] getColorCode() throws IllegalArgumentException, IllegalAccessException {
		if(colorCode == null) colorCode = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_colorCode);
		return (int[]) boldStyle.get(this.getFont());
	}

	protected void setColorCode(int[] value) throws IllegalArgumentException, IllegalAccessException {
		if(colorCode == null) colorCode = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_colorCode);
		boldStyle.set(this.getFont(), value);
	}

	protected void setColor(float r, float g, float b, float a) {
		GlStateManager.color(r, g, b, a);
	}

	protected void enableAlpha() {
		GlStateManager.enableAlpha();
	}

	/**
	 * Draws the specified string with a shadow.
	 */
	public int drawStringWithShadow(String text, float x, float y, int color) {
		return this.drawString(text, x, y, color, true);
	}

	/**
	 * Draws the specified string.
	 */
	public int drawString(String text, int x, int y, int color) {
		return this.drawString(text, (float)x, (float)y, color, false);
	}

	/**
	 * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
	 */
	//FIXME for some reasons, FontRenderer#getStringWidth(String) seams to get stuck in an infinite loop when the Screen is too small, and eclipse's debugger cannot even step there ?
	public int getStringWidth(String text) {
		return this.getFont().getStringWidth(text);
	}

	/**
	 * Returns the width of this character as rendered.
	 */
	public int getCharWidth(char character) {
		return this.getFont().getCharWidth(character);
	}

	/**
	 * Trims a string to fit a specified Width.
	 */
	public String trimStringToWidth(String text, float width) {
		return this.getFont().trimStringToWidth(text, (int)Math.floor(width));
	}

	/**
	 * Trims a string to a specified width, optionally starting from the end and working backwards.
	 * <h3>Samples:</h3>
	 * (Assuming that {@link #getCharWidth(char)} returns <code>6</code> for all of the characters in
	 * <code>0123456789</code> on the current resource pack)
	 * <table>
	 * <tr><th>Input</th><th>Returns</th></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 1, false)</code></td><td><samp>""</samp></td></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 6, false)</code></td><td><samp>"0"</samp></td></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 29, false)</code></td><td><samp>"0123"</samp></td></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 30, false)</code></td><td><samp>"01234"</samp></td></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 9001, false)</code></td><td><samp>"0123456789"</samp></td></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 1, true)</code></td><td><samp>""</samp></td></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 6, true)</code></td><td><samp>"9"</samp></td></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 29, true)</code></td><td><samp>"6789"</samp></td></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 30, true)</code></td><td><samp>"56789"</samp></td></tr>
	 * <tr><td><code>trimStringToWidth("0123456789", 9001, true)</code></td><td><samp>"0123456789"</samp></td></tr>
	 * </table>
	 */
	public String trimStringToWidth(String text, float width, boolean reverse) {
		return this.getFont().trimStringToWidth(text, (int)Math.ceil(width), reverse);
	}

	/**
	 * Returns the height (in pixels) of the given string if it is wordwrapped to the given max width.
	 */
	public int getWordWrappedHeight(String str, int maxLength) {
		return this.getFont().getWordWrappedHeight(str, maxLength);
	}

	/**
	 * Set unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
	 * font.
	 */
	public void setUnicodeFlag(boolean unicodeFlagIn) {
		this.getFont().setUnicodeFlag(unicodeFlagIn);
	}

	/**
	 * Get unicodeFlag controlling whether strings should be rendered with Unicode fonts instead of the default.png
	 * font.
	 */
	public boolean getUnicodeFlag() {
		return this.getFont().getUnicodeFlag();
	}

	/**
	 * Set bidiFlag to control if the Unicode Bidirectional Algorithm should be run before rendering any string.
	 */
	public void setBidiFlag(boolean bidiFlagIn) {
		this.getFont().setBidiFlag(bidiFlagIn);
	}

	/**
	 * Breaks a string into a list of pieces where the width of each line is always less than or equal to the provided
	 * width. Formatting codes will be preserved between lines.
	 */
	public List<String> listFormattedStringToWidth(String str, int wrapWidth) {
		return this.getFont().listFormattedStringToWidth(str, wrapWidth);
	}
	
	private FontRenderer getFont() {
		return Minecraft.getMinecraft().fontRenderer;
	}

}
