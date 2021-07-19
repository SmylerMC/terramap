package fr.thesmyler.terramap.maps.vector.features;

import java.util.Arrays;

import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.geo.GeoPoint;

public class UnmutablePolygonRing implements PolygonRing {
    
    private final GeoPoint[] points;
    private transient GeoBounds bounds = null;
    private transient double pseudoArea = Double.NaN;
    
    public UnmutablePolygonRing(GeoPoint[] points) {
        this.points = points;
        if(this.isValid()) throw new IllegalArgumentException("Invalid polygon ring");
    }

    @Override
    public GeoPoint[] getPoints() {
        return this.points;
    }
    
    @Override
    public GeoBounds bounds() {
        if(this.bounds == null) this.bounds = PolygonRing.super.bounds();
        return this.bounds;
    }

    @Override
    public double pseudoArea() {
        if(Double.isNaN(this.pseudoArea)) this.pseudoArea = PolygonRing.super.pseudoArea();
        return this.pseudoArea;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Arrays.hashCode(points);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        UnmutablePolygonRing other = (UnmutablePolygonRing) obj;
        if(!Arrays.equals(points, other.points)) return false;
        return true;
    }
    
}
