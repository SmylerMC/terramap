package fr.thesmyler.terramap.maps.vector.features;

import java.util.Map;
import java.util.UUID;

import javax.annotation.Nullable;

import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.math.DoubleRange;

/**
 * A feature that can be displayed on a map as part of a vector dataset
 * 
 * @author SmylerMC
 *
 */
public interface VectorFeature {
    
    /**
     * @return the name that should be displayed for this feature, or null if no such name exist
     */
    @Nullable String getDisplayName();
    
    /**
     * @return the range for which this feature should be displayed depending on the zoom level
     */
    DoubleRange getZoomRange();
    
    /**
     * @return a uuid that should be unique to this feature
     */
    UUID uid();
    
    /**
     * @return this feature's metadata
     */
    Map<String, String> getMetadata();
    
    /**
     * @param bounds the bounds to check against
     * 
     * @return whether or not this feature is in the given bounds
     */
    boolean isIn(GeoBounds bounds);
    
    /**
     * @return bounds that contain this feature
     */
    GeoBounds bounds();

}
