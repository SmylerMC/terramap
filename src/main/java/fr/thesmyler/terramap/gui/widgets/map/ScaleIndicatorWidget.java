package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.RenderUtil;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.TerramapUtils;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;

public class ScaleIndicatorWidget implements IWidget {

	private float x, y;
	private int z;
	private float width;
	private boolean visible = true;
	
	public ScaleIndicatorWidget(float x, float y, int z, float width) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
	}
	
	public ScaleIndicatorWidget(int z) {
		this(0, 0, z, 50);
	}

	@Override
	public float getX() {
		return this.x;
	}
	
	public ScaleIndicatorWidget setX(float x) {
		this.x = x;
		return this;
	}

	@Override
	public float getY() {
		return this.y;
	}
	
	public ScaleIndicatorWidget setY(float y) {
		this.y = y;
		return this;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public float getWidth() {
		return this.width;
	}
	
	public ScaleIndicatorWidget setWidth(float width) {
		this.width = width;
		return this;
	}

	@Override
	public float getHeight() {
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
	public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, Screen parent) {
		float barY = y + 5;
		String lengthstr = "-";
		float barwidth = this.getWidth();
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
		float strwidth = parent.getFont().getStringWidth(lengthstr);
		parent.getFont().drawString(lengthstr, x + barwidth/2 - strwidth/2, barY - parent.getFont().height() - 5, 0xFF444444, false);
		RenderUtil.drawRect(x, barY, x + barwidth, barY+2, 0xFF444444);
		RenderUtil.drawRect(x, barY-4, x+2, barY+6, 0xFF444444);
		RenderUtil.drawRect(x-2 + barwidth, barY-4, x + barwidth, barY+6, 0xFF444444);
	}

}
