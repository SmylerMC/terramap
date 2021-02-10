package fr.thesmyler.terramap.gui.screens;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.Gson;

import fr.thesmyler.terramap.TerramapMod;

public class TerramapScreenSavedState {
	
	public double zoomLevel = 0;
	public double centerLongitude = 0;
	public double centerLatitude = 0;
	public String mapStyle = "";
	
	public boolean infoPannel = false;
	public boolean debug = false;
	public boolean f1 = false;
	public Map<String, Boolean> visibilitySettings = new HashMap<String, Boolean>();
	public String trackedMarker = null;
	
	public TerramapScreenSavedState(
			double zoomLevel,
			double centerLongitude,
			double centerLatitude,
			String mapStyle,
			boolean infoPannel,
			boolean debug,
			boolean f1,
			Map<String, Boolean> markerSettings,
			String trackedMarker) {
		super();
		this.zoomLevel = zoomLevel;
		this.centerLongitude = centerLongitude;
		this.centerLatitude = centerLatitude;
		this.mapStyle = mapStyle;
		this.infoPannel = infoPannel;
		this.debug = debug;
		this.f1 = f1;
		this.visibilitySettings = markerSettings;
		this.trackedMarker = trackedMarker;
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
