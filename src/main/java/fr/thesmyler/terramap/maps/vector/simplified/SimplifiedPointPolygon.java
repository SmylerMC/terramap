package fr.thesmyler.terramap.maps.vector.simplified;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.maps.vector.features.Point;
import fr.thesmyler.terramap.maps.vector.features.Polygon;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoPoint;


public class SimplifiedPointPolygon extends SimplifiedVectorFeature implements Point {
    
    private final Polygon original;
    private final GeoPoint point;

    SimplifiedPointPolygon(GeoPoint point, Polygon original, boolean splitAtEdges) {
        super(splitAtEdges);
        this.point = point;
        this.original = original;
    }

    @Override
    public GeoPoint getPosition() {
        return this.point;
    }

    @Override
    public Color getColor() {
        return this.original.getContourColor();
    }

    @Override
    public String getIconName() {
        return null;
    }

    @Override
    public VectorFeature getOriginal() {
        return this.original;
    }

}
