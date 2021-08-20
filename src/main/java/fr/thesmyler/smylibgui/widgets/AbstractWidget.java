package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.container.WidgetContainer;

public abstract class AbstractWidget implements IWidget {

    protected boolean visible = true;
    protected final int z;
    protected float x, y, width, height;
    protected String tooltip = null;

    public AbstractWidget(float x, float y, int z, float width, float height) {
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

    public AbstractWidget setVisibility(boolean yesNo) {
        this.visible = yesNo;
        return this;
    }

    public AbstractWidget show() {
        return this.setVisibility(true);
    }

    public AbstractWidget hide() {
        return this.setVisibility(false);
    }


    public AbstractWidget setTooltip(String tooltip) {
        this.tooltip = tooltip;
        return this;
    }

    @Override
    public String getTooltipText() {
        return this.tooltip;
    }

}
