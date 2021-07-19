package fr.thesmyler.terramap.maps.vector.terracompat;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import fr.thesmyler.terramap.maps.vector.features.MultiGeometry;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.DoubleRange;
import net.buildtheearth.terraplusplus.dataset.geojson.object.Feature;

public class TerraJsonMultiGeometry extends TerraJsonVectorFeature implements MultiGeometry {
    
    private final TerraJsonVectorFeature[] features;

    TerraJsonMultiGeometry(TerraJsonVectorFeature[] features, Feature feature) {
        super(feature);
        this.features = features;
    }

    @Override
    public int count() {
        return this.features.length;
    }

    @Override
    public DoubleRange getZoomRange() {
        // TODO Auto-generated method stub
        return WebMercatorUtil.ZOOM_RANGE;
    }

    @Override
    public Iterator<VectorFeature> iterator() {
        return Iterators.forArray(this.features);
    }

}
