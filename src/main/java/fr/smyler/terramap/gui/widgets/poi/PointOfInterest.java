package fr.smyler.terramap.gui.widgets.poi;

import fr.smyler.terramap.gui.GuiTiledMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;

public abstract class PointOfInterest extends Gui {

	protected double longitude;
	protected double latitude;
	protected ResourceLocation texture = GuiTiledMap.WIDGET_TEXTURES;
	
	public void draw(int x, int y, boolean hovered) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		this.drawTexturedModalRect(x - 5, y - 5, 0, hovered? 32:0, 10, hovered? 43:11);
	}
	
	public void drawName(int x, int y, boolean hovered) {
		FontRenderer f = Minecraft.getMinecraft().fontRenderer;
		String name = this.getDisplayName();
		int strWidth = f.getStringWidth(name);
		int nameY = y + this.getYOffset() - f.FONT_HEIGHT - 2;
		Gui.drawRect(x - strWidth / 2 - 2, y + this.getYOffset() - f.FONT_HEIGHT - 4, x + strWidth / 2 + 2, y + this.getYOffset() - 1, 0x50000000);
		this.drawCenteredString(f, name, x, nameY, 0xFFFFFF);
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}
	
	/**
	 * 
	 * @return Name formated for rendering
	 */
	public abstract String getDisplayName();
	
	public abstract int getWidth();
	
	public abstract int getHeight();
	
	public abstract int getXOffset();
	
	public abstract int getYOffset();
	
}
