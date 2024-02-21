package net.smyler.smylib.gui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.smyler.smylib.Color;

public class ReflectedFontRenderer extends BaseFont {

    protected final float scale;

    public ReflectedFontRenderer(float size) {
        this.scale = size;
    }

    @Override
    public float scale() {
        return this.scale;
    }

    @Override
    public Font withScale(float scale) {
        return new ReflectedFontRenderer(scale);
    }

    @Override
    public Font scaled(float scaleFactor) {
        return new ReflectedFontRenderer(this.scale * scaleFactor);
    }

    @Override
    public float height() {
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * this.scale;
    }

    @Override
    public float drawString(float x, float y, String text, Color color, boolean shadow) {
        try {
            this.enableAlpha();
            GlStateManager.enableBlend();
            this.resetStyles();
            if (shadow) {
                float endX = this.renderString(text, x + this.scale, y + this.scale, color, true);
                return Math.max(endX, this.renderString(text, x, y, color, false));
            } else {
                return this.renderString(text, x, y, color, false);
            }
        } catch (Exception e) {
            throw new IllegalStateException("Failed to draw String!");
        }
    }

    @Override
    public void drawCenteredString(float x, float y, String text, Color color, boolean shadow) {
        float w = this.getStringWidth(text);
        this.drawString(x - w/2, y, text, color, shadow);
    }

    @Override
    public void drawSplitString(float x, float y, String text, float wrapWidth, Color color, boolean shadow) {
        try {
            this.resetStyles();
            String trimed = this.trimStringNewline(text);
            this.renderSplitString(x, y, trimed, wrapWidth, color, shadow);
        } catch (Exception e) {
            throw new IllegalStateException("Failed to use custom drawSplitString in custom font renderer!");
        }
    }

    protected float renderString(String text, float x, float y, Color color, boolean shadow) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        if (text == null) {
            return 0;
        } else {
            String actualText = text;
            if (this.getFont().getBidiFlag()) {
                actualText = this.bidiReorder(text);
            }

            Color actualColor = color;

            if (shadow) {
                actualColor = new Color((color.encoded() & 16579836) >> 2 | color.encoded() & 0xFF000000);
            }

            float red = actualColor.redf();
            float green = actualColor.greenf();
            float blue = actualColor.bluef();
            float alpha = actualColor.alphaf();
            this.setRed(red);
            this.setGreen(green);
            this.setBlue(blue);
            this.setAlpha(alpha);
            this.setColor(red, green, blue, alpha);
            GlStateManager.pushMatrix();
            this.setPosX(0);
            this.setPosY(0);
            GlStateManager.translate(x, y, 0);
            GlStateManager.scale(this.scale, this.scale, 1);
            this.renderStringAtPos(actualText, shadow);
            GlStateManager.popMatrix();
            this.setPosX(x + this.getPosX()*this.scale);
            this.setPosY(y);
            return this.getPosX();
        }
    }

    protected void renderSplitString(float x, float y, String text, float wrapWidth, Color color, boolean shadow) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        float y2 = y;
        for(String s : this.listFormattedStringToWidth(text, wrapWidth)) {
            this.renderStringAligned(x, y2, s, wrapWidth, color, shadow);
            y2 += this.height();
        }
    }

    /**
     * Render string either left or right aligned depending on bidiFlag
     */
    protected float renderStringAligned(float x, float y, String text, float width, Color color, boolean shadow) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        float actualX = x;
        if (this.getFont().getBidiFlag()) {
            float i = this.getStringWidth(this.bidiReorder(text));
            actualX = actualX + width - i;
        }
        return this.renderString(text, actualX, y, color, shadow);
    }

    protected void setColor(float r, float g, float b, float a) {
        GlStateManager.color(r, g, b, a);
    }

    protected void enableAlpha() {
        GlStateManager.enableAlpha();
    }

    /**
     * Returns the width of this string. Equivalent of FontMetrics.stringWidth(String s).
     */
    //FIXME for some reasons, FontRenderer#getStringWidth(String) seams to get stuck in an infinite loop when the Screen is too small, and eclipse's debugger cannot even step there ?
    @Override
    public float getStringWidth(String text) {
        return this.getFont().getStringWidth(text) * this.scale;
    }

    /**
     * Returns the width of this character as rendered.
     */
    @Override
    public float getCharWidth(char character) {
        return this.getFont().getCharWidth(character) * this.scale;
    }

    /**
     * Trims a string to fit a specified Width.
     */
    @Override
    public String trimStringToWidth(String text, float width) {
        return this.getFont().trimStringToWidth(text, (int)Math.floor(width / this.scale));
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
    @Override
    public String trimStringToWidth(String text, float width, boolean reverse) {
        return this.getFont().trimStringToWidth(text, (int)Math.ceil(width / this.scale), reverse);
    }

    /**
     * Returns the height (in pixels) of the given string if it is wordwrapped to the given max width.
     */
    @Override
    public int getWordWrappedHeight(String str, float width) {
        return this.getFont().getWordWrappedHeight(str, Math.round(width / this.scale));
    }

    @Override
    public boolean isUnicode() {
        return false;
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
    @Override
    public List<String> listFormattedStringToWidth(String str, float wrapWidth) {
        return this.getFont().listFormattedStringToWidth(str, Math.round(wrapWidth / this.scale));
    }

    private FontRenderer getFont() {
        return Minecraft.getMinecraft().fontRenderer;
    }

    /* Only delegated calls to FontRenderer and reflection stuff from this point */

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

    /* All there is from this point onwards is reflection stuff */
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

}
