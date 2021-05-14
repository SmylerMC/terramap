package fr.thesmyler.terramap.gui.widgets.map;

import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.terramap.GeoServices;
import fr.thesmyler.terramap.maps.utils.WebMercatorUtils;
import fr.thesmyler.terramap.util.Mat2d;
import fr.thesmyler.terramap.util.Vec2d;

/**
 * A layer of a map.
 * Takes care of some of the math for the implementing subclasses.
 * Especially, provides abstraction for rendering maps with rotation.
 * Deals with two sets of dimensions:
 * {@link #viewPortWidth} and {@link #viewPortHeight} are the width and height of the actual map widget
 * {@link #extendedHeight} and {@link #extendedHeight} are the width and height of the rendered map, which needs to be bigger to cover the whole area after rotation is applied
 * 
 * @author SmylerMC
 *
 */
abstract class MapLayerWidget implements IWidget {
	
	protected int z;
	private float viewPortWidth, viewPortHeight;
	private double extendedWidth, extendedHeight;
	private double centerLatitude, centerLongitude, zoom;
	private float rotation;
	private double tileScaling;
	
	private Mat2d directRotation = Mat2d.INDENTITY;
	private Mat2d inverseRotation = Mat2d.INDENTITY;
	
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
	
	protected Vec2d getScreenPos(double longitude, double latitude) {
		Vec2d pos = new Vec2d(
				this.getMapX(longitude) - this.getMapX(this.centerLongitude),
				this.getMapY(latitude) - this.getMapY(this.centerLatitude));
		pos = this.directRotation.prod(pos);
		return pos.add(this.viewPortWidth / 2, this.viewPortHeight / 2);
	}
	
	protected double getUpperLeftX() {
		return this.getMapX(this.centerLongitude) - this.extendedWidth / 2;
	}

	protected double getUpperLeftY() {
		return this.getMapY(this.centerLatitude) - this.extendedHeight / 2;
	}
	
	public double[] getScreenGeoPos(double x, double y) {
		Vec2d pos = new Vec2d(x - this.viewPortWidth / 2, y - this.viewPortHeight / 2);
		pos = this.inverseRotation.prod(pos);
		pos = pos.add(
				this.extendedWidth / 2 + this.getUpperLeftX(),
				this.extendedHeight / 2 + this.getUpperLeftY());
		pos = pos.scale(this.tileScaling);
		double lon = GeoServices.getLongitudeInRange(WebMercatorUtils.getLongitudeFromX(pos.x, this.zoom));
		double lat = WebMercatorUtils.getLatitudeFromY(pos.y, this.zoom);
		return new double[] {lon, lat};
	}
	
	private void updateViewPort() {
		this.directRotation = Mat2d.forRotation(Math.toRadians(this.rotation));
		this.inverseRotation = this.directRotation.transpose(); // For rotations, the inverse is the transposed
		Vec2d dim = new Vec2d(this.viewPortWidth, this.viewPortHeight);
		this.extendedWidth = dim.hadamardProd(this.directRotation.column1()).taxicabNorm();
		this.extendedHeight = dim.hadamardProd(this.directRotation.column2()).taxicabNorm();
	}

	@Override
	public float getX() {
		return 0;
	}

	@Override
	public float getY() {
		return 0;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public float getWidth() {
		return this.viewPortWidth;
	}

	@Override
	public float getHeight() {
		return this.viewPortHeight;
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

	public void setDimensions(float width, float height) {
		this.viewPortWidth = width;
		this.viewPortHeight = height;
		this.updateViewPort();
	}
	
	public float getRotation() {
		return this.rotation;
	}
	
	public void setRotation(float rotation) {
		this.rotation = rotation;
		this.updateViewPort();
	}
	
	public double getTileScaling() {
		return this.tileScaling;
	}
	
	public void setTileScaling(double tileScaling) {
		this.tileScaling = tileScaling;
	}
	
	public double getExtendedWidth() {
		return this.extendedWidth;
	}
	
	public double getExtendedHeight() {
		return this.extendedHeight;
	}
	
	public Mat2d getRotationMatrix() {
		return this.directRotation;
	}
	
	public Mat2d getInverseRotationMatrix() {
		return this.inverseRotation;
	}

}
