package fr.smyler.terramap.gui.widgets.poi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public abstract class PointOfInterest extends Gui {

	protected double longitude;
	protected double latitude;
	protected ResourceLocation texture;
	
	public void draw(int x, int y, boolean hovered) {
		int ux = x + this.getXOffset();
		int uy = y + this.getYOffset();
		Gui.drawRect(ux,  uy, ux + this.getWidth(), uy + this.getHeight(), 0xFFFF0000);
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
