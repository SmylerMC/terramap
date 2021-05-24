package fr.thesmyler.smylibgui.widgets.sliders;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.util.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

/**
 * AbstractSliderWidget
 * An abstract slider, providing the basis for rendering and positioning
 * Calculating the progress and the string to displayed is left to the implementing subclass
 * 
 * @author SmylerMC
 *
 */
public abstract class AbstractSliderWidget extends WidgetContainer {

    protected float x, y, width;

    protected String displayPrefix = "";
    protected String tooltip;
    protected boolean enabled = true;
    protected boolean visible = true;

    protected Color enabledTextColor = Color.LIGHT_GRAY;
    protected Color activeTextColor = Color.SELECTION;
    protected Color disabledTextColor = Color.MEDIUM_GRAY;

    public AbstractSliderWidget(float x, float y, int z, float width) {
        super(z);
        this.x = x;
        this.y = y;
        this.width = width;
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
        if(!this.isEnabled()) return false;
        float pos = Util.saturate((mouseX) / this.getWidth());
        this.setValueFromPos(pos);
        return false;
    }

    @Override
    public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        if(!this.isEnabled()) return false;
        return this.onClick(mouseX, mouseY, mouseButton, parent);
    }

    @Override
    public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, @Nullable WidgetContainer parent, long dt) {
        if(!this.isEnabled()) return;
        this.onClick(mouseX, mouseY, mouseButton, parent);
    }

    @Override
    public boolean onMouseWheeled(float mouseX, float mouseY, int amount, @Nullable WidgetContainer parent) {
        if(!this.isEnabled()) return false;
        if(amount > 0) this.goToNext();
        else this.goToPrevious();
        return false;
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode, @Nullable WidgetContainer parent) {
        if(!this.isEnabled()) return;
        switch(keyCode) {
            case Keyboard.KEY_DOWN:
            case Keyboard.KEY_LEFT:
                this.goToPrevious();
                break;
            case Keyboard.KEY_UP:
            case Keyboard.KEY_RIGHT:
                this.goToNext();
                break;
        }
    }


    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean hasFocus, WidgetContainer parent) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGui.BUTTON_TEXTURES);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        float leftWidth = this.width / 2;
        float rightWidth = leftWidth;
        leftWidth += this.width % 2;
        RenderUtil.drawTexturedModalRect(x, y, 0, 46, leftWidth, 20);
        RenderUtil.drawTexturedModalRect(x + leftWidth, y, 200 - rightWidth, 46, rightWidth, 20);

        float sliderPosition = this.getPosition();
        Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGui.BUTTON_TEXTURES);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        RenderUtil.drawTexturedModalRect(x + sliderPosition * (this.width - 8), y, 0, 66, 4, 20);
        RenderUtil.drawTexturedModalRect(x + sliderPosition * (this.width - 8) + 4, y, 196, 66, 4, 20);

        Color textColor = this.enabledTextColor;
        if (!this.isEnabled()) textColor = this.disabledTextColor;
        else if (hovered || hasFocus) textColor = this.activeTextColor;

        parent.getFont().drawCenteredString(x + this.width / 2, y + (20 - 8) / 2, this.getDisplayPrefix() + this.getDisplayString(), textColor, false);


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
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return 20;
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

}
