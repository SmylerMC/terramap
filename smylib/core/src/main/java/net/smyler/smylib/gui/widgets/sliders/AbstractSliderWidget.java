package net.smyler.smylib.gui.widgets.sliders;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.gui.Sprite;
import org.jetbrains.annotations.Nullable;

import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.game.Key;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.widgets.Widget;
import net.smyler.smylib.gui.Font;

import static java.lang.Math.min;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.math.Math.saturate;

/**
 * AbstractSliderWidget
 * An abstract slider, providing the basis for rendering and positioning
 * Calculating the progress and the string to displayed is left to the implementing subclass
 * 
 * @author Smyler
 *
 */
public abstract class AbstractSliderWidget implements Widget {

    private static final Identifier TEXTURE = new Identifier("minecraft", "textures/gui/widgets.png");
    private static final Sprite BACKGROUND = new Sprite(TEXTURE, 256d, 256d, 0d, 46d, 200d, 66d);
    private static final Sprite SLIDER = new Sprite(TEXTURE, 256d, 256d, 0d, 66d, 200d, 86d);

    protected float x, y, width, height;
    private final int z;

    protected String displayPrefix = "";
    protected String tooltip;
    protected boolean enabled = true;
    protected boolean visible = true;

    protected Color enabledTextColor = Color.LIGHT_GRAY;
    protected Color activeTextColor = Color.SELECTION;
    protected Color disabledTextColor = Color.MEDIUM_GRAY;

