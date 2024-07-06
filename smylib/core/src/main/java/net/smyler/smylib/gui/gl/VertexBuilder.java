package net.smyler.smylib.gui.gl;

import net.smyler.smylib.Color;

public interface VertexBuilder {

    VertexBuilder position(double x, double y, double z);

    VertexBuilder color(float r, float g, float b, float a);
    VertexBuilder color(Color color);

    VertexBuilder texture(double u, double v);

    void end();

}
