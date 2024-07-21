package net.smyler.smylib.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.gl.GlContext;
import org.jetbrains.annotations.NotNull;

import static net.smyler.smylib.SmyLib.getGameClient;


public class WrappedFont extends BaseFont {

    private final net.minecraft.client.gui.Font vanillaFont;
    private GuiGraphics vanillaGraphics;
    private final float scale;

    public WrappedFont(float interlineFactor, float scale, net.minecraft.client.gui.Font vanillaFont, GuiGraphics vanillaGraphics) {
        super(interlineFactor);
        this.vanillaFont = vanillaFont;
        this.scale = scale;
        this.vanillaGraphics = vanillaGraphics;
    }

    public WrappedFont(float interlineFactor, float scale, net.minecraft.client.gui.Font vanillaFont) {
        super(interlineFactor);
        this.vanillaFont = vanillaFont;
        this.scale = scale;
    }

    @Override
    float getCharWidth(char character) {
        return this.vanillaFont.width(String.valueOf(character)) * this.scale;
    }

    @Override
    public float scale() {
        return this.scale;
    }

    @Override
    public Font withScale(float scale) {
        return new WrappedFont(this.interlineFactor, scale, this.vanillaFont, this.vanillaGraphics);
    }

    @Override
    public Font scaled(float scaleFactor) {
        return new WrappedFont(this.interlineFactor, this.scale * scaleFactor, this.vanillaFont, this.vanillaGraphics);
    }

    @Override
    public float height() {
        return this.vanillaFont.lineHeight * this.scale;
    }

    @Override
    public Font withInterlineRatio(float interline) {
        return this;  //TODO font scaling
    }

    @Override
    public float draw(float x, float y, @NotNull String text, @NotNull Color color, boolean shadow) {
        GlContext gl = getGameClient().guiDrawContext().gl();
        this.vanillaGraphics.setColor(color.redf(), color.greenf(), color.bluef(), color.alphaf());
        float invScale = 1.0f / this.scale;
        gl.scale(this.scale, this.scale);
        x *= invScale;
        y *= invScale;

        // Vanilla does a z-translation call to draw the shadow,
        // We need to work around that (it is useless and messes z coordinates).
        PatchedFont patchedFont = (PatchedFont) this.vanillaFont;
        patchedFont.smylib$setCancelShadowOffset(true);
        float result = this.vanillaGraphics.drawString(this.vanillaFont, text, (int) x, (int) y, color.asInt());
        patchedFont.smylib$setCancelShadowOffset(false);
        gl.scale(invScale, invScale);
        return result;
    }

    @Override
    public boolean isUnicode() {
        return false; //TODO hard-coded unicode
    }

    public void setVanillaGraphics(GuiGraphics vanillaGraphics) {
        this.vanillaGraphics = vanillaGraphics;
    }

}
