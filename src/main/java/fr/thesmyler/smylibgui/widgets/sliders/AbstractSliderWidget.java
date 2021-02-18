package fr.thesmyler.smylibgui.widgets.sliders;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.Utils;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.AbstractWidget;
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
public abstract class AbstractSliderWidget extends AbstractWidget {
	
	protected String displayPrefix = "";
	protected boolean enabled = true;
	
	public AbstractSliderWidget(int x, int y, int z, int width) {
		super(x, y, z, width, 20);
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
	public boolean onClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		if(!this.isEnabled()) return false;
		float pos = Utils.saturate(((float)mouseX) / this.getWidth());
		this.setValueFromPos(pos);
		return false;
	}
	
	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		if(!this.isEnabled()) return false;
		return this.onClick(mouseX, mouseY, mouseButton, parent);
	}
	
	@Override
	public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, @Nullable Screen parent) {
		if(!this.isEnabled()) return;
		this.onClick(mouseX, mouseY, mouseButton, parent);
	}

	@Override
	public boolean onMouseWheeled(int mouseX, int mouseY, int amount, @Nullable Screen parent) {
		if(!this.isEnabled()) return false;
		if(amount > 0) this.goToNext();
		else this.goToPrevious();
		return false;
	}
	
	@Override
	public void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {
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
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean hasFocus, Screen parent) {
        Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGui.BUTTON_TEXTURES);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        int leftWidth = this.width / 2;
        int rightWidth = leftWidth;
        leftWidth += this.width % 2;
        parent.drawTexturedModalRect(x, y, 0, 46, leftWidth, 20);
        parent.drawTexturedModalRect(x + leftWidth, y, 200 - rightWidth, 46, rightWidth, 20);
        
		float sliderPosition = this.getPosition();
		Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGui.BUTTON_TEXTURES);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        parent.drawTexturedModalRect(x + (int)(sliderPosition * (float)(this.width - 8)), y, 0, 66, 4, 20);
        parent.drawTexturedModalRect(x + (int)(sliderPosition * (float)(this.width - 8)) + 4, y, 196, 66, 4, 20);
        
		int textColor = 0xFFE0E0E0;
		if (!this.isEnabled()) textColor = 0xFFA0A0A0;
		else if (hovered || hasFocus) textColor = 0xFFFFFFA0;

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
	public AbstractSliderWidget setX(int x) {
		this.x = x;
		return this;
	}

	public AbstractSliderWidget setY(int y) {
		this.y = y;
		return this;
	}

	public AbstractSliderWidget setWidth(int width) {
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

}
