package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.markers.markers.MapMarker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.SelfPlayerMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class SelfPlayerMarkerController extends MarkerController<SelfPlayerMarker> {
	
	protected ToggleButtonWidget button = new ToggleButtonWidget(10, 14, 14,
			74, 108, 74, 122,
			74, 108, 74, 122,
			74, 136, 74, 150,
			this.areMakersVisible(), null, null);

	public SelfPlayerMarkerController() {
		super("self_player_marker", 900, SelfPlayerMarker.class);
		this.button.setOnActivate(() -> this.setVisibility(true));
		this.button.setOnDeactivate(() -> this.setVisibility(false));
		this.button.setTooltip("Toggle main player visibility"); //TODO Localization
	}

	@Override
	public SelfPlayerMarker[] getNewMarkers(MapMarker[] existingMarkers) {
		EntityPlayerSP self = Minecraft.getMinecraft().player;
		if(existingMarkers.length < 1 && self != null && TerramapServer.getServer().getProjection() != null) {
			return new SelfPlayerMarker[] { new SelfPlayerMarker(this) };
		}
		return new SelfPlayerMarker[0];
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
