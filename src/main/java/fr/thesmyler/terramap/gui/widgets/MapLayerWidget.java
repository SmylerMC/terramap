package fr.thesmyler.terramap.gui.widgets;

import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.config.TerramapConfiguration;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;

public abstract class MapLayerWidget implements IWidget {
	
	protected int x, y, z, width, height;
	protected double centerLatitude, centerLongitude, zoom;

	protected double getMapX(double longitude) {
		return WebMercatorUtils.getXFromLongitude(longitude, this.zoom) * TerramapConfiguration.tileScaling;
	}

	protected double getMapY(double latitude) {
		return WebMercatorUtils.getYFromLatitude(latitude, this.zoom) * TerramapConfiguration.tileScaling;
	}

	protected double getScreenX(double longitude) {
		return this.getMapX(longitude) -  this.getUpperLeftX();
	}

	protected double getScreenY(double latitude) {
		return this.getMapY(latitude) - this.getUpperLeftY();
	}
	
	protected double getUpperLeftX() {
		return this.getMapX(this.centerLongitude) - (double)this.width / 2;
	}

	protected double getUpperLeftY() {
		return this.getMapY(this.centerLatitude) - (double)this.height / 2;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	public double getCenterLatitude() {
		return centerLatitude;
	}

	public void setCenterLatitude(double centerLatitude) {
		this.centerLatitude = centerLatitude;
	}

	public double getCenterLongitude() {
		return centerLongitude;
	}

	public void setCenterLongitude(double centerLongitude) {
		this.centerLongitude = centerLongitude;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setZ(int z) {
		this.z = z;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
