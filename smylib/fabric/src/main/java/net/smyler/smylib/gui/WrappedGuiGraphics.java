package net.smyler.smylib.gui;

import net.minecraft.client.gui.GuiGraphics;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.sprites.Sprite;

public class WrappedGuiGraphics implements DrawContext {

    public final GuiGraphics vanillaGraphics;
    private final Scissor scissor = new WrappedGuiGraphicsScissor();
    private final GlState glState = new Lwjgl3GlState();

    public WrappedGuiGraphics(GuiGraphics vanillaGraphics) {
        this.vanillaGraphics = vanillaGraphics;
    }

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

    @Override
    public GlState glState() {
        return this.glState;
    }

    @Override
    public void drawGradientRectangle(double z, double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {

    }

    @Override
    public void drawPolygon(double z, Color color, double... points) {

    }

    @Override
    public void drawStrokeLine(double z, Color color, float size, double... points) {

    }

    @Override
    public void drawClosedStrokeLine(double z, Color color, float size, double... points) {

    }

    @Override
    public void drawSpriteCropped(double x, double y, double z, Sprite sprite, double leftCrop, double topCrop, double rightCrop, double bottomCrop) {

    }

    @Override
    public void drawTooltip(String text, double x, double y) {

    }

}
