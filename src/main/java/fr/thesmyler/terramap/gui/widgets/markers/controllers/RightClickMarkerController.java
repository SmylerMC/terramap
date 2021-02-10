package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.RightClickMarker;

public class RightClickMarkerController extends MarkerController<RightClickMarker> {
	
	public static final String ID = "right_click_marker";

	public RightClickMarkerController() {
		super(ID, 1000, RightClickMarker.class);
	}

	@Override
	public RightClickMarker[] getNewMarkers(Marker[] existingMarkers, MapWidget map) {
		if(existingMarkers.length > 0) return new RightClickMarker[] {};
		return new RightClickMarker[] { new RightClickMarker(this)};
	}

	@Override
	public boolean showButton() {
		return false;
	}

	@Override
	public ToggleButtonWidget getButton() {
		return null;
	}
	
	@Override
	public String getSaveName() {
		return ID;
	}

}
