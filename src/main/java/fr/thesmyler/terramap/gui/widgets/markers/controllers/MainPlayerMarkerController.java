package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.MainPlayerMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class MainPlayerMarkerController extends MarkerController<MainPlayerMarker> {
	
	protected ToggleButtonWidget button = new ToggleButtonWidget(10, 14, 14,
			74, 108, 74, 122,
			74, 108, 74, 122,
			74, 136, 74, 150,
			this.areMakersVisible(), null, null);

	public MainPlayerMarkerController() {
		super("main_player_marker", 900, MainPlayerMarker.class);
		this.button.setOnActivate(() -> this.setVisibility(true));
		this.button.setOnDeactivate(() -> this.setVisibility(false));
		this.button.setTooltip("Toggle main player visibility"); //TODO Localization
	}

	@Override
	public MainPlayerMarker[] getNewMarkers(Marker[] existingMarkers) {
		EntityPlayerSP self = Minecraft.getMinecraft().player;
		if(existingMarkers.length < 1 && self != null && TerramapServer.getServer().getProjection() != null) {
			return new MainPlayerMarker[] { new MainPlayerMarker(this) };
		}
		return new MainPlayerMarker[0];
	}

	@Override
	public boolean showToggleButton() {
		return true;
	}

	@Override
	public ToggleButtonWidget getToggleButton() {
		return this.button;
	}

}
