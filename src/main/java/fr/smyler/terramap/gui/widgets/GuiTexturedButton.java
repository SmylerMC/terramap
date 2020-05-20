package fr.smyler.terramap.gui.widgets;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public class GuiTexturedButton extends GuiButton {

	protected int u;
	protected int v;
	protected int hoverU;
	protected int hoverV;
	protected int disabledU;
	protected int disabledV;
	protected ResourceLocation texture;

	public GuiTexturedButton(int buttonId, int x, int y, int widthIn, int heightIn, int u, int v, int hoverU, int hoverV, int disabledU, int disabledV, ResourceLocation texture) {
		super(buttonId, x, y, widthIn, heightIn, "");
		this.u = u;
		this.v = v;
		this.hoverU = hoverU;
		this.hoverV = hoverV;
		this.disabledU = disabledU;
		this.disabledV = disabledV;
		this.texture = texture;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if (!this.visible) return;
		mc.getTextureManager().bindTexture(this.texture);
		GlStateManager.color(255, 255, 255, 255);
		this.hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;
		GlStateManager.enableBlend();
		GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
		GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
		int u = this.u;
		int v = this.v;
		if(!this.enabled) {
			u = this.disabledU;
			v = this.disabledV;
		} else if(hovered) {
			u = this.hoverU;
			v = this.hoverV;
		}
		this.drawTexturedModalRect(this.x, this.y, u, v, this.width, this.height);
//		this.mouseDragged(mc, mouseX, mouseY);
	}

}
