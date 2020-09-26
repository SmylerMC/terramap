package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.markers.markers.AnimalMarker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.MapMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;

public class AnimalMarkerController extends MarkerController<AnimalMarker> {
	
	protected ToggleButtonWidget button = new ToggleButtonWidget(10, 14, 14,
			116, 108, 116, 122,
			116, 108, 116, 122,
			116, 136, 116, 150,
			this.areMakersVisible(), null, null);

	public AnimalMarkerController() {
		super("creatures", 700, AnimalMarker.class);
		this.button.setOnActivate(() -> this.setVisibility(true));
		this.button.setOnDeactivate(() -> this.setVisibility(false));
		this.button.setTooltip("Toggle neutral creatures visibility"); //TODO Localize
	}

	@Override
	public AnimalMarker[] getNewMarkers(MapMarker[] existingMarkers) {
		if(TerramapServer.getServer().getProjection() == null) return new AnimalMarker[0];
		Map<UUID, Entity> entities = new HashMap<UUID, Entity>();
		for(Entity entity: Minecraft.getMinecraft().world.loadedEntityList) {
			if(entity instanceof IAnimals && !(entity instanceof IMob)) {
				entities.put(entity.getPersistentID(), entity);
			}
		}
		for(MapMarker rawMarker: existingMarkers) {
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
		return true;
	}

	@Override
	public ToggleButtonWidget getToggleButton() {
		return this.button;
	}

}
