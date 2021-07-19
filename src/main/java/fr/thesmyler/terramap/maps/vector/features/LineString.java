package fr.thesmyler.terramap.maps.vector.features;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.geo.GeoPoint;

/**
 * A line string to display on a map
 * 
 * @author SmylerMC
 *
 */
public interface LineString extends VectorFeature, Iterable<GeoPoint> {
    
    /**
     * @return the points that compose this line when drawing
     */
    GeoPoint[] getPoints();
    
    /**
     * @return the color to use when drawing this line
     */
    Color getColor();
    
    /**
     * @return the width to draw this line with
     */
    float getWidth();

    @Override default Iterator<GeoPoint> iterator() {
        return Iterators.forArray(this.getPoints());
    }
    
    @Override default boolean isIn(GeoBounds bounds) {
        return bounds.contains(this.bounds());
    }
    
    @Override default GeoBounds bounds() {
        return new GeoBounds.Builder().addAllPoints(this).build();
    }

}
