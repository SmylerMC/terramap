package fr.thesmyler.terramap.maps.vector.simplified;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.maps.vector.features.LineString;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoPoint;

public class SimplifiedLineString extends SimplifiedVectorFeature implements LineString {
    
    private final LineString original;
    private final GeoPoint[] points;

    SimplifiedLineString(GeoPoint[] points, LineString original, boolean splitAtEdges) {
        super(splitAtEdges);
        this.original = original;
        this.points = points;
    }
    
    @Override
    public VectorFeature getOriginal() {
        return this.original;
    }
    
    @Override
    public Color getColor() {
        return this.original.getColor();
    }

    @Override
    public float getWidth() {
        return this.original.getWidth();
    }

    @Override
    public GeoPoint[] getPoints() {
        return this.points;
    }

}
