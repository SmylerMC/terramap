package fr.thesmyler.smylibgui.widgets.sliders;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.SmyLibGuiTextures;
import fr.thesmyler.smylibgui.devices.Key;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.util.Util;
import fr.thesmyler.smylibgui.widgets.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

import static fr.thesmyler.smylibgui.SmyLibGui.getGameContext;

/**
 * AbstractSliderWidget
 * An abstract slider, providing the basis for rendering and positioning
 * Calculating the progress and the string to displayed is left to the implementing subclass
 * 
 * @author SmylerMC
 *
 */
public abstract class AbstractSliderWidget implements IWidget {

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
        float pos = Util.saturate((mouseX) / this.getWidth());
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
    public void onKeyTyped(char typedChar, Key key, @Nullable WidgetContainer parent) {
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
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean hasFocus, WidgetContainer parent) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGuiTextures.BUTTON_TEXTURES);
        Color.WHITE.applyGL();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        float leftWidth = this.width / 2;
        float rightWidth = this.width - leftWidth;
        float splitHeight = Math.min(10, this.height / 2);
        RenderUtil.drawTexturedModalRect(x, y, 0, 46, leftWidth, splitHeight);
        RenderUtil.drawTexturedModalRect(x + leftWidth, y, 200 - rightWidth, 46, rightWidth, splitHeight);
        for(int i=0; i*18 < this.height - 20; i++) {
            RenderUtil.drawTexturedModalRect(x, y + splitHeight + 18*i, 0, 47, leftWidth, 18);
            RenderUtil.drawTexturedModalRect(x + leftWidth, y + splitHeight + 18*i, 200 - rightWidth, 47, rightWidth, 18);
        }
        RenderUtil.drawTexturedModalRect(x, y + this.height - splitHeight, 0, 46 + 20 - splitHeight, leftWidth, splitHeight);
        RenderUtil.drawTexturedModalRect(x + leftWidth, y + this.height - splitHeight, 200 - rightWidth, 46 + 20 - splitHeight, rightWidth, splitHeight);

        float sliderPosition = this.getPosition();
        Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGuiTextures.BUTTON_TEXTURES);
        Color.WHITE.applyGL();
        
        float sliderX = x + sliderPosition * (this.width - 8);
        RenderUtil.drawTexturedModalRect(sliderX, y, 0, 66, 4, splitHeight);
        RenderUtil.drawTexturedModalRect(sliderX + 4, y, 196, 66, 4, splitHeight);
        for(int i=0; i*18 < this.height - 20; i++) {
            RenderUtil.drawTexturedModalRect(sliderX, y + splitHeight + 18*i, 0, 68, 4, 15);
            RenderUtil.drawTexturedModalRect(sliderX + 4, y + splitHeight + 18*i, 196, 68, 4, 15);
        }
        RenderUtil.drawTexturedModalRect(sliderX, y + this.height - splitHeight, 0, 66 + 20 - splitHeight, 4, splitHeight);
        RenderUtil.drawTexturedModalRect(sliderX + 4, y + this.height - splitHeight, 196, 66 + 20 -splitHeight, 4, splitHeight);

        Color textColor = this.enabledTextColor;
        if (!this.isEnabled()) textColor = this.disabledTextColor;
        else if (hovered || hasFocus) textColor = this.activeTextColor;

        float fontSize = SmyLibGui.getDefaultFont().height();
        double gameScale = getGameContext().getScaleFactor();
        float fontScale = 1f;
        while(fontSize / fontScale > this.height - 1 && fontScale < gameScale) fontScale++;
        Font font = new Font(1 / fontScale + 0.0001f);
        font.drawCenteredString(x + this.width / 2, y + (this.height - font.height() + 1) / 2, this.getDisplayPrefix() + this.getDisplayString(), textColor, false);

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
