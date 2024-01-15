package fr.thesmyler.smylibgui.container;

/**
 * A widget container that can change shape and position.
 *
 * @author SmylerMC
 */
public class FlexibleWidgetContainer extends WidgetContainer {

    private float x, y, width, height;

    public FlexibleWidgetContainer(float x, float y, int z, float width, float height) {
        super(z);
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setSize(float width, float height) {
        this.width = width;
        this.height = height;
        this.init();
    }

    public void setWidth(float width) {
        this.width = width;
        this.init();
    }

    public void setHeight(float height) {
        this.height = height;
        this.init();
    }

    public void setPosition(float x, float y) {
        this.x = x;
        this.y = y;
    }

    @Override
    public float getX() {
        return this.x;
    }

    public void setX(float x) {
        this.x = x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    public void setY(float y) {
        this.y = y;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

}
