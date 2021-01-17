package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.TerramapUtils;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import net.minecraft.client.gui.GuiScreen;

public class ScaleIndicatorWidget implements IWidget {

	private int x, y, z, width;
	private boolean visible = true;
	
	public ScaleIndicatorWidget(int x, int y, int z, int width) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
	}
	
	public ScaleIndicatorWidget(int z) {
		this(0, 0, z, 50);
	}

	@Override
	public int getX() {
		return this.x;
	}
	
	public ScaleIndicatorWidget setX(int x) {
		this.x = x;
		return this;
	}

	@Override
	public int getY() {
		return this.y;
	}
	
	public ScaleIndicatorWidget setY(int y) {
		this.y = y;
		return this;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public int getWidth() {
		return this.width;
	}
	
	public ScaleIndicatorWidget setWidth(int width) {
		this.width = width;
		return this;
	}

	@Override
	public int getHeight() {
		return 10;
	}

	@Override
	public boolean isVisible(Screen parent) {
		return this.visible;
	}
	
	public ScaleIndicatorWidget setVisibility(boolean yesNo) {
		this.visible = yesNo;
		return this;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
		int barY = y + 5;
		String lengthstr = "-";
		int barwidth = this.getWidth();
		if(parent instanceof MapWidget) {

			MapWidget map = (MapWidget) parent;

			double latAtScreenBottom = map.getScreenLatitude(this.y + 5);
			if(Math.abs(latAtScreenBottom) < 85) {

				double circAtLat = TerramapUtils.EARTH_CIRCUMFERENCE * Math.cos(Math.toRadians(latAtScreenBottom));
				double scale = circAtLat / WebMercatorUtils.getMapDimensionInPixel((int) map.getZoom()) * barwidth;
				String[] units = {"m", "km"};
				int j=0;
				for(; scale >= 1000 && j<units.length-1; j++) scale /= 1000;
				lengthstr = "" + Math.round(scale) + " " + units[j];
			}
		}
		int strwidth = parent.getFont().getStringWidth(lengthstr);
		parent.getFont().drawString(lengthstr, x + barwidth/2 - strwidth/2, barY - parent.getFont().FONT_HEIGHT - 5, 0xFF444444);
		GuiScreen.drawRect(x, barY, x + barwidth, barY+2, 0xFF444444);
		GuiScreen.drawRect(x, barY-4, x+2, barY+6, 0xFF444444);
		GuiScreen.drawRect(x-2 + barwidth, barY-4, x + barwidth, barY+6, 0xFF444444);
	}

}
