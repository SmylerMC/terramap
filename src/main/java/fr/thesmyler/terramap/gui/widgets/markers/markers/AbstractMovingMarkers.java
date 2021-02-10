package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;

public abstract class AbstractMovingMarkers extends Marker {

	protected Animation movingAnimation;

	protected double longitude, latitude;
	protected float azimuth;
	protected double oldLongitude, oldLatitude;

	public AbstractMovingMarkers(MarkerController<?> controller, int width, int height, int minZoom, int maxZoom) {
		super(controller, width, height, minZoom, maxZoom);
		this.movingAnimation = new Animation(10000);
	}

	public AbstractMovingMarkers(MarkerController<?> controller, int width, int height) {
		this(controller, width, height, Integer.MIN_VALUE, Integer.MAX_VALUE);
	}

	@Override
	public void update(MapWidget map) {

		//TODO Animate for smoother movements
		try {
			double[] coordinates = this.getActualCoordinates();
			double realLo = coordinates[0];
			double realLa = coordinates[1];
			this.longitude = realLo;
			this.latitude = realLa;
		} catch(OutOfProjectionBoundsException e) {
			this.latitude = this.longitude = Double.NaN;
		}
		try {
			float realAzimuth = this.getActualAzimuth();
			this.azimuth = Math.round(realAzimuth);
		} catch (OutOfProjectionBoundsException e) {
			this.azimuth = Float.NaN;
		}
		

	}

	protected abstract double[] getActualCoordinates() throws OutOfProjectionBoundsException;

	@Override
	public double getLongitude() {
		return this.longitude;
	}

	@Override
	public double getLatitude() {
		return this.latitude;
	}
	
	protected abstract float getActualAzimuth() throws OutOfProjectionBoundsException;

}
