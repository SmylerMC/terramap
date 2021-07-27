package fr.thesmyler.terramap.maps.vector.simplified;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.maps.vector.features.Polygon;
import fr.thesmyler.terramap.maps.vector.features.PolygonRing;
import fr.thesmyler.terramap.maps.vector.features.UnmutablePolygonRing;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoPoint;


public class SimplifiedPolygon extends SimplifiedVectorFeature implements Polygon {
    
    private UnmutablePolygonRing outerRing;
    private UnmutablePolygonRing[] innerRings;
    private Polygon original;

    SimplifiedPolygon(GeoPoint[][] rings, Polygon original, boolean splitAtEdges) {
        super(splitAtEdges);
        this.outerRing = new UnmutablePolygonRing(rings[0]);
        this.innerRings = new UnmutablePolygonRing[rings.length - 1];
        for(int i=1; i<rings.length; i++) this.innerRings[i - 1] = new UnmutablePolygonRing(rings[i]);
        this.original = original;
    }

    @Override
    public PolygonRing getOuterRing() {
        return this.outerRing;
    }

    @Override
    public PolygonRing[] getInnerRings() {
        return this.innerRings;
    }

    @Override
    public Color getInnerColor() {
        return this.original.getInnerColor();
    }

    @Override
    public Color getContourColor() {
        return this.original.getContourColor();
    }

    @Override
    public float getContourWidth() {
        return this.original.getContourWidth();
    }

    @Override
    public VectorFeature getOriginal() {
        return this.original;
    }

}
