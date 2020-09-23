package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.markers.markers.MapMarker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.SelfPlayerMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class SelfPlayerMarkerController extends MarkerController<SelfPlayerMarker> {

	public SelfPlayerMarkerController() {
		super("self_player_marker", 900, SelfPlayerMarker.class);
	}

	@Override
	public SelfPlayerMarker[] getNewMarkers(MapMarker[] existingMarkers) {
		EntityPlayerSP self = Minecraft.getMinecraft().player;
		if(existingMarkers.length < 1 && self != null && TerramapServer.getServer().getProjection() != null) {
			return new SelfPlayerMarker[] { new SelfPlayerMarker(this) };
		}
		return new SelfPlayerMarker[0];
	}

}
