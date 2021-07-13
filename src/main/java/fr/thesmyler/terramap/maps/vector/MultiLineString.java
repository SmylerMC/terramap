package fr.thesmyler.terramap.maps.vector;

import java.util.Iterator;

/**
 * A collection of {@link LineString} that can be rendered
 * 
 * @author SmylerMC
 *
 */
public interface MultiLineString extends VectorFeature {
    
    /**
     * @return an iterator over the lines in this collection
     */
    Iterator<LineString> lineStrings();

}
