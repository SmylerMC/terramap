package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;

public class PlayerDirectionsVisibilityController implements FeatureVisibilityController {
	
	public static final String ID = "player_directions";
	
	private MainPlayerMarkerController main;
	private OtherPlayerMarkerController other;
	private ToggleButtonWidget button;
	
	public PlayerDirectionsVisibilityController(MainPlayerMarkerController main, OtherPlayerMarkerController other) {
		this.main = main;
		this.other = other;
		this.button = new ToggleButtonWidget(10, 14, 14,
				158, 108, 158, 122,
				158, 108, 158, 122,
				158, 136, 158, 150,
				this.main.doesMarkersShowDirection(),
				() -> {
					this.main.setShowMarkerDirection(true);
					this.other.setShowMarkerDirection(true);
				},
				() -> {
					this.main.setShowMarkerDirection(false);
					this.other.setShowMarkerDirection(false);
				}
		);
		 //TODO Tooltip
	}

	@Override
	public boolean showButton() {
		return true;
	}
	
	@Override
	public ToggleButtonWidget getButton() {
		return this.button;
	}
	
	@Override
	public String getSaveName() {
		return ID;
	}

	@Override
	public void setVisibility(boolean visibility) {
		this.main.setShowMarkerDirection(visibility);
		this.other.setShowMarkerDirection(visibility);
		this.button.setState(visibility);
	}
	
	@Override
	public boolean getVisibility() {
		return this.main.doesMarkersShowDirection();
	}

}
