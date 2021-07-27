package fr.thesmyler.terramap.maps.vector.simplified;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import fr.thesmyler.terramap.maps.vector.features.LineString;
import fr.thesmyler.terramap.maps.vector.features.MultiLineString;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;

public class SimplifiedMultiLineString extends SimplifiedVectorFeature implements MultiLineString {
    
    private final VectorFeature original;
    
    private final LineString[] lines;

    SimplifiedMultiLineString(LineString[] lines, VectorFeature original, boolean splitAtEdges) {
        super(splitAtEdges);
        this.lines = lines;
        this.original = original;
    }

    @Override
    public int count() {
        return this.lines.length;
    }

    @Override
    public Iterator<LineString> iterator() {
        return Iterators.forArray(this.lines);
    }

    @Override
    public VectorFeature getOriginal() {
        return this.original;
    }

}
