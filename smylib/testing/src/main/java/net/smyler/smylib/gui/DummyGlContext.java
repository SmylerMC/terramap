package net.smyler.smylib.gui;

import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.gl.*;

import static net.smyler.smylib.Color.WHITE;

public class DummyGlContext implements GlContext {

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
    public void setTexture(Identifier texture) {

    }

    @Override
    public void enableColorLogic(ColorLogic colorLogic) {

    }

    @Override
    public void disableColorLogic() {

    }

    @Override
    public void enableSmoothShading() {

    }

    @Override
    public void enableFlatShading() {

    }

    @Override
    public void pushViewMatrix() {

    }

    @Override
    public void rotate(double angle) {

    }

    @Override
    public void translate(double x, double y) {

    }

    @Override
    public void scale(double x, double y) {

    }

    @Override
    public void popViewMatrix() {

    }

    @Override
    public void startDrawing(DrawMode mode, VertexFormat format) {

    }

    @Override
    public VertexBuilder vertex() {
        return new DummyVertexBuilder();
    }

    @Override
    public void draw() {

    }

    private static class DummyVertexBuilder implements VertexBuilder {

        @Override
        public VertexBuilder position(double x, double y, double z) {
            return this;
        }

        @Override
        public VertexBuilder color(float r, float g, float b, float a) {
            return this;
        }

        @Override
        public VertexBuilder color(Color color) {
            return this;
        }

        @Override
        public VertexBuilder texture(double u, double v) {
            return this;
        }

        @Override
        public void end() {

        }

    }

}
