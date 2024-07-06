package net.smyler.smylib.gui;

import net.smyler.smylib.Color;
import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.gl.GlContext;
import net.smyler.smylib.gui.gl.Scissor;
import net.smyler.smylib.gui.sprites.Sprite;

import java.awt.image.BufferedImage;
import java.util.HashSet;
import java.util.Set;

public class DummyUiDrawContext implements UiDrawContext {

    private final Scissor scissor = new DummyScissor();
    private final GlContext state = new DummyGlContext();
    private int dynamicTextureIndex = 0;
    private final Set<Identifier> dynamicTextures = new HashSet<>();

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

    @Override
    public GlContext gl() {
        return this.state;
    }

    @Override
    public void drawGradientRectangle(double z, double xLeft, double yTop, double xRight, double yBottom, Color upperLeftColor, Color lowerLeftColor, Color lowerRightColor, Color upperRightColor) {

    }

    @Override
    public void drawPolygon(double z, Color color, double... points) {

    }

    @Override
    public void drawStrokeLine(double z, Color color, float size, double... points) {

    }

    @Override
    public void drawClosedStrokeLine(double z, Color color, float size, double... points) {

    }

    @Override
    public void drawSpriteCropped(double x, double y, double z, Sprite sprite, double leftCrop, double topCrop, double rightCrop, double bottomCrop) {

    }

    @Override
    public void drawTooltip(String text, double x, double y) {

    }

    @Override
    public void drawTexture(Identifier texture, double x, double y, double u, double v, double width, double height, double textureWidth, double textureHeight) {

    }

    @Override
    public Identifier loadDynamicTexture(BufferedImage image) {
        Identifier id = new Identifier(
                "smylib",
                "smylib_test_dynamic_" + this.dynamicTextureIndex++
        );
        this.dynamicTextures.add(id);
        return id;
    }

    @Override
    public void unloadDynamicTexture(Identifier texture) {
        if (!this.dynamicTextures.remove(texture)) {
            throw new IllegalStateException("Tried to unload a dynamic texture that is not loaded.");
        }
    }

}
