package fr.thesmyler.terramap.gui.screens;

import java.util.HashMap;
import java.util.Map;

import fr.thesmyler.terramap.maps.SavedMapState;

public class SavedMainScreenState {

    public SavedMapState map;

    public boolean infoPanel = false;
    public boolean layerPanel = false;
    public boolean debug = false;
    public boolean f1 = false;
    public Map<String, Boolean> visibilitySettings = new HashMap<>();
    public String trackedMarker = null;

}
