package fr.thesmyler.terramap.maps.vector;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.util.geo.GeoPoint;

/**
 * 
 * A polygon to be displayed on a map
 * 
 * @author SmylerMC
 *
 */
public interface Polygon extends VectorFeature {
    
    /**
     * @return the points of this polygon. The first dimension of the array represents the rings of the polygon,
     * and must start with the outer ring. The second dimensions corresponds to the points of each ring.
     */
    GeoPoint[][] getPoints();
    
    /**
     * @return get the color of the inside of this polygon for drawing
     */
    Color getInnerColor();
    
    /**
     * @return the color of the contours of this polygon for drawing
     */
    Color getContourColor();
    
    /**
     * @return the signed are of this polygon
     */
    double area();
    
    /**
     * @return the perimeter of this polygon
     * 
     * @see #outerPerimeter()
     */
    double perimeter();
    
    /**
     * @return the perimetr of the outer edges of this polygon
     * 
     * @see #perimeter()
     */
    double outerPerimeter();

}
