package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.MainPlayerMarker;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.resources.I18n;

public class MainPlayerMarkerController extends AbstractPlayerMarkerController<MainPlayerMarker> {
	
	public static final String ID = "main_player_marker";

	public MainPlayerMarkerController() {
		super(ID, 900, MainPlayerMarker.class, new ToggleButtonWidget(10, 14, 14,
				74, 108, 74, 122,
				74, 108, 74, 122,
				74, 136, 74, 150,
				false, null));
		this.button.setTooltip(I18n.format("terramap.terramapscreen.markercontrollers.buttons.mainplayer"));
	}

	@Override
	public MainPlayerMarker[] getNewMarkers(Marker[] existingMarkers, MapWidget map) {
		int factor = map.getContext().equals(MapContext.MINIMAP)? 2: 1;
		EntityPlayerSP self = Minecraft.getMinecraft().player;
		if(existingMarkers.length < 1 && self != null && TerramapClientContext.getContext().getProjection() != null) {
			return new MainPlayerMarker[] { new MainPlayerMarker(this, factor) };
		}
		return new MainPlayerMarker[0];
	}

	@Override
	public boolean showButton() {
		return true;
	}
	
	@Override
	public String getSaveName() {
		return ID;
	}

}
