package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.smylibgui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.gui.widgets.markers.markers.AgressiveMobMarker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.MapMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityGhast;
import net.minecraft.entity.monster.IMob;

public class AgressiveMobMarkerController extends MarkerController<AgressiveMobMarker> {
	
	protected ToggleButtonWidget button = new ToggleButtonWidget(10, 14, 14,
			102, 108, 102, 122,
			102, 108, 102, 122,
			102, 136, 102, 150,
			this.areMakersVisible(), null, null);

	public AgressiveMobMarkerController() {
		super("agressive_mobs", 700, AgressiveMobMarker.class);
		this.button.setOnActivate(() -> this.setVisibility(true));
		this.button.setOnDeactivate(() -> this.setVisibility(false));
		this.button.setTooltip("Toggle agressive creatures visibility"); //TODO Localize
	}

	@Override
	public AgressiveMobMarker[] getNewMarkers(MapMarker[] existingMarkers) {
		if(TerramapServer.getServer().getProjection() == null) return new AgressiveMobMarker[0];
		Map<UUID, Entity> entities = new HashMap<UUID, Entity>();
		for(Entity entity: Minecraft.getMinecraft().world.loadedEntityList) {
			if(entity instanceof IMob || entity instanceof EntityGhast) {
				entities.put(entity.getPersistentID(), entity);
			}
		}
		for(MapMarker rawMarker: existingMarkers) {
			AgressiveMobMarker marker = (AgressiveMobMarker) rawMarker;
			entities.remove(marker.getEntity().getUniqueID());
		}
		AgressiveMobMarker[] newMarkers = new AgressiveMobMarker[entities.size()];
		int i = 0;
		for(Entity entity: entities.values()) {
			newMarkers[i++] = new AgressiveMobMarker(this, entity);
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
