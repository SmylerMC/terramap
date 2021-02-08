package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;

public class PlayerNameVisibilityController implements FeatureVisibilityController {
	
	public static final String ID = "player_names";

	private MainPlayerMarkerController main;
	private OtherPlayerMarkerController other;
	private ToggleButtonWidget button;
	
	public PlayerNameVisibilityController(MainPlayerMarkerController main, OtherPlayerMarkerController other) {
		this.main = main;
		this.other = other;
		this.button = new ToggleButtonWidget(10, 14, 14,
				172, 108, 172, 122,
				172, 108, 172, 122,
				172, 136, 172, 150,
				this.main.doesMarkersShowNames(),
				b -> {
					this.main.setShowMarkerNames(b);
					this.other.setShowMarkerNames(b);
				}
			);  //TODO Tooltip
	}
	
	@Override
	public boolean showButton() {
		return true;
	}
	
	@Override
	public ToggleButtonWidget getButton() {
		return button;
	}

	@Override
	public String getSaveName() {
		return ID;
	}

	@Override
	public void setVisibility(boolean visibility) {
		this.main.setShowMarkerNames(visibility);
		this.other.setShowMarkerNames(visibility);
		this.button.setState(visibility);
	}
	
	@Override
	public boolean getVisibility() {
		return this.main.doesMarkersShowNames();
	}

}
