package fr.thesmyler.terramap.gui.widgets.poi;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;

public class LocationPOI extends PointOfInterest {

	protected String name;
	
	public LocationPOI(double longitude, double latitude, String name) {
		this.longitude = longitude;
		this.latitude = latitude;
		this.name = name;
	}
	
	public LocationPOI(double longitude, double latitude) {
		this(longitude, latitude, "");
	}
	
	@Override
	public void draw(int x, int y, boolean hovered) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(this.texture);
		GlStateManager.enableAlpha();
		GlStateManager.enableBlend();
		this.drawTexturedModalRect(x - 7, y - 23, 11, hovered? 32:0, 15, hovered? 38:25);
	}
	
	@Override
	public String getDisplayName() {
		return this.name;
	}

	@Override
	public int getWidth() {
		return 15;
	}

	@Override
	public int getHeight() {
		return 23;
	}

	@Override
	public int getXOffset() {
		return -8;
	}

	@Override
	public int getYOffset() {
		return -23;
	}

}
