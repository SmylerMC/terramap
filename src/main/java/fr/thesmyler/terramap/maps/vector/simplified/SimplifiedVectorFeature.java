package fr.thesmyler.terramap.maps.vector.simplified;

import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
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

}
