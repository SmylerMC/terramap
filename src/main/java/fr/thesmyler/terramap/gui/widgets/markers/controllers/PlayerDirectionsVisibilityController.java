package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import net.minecraft.client.resources.I18n;


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
				this.main.doesShowDirection(),
				b -> {
					this.main.setShowDirection(b);
					this.other.setShowDirection(b);
				}
		);
		this.button.setTooltip(I18n.format("terramap.terramapscreen.markercontrollers.buttons.direction"));
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
		this.main.setShowDirection(visibility);
		this.other.setShowDirection(visibility);
		this.button.setState(visibility);
	}
	
	@Override
	public boolean getVisibility() {
		return this.main.doesShowDirection();
	}

}
