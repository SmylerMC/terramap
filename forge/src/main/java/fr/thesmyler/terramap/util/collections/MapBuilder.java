package fr.thesmyler.terramap.util.collections;

import java.util.Map;

/**
 * Interface for make map creation easier and cleaner
 * 
 * @author SmylerMC
 *
 * @param <T> type of the {@link Map} that will be built
 * @param <K> type of the keys of the {@link Map}
 * @param <V> type of the values of the {@link Map}
 */
public interface MapBuilder<T extends Map<K, V>, K, V> {
    
    /**
     * Creates the map that was being built and resets this builder.
     * No internal reference to the map returned should be kept after this call.
     * 
     * @return the map that was being built
     */
    T build();
    
    /**
     * Resets this builder: loose all references to the map that was being built.
     * 
     * @return this builder for chaining
     */
    MapBuilder<T, K, V> reset();
    
    /**
     * Puts a key => value pair into the map that was being built.
     * Behaves as if {@link Map#put(Object, Object)} was called.
     * 
     * @param key - key to inset the value for
     * @param value - value to insert for the key
     * 
     * @return this builder for chaining
     */
    MapBuilder<T, K, V> put(K key, V value);
    
    /**
     * Counts how many entries will be in the map according to the current state of this builder.
     * This may not be equal to the number of previous calls to {@link #put(Object, Object)} as the same key could have been used multiple times.
     * 
     * @return the number of items that would be in the map if {@link #build()} was called
     */
    int count();

}
