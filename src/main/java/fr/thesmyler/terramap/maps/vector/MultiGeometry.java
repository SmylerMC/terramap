package fr.thesmyler.terramap.maps.vector;

import java.util.Iterator;

/**
 * A collections of geometries that can be drawn on a map
 * 
 * @author SmylerMC
 *
 */
public interface MultiGeometry extends VectorFeature {
    
    /**
     * @return an iterator over the geometries in this collection
     */
    Iterator<Geometry> geometries();

}
