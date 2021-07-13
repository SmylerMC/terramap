package fr.thesmyler.terramap.maps.vector;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.util.geo.GeoPoint;

/**
 * A line string to display on a map
 * 
 * @author SmylerMC
 *
 */
public interface LineString extends VectorFeature {
    
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
    
    /**
     * Computes or returns the length of this line along geodesics.
     * It does not have to match the distances between the points that compose this line when drawing,
     * e.g. if this line is a simplified version of an other one.
     * 
     * @return the length of this line, in meters
     */
    double length();

}
