package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.AnimalMarker;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;

public class AnimalMarkerController extends MarkerController<AnimalMarker> {
	
	public static final String ID = "creatures";
	
	protected ToggleButtonWidget button = new ToggleButtonWidget(10, 14, 14,
			116, 108, 116, 122,
			116, 108, 116, 122,
			116, 136, 116, 150,
			this.areMakersVisible(), null, null);

	public AnimalMarkerController() {
		super(ID, 700, AnimalMarker.class);
		this.button.setOnActivate(() -> this.setVisibility(true));
		this.button.setOnDeactivate(() -> this.setVisibility(false));
		this.button.setTooltip(I18n.format("terramap.terramapscreen.markercontrollers.buttons.animals"));
	}

	@Override
	public AnimalMarker[] getNewMarkers(Marker[] existingMarkers, MapWidget map) {
		if(TerramapRemote.getRemote().getProjection() == null) return new AnimalMarker[0];
		Map<UUID, Entity> entities = new HashMap<UUID, Entity>();
		for(Entity entity: TerramapRemote.getRemote().getEntities()) {
			if(entity instanceof IAnimals && !(entity instanceof IMob)) {
				entities.put(entity.getPersistentID(), entity);
			}
		}
		for(Marker rawMarker: existingMarkers) {
			AnimalMarker marker = (AnimalMarker) rawMarker;
			entities.remove(marker.getEntity().getUniqueID());
		}
		AnimalMarker[] newMarkers = new AnimalMarker[entities.size()];
		int i = 0;
		for(Entity entity: entities.values()) {
			newMarkers[i++] = new AnimalMarker(this, entity);
		}
		return newMarkers;
	}

	@Override
	public boolean showToggleButton() {
		return TerramapRemote.getRemote().allowsAnimalRadar();
	}

	@Override
	public ToggleButtonWidget getToggleButton() {
		return this.button;
	}
	
	@Override
	public boolean areMakersVisible() {
		return super.areMakersVisible() && TerramapRemote.getRemote().allowsAnimalRadar();
	}

}
