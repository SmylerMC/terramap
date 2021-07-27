package fr.thesmyler.terramap.maps.vector.features;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.util.geo.GeoBounds;
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
     * @return outer ring of this polygon
     */
    PolygonRing getOuterRing();
    
    /**
     * @return the inner rings of this polygon (i.e. holes)
     */
    PolygonRing[] getInnerRings();
    
    /**
     * @return get the color of the inside of this polygon for drawing
     */
    Color getInnerColor();
    
    /**
     * @return the color of the contours of this polygon for drawing
     */
    Color getContourColor();
    
    /**
     * @return the width of the contours of this polygon for drawing
     */
    float getContourWidth();
    
    @Override default boolean isIn(GeoBounds bounds) {
        for(GeoPoint point: this.getOuterRing()) {
            if(!bounds.contains(point)) return false;
        }
        return true;
    }
    
    @Override default GeoBounds bounds() {
        return this.getOuterRing().bounds();
    }

}
