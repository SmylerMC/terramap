package fr.thesmyler.terramap.maps.vector.simplified;

import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoBounds;

public abstract class SimplifiedVectorFeature {
    
    private final VectorFeature original;
    private final boolean splitAtEdges;
    
    public SimplifiedVectorFeature(VectorFeature original, boolean splitAtEdges) {
        this.original = original;
        this.splitAtEdges = splitAtEdges;
    }

    public VectorFeature getOriginal() {
        return original;
    }

    public boolean isSplitAtEdges() {
        return splitAtEdges;
    }
    
    public static VectorFeature simplify(VectorFeature feature, GeoBounds viewPort, double zoom, float distance) {
        if(!feature.getZoomRange().matches(zoom)) return null;
        if(Thread.interrupted()) return null;
        if(!viewPort.intersects(feature.bounds())) return null;
        if(Thread.interrupted()) return null;
        //TODO Simplify more
        return feature;
    }

}
