package fr.thesmyler.terramap.maps.vector;

import java.util.Iterator;

/**
 * A collection of features that can be drawn onto a map
 * 
 * @author SmylerMC
 *
 */
public interface Geometry extends VectorFeature {
    
    /**
     * @return an iterator over the features in this geometry
     */
    Iterator<VectorFeature> features();

}
