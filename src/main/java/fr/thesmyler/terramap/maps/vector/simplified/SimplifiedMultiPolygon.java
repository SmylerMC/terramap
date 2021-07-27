package fr.thesmyler.terramap.maps.vector.simplified;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import fr.thesmyler.terramap.maps.vector.features.MultiPolygon;
import fr.thesmyler.terramap.maps.vector.features.Polygon;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;

public class SimplifiedMultiPolygon extends SimplifiedVectorFeature implements MultiPolygon {
    
    private final Polygon[] polygons;
    private final VectorFeature original;

    SimplifiedMultiPolygon(Polygon[] polygons, VectorFeature original, boolean splitAtEdges) {
        super(splitAtEdges);
        this.original = original;
        this.polygons = polygons;
    }

    @Override
    public int count() {
        return this.polygons.length;
    }

    @Override
    public Iterator<Polygon> iterator() {
        return Iterators.forArray(this.polygons);
    }

    @Override
    public VectorFeature getOriginal() {
        return this.original;
    }

}
