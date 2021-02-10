package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;

public abstract class MarkerController<T extends Marker> implements FeatureVisibilityController {
	
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
	
	@Override
	public boolean getVisibility() {
		return this.visibility;
	}
	
	@Override
	public void setVisibility(boolean yesNo) {
		this.visibility = yesNo;
		ToggleButtonWidget b = this.getButton();
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
	
	@Override
	public abstract boolean showButton();
	
	@Override
	public abstract ToggleButtonWidget getButton();

}
