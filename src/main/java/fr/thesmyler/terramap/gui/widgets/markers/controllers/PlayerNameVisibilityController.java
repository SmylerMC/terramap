package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import net.minecraft.client.resources.I18n;

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
				this.main.doesShowNames(),
				b -> {
					this.main.setShowNames(b);
					this.other.setShowNames(b);
				}
			);
		this.button.setTooltip(I18n.format("terramap.terramapscreen.markercontrollers.buttons.name"));
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
		this.main.setShowNames(visibility);
		this.other.setShowNames(visibility);
		this.button.setState(visibility);
	}
	
	@Override
	public boolean getVisibility() {
		return this.main.doesShowNames();
	}

}
