package net.smyler.terramap.gui.widgets;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.gl.GlContext;
import net.smyler.terramap.Terramap;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.gui.widgets.Widget;

import static net.smyler.smylib.Color.WHITE;
import static net.smyler.smylib.gui.gl.DrawMode.QUADS;
import static net.smyler.smylib.gui.gl.VertexFormat.POSITION_TEXTURE;
import static net.smyler.smylib.gui.gl.VertexFormat.POSITION_TEXTURE_COLOR;

public class RibbonCompassWidget implements Widget {

    private static final Identifier COMPASS_BACKGROUND_TEXTURE = new Identifier(Terramap.MOD_ID, "textures/gui/compass_ribbon_background.png");
    private static final Identifier COMPASS_INDICATOR_TEXTURE = new Identifier(Terramap.MOD_ID, "textures/gui/compass_ribbon_indicator.png");

    private float x, y;
    private final int z;
    private float width;
    private final float height;
    private final float textureWidth;
    private final float indicatorWidth;
    private final float indicatorHeight;
    private float azimuth = 0;
    private boolean visibility = true;

    public RibbonCompassWidget(float x, float y, int z, float width) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = 16f;
        this.textureWidth = 360f;
        this.indicatorHeight = 16f;
        this.indicatorWidth = 1f;
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        double blendBorder = 10d; // How many pixels to fade to alpha=0 on the sides
        double leftU = (double)(this.azimuth - 180) / 360 + (double)(this.textureWidth - this.width) / this.textureWidth / 2;
        double leftCU = leftU + blendBorder/this.textureWidth;
        double rightU = leftU + (double) this.width / this.textureWidth;
        double rightCU = rightU - blendBorder/this.textureWidth;

        GlContext gl = context.gl();

        gl.enableAlpha();
        gl.enableSmoothShading();

        gl.startDrawing(QUADS, POSITION_TEXTURE_COLOR);
        gl.setTexture(COMPASS_BACKGROUND_TEXTURE);

        gl.vertex().position(x, y, 0d).texture(leftU, 0d).color(1f, 1f, 1f, 0f).end();
        gl.vertex().position(x, y + this.height, 0d).texture(leftU, 1d).color(1f, 1f, 1f, 0f).end();
        gl.vertex().position(x + blendBorder, y + this.height, 0d).texture(leftCU, 1f).color(1f, 1f, 1f, 1f).end();
        gl.vertex().position(x + blendBorder, y, 0d).texture(leftCU, 0f).color(1f, 1f, 1f, 1f).end();

        gl.vertex().position(x + blendBorder, y, 0d).texture(leftCU, 0d).color(1f, 1f, 1f, 1f).end();
        gl.vertex().position(x + blendBorder, y + this.height, 0d).texture(leftCU, 1d).color(1f, 1f, 1f, 1f).end();
        gl.vertex().position(x + this.width - blendBorder, y + this.height, 0d).texture(rightCU, 1d).color(1f, 1f, 1f, 1f).end();
        gl.vertex().position(x + this.width - blendBorder, y, 0d).texture(rightCU, 0d).color(1f, 1f, 1f, 1f).end();

        gl.vertex().position(x + this.width - blendBorder, y, 0d).texture(rightCU, 0d).color(1f, 1f, 1f, 1f).end();
        gl.vertex().position(x + this.width - blendBorder, y + this.height, 0d).texture(rightCU, 1d).color(1f, 1f, 1f, 1f).end();
        gl.vertex().position(x + this.width, y + this.height, 0d).texture(rightU, 1d).color(1f, 1f, 1f, 0f).end();
        gl.vertex().position(x + this.width, y, 0d).texture(rightU, 0d).color(1f, 1f, 1f, 0f).end();

        gl.draw();

        gl.setColor(WHITE);

        double indX = x + (double)(this.width - this.indicatorWidth) / 2;
        double indY = y + (double)(this.height - this.indicatorHeight) / 2;

        gl.startDrawing(QUADS, POSITION_TEXTURE);
        gl.setTexture(COMPASS_INDICATOR_TEXTURE);
        gl.vertex().position(indX, indY, 0d).texture(0d, 0d).end();
        gl.vertex().position(indX, indY + this.indicatorHeight, 0d).texture(0d, 1d).end();
        gl.vertex().position(indX + this.indicatorWidth, indY + this.indicatorHeight, 0d).texture(1d, 1d).end();
        gl.vertex().position(indX + this.indicatorWidth, indY, 0d).texture(1d, 0d).end();
        gl.draw();

    }

    @Override
    public float getX() {
        return x;
    }

    public RibbonCompassWidget setX(float x) {
        this.x = x;
        return this;
    }

    @Override
    public float getY() {
        return y;
    }

    public RibbonCompassWidget setY(float y) {
        this.y = y;
        return this;
    }

    @Override
    public float getWidth() {
        return width;
    }

    public RibbonCompassWidget setWidth(float width) {
        this.width = width;
        return this;
    }

    @Override
    public float getHeight() {
        return height;
    }

    @Override
    public int getZ() {
        return z;
    }

    public float getAzimuth() {
        return this.azimuth;
    }

    public RibbonCompassWidget setAzimuth(float azimuth) {
        this.azimuth = azimuth;
        return this;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visibility;
    }


    public RibbonCompassWidget setVisibility(boolean yesNo) {
        this.visibility = yesNo;
        return this;
    }

}
