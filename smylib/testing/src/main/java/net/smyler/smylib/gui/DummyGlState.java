package net.smyler.smylib.gui;

import net.smyler.smylib.Color;

import static net.smyler.smylib.Color.WHITE;

public class DummyGlState implements GlState {

    @Override
    public void enableAlpha() {

    }

    @Override
    public void disableAlpha() {

    }

    @Override
    public void setColor(Color color) {

    }

    @Override
    public Color getColor() {
        return WHITE;
    }

    @Override
    public void enableColorLogic(ColorLogic colorLogic) {

    }

    @Override
    public void disableColorLogic() {

    }

}
