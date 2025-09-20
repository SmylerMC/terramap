package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.EntityMarker;
import net.smyler.smylib.gui.sprites.Sprite;
import net.smyler.smylib.gui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.IMob;
import net.smyler.terramap.gui.widgets.markers.EntityMarkerStyle;
import net.smyler.terramap.gui.widgets.markers.ForgeEntityMarkerStylingRuleset;

import static net.smyler.smylib.Objects.requireNonNullElse;
import static net.smyler.smylib.gui.sprites.SmyLibSprites.*;
import static net.smyler.smylib.gui.sprites.SmyLibSprites.BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED;
import static net.smyler.terramap.Terramap.getTerramapClient;
import static net.smyler.terramap.gui.sprites.TerramapSprites.MARKER_TOKEN_GREY;

public class MobMarkerController extends MarkerController<EntityMarker> {

    public static final String ID = "mobs";

    protected final ToggleButtonWidget button = new ToggleButtonWidget(10, 14, 14,
            BUTTON_VISIBILITY_ON_15, BUTTON_VISIBILITY_OFF_15,
            BUTTON_VISIBILITY_ON_15_DISABLED, BUTTON_VISIBILITY_OFF_15_DISABLED,
            BUTTON_VISIBILITY_ON_15_HIGHLIGHTED, BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED,
            this.isVisible(), null);

    public MobMarkerController() {
        super(ID, 700, EntityMarker.class);
        this.button.setOnChange(this::setVisibility);
        this.button.setTooltip(I18n.format("terramap.terramapscreen.markercontrollers.buttons.mobs"));
    }

    @Override
    public EntityMarker[] getNewMarkers(Marker[] existingMarkers, MapWidget map) {
        if (!getTerramapClient().projection().isPresent()) {
            return new EntityMarker[0];
        }
        Map<UUID, Entity> entities = new HashMap<>();
        for(Entity entity: TerramapClientContext.getContext().getEntities()) {
            if(entity instanceof IMob) {
                entities.put(entity.getPersistentID(), entity);
            }
        }
        for(Marker rawMarker: existingMarkers) {
            EntityMarker marker = (EntityMarker) rawMarker;
            entities.remove(marker.getEntity().getUniqueID());
        }
        EntityMarker[] newMarkers = new EntityMarker[entities.size()];
        int i = 0;
        for(Entity entity: entities.values()) {
            EntityMarkerStyle style = ForgeEntityMarkerStylingRuleset.INSTANCE.getStyleFor(entity);
            Sprite sprite = requireNonNullElse(style.sprite(), MARKER_TOKEN_GREY);
            newMarkers[i++] = new EntityMarker(this, sprite, entity);
        }
        return newMarkers;
    }

    @Override
    public boolean showButton() {
        boolean hasProjection = getTerramapClient().projection().isPresent();
        return TerramapClientContext.getContext().allowsMobRadar() && hasProjection;
    }

    @Override
    public ToggleButtonWidget getButton() {
        return this.button;
    }

    @Override
    public boolean isVisible() {
        return super.isVisible() && TerramapClientContext.getContext().allowsMobRadar();
    }

    @Override
    public String getSaveName() {
        return ID;
    }

}
