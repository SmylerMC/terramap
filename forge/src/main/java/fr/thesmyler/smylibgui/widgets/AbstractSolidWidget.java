package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.container.WidgetContainer;

import javax.annotation.Nullable;

public abstract class AbstractSolidWidget implements IWidget {

    protected boolean visible = true;
    protected boolean enabled= true;
    protected final int z;
    protected float x, y, width, height;
    protected String tooltip = null;

    public AbstractSolidWidget(float x, float y, int z, float width, float height) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
    }

    @Override
    public float getX() {
        return x;
    }

    @Override
    public float getY() {
        return y;
    }

    @Override
    public int getZ() {
        return z;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visible;
    }

    public AbstractSolidWidget setVisibility(boolean yesNo) {
        this.visible = yesNo;
        return this;
    }

    public AbstractSolidWidget show() {
        return this.setVisibility(true);
    }

    public AbstractSolidWidget hide() {
        return this.setVisibility(false);
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public AbstractSolidWidget setEnabled(boolean yesNo) {
        this.enabled = yesNo;
        return this;
    }

    public AbstractSolidWidget enable() {
        this.setEnabled(true);
        return this;
    }

    public AbstractSolidWidget disable() {
        this.setEnabled(false);
        return this;
    }

    public AbstractSolidWidget setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public String getTooltipText() {
        return this.tooltip;
    }

    @Override
    public boolean takesInputs() {
        return this.enabled;
    }

    @Override
    public boolean onInteractWhenNotTakingInputs(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        return false;
    }

}
