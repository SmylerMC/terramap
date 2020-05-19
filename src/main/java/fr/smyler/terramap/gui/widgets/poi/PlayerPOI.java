package fr.smyler.terramap.gui.widgets.poi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class PlayerPOI extends EntityPOI{

	public PlayerPOI(AbstractClientPlayer e) {
		super(e);
		this.texture = this.getPlayer().getLocationSkin();
	}

	public AbstractClientPlayer getPlayer() {
		return (AbstractClientPlayer) this.getEntity();
	}

	@Override
	public void draw(int x, int y, boolean hovered) {
		GlStateManager.enableAlpha();
		if(hovered) Gui.drawRect(x - 7, y - 7, x + 9, y + 9, 0x80000000);
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
		GlStateManager.color(255, 255, 255, 255);
		Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 16, 16, 16, 16, 128, 128);
		Gui.drawModalRectWithCustomSizedTexture(x - 8, y - 8, 80, 16, 16, 16, 128, 128);
	}
	
	@Override
	public int getWidth() {
		return 16;
	}

	@Override
	public int getHeight() {
		return 16;
	}

	@Override
	public int getXOffset() {
		return -8;
	}

	@Override
	public int getYOffset() {
		return -8;
	}

}
