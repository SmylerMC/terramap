package fr.thesmyler.terramap.maps.vector;

import java.util.Iterator;

/**
 * A collection of points that can be rendered.
 * 
 * @author SmylerMC
 *
 */
public interface MultiPoint extends VectorFeature {
    
    /**
     * @return an iterator over the points in this collection
     */
    Iterator<Point> points();

}
