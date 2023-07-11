package fr.thesmyler.terramap.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.maps.SavedMapState;

public class SavedMainScreenState {

    public SavedMapState map;

    public boolean infoPanel = false;
    public boolean debug = false;
    public boolean f1 = false;
    public Map<String, Boolean> visibilitySettings = new HashMap<>();
    public String trackedMarker = null;

    public SavedMainScreenState(
            SavedMapState map,
            boolean infoPanel,
            boolean debug,
            boolean f1,
            Map<String, Boolean> markerSettings,
            String trackedMarker) {
        this.map = map;
        this.infoPanel = infoPanel;
        this.debug = debug;
        this.f1 = f1;
        this.visibilitySettings = markerSettings;
        this.trackedMarker = trackedMarker;
    }

    public SavedMainScreenState() {
    }

    @Override
    public String toString() {
        try {
            return new Gson().toJson(this);
        }catch(Exception e) {
            TerramapMod.logger.error("Failed to generate map state json!");
            TerramapMod.logger.catching(e);
            return "";
        }
    }

}
