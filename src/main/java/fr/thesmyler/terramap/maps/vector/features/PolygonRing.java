package fr.thesmyler.terramap.maps.vector.features;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.math.Vec2d;

/**
 * A polygon ring.
 * A polygon ring is mostly a list of {@link GeoPoint} that form a ring.
 * None of these points shall be null.
 * 
 * A ring can be either an <b>outer ring</b> or an <b>inner ring</b>.
 * An outer rings delimits the the boundaries of a surface, and an inner ring delimits holes in a surface.
 * Although implementations should not reject rings that do not respect the that rule for backward compatibility,
 * outer rings should be counterclockwise and inner rings clockwise (so the right hand rule is followed).
 * 
 * A ring should have at least 4 points, and the first and last point should be strictly identical.
 * 
 * @author SmylerMC
 */
public interface PolygonRing extends Iterable<GeoPoint> {
    
    /**
     * @return the points that form this ring.
     * None of these points shall be null,
     * and there should be at least 4 points,
     * with the first and last one being strictly identical.
     */
    GeoPoint[] getPoints();

    @Override default Iterator<GeoPoint> iterator() {
        return Iterators.forArray(this.getPoints());
    }
    
    /**
     * @return whether or not this is a valid ring according to this interface's specifications.
     */
    default boolean isValid() {
        GeoPoint[] points = this.getPoints();
        if(points.length < 4) return false;
        for(GeoPoint point: points) if(point == null) return false;
        return points.length > 3 && points[0].equals(points[points.length - 1]);
    }
    
    /**
     * @return the orientation of this ring
     */
    default RingOrientation orientation() {
        return this.pseudoArea() > 0? RingOrientation.TRIGONOMETRIC: RingOrientation.CLOCKWISE;
    }
    
    /**
     * @return {@link GeoBounds} that contain this ring
     */
    default GeoBounds bounds() {
        return new GeoBounds.Builder().addAllPoints(this).build();
    }
    
    /**
     * @return the signed area of this ring in the equirectangular projection
     */
    default double pseudoArea() {
        GeoPoint[] points = this.getPoints();
        double area = 0;
        Vec2d start = points[0].asVec2d();
        for(int i=1; i<points.length-1; i++) {
            Vec2d first = points[i].asVec2d().substract(start);
            Vec2d second = points[i + 1].asVec2d().substract(start);
            area += .5d * first.crossProd(second);
        }
        return area;
    }
    
    public static enum RingOrientation {
        TRIGONOMETRIC, CLOCKWISE;
    }

}
