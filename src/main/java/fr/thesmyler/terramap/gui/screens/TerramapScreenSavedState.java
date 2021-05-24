package fr.thesmyler.terramap.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.util.Vec2d;

public class TerramapScreenSavedState {

    public double zoomLevel = 0;
    public double centerLongitude = 0;
    public double centerLatitude = 0;
    public float rotation = 0;
    public String mapStyle = "";

    public boolean infoPannel = false;
    public boolean debug = false;
    public boolean f1 = false;
    public Map<String, Boolean> visibilitySettings = new HashMap<String, Boolean>();
    public String trackedMarker = null;
    public Map<String, Vec2d> layerOffsets;

    public TerramapScreenSavedState(
            double zoomLevel,
            double centerLongitude,
            double centerLatitude,
            float rotation,
            String mapStyle,
            boolean infoPannel,
            boolean debug,
            boolean f1,
            Map<String, Boolean> markerSettings,
            String trackedMarker,
            Map<String, Vec2d> layerOffsets) {
        this.zoomLevel = zoomLevel;
        this.centerLongitude = centerLongitude;
        this.centerLatitude = centerLatitude;
        this.rotation = rotation;
        this.mapStyle = mapStyle;
        this.infoPannel = infoPannel;
        this.debug = debug;
        this.f1 = f1;
        this.visibilitySettings = markerSettings;
        this.trackedMarker = trackedMarker;
        this.layerOffsets = layerOffsets;
    }

    public TerramapScreenSavedState() {
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
