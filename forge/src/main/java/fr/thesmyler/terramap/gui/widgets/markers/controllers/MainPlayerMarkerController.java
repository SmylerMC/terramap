package fr.thesmyler.terramap.gui.widgets.markers.controllers;

import net.smyler.smylib.gui.widgets.buttons.ToggleButtonWidget;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.markers.Marker;
import fr.thesmyler.terramap.gui.widgets.markers.markers.entities.MainPlayerMarker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;

import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.gui.sprites.SmyLibSprites.*;
import static net.smyler.terramap.Terramap.getTerramapClient;

public class MainPlayerMarkerController extends AbstractPlayerMarkerController<MainPlayerMarker> {

    public static final String ID = "main_player_marker";

    public MainPlayerMarkerController() {
        super(ID, 900, MainPlayerMarker.class, new ToggleButtonWidget(10, 14, 14,
                BUTTON_VISIBILITY_ON_15, BUTTON_VISIBILITY_OFF_15,
                BUTTON_VISIBILITY_ON_15_DISABLED, BUTTON_VISIBILITY_OFF_15_DISABLED,
                BUTTON_VISIBILITY_ON_15_HIGHLIGHTED, BUTTON_VISIBILITY_OFF_15_HIGHLIGHTED,
                false, null));
        this.button.setTooltip(getGameClient().translator().format("terramap.terramapscreen.markercontrollers.buttons.mainplayer"));
    }

    @Override
    public MainPlayerMarker[] getNewMarkers(Marker[] existingMarkers, MapWidget map) {
        int factor = map.getContext().equals(MapContext.MINIMAP)? 2: 1;
        EntityPlayerSP self = Minecraft.getMinecraft().player;
        boolean hasProjection = getTerramapClient().projection().isPresent();
        if(existingMarkers.length < 1 && self != null && hasProjection) {
            return new MainPlayerMarker[] { new MainPlayerMarker(this, factor) };
        }
        return new MainPlayerMarker[0];
    }

    @Override
    public boolean showButton() {
        return getTerramapClient().projection().isPresent();
    }

    @Override
    public String getSaveName() {
        return ID;
    }

}
