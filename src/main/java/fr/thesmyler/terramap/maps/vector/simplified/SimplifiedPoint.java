package fr.thesmyler.terramap.maps.vector.simplified;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.maps.vector.features.Point;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoPoint;

public class SimplifiedPoint extends SimplifiedVectorFeature implements Point {
    
    private final GeoPoint position;
    private final Point original;

    SimplifiedPoint(GeoPoint point, Point original, boolean splitAtEdges) {
        super(splitAtEdges);
        this.position = point;
        this.original = original;
    }

    @Override
    public GeoPoint getPosition() {
        return this.position;
    }

    @Override
    public Color getColor() {
        return this.original.getColor();
    }

    @Override
    public String getIconName() {
        return this.original.getIconName();
    }

    @Override
    public VectorFeature getOriginal() {
        return this.original;
    }

}
