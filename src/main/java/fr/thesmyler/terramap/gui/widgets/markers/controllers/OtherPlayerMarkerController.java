package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.Map;
import java.util.UUID;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.OtherPlayerMarker;
import fr.thesmyler.terramap.network.mapsync.TerramapPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

public class OtherPlayerMarkerController extends MarkerController<OtherPlayerMarker> {
	
	public static final String ID = "other_players";
	
	protected ToggleButtonWidget button = new ToggleButtonWidget(10, 14, 14,
			88, 108, 88, 122,
			88, 108, 88, 122,
			88, 136, 88, 150,
			this.areMakersVisible(), null, null);

	public OtherPlayerMarkerController() {
		super(ID, 800, OtherPlayerMarker.class);
		this.button.setOnActivate(() -> this.setVisibility(true));
		this.button.setOnDeactivate(() -> this.setVisibility(false));
		this.button.setTooltip("Toggle other players visibility"); //TODO Localization
	}

	@Override
	public OtherPlayerMarker[] getNewMarkers(Marker[] existingMarkers) {
		
		if(TerramapServer.getServer().getProjection() != null) {
			
			Map<UUID, TerramapPlayer> players = TerramapServer.getServer().getPlayerMap();
			
			for(Marker marker: existingMarkers) {
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

	@Override
	public boolean showToggleButton() {
		return true;
	}

	@Override
	public ToggleButtonWidget getToggleButton() {
		return this.button;
	}

}
