package fr.thesmyler.terramap.maps.vector.features;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.geo.GeoPoint;

/**
 * A vector point to display on a map.
 * 
 * @author SmylerMC
 *
 */
public interface Point extends VectorFeature {
    
    /**
     * @return this point geographic position
     */
    GeoPoint getPosition();
    
    /**
     * @return the {@link Color} that should be used when rendering this point
     */
    Color getColor();
    
    /**
     * @return the name of the icon to use to represent this point.
     * If null is returned, a simple point will be rendered with the color specified by {@link #getColor()}
     */
    @Nullable String getIconName();
    
    @Override default boolean isIn(GeoBounds bounds) {
        return bounds.contains(this.getPosition());
    }
    
    @Override default GeoBounds bounds() {
        GeoPoint point = this.getPosition();
        return new GeoBounds(point, point);
    }

}
