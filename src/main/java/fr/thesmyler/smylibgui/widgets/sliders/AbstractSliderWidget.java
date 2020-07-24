package fr.thesmyler.smylibgui.widgets.sliders;

import javax.annotation.Nullable;

import org.lwjgl.input.Keyboard;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.Utils;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
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
public abstract class AbstractSliderWidget implements IWidget {

	private int x, y, z, width;
	
	public AbstractSliderWidget(int x, int y, int z, int width) {
		this.x = x;
		this.y = y;
		this.z = z;
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
	public boolean onClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		float pos = Utils.saturate(((float)mouseX) / this.getWidth());
		this.setValueFromPos(pos);
		return false;
	}
	
	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		return this.onClick(mouseX, mouseY, mouseButton, parent);
	}
	
	@Override
	public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, @Nullable Screen parent) {
		this.onClick(mouseX, mouseY, mouseButton, parent);
	}

	@Override
	public boolean onMouseWheeled(int mouseX, int mouseY, int amount, @Nullable Screen parent) {
		if(amount > 0) this.goToNext();
		else this.goToPrevious();
		return false;
	}
	
	@Override
	public void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {
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
        parent.drawTexturedModalRect(x, y, 0, 46, this.width / 2, 20);
        parent.drawTexturedModalRect(x + this.width / 2, y, 200 - this.width / 2, 46, this.width / 2, 20);
        
		float sliderPosition = this.getPosition();
		Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGui.BUTTON_TEXTURES);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        parent.drawTexturedModalRect(x + (int)(sliderPosition * (float)(this.width - 8)), y, 0, 66, 4, 20);
        parent.drawTexturedModalRect(x + (int)(sliderPosition * (float)(this.width - 8)) + 4, y, 196, 66, 4, 20);
        
		int textColor = 0xFFE0E0E0;
		if (!this.isEnabled()) textColor = 0xFFA0A0A0;
		else if (hovered || hasFocus) textColor = 0xFFFFFFA0;

        parent.getFont().drawCenteredString(x + this.width / 2, y + (20 - 8) / 2, this.getDisplayString(), textColor, false);
        

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
	
	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return 20;
	}	

}
