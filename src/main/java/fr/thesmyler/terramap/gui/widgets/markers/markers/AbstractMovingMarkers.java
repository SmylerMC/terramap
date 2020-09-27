package fr.thesmyler.terramap.gui.widgets.markers.markers;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MarkerController;

public abstract class AbstractMovingMarkers extends Marker {
	
	protected Animation movingAnimation;
	
	protected double longitude, latitude;
	protected double oldLongitude, oldLatitude;

	public AbstractMovingMarkers(MarkerController<?> controller, int width, int height) {
		super(controller, width, height);
		this.movingAnimation = new Animation(10000);
	}
	
	@Override
	public void update(MapWidget map) {
		
		//TODO Animate for smoother movements
		double realLo = this.getActualLongitude();
		double realLa = this.getActualLatitude();
		this.longitude = realLo;
		this.latitude = realLa;
		
	}
	
	protected abstract double getActualLongitude();
	
	protected abstract double getActualLatitude();

	@Override
	public double getLongitude() {
		return this.longitude;
	}

	@Override
	public double getLatitude() {
		return this.latitude;
	}

}
