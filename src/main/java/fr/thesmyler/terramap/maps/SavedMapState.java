package fr.thesmyler.terramap.maps;

import fr.thesmyler.terramap.util.geo.GeoPointMutable;
import fr.thesmyler.terramap.util.math.Vec2dMutable;

import java.util.ArrayList;
import java.util.List;

/**
 * A map saved state.
 * Having this in a different class makes it side independent, which might come in handy in the future.
 *
 * @author Smylermap
 */
public class SavedMapState {

    public final GeoPointMutable center = new GeoPointMutable();
    public double zoom;
    public float rotation;

    public final List<SavedLayerState> layers = new ArrayList<>();

    public static class SavedLayerState {
        public String type;
        public int z;
        public final Vec2dMutable cartesianOffset = new Vec2dMutable();
        public float rotationOffset = 0f;
        public float alpha = 1f;
        public boolean overlay = true;
    }

}
