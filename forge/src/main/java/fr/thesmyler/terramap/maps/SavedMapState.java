package fr.thesmyler.terramap.maps;

import net.smyler.terramap.geo.GeoPointMutable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A map saved state.
 * Having this in a different class makes it side independent, which might come in handy in the future.
 *
 * @author Smyler
 */
public class SavedMapState {

    public final GeoPointMutable center = new GeoPointMutable();
    public double zoom;
    public float rotation;

    public final List<SavedLayerState> layers = new ArrayList<>();
    public final Map<String, Boolean> visibilitySettings = new HashMap<>();
    public String trackedMarker = null;

}