    public AbstractSliderWidget(float x, float y, int z, float width, float height) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.width = width;
        this.height = height;
    }
    
    public AbstractSliderWidget(float x, float y, int z, float width) {
        this(x, y, z, width, 20);
    }

    /**
     * Sets the value from the slider's position
     * 
     * @param sliderPosition a float between 0 (left) and 1 (right)
     */
    protected abstract void setValueFromPos(float sliderPosition);

    /**
     * 
     * @return the position at which to draw the slider, between 0 and 1
     */
    protected abstract float getPosition();

    /**
     * 
     * @return The string to display on top of the slider
     */
    protected abstract String getDisplayString();

    @Override
    public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        float pos = saturate((mouseX) / this.getWidth());
        this.setValueFromPos(pos);
        return false;
    }

    @Override
    public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        return this.onClick(mouseX, mouseY, mouseButton, parent);
    }

    @Override
    public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, @Nullable WidgetContainer parent, long dt) {
        this.onClick(mouseX, mouseY, mouseButton, parent);
    }

    @Override
    public boolean onMouseWheeled(float mouseX, float mouseY, int amount, @Nullable WidgetContainer parent) {
        if(amount > 0) this.goToNext();
        else this.goToPrevious();
        return false;
    }

    @Override
    public void onKeyTyped(char typedChar, @Nullable Key key, @Nullable WidgetContainer parent) {
        if (key == null) {
            return;
        }
        switch(key) {
            case KEY_DOWN:
            case KEY_LEFT:
                this.goToPrevious();
                break;
            case KEY_UP:
            case KEY_RIGHT:
                this.goToNext();
                break;
        }
    }


    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean hasFocus, WidgetContainer parent) {

        double leftWidth = this.width / 2;
        double splitHeight = min(10, this.height / 2);
        double xCrop = BACKGROUND.width() - this.width / 2d;
        double yCrop = BACKGROUND.height() - min(10d, this.height / 2);
        context.drawSpriteCropped(x, y, BACKGROUND, 0d, 0d, xCrop, yCrop);
        context.drawSpriteCropped(x + leftWidth, y, BACKGROUND, xCrop, 0, 0, yCrop);
        for(int i=0; i*18 < this.height - 20; i++) {
            context.drawSpriteCropped(x, y + splitHeight + 16d * i, BACKGROUND, 0d, 2d, xCrop, 2d);
            context.drawSpriteCropped(x + leftWidth, y + splitHeight + 16d * i, BACKGROUND, xCrop, 2d, 0d, 2d);
        }
        context.drawSpriteCropped(x, y + this.height - splitHeight, BACKGROUND, 0, yCrop, xCrop, 0);
        context.drawSpriteCropped(x + leftWidth, y + this.height - splitHeight, BACKGROUND, xCrop, yCrop, 0, 0);

        float sliderPosition = this.getPosition();

        float sliderX = x + sliderPosition * (this.width - 8);
        context.drawSpriteCropped(sliderX, y, SLIDER, 0, 0, SLIDER.width() - 4d, SLIDER.height() - splitHeight);
        context.drawSpriteCropped(sliderX + 4, y, SLIDER, SLIDER.width() - 4d, 0d, 0d, SLIDER.height() - splitHeight);
        for(int i=0; i*18 < this.height - 20; i++) {
            context.drawSpriteCropped(sliderX, y + splitHeight + 16*i, SLIDER, 0d, 2d, SLIDER.width() - 4d, 2d);
            context.drawSpriteCropped(sliderX + 4d, y + splitHeight + 16*i, SLIDER, SLIDER.width() - 4d, 2d, 0d, 2d);
        }
        context.drawSpriteCropped(sliderX, y + this.height - splitHeight, SLIDER, 0d, SLIDER.height() - splitHeight, SLIDER.width() - 4d, 0d);
        context.drawSpriteCropped(sliderX + 4d, y + this.height - splitHeight, SLIDER, SLIDER.width() - 4d, SLIDER.height() - splitHeight, 0d, 0d);

        Color textColor = this.enabledTextColor;
        if (!this.isEnabled()) {
            textColor = this.disabledTextColor;
        } else if (hovered || hasFocus) {
            textColor = this.activeTextColor;
        }

        GameClient game = getGameClient();
        float fontSize = game.defaultFont().height();
        double gameScale = game.scaleFactor();
        float fontScale = 1f;
        while(fontSize / fontScale > this.height - 1 && fontScale < gameScale) {
            fontScale++;
        }
        Font font = game.defaultFont().withScale(1 / fontScale + 0.0001f);
        font.drawCentered(x + this.width / 2, y + (this.height - font.height() + 1) / 2, this.getDisplayPrefix() + this.getDisplayString(), textColor, false);

    }

    /**
     * Move the slider right
     * Triggered when the mouse is wheeled
     */
    public abstract void goToNext();

    /**
     * Move the slider left
     * Triggered when the mouse is wheeled
     */
    public abstract void goToPrevious();
    
    public AbstractSliderWidget setX(float x) {
        this.x = x;
        return this;
    }

    public AbstractSliderWidget setY(float y) {
        this.y = y;
        return this;
    }

    public AbstractSliderWidget setWidth(float width) {
        this.width = width;
        return this;
    }

    public AbstractSliderWidget setHeight(float height) {
        this.height = height;
        return this;
    }

    public AbstractSliderWidget setDisplayPrefix(String prefix) {
        this.displayPrefix = prefix;
        return this;
    }

    public String getDisplayPrefix() {
        return this.displayPrefix;
    }

    public boolean isEnabled() {
        return this.enabled;
    }

    public AbstractSliderWidget setEnabled(boolean yesNo) {
        this.enabled = yesNo;
        return this;
    }

    public Color getEnabledTextColor() {
        return enabledTextColor;
    }

    public void setEnabledTextColor(Color enabledTextColor) {
        this.enabledTextColor = enabledTextColor;
    }

    public Color getActiveTextColor() {
        return activeTextColor;
    }

    public void setActiveTextColor(Color activeTextColor) {
        this.activeTextColor = activeTextColor;
    }

    public Color getDisabledTextColor() {
        return disabledTextColor;
    }

    public void setDisabledTextColor(Color disabledTextColor) {
        this.disabledTextColor = disabledTextColor;
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

    public AbstractSliderWidget setVisibility(boolean yesNo) {
        this.visible = yesNo;
        return this;
    }

    public AbstractSliderWidget show() {
        return this.setVisibility(true);
    }

    public AbstractSliderWidget hide() {
        return this.setVisibility(false);
    }


    public AbstractSliderWidget setTooltip(String tooltip) {
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
