package fr.thesmyler.terramap.gui.widgets;

import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.gl.GlContext;
import net.smyler.smylib.math.Vec2dMutable;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Animation;
import net.smyler.smylib.Animation.AnimationState;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.widgets.Widget;
import net.smyler.smylib.math.Mat2d;

import static net.smyler.smylib.Color.*;
import static net.smyler.smylib.gui.gl.DrawMode.*;
import static net.smyler.smylib.gui.gl.VertexFormat.*;

public class CircularCompassWidget implements Widget {

    private float azimuth = 0f;
    private float x, y, size;
    private final int z;
    private Color backgroundColor = Color.DARKER_OVERLAY;
    private Color northColor = RED;
    private Color northDarkColor = RED.withRed(0.4f);
    private Color southColor = WHITE;
    private Color southColorDark = new Color(0.4f, 0.4f, 0.4f);
    private Runnable onClick;
    private boolean visible = true;
    private boolean fadeAwayOnZero = false;
    private String tooltip = null;

    private final Animation fader = new Animation(1000);

    private double[][] vertices;
    private final Vec2dMutable vertexCalculationHelper = new Vec2dMutable();

    public CircularCompassWidget(float x, float y, int z, float size) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.setSize(size);
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {

        Color background = this.backgroundColor;
        Color north = this.northColor;
        Color northDark = this.northDarkColor;
        Color south = this.southColor;
        Color southColor = this.southColorDark;

        if(this.fadeAwayOnZero) {
            background = this.fader.fadeColor(background);
            north = this.fader.fadeColor(north);
            northDark = this.fader.fadeColor(northDark);
            south = this.fader.fadeColor(south);
            southColor = this.fader.fadeColor(southColor);
        }

        float radius = this.size / 2;

        GlContext gl = context.gl();
        gl.pushViewMatrix();
        gl.translate(x + radius, y + radius);

        // Background dark circle
        gl.startDrawing(TRIANGLE_FAN, POSITION);
        gl.setColor(background);
        gl.vertex().position(0d, 0d, 0d).end();
        for (double[] vertex : this.vertices) {
            gl.vertex().position(vertex[0], vertex[1], 0d).end();
        }
        gl.vertex().position(this.vertices[0][0], this.vertices[0][1], 0d).end();
        gl.draw();

        gl.rotate(this.azimuth);

        // North arrow
        gl.startDrawing(TRIANGLE_FAN, POSITION_COLOR);
        gl.setColor(WHITE);
        gl.vertex().position(0d, -radius, 0d).color(north.redf(), north.greenf(), north.bluef(), north.alphaf()).end();
        gl.vertex().position(-radius/3, radius * 0.1, 0d).color(north.redf(), north.greenf(), north.bluef(), north.alphaf()).end();
        gl.vertex().position(0d, 0d, 0d).color(north.redf(), north.greenf(), north.bluef(), north.alphaf()).end();
        gl.vertex().position(radius/3, radius * 0.1, 0d).color(northDark.redf(), northDark.greenf(), northDark.bluef(), northDark.alphaf()).end();
        gl.draw();

        // South arrow
        gl.startDrawing(TRIANGLE_FAN, POSITION_COLOR);
        gl.vertex().position(0, 0, 0).color(south.redf(), south.greenf(), south.bluef(), south.alphaf()).end();
        gl.vertex().position(-radius/3, radius * 0.1, 0).color(south.redf(), south.greenf(), south.bluef(), south.alphaf()).end();
        gl.vertex().position(0, radius, 0).color(south.redf(), south.greenf(), south.bluef(), south.alphaf()).end();
        gl.vertex().position(radius/3, radius * 0.1, 0).color(southColor.redf(), southColor.greenf(), southColor.bluef(), southColor.alphaf()).end();
        gl.draw();

        gl.popViewMatrix();
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        if(this.fadeAwayOnZero) {
            if(this.fader.getState() == AnimationState.STOPPED) {
                if(this.fader.getProgress() <= 0f && this.azimuth != 0f)
                    this.fader.start(AnimationState.ENTER);
                if(this.fader.getProgress() >= 1f && this.azimuth == 0f) 
                    this.fader.start(AnimationState.LEAVE);
            }
            this.fader.update();
        }
    }

    @Override
    public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        float dX = mouseX - this.size / 2;
        float dY = mouseY - this.size / 2;
        if(dX*dX + dY*dY < this.size*this.size / 4) {
            if(this.onClick != null) this.onClick.run();
            return false;
        }
        return true;
    }

    @Override
    public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        return this.onClick(mouseX, mouseY, mouseButton, parent);
    }

    public float getAzimuth() {
        return azimuth;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public float getWidth() {
        return this.size;
    }

    @Override
    public float getHeight() {
        return this.size;
    }

    public float getSize() {
        return size;
    }

    public void setSize(float size) {
        this.size = size;
        int vertexCount = (int) (2*Math.PI*this.size);
        float radius = this.size / 2;
        this.vertices = new double[vertexCount][2];
        this.vertexCalculationHelper.set(0, -radius);
        Mat2d rot = Mat2d.forRotation(-Math.PI*2 / vertexCount);
        for(int i = 0; i < vertexCount; i++) {
            this.vertices[i] = new double[] {
                    this.vertexCalculationHelper.x(),
                    this.vertexCalculationHelper.y()
            };
            this.vertexCalculationHelper.apply(rot);
        }
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public void setX(float x) {
        this.x = x;
    }

    public void setY(float y) {
        this.y = y;
    }

    public Runnable getOnClick() {
        return onClick;
    }

    public void setOnClick(Runnable onClick) {
        this.onClick = onClick;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        if(this.fadeAwayOnZero && this.fader.getProgress() <= 0f) return false;
        return this.visible;
    }

    public void setVisible(boolean yesNo) {
        this.visible = yesNo;
    }

    public Color getNorthColor() {
        return northColor;
    }

    public void setNorthColor(Color northColor) {
        this.northColor = northColor;
    }

    public Color getNorthDarkColor() {
        return northDarkColor;
    }

    public void setNorthDarkColor(Color northDarkColor) {
        this.northDarkColor = northDarkColor;
    }

    public Color getSouthColor() {
        return southColor;
    }

    public void setSouthColor(Color southColor) {
        this.southColor = southColor;
    }

    public Color getSouthColorDark() {
        return southColorDark;
    }

    public void setSouthColorDark(Color southColorDark) {
        this.southColorDark = southColorDark;
    }

    public boolean isFadeAwayOnZero() {
        return fadeAwayOnZero;
    }

    public void setFadeAwayOnZero(boolean fadeAwayOnZero) {
        this.fadeAwayOnZero = fadeAwayOnZero;
    }

    @Override
    public String getTooltipText() {
        return this.tooltip;
    }

    public void setTooltip(String tooltip) {
        this.tooltip = tooltip;
    }

}
