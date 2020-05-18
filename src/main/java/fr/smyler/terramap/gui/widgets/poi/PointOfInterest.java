package fr.smyler.terramap.gui.widgets.poi;

import net.minecraft.client.gui.Gui;
import net.minecraft.util.ResourceLocation;

public abstract class PointOfInterest extends Gui {

	protected double longitude;
	protected double latitude;
	protected ResourceLocation texture;
	
	public void draw(int x, int y) {
		Gui.drawRect(x-5,  y-5, x+5, y+5, 0xFFFF0000);
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
	
	
	
}
