package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.Map;
import java.util.UUID;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.OtherPlayerMarker;
import fr.thesmyler.terramap.network.playersync.TerramapPlayer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;

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
		this.button.setTooltip(I18n.format("terramap.terramapscreen.markercontrollers.buttons.otherplayer"));
	}

	@Override
	public OtherPlayerMarker[] getNewMarkers(Marker[] existingMarkers, MapWidget map) {
		
		int factor = map.getContext().equals(MapContext.MINIMAP)? 2: 1;
		
		if(TerramapRemote.getRemote().getProjection() != null) {
			
			Map<UUID, TerramapPlayer> players = TerramapRemote.getRemote().getPlayerMap();
			
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
				newMarkers[i++] = new OtherPlayerMarker(this, player, factor);
			}
			
			return newMarkers;
		}
		return new OtherPlayerMarker[] {};
	}

	@Override
	public boolean showToggleButton() {
		return TerramapRemote.getRemote().allowsPlayerRadar();
	}

	@Override
	public ToggleButtonWidget getToggleButton() {
		return this.button;
	}

	@Override
	public boolean areMakersVisible() {
		return super.areMakersVisible() && TerramapRemote.getRemote().allowsPlayerRadar();
	}
	
	

}
