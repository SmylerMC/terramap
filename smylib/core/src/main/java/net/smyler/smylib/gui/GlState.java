package net.smyler.smylib.gui;

import net.smyler.smylib.Color;

public interface GlState {

    void enableAlpha();

    void disableAlpha();

    void setColor(Color color);

    Color getColor();

    void enableColorLogic(ColorLogic colorLogic);

    void disableColorLogic();

}
