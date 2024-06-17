package net.smyler.smylib.gui;

import net.smyler.smylib.Color;

public interface GlState {

    void enableAlpha();

    void disableAlpha();

    void setColor(Color color);

    Color getColor();

    void enableColorLogic(ColorLogic colorLogic);

    void disableColorLogic();

    void pushViewMatrix();

    void rotate(double angle);

    void translate(double x, double y);

    void scale(double x, double y);

    void popViewMatrix();

}
