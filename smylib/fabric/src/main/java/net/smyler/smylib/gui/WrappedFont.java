package net.smyler.smylib.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.smyler.smylib.Color;
import org.jetbrains.annotations.NotNull;


public class WrappedFont extends BaseFont {

    private final net.minecraft.client.gui.Font vanillaFont;
    private GuiGraphics vanillaGraphics;

    public WrappedFont(float interlineFactor, net.minecraft.client.gui.Font vanillaFont) {
        super(interlineFactor);
        this.vanillaFont = vanillaFont;
    }

    @Override
    float getCharWidth(char character) {
        return this.vanillaFont.width(String.valueOf(character));
    }

    @Override
    public float scale() {
        return 1f;  //TODO Hard-coded scale
    }

    @Override
    public Font withScale(float scale) {
        return this;  //TODO font scaling
    }

    @Override
    public Font scaled(float scaleFactor) {
        return this;  //TODO font scaling
    }

    @Override
    public float height() {
        return this.vanillaFont.lineHeight;  //TODO font scaling
    }

    @Override
    public Font withInterlineRatio(float interline) {
        return this;  //TODO font scaling
    }

    @Override
    public float draw(float x, float y, @NotNull String text, @NotNull Color color, boolean shadow) {
        //TODO support color when drawing strings
        return this.vanillaGraphics.drawString(this.vanillaFont, text, (int) x, (int) y, 0);
    }

    @Override
    public boolean isUnicode() {
        return false; //TODO hard-coded unicode
    }

    public void setVanillaGraphics(GuiGraphics vanillaGraphics) {
        this.vanillaGraphics = vanillaGraphics;
    }

}
