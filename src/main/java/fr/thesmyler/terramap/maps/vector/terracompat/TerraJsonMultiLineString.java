package fr.thesmyler.terramap.maps.vector.terracompat;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import fr.thesmyler.terramap.maps.vector.features.LineString;
import fr.thesmyler.terramap.maps.vector.features.MultiLineString;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.DoubleRange;
import net.buildtheearth.terraplusplus.dataset.geojson.object.Feature;

public class TerraJsonMultiLineString extends TerraJsonVectorFeature implements MultiLineString {
    
    private final TerraJsonLineString[] lines;

    TerraJsonMultiLineString(TerraJsonLineString[] lines, Feature feature) {
        super(feature);
        this.lines = lines;
    }

    @Override
    public int count() {
        return this.lines.length;
    }

    @Override
    public DoubleRange getZoomRange() {
        // TODO Auto-generated method stub
        return WebMercatorUtil.ZOOM_RANGE;
    }

    @Override
    public Iterator<LineString> iterator() {
        return Iterators.forArray(this.lines);
    }

}
