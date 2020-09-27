package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;

public abstract class AbstractFixedMarker extends Marker {
	
	private double longitude, latitude;

	public AbstractFixedMarker(MarkerController<?> controller, int width, int height, double longitude, double latitude) {
		super(controller, width, height);
		this.longitude = longitude;
		this.latitude = latitude;
	}

	@Override
	public double getLongitude() {
		return this.longitude;
	}

	@Override
	public double getLatitude() {
		return this.latitude;
	}
	
	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}
	
	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

}
