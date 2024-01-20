package net.smyler.smylib.gui;

import net.smyler.smylib.Identifier;

public class Sprite {

    public final Identifier texture;
    public final double textureWidth, textureHeight;
    public final double xLeft, yTop, xRight, yBottom;

    public Sprite(Identifier texture, double textureWidth, double textureHeight, double xLeft, double yTop, double xRight, double yBottom) {
        this.texture = texture;
        this.textureWidth = textureWidth;
        this.textureHeight = textureHeight;
        this.xLeft = xLeft;
        this.yTop = yTop;
        this.xRight = xRight;
        this.yBottom = yBottom;
    }

    public double width() {
        return this.xRight - this.xLeft;
    }

    public double height() {
        return this.yBottom - this.yTop;
    }

}
