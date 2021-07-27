package fr.thesmyler.terramap.maps.vector.simplified;

import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.math.DoubleRange;

public abstract class SimplifiedVectorFeature implements VectorFeature {
    
    private final boolean splitAtEdges;
    
    SimplifiedVectorFeature(boolean splitAtEdges) {
        this.splitAtEdges = splitAtEdges;
    }

    public abstract VectorFeature getOriginal();

    public boolean isSplitAtEdges() {
        return splitAtEdges;
    }
    
    @Override
    public String getDisplayName() {
        return this.getOriginal().getDisplayName();
    }

    @Override
    public DoubleRange getZoomRange() {
        return this.getOriginal().getZoomRange();
    }

    @Override
    public UUID uid() {
        return this.getOriginal().uid();
    }
    
    @Override
    public Map<String, String> getMetadata() {
        return this.getOriginal().getMetadata();
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
