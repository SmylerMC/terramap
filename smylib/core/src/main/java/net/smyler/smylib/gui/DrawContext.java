package net.smyler.smylib.gui;

import net.smyler.smylib.Color;

public interface DrawContext {

    Scissor scissor();

    GlState glState();

    default void drawRectangle(double z, double xLeft, double yTop, double xRight, double yBottom, Color color) {
        this.drawGradientRectangle(z, xLeft, yTop, xRight, yBottom, color, color, color, color);
    }

    default void drawRectangle(double xLeft, double yTop, double xRight, double yBottom, Color color) {
        this.drawGradientRectangle(0d, xLeft, yTop, xRight, yBottom, color, color, color, color);
    }

    void drawGradientRectangle(double z, double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor);

    default void drawGradientRectangle(double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {
        this.drawGradientRectangle(0, xLeft, yTop, xRight, yBottom, upperLeftColor, lowerLeftColor, lowerRightColor, upperRightColor);
    }

}
