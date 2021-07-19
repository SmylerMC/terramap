package fr.thesmyler.terramap.maps.vector.terracompat;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.maps.vector.features.LineString;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.DoubleRange;
import net.buildtheearth.terraplusplus.dataset.geojson.object.Feature;

public class TerraJsonLineString extends TerraJsonVectorFeature implements LineString {
    
    private final GeoPoint[] points;

    TerraJsonLineString(net.buildtheearth.terraplusplus.dataset.geojson.geometry.LineString delegate, Feature feature) {
        super(feature);
        net.buildtheearth.terraplusplus.dataset.geojson.geometry.Point[] points = delegate.points();
        this.points = new GeoPoint[points.length];
        for(int i=0; i<this.points.length; i++) {
            this.points[i] = new GeoPoint(points[i].lon(), points[i].lat());
        }
    }

    @Override
    public DoubleRange getZoomRange() {
        return WebMercatorUtil.ZOOM_RANGE;
    }

    @Override
    public GeoPoint[] getPoints() {
        return this.points;
    }

    @Override
    public Color getColor() {
        // TODO Auto-generated method stub
        return Color.BLUE;
    }

    @Override
    public float getWidth() {
        // TODO Auto-generated method stub
        return 1;
    }

}
