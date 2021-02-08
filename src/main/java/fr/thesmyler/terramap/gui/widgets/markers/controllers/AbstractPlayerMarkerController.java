package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.AbstractPlayerMarker;

public abstract class AbstractPlayerMarkerController<T extends AbstractPlayerMarker> extends MarkerController<T> {

	protected final ToggleButtonWidget button;
	private boolean direction = true;
	private boolean names = true;
	
	public AbstractPlayerMarkerController(String id, int zLayer, Class<T> clazz, ToggleButtonWidget button) {
		super(id, zLayer, clazz);
		this.button = button;
		this.button.setOnChange(b -> this.setVisibility(b));
		this.button.setState(this.getVisibility());
	}

	public boolean doesShowNames() {
		return this.names;
	}

	public void setShowNames(boolean yesNo) {
		this.names = yesNo;
	}
	
	public boolean doesShowDirection() {
		return this.direction;
	}

	public void setShowDirection(boolean direction) {
		this.direction = direction;
	}
	
	@Override
	public ToggleButtonWidget getButton() {
		return this.button;
	}

}
