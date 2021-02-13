package fr.thesmyler.smylibgui.toast;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;

public class TextureToast extends AbstractToast {
	
	protected ResourceLocation icon;

	public TextureToast(String titleKey, String descriptionKey, ResourceLocation icon) {
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

		GlStateManager.enableAlpha();
		GlStateManager.enableTexture2D();
		GlStateManager.color(1.0F, 1.0F, 1.0F);
		GlStateManager.enableBlend();
		manager.getMinecraft().getTextureManager().bindTexture(this.icon);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        bufferbuilder.pos(8, 8 + 16, 300).tex(0, 1).endVertex();
        bufferbuilder.pos(8 + 16, 8 + 16, 300).tex(1, 1).endVertex();
        bufferbuilder.pos(8 + 16, 8, 300).tex(1, 0).endVertex();
        bufferbuilder.pos(8, 8, 300).tex(0, 0).endVertex();
        tessellator.draw();
		
		return currentTime - this.startTime < 5000L ? IToast.Visibility.SHOW : IToast.Visibility.HIDE;
	}

}
