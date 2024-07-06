package net.smyler.smylib.gui.gl;

import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;

public interface GlContext {

    void enableAlpha();

    void disableAlpha();

    void setColor(Color color);

    Color getColor();

    void setTexture(Identifier texture);

    void enableColorLogic(ColorLogic colorLogic);

    void disableColorLogic();

    void pushViewMatrix();

    void rotate(double angle);

    void translate(double x, double y);

    void scale(double x, double y);

    void popViewMatrix();

    void startDrawing(DrawMode mode, VertexFormat format);

    VertexBuilder vertex();

    void draw();
}
