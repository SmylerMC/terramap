package fr.thesmyler.smylibgui.toast;

import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;

/**
 * A toast with just text
 * 
 * @author SmylerMC
 *
 */
public class SimpleToast extends AbstractToast {

	public SimpleToast(String titleKey, String descriptionKey) {
		super(titleKey, descriptionKey);
	}

	@Override
	public Visibility draw(GuiToast manager, long currentTime) {
		if (this.justUpdated) {
			this.startTime = currentTime;
			this.justUpdated = false;
		}
		manager.getMinecraft().getTextureManager().bindTexture(TEXTURE_TOASTS);
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		manager.drawTexturedModalRect(0, 0, 0, 32, 160, 32);
		if (this.descriptionKey == null) {
			manager.getMinecraft().fontRenderer.drawString(this.getLocalizedTitle(), 10, 12, 0x000000);
		} else {
			manager.getMinecraft().fontRenderer.drawString(this.getLocalizedTitle(), 10, 7, 0x000000);
			manager.getMinecraft().fontRenderer.drawString(this.getLocalizedDescription(), 10, 18, 0xFF222222);
		}
		return currentTime - this.startTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
	}
	
}
