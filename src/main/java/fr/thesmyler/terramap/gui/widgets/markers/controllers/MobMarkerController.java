package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.MobMarker;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;

public class MobMarkerController extends MarkerController<MobMarker> {
	
	public static final String ID = "mobs";
	
	protected ToggleButtonWidget button = new ToggleButtonWidget(10, 14, 14,
			102, 108, 102, 122,
			102, 108, 102, 122,
			102, 136, 102, 150,
			this.getVisibility(), null);

	public MobMarkerController() {
		super(ID, 700, MobMarker.class);
		this.button.setOnChange(b -> this.setVisibility(b));
		this.button.setTooltip(I18n.format("terramap.terramapscreen.markercontrollers.buttons.mobs"));
	}

	@Override
	public MobMarker[] getNewMarkers(Marker[] existingMarkers, MapWidget map) {
		if(TerramapClientContext.getContext().getProjection() == null) return new MobMarker[0];
		Map<UUID, Entity> entities = new HashMap<UUID, Entity>();
		for(Entity entity: TerramapClientContext.getContext().getEntities()) {
			if(entity instanceof IMob) {
				entities.put(entity.getPersistentID(), entity);
			}
		}
		for(Marker rawMarker: existingMarkers) {
			MobMarker marker = (MobMarker) rawMarker;
			entities.remove(marker.getEntity().getUniqueID());
		}
		MobMarker[] newMarkers = new MobMarker[entities.size()];
		int i = 0;
		for(Entity entity: entities.values()) {
			newMarkers[i++] = new MobMarker(this, entity);
		}
		return newMarkers;
	}

	@Override
	public boolean showButton() {
		return TerramapClientContext.getContext().allowsMobRadar();
	}

	@Override
	public ToggleButtonWidget getButton() {
		return this.button;
	}

	@Override
	public boolean getVisibility() {
		return super.getVisibility() && TerramapClientContext.getContext().allowsMobRadar();
	}
	
	@Override
	public String getSaveName() {
		return ID;
	}

}
