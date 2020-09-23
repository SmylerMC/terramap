package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.markers.markers.MapMarker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.PlayerMarker;
import fr.thesmyler.terramap.network.mapsync.TerramapPlayer;

public class PlayerMarkerController extends MarkerController<PlayerMarker> {

	public PlayerMarkerController() {
		super("players", 400, PlayerMarker.class);
	}

	@Override
	public PlayerMarker[] getNewMarkers(MapMarker[] existingMarkers) {
		
		if(TerramapServer.getServer().getProjection() != null) {
			
			Map<UUID, TerramapPlayer> players = TerramapServer.getServer().getPlayerMap();
			
			for(MapMarker marker: existingMarkers) {
				TerramapPlayer player = ((PlayerMarker) marker).getPlayer();
				players.remove(player.getUUID());
			}
			
			PlayerMarker[] newMarkers = new PlayerMarker[players.size()];
			int i = 0;
			for(TerramapPlayer player: players.values()) {
				newMarkers[i++] = new PlayerMarker(this, player);
			}
			
			return newMarkers;
		}
		return new PlayerMarker[] {};
	}

}
