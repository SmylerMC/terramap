package net.smyler.smylib.gui.popups;

import net.smyler.smylib.Color;
import net.smyler.smylib.game.Key;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.gui.containers.WidgetContainer;
import org.jetbrains.annotations.Nullable;

import static net.smyler.smylib.SmyLib.getGameClient;


/**
 * A widget container that will be shown on top of the current screen.
 * 
 * @see net.smyler.smylib.game.GameClient#displayPopup(Popup) 
 *
 * @author Smyler
 */
public class Popup extends WidgetContainer {

    private float x, y;
    private float width, height;
    private boolean closeOnClickOutContent = true;
    private Color backgroundColor = Color.DARKER_OVERLAY;
    private Color shadingColor = Color.TRANSPARENT;  // The color that will shade the screen
    private Color contourColor = Color.DARK_GRAY;
    private float contourSize = 2f;

    public Popup(float width, float height) {
        super(0);
        this.width = width;
        this.height = height;
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        float right = x + this.getWidth();
        float bottom = y + this.getHeight();
        context.drawRectangle(x, y, right, bottom, this.backgroundColor);
        super.draw(context, x, y, mouseX, mouseY, hovered, focused, parent);
        context.drawClosedStrokeLine(this.contourColor, this.contourSize,
                x, y,
                x, bottom,
                right, bottom,
                right, y);
    }

    @Override
    public final float getX() {
        return this.x;
    }

    @Override
    public final float getY() {
        return this.y;
    }

    void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public void resize(float width, float height) {
        this.width = width;
        this.height = height;
    }

    @Override
    public final float getWidth() {
        return this.width;
    }

    @Override
    public final float getHeight() {
        return this.height;
    }

    @Override
    public boolean onParentClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        if (this.closeOnClickOutContent) {
            this.close();
            return false;
        }
        return super.onParentClick(mouseX, mouseY, mouseButton, parent);
    }

    @Override
    public boolean onParentDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        if (this.closeOnClickOutContent) return this.onParentClick(mouseX, mouseY, mouseButton, parent);
        return super.onParentDoubleClick(mouseX, mouseY, mouseButton, parent);
    }

    @Override
    public void onKeyTyped(char typedChar, @Nullable Key key, WidgetContainer parent) {
        if (key == Key.KEY_ESCAPE) {
            this.close();
        } else {
            super.onKeyTyped(typedChar, key, parent);
        }
    }

    public void close() {
        if (getGameClient().getTopPopup() != this) {
            return;
        }
        getGameClient().closeTopPopup();
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getShadingColor() {
        return this.shadingColor;
    }

    public void setShadingColor(Color shadingColor) {
        this.shadingColor = shadingColor;
    }

    public Color getContourColor() {
        return this.contourColor;
    }

    public void setContourColor(Color contourColor) {
        this.contourColor = contourColor;
    }

    public float getContourSize() {
        return this.contourSize;
    }

    public void setContourSize(float contourSize) {
        this.contourSize = contourSize;
    }

    public boolean isCloseOnClickOutContent() {
        return this.closeOnClickOutContent;
    }

    public void setCloseOnClickOutContent(boolean closeOnClickOutContent) {
        this.closeOnClickOutContent = closeOnClickOutContent;
    }

    @Deprecated
    public WidgetContainer getContent() {
        return this;
    }

}
