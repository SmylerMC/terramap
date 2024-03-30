package net.smyler.smylib.gui;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.smyler.smylib.Color;
import org.jetbrains.annotations.NotNull;


public class ReflectedFontRenderer extends BaseFont {

    protected final float scale;

    public ReflectedFontRenderer(float size, float interlineFactor) {
        super(interlineFactor);
        this.scale = size;
    }

    @Override
    public float scale() {
        return this.scale;
    }

    @Override
    public Font withScale(float scale) {
        return new ReflectedFontRenderer(scale, this.interlineFactor);
    }

    @Override
    public Font scaled(float scaleFactor) {
        return new ReflectedFontRenderer(this.scale * scaleFactor, this.interlineFactor);
    }

    @Override
    public float height() {
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT * this.scale;
    }

    @Override
    public Font withInterlineRatio(float interline) {
        return new ReflectedFontRenderer(this.scale, interline);
    }

    @Override
    public float draw(float x, float y, @NotNull String text, @NotNull Color color, boolean shadow) {
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

    protected void setColor(float r, float g, float b, float a) {
        GlStateManager.color(r, g, b, a);
    }

    protected void enableAlpha() {
        GlStateManager.enableAlpha();
    }

    /**
     * Returns the width of this character as rendered.
     */
    @Override
    public float getCharWidth(char character) {
        return this.getFont().getCharWidth(character) * this.scale;
    }

    @Override
    public boolean isUnicode() {
        return false;
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

    protected void setRed(float value) throws IllegalArgumentException, IllegalAccessException {
        if(red == null) red = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_red);
        red.setFloat(this.getFont(), value);
    }

    protected void setGreen(float value) throws IllegalArgumentException, IllegalAccessException {
        if(green == null) green = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_green);
        green.setFloat(this.getFont(), value);
    }

    protected void setBlue(float value) throws IllegalArgumentException, IllegalAccessException {
        if(blue == null) blue = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_blue);
        blue.setFloat(this.getFont(), value);
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

    protected void setPosY(float value) throws IllegalArgumentException, IllegalAccessException {
        if(posY == null) posY = ObfuscationReflectionHelper.findField(FontRenderer.class, SRG_posY);
        posY.set(this.getFont(), value);
    }

    /* All there is from this point onwards is reflection stuff */
    private static final String SRG_renderStringAtPos = "func_78255_a";
    private static Method renderStringAtPos;
    private static final String SRG_bidiReorder = "func_147647_b";
    private static Method bidiReorder;
    private static final String SRG_resetStyles = "func_78265_b";
    private static Method resetStyles;
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

}
