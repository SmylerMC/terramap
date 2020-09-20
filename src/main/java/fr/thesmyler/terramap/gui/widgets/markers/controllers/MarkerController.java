package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.terramap.gui.widgets.markers.markers.MapMarker;

public abstract class MarkerController<T extends MapMarker> {
	
	private boolean visibility = true;
	private final int zLayer;
	private final String id;
	private final Class<T> clazz;
	
	protected MarkerController(String id, int zLayer, Class<T> clazz) {
		this.zLayer = zLayer;
		this.id = id;
		this.clazz = clazz;
	}
	
	public abstract T[] getNewMarkers(MapMarker[] existingMarkers);
	
	public int getZLayer() {
		return this.zLayer;
	}
	
	public boolean areMakersVisible() {
		return this.visibility;
	}
	
	public void setVisibility(boolean yesNo) {
		this.visibility = yesNo;
	}
	
	public void toggleVisibility() {
		this.visibility = !this.visibility;
	}
	
	public String getId() {
		return this.id;
	}
	
	public Class<T> getMarkerType() {
		return this.clazz;
	}

}
