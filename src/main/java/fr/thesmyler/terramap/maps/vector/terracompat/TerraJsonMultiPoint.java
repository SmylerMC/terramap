package fr.thesmyler.terramap.maps.vector.terracompat;

import java.util.Iterator;

import com.google.common.collect.Iterators;

import fr.thesmyler.terramap.maps.vector.features.MultiPoint;
import fr.thesmyler.terramap.maps.vector.features.Point;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.DoubleRange;
import net.buildtheearth.terraplusplus.dataset.geojson.object.Feature;

public class TerraJsonMultiPoint extends TerraJsonVectorFeature implements MultiPoint {
    
    private final TerraJsonPoint[] points;

    TerraJsonMultiPoint(TerraJsonPoint[] points, Feature feature) {
        super(feature);
        this.points = points;
    }

    @Override
    public int count() {
        return this.points.length;
    }

    @Override
    public DoubleRange getZoomRange() {
        // TODO Auto-generated method stub
        return WebMercatorUtil.ZOOM_RANGE;
    }

    @Override
    public Iterator<Point> iterator() {
        return Iterators.forArray(this.points);
    }

}
