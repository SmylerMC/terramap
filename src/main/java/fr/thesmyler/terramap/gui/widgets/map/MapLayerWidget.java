package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;

abstract class MapLayerWidget implements IWidget {
	
	protected int z, width, height;
	protected double centerLatitude, centerLongitude, zoom;
	protected double tileScaling;
	
	public MapLayerWidget(double tileScaling) {
		if(Double.isInfinite(tileScaling)) throw new RuntimeException("tileScaling cannot be null");
		this.tileScaling = tileScaling;
	}

	protected double getMapX(double longitude) {
		return WebMercatorUtils.getXFromLongitude(longitude, this.zoom) / this.tileScaling;
	}

	protected double getMapY(double latitude) {
		return WebMercatorUtils.getYFromLatitude(latitude, this.zoom) / this.tileScaling;
	}

	protected double getScreenX(double longitude) {
		return this.getMapX(longitude) - this.getUpperLeftX();
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
	
	protected double getScreenLongitude(double xOnScreen) {
		double xOnMap = (this.getUpperLeftX() + xOnScreen) * this.tileScaling;
		return GeoServices.getLongitudeInRange(WebMercatorUtils.getLongitudeFromX(xOnMap, this.zoom));
	}

	protected double getScreenLatitude(double yOnScreen) {
		double yOnMap = (this.getUpperLeftY() + yOnScreen) * this.tileScaling;
		return WebMercatorUtils.getLatitudeFromY(yOnMap, this.zoom);
	}

	@Override
	public int getX() {
		return 0;
	}

	@Override
	public int getY() {
		return 0;
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
