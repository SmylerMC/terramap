package fr.thesmyler.terramap.maps.vector.terracompat;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import fr.thesmyler.terramap.maps.vector.features.MultiPolygon;
import fr.thesmyler.terramap.maps.vector.features.Polygon;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.DoubleRange;
import net.buildtheearth.terraplusplus.dataset.geojson.object.Feature;

public class TerraJsonMultiPolygon extends TerraJsonVectorFeature implements MultiPolygon {
    
    private final TerraJsonPolygon[] polygons;

    TerraJsonMultiPolygon(TerraJsonPolygon[] polygons, Feature feature) {
        super(feature);
        this.polygons = polygons;
    }

    @Override
    public int count() {
        return this.polygons.length;
    }

    @Override
    public DoubleRange getZoomRange() {
        // TODO Auto-generated method stub
        return WebMercatorUtil.ZOOM_RANGE;
    }

    @Override
    public Iterator<Polygon> iterator() {
        return Iterators.forArray(this.polygons);
    }

}
