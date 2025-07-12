package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import net.smyler.smylib.gui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.AnimalMarker;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;

import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.gui.sprites.SmyLibSprites.*;
import static net.smyler.smylib.gui.sprites.SmyLibSprites.BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED;

public class AnimalMarkerController extends MarkerController<AnimalMarker> {

    public static final String ID = "creatures";

    protected final ToggleButtonWidget button = new ToggleButtonWidget(10, 14, 14,
            BUTTON_VISIBILITY_ON_15, BUTTON_VISIBILITY_OFF_15,
            BUTTON_VISIBILITY_ON_15_DISABLED, BUTTON_VISIBILITY_OFF_15_DISABLED,
            BUTTON_VISIBILITY_ON_15_HIGHLIGHTED, BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED,
            this.isVisible(), null);

    public AnimalMarkerController() {
        super(ID, 700, AnimalMarker.class);
        this.button.setOnChange(this::setVisibility);
        this.button.setTooltip(getGameClient().translator().format("terramap.terramapscreen.markercontrollers.buttons.animals"));
    }

    @Override
    public AnimalMarker[] getNewMarkers(Marker[] existingMarkers, MapWidget map) {
        if(TerramapClientContext.getContext().getProjection() == null) return new AnimalMarker[0];
        Map<UUID, Entity> entities = new HashMap<>();
        for(Entity entity: TerramapClientContext.getContext().getEntities()) {
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
    public boolean showButton() {
        return TerramapClientContext.getContext().allowsAnimalRadar() && TerramapClientContext.getContext().getProjection() != null;
    }

    @Override
    public ToggleButtonWidget getButton() {
        return this.button;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && TerramapClientContext.getContext().allowsAnimalRadar();
    }

    @Override
    public String getSaveName() {
        return ID;
    }

}
