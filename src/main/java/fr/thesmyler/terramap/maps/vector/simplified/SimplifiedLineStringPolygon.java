package fr.thesmyler.terramap.maps.vector.simplified;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.maps.vector.features.LineString;
import fr.thesmyler.terramap.maps.vector.features.Polygon;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoPoint;

public class SimplifiedLineStringPolygon extends SimplifiedVectorFeature implements LineString {

    private final GeoPoint[] points;
    private final Polygon original;
    
    SimplifiedLineStringPolygon(GeoPoint[] points, Polygon original, boolean splitAtEdges) {
        super(splitAtEdges);
        this.original = original;
        this.points = points;
    }

    @Override
    public GeoPoint[] getPoints() {
        return this.points;
    }

    @Override
    public Color getColor() {
        return this.original.getContourColor();
    }

    @Override
    public float getWidth() {
        return this.original.getContourWidth();
    }

    @Override
    public VectorFeature getOriginal() {
        return this.original;
    }
}
