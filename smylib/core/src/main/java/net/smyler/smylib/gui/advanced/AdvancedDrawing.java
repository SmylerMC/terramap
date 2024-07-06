package net.smyler.smylib.gui.advanced;

import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;

public interface AdvancedDrawing {

    void begin(DrawMode mode, VertexFormat components);
    void texture(Identifier identifier);
    void color(Color color);
    VertexBuilder vertex();
    void draw();

}
