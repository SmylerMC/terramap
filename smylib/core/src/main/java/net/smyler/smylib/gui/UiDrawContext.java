package net.smyler.smylib.gui;

import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.gl.GlContext;
import net.smyler.smylib.gui.gl.Scissor;
import net.smyler.smylib.gui.sprites.Sprite;

import java.awt.image.BufferedImage;

public interface UiDrawContext {

    Scissor scissor();

    GlContext gl();

    default void drawRectangle(double z, double xLeft, double yTop, double xRight, double yBottom, Color color) {
        this.drawGradientRectangle(z, xLeft, yTop, xRight, yBottom, color, color, color, color);
    }

    default void drawRectangle(double xLeft, double yTop, double xRight, double yBottom, Color color) {
        this.drawGradientRectangle(0d, xLeft, yTop, xRight, yBottom, color, color, color, color);
    }

    default void drawRectangleWithContours(double z, double xLeft, double yTop, double xRight, double yBottom, Color color, float contoursSize, Color contoursColor) {
        this.drawRectangle(z, xLeft, yTop, xRight, yBottom, color);
        this.drawClosedStrokeLine(z, contoursColor, contoursSize,
                xLeft, yTop,
                xLeft, yBottom,
                xRight, yBottom,
                xRight, yTop
        );
    }

    default void drawRectangleWithContours(double xLeft, double yTop, double xRight, double yBottom, Color color, float contoursSize, Color contoursColor) {
        this.drawRectangleWithContours(0, xLeft, yTop, xRight, yBottom, color, contoursSize, contoursColor);
    }

    void drawGradientRectangle(double z, double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor);

    default void drawGradientRectangle(double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {
        this.drawGradientRectangle(0d, xLeft, yTop, xRight, yBottom, upperLeftColor, lowerLeftColor, lowerRightColor, upperRightColor);
    }

    void drawStrokeLine(double z, Color color, float size, double... points);

    default void drawStrokeLine(Color color, float size, double... points) {
        this.drawStrokeLine(0d, color, size, points);
    }

    void drawClosedStrokeLine(double z, Color color, float size, double... points);

    default void drawClosedStrokeLine(Color color, float size, double... points) {
        this.drawClosedStrokeLine(0d, color, size, points);
    }

    default void drawSprite(double x, double y, double z, Sprite sprite) {
        this.drawSpriteCropped(x, y, z, sprite, 0d, 0d, 0d, 0d);
    }

    default void drawSprite(double x, double y, Sprite sprite) {
        this.drawSpriteCropped(x, y, 0d, sprite, 0d, 0d, 0d, 0d);
    }

    void drawSpriteCropped(double x, double y, double z, Sprite sprite, double leftCrop, double topCrop, double rightCrop, double bottomCrop);

    default void drawSpriteCropped(double x, double y, Sprite sprite, double leftCrop, double topCrop, double rightCrop, double bottomCrop) {
        this.drawSpriteCropped(x, y, 0d, sprite, leftCrop, topCrop, rightCrop, bottomCrop);
    }

    //TODO use Text in drawTooltip
    void drawTooltip(String text, double x, double y);

    Identifier loadDynamicTexture(BufferedImage image);

    void unloadDynamicTexture(Identifier texture);

}
