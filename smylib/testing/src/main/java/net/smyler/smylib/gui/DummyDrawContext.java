package net.smyler.smylib.gui;

import net.smyler.smylib.Color;

public class DummyDrawContext implements DrawContext {

    private final Scissor scissor = new DummyScissor();
    private final GlState state = new DummyGlState();

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

    @Override
    public GlState glState() {
        return this.state;
    }

    @Override
    public void drawGradientRectangle(double z, double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {

    }

}
