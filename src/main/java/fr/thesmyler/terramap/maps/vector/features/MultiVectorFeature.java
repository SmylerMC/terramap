package fr.thesmyler.terramap.maps.vector.features;

import fr.thesmyler.terramap.util.geo.GeoBounds;

interface MultiVectorFeature<T extends VectorFeature> extends VectorFeature, Iterable<T> {
    
    /**
     * @return the number of features in this collection
     */
    int count();
    
    @Override default boolean isIn(GeoBounds bounds) {
        for(T feature: this) if(!feature.isIn(bounds)) return false;
        return true;
    }
    
    @Override default GeoBounds bounds() {
        GeoBounds bounds = GeoBounds.EMPTY;
        for(T feature: this) bounds = bounds.smallestEncompassingSquare(feature.bounds());
        return bounds;
    }

}
