package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.markers.markers.MapMarker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.OtherPlayerMarker;
import fr.thesmyler.terramap.network.mapsync.TerramapPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class OtherPlayerMarkerController extends MarkerController<OtherPlayerMarker> {

	public OtherPlayerMarkerController() {
		super("other_players", 400, OtherPlayerMarker.class);
	}

	@Override
	public OtherPlayerMarker[] getNewMarkers(MapMarker[] existingMarkers) {
		
		if(TerramapServer.getServer().getProjection() != null) {
			
			Map<UUID, TerramapPlayer> players = TerramapServer.getServer().getPlayerMap();
			
			for(MapMarker marker: existingMarkers) {
				TerramapPlayer player = ((OtherPlayerMarker) marker).getPlayer();
				players.remove(player.getUUID());
			}
			
			// The main player has its own controller
			EntityPlayerSP self = Minecraft.getMinecraft().player;
			if(self != null) {
				players.remove(self.getUniqueID());
			}
			
			OtherPlayerMarker[] newMarkers = new OtherPlayerMarker[players.size()];
			int i = 0;
			for(TerramapPlayer player: players.values()) {
				newMarkers[i++] = new OtherPlayerMarker(this, player);
			}
			
			return newMarkers;
		}
		return new OtherPlayerMarker[] {};
	}

}
