package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;

public interface FeatureVisibilityController {
	
	public boolean showButton();
	
	public ToggleButtonWidget getButton();
	
	public String getSaveName();
	
	public void setVisibility(boolean visibility);
	
	public boolean getVisibility();
	
}