package fr.thesmyler.terramap.maps.vector;

import java.util.Iterator;

/**
 * A collection of polygons that can be drawn
 * 
 * @author SmylerMC
 *
 */
public interface MultiPolygon extends VectorFeature {
    
    /**
     * @return an iterator over the polygons in this collection
     */
    Iterator<Polygon> polygons();    

}
