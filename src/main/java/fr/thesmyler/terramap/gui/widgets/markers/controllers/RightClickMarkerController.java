package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.RightClickMarker;

public class RightClickMarkerController extends MarkerController<RightClickMarker> {

	public RightClickMarkerController() {
		super("right_click_marker", 1000, RightClickMarker.class);
	}

	@Override
	public RightClickMarker[] getNewMarkers(Marker[] existingMarkers) {
		if(existingMarkers.length > 0) return new RightClickMarker[] {};
		return new RightClickMarker[] { new RightClickMarker(this)};
	}

	@Override
	public boolean showToggleButton() {
		return false;
	}

	@Override
	public ToggleButtonWidget getToggleButton() {
		return null;
	}

}
