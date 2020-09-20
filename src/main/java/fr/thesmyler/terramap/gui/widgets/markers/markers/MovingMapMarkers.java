package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;

public abstract class MovingMapMarkers extends MapMarker {
	
	protected Animation movingAnimation;
	
	protected double longitude, latitude;
	protected double oldLongitude, oldLatitude;

	public MovingMapMarkers(MarkerController<?> controller, int width, int height) {
		super(controller, width, height);
	}

	@Override
	public double getLongitude() {
		return longitude;
	}

	@Override
	public double getLatitude() {
		return latitude;
	}

}
