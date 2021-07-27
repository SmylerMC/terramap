package fr.thesmyler.terramap.maps.vector.simplified;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.maps.vector.features.LineString;
import fr.thesmyler.terramap.maps.vector.features.Point;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoPoint;

public class SimplifiedPointLineString extends SimplifiedVectorFeature implements Point {
    
    private GeoPoint point;
    private LineString original;

    SimplifiedPointLineString(GeoPoint point, LineString original, boolean splitAtEdges) {
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
        return this.original.getColor();
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
