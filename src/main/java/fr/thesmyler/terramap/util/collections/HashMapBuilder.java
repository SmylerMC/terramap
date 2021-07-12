package fr.thesmyler.terramap.util.collections;

import java.util.HashMap;

public class HashMapBuilder<K, V> implements MapBuilder<HashMap<K, V>, K, V>{
    
    private HashMap<K, V> map = new HashMap<>();

    @Override
    public HashMap<K, V> build() {
        HashMap<K, V> map = this.map;
        this.map = new HashMap<>();
        return map;
    }

    @Override
    public HashMapBuilder<K, V> reset() {
        this.map.clear();
        return this;
    }

    @Override
    public HashMapBuilder<K, V> put(K key, V value) {
        this.map.put(key, value);
        return this;
    }

    @Override
    public int count() {
        return this.map.size();
    }

}
