package fr.thesmyler.terramap.maps;

import com.google.gson.JsonObject;
import fr.thesmyler.terramap.util.math.Vec2dMutable;

/**
 * The saved state of a map layer.
 *
 * @see SavedMapState
 *
 * @author Smyler
 */
public class SavedLayerState {

    public String type;
    public int z;
    public final Vec2dMutable cartesianOffset = new Vec2dMutable();
    public float rotationOffset = 0f;
    public boolean visible = true;
    public float alpha = 1f;
    public boolean setByUser = true;

    public JsonObject settings = new JsonObject();

}
