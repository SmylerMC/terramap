package net.smyler.smylib.gui.containers;

import net.smyler.smylib.Animation;
import net.smyler.smylib.Animation.AnimationState;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.UiDrawContext;
import org.jetbrains.annotations.Nullable;

public class SlidingPanelWidget extends FlexibleWidgetContainer {

    protected float showX, hiddenX, showY, hiddenY;
    protected Color backgroundColor = Color.DARKER_OVERLAY;
    protected Color contourColor = Color.DARK_GRAY;
    protected float contourSize = 2f;
    protected final Animation mainAnimation;
    protected boolean closeOnClickOther = false;
    protected boolean visible = true;

    public SlidingPanelWidget(float showX, float hiddenX, float showY, float hiddenY, int z, float width, float height, long delay) {
        super(hiddenX, hiddenY, z, width, height);
        this.showX = showX;
        this.showY = showY;
        this.hiddenX = hiddenX;
        this.hiddenY = hiddenY;
        this.mainAnimation = new Animation(delay);
    }

    public SlidingPanelWidget(int z, long delay) {
        this(0, 0, 0, 0, z, 50, 50, delay);
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, @Nullable WidgetContainer parent){
        context.drawRectangleWithContours(x, y, x + this.getWidth(), y + this.getHeight(), this.backgroundColor, this.contourSize, this.contourColor);
        super.draw(context, x, y, mouseX, mouseY, hovered, focused, parent);
        this.mainAnimation.update();
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        this.mainAnimation.update();
        super.onUpdate(mouseX, mouseY, parent);
    }

    public PanelTarget getTarget() {
        switch(this.mainAnimation.getState()) {
            case LEAVE:
                return PanelTarget.CLOSED;
            case ENTER:
                return PanelTarget.OPENED;
            default:
                return this.mainAnimation.getProgress() < 0.5 ? PanelTarget.CLOSED: PanelTarget.OPENED;
        }
    }

    @Override
    public boolean onParentClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        if(this.closeOnClickOther && !this.getTarget().equals(PanelTarget.CLOSED)) {
            this.close();
            return false;
        }
        return true;
    }

    public void open() {
        this.mainAnimation.start(AnimationState.ENTER);
    }

    public void close() {
        this.mainAnimation.start(AnimationState.LEAVE);
    }

    public SlidingPanelWidget setStateNoAnimation(boolean opened) {
        this.mainAnimation.start(opened? AnimationState.LEAVE: AnimationState.ENTER);
        this.mainAnimation.stop();
        return this;
    }

    public float getOpenX() {
        return this.showX;
    }

    public SlidingPanelWidget setOpenX(float x) {
        this.showX = x;
        return this;
    }

    public float getClosedX() {
        return this.hiddenX;
    }

    public SlidingPanelWidget setClosedX(float x) {
        this.hiddenX = x;
        return this;
    }

    public float getOpenY() {
        return this.showY;
    }

    public SlidingPanelWidget setOpenY(float y) {
        this.showY = y;
        return this;
    }

    public float getClosedY() {
        return this.hiddenY;
    }

    public SlidingPanelWidget setClosedY(float y) {
        this.hiddenY = y;
        return this;
    }

    public boolean closesOnClickOther() {
        return this.closeOnClickOther;
    }

    public SlidingPanelWidget setCloseOnClickOther(boolean yesNo) {
        this.closeOnClickOther = yesNo;
        return this;
    }

    @Override
    public float getX() {
        return this.mainAnimation.blend(this.showX, this.hiddenX);
    }

    @Override
    public float getY() {
        return this.mainAnimation.blend(this.showY, this.hiddenY);
    }

    public Color getBackroundColor() {
        return this.backgroundColor;
    }

    public SlidingPanelWidget setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }
    
    public Color getContourColor() {
        return contourColor;
    }

    public void setContourColor(Color contourColor) {
        this.contourColor = contourColor;
    }

    public float getContourSize() {
        return contourSize;
    }

    public void setContourSize(float contourSize) {
        this.contourSize = contourSize;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visible;
    }

    public SlidingPanelWidget setVisibility(boolean yesNo) {
        this.visible = yesNo;
        return this;
    }

    public SlidingPanelWidget show() {
        return this.setVisibility(true);
    }

    public SlidingPanelWidget hide() {
        return this.setVisibility(false);
    }

    public enum PanelTarget {
        OPENED, CLOSED
    }

}
