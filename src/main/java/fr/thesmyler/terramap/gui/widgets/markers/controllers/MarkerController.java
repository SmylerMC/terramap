package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;

public abstract class MarkerController<T extends Marker> {
	
	private boolean visibility = true;
	private final int zLayer;
	private final String id;
	private final Class<T> clazz;
	
	public MarkerController(String id, int zLayer, Class<T> clazz) {
		this.zLayer = zLayer;
		this.id = id;
		this.clazz = clazz;
	}
	
	public abstract T[] getNewMarkers(Marker[] existingMarkers, MapWidget map);
	
	public int getZLayer() {
		return this.zLayer;
	}
	
	public boolean areMakersVisible() {
		return this.visibility;
	}
	
	public void setVisibility(boolean yesNo) {
		this.visibility = yesNo;
		ToggleButtonWidget b = this.getToggleButton();
		if(b != null) b.setState(this.visibility);
	}
	
	public void toggleVisibility() {
		this.setVisibility(!this.visibility);
	}
	
	public String getId() {
		return this.id;
	}
	
	public Class<T> getMarkerType() {
		return this.clazz;
	}
	
	public abstract boolean showToggleButton();
	
	public abstract ToggleButtonWidget getToggleButton();

}
