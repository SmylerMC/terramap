package fr.thesmyler.terramap.maps.vector.simplified;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import fr.thesmyler.terramap.maps.vector.features.MultiGeometry;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;

public class SimplifiedMultiGeometry extends SimplifiedVectorFeature implements MultiGeometry {
    
    private final VectorFeature original;
    private final VectorFeature[] features;

    SimplifiedMultiGeometry(VectorFeature[] features, VectorFeature original, boolean splitAtEdges) {
        super(splitAtEdges);
        this.original = original;
        this.features = features;
    }

    @Override
    public int count() {
        return this.features.length;
    }

    @Override
    public Iterator<VectorFeature> iterator() {
        return Iterators.forArray(this.features);
    }

    @Override
    public VectorFeature getOriginal() {
        return this.original;
    }


}
