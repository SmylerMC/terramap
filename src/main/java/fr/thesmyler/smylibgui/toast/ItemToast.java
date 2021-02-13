package fr.thesmyler.smylibgui.toast;

import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;

/**
 * Render a toast that has an item on the left
 * @author SmylerMC
 *
 */
public class ItemToast extends AbstractToast {
	
	protected ItemStack icon;

	public ItemToast(String titleKey, String descriptionKey, ItemStack icon) {
		super(titleKey, descriptionKey);
		this.icon = icon;
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
			manager.getMinecraft().fontRenderer.drawString(this.getLocalizedTitle(), 30, 12, 0x000000);
		} else {
			manager.getMinecraft().fontRenderer.drawString(this.getLocalizedTitle(), 30, 7, 0x000000);
			manager.getMinecraft().fontRenderer.drawString(this.getLocalizedDescription(), 30, 18, 0xFF222222);
		}
		if(this.icon != null) manager.getMinecraft().getRenderItem().renderItemAndEffectIntoGUI((EntityLivingBase)null, this.icon, 8, 8);
		return currentTime - this.startTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
	}

}
