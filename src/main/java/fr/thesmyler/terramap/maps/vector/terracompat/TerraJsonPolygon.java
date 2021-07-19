package fr.thesmyler.terramap.maps.vector.terracompat;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.terramap.maps.vector.features.Polygon;
import fr.thesmyler.terramap.maps.vector.features.PolygonRing;
import fr.thesmyler.terramap.maps.vector.features.UnmutablePolygonRing;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.DoubleRange;
import net.buildtheearth.terraplusplus.dataset.geojson.object.Feature;

public class TerraJsonPolygon extends TerraJsonVectorFeature implements Polygon {
    
    private final PolygonRing outerRing;
    private final PolygonRing[] innerRings;

    TerraJsonPolygon(net.buildtheearth.terraplusplus.dataset.geojson.geometry.Polygon delegate, Feature feature) {
        super(feature);
        this.outerRing = this.convertToRing(delegate.outerRing());
        net.buildtheearth.terraplusplus.dataset.geojson.geometry.LineString[] lineRings = delegate.innerRings();
        this.innerRings = new PolygonRing[lineRings.length];
        for(int i=0; i<lineRings.length; i++) {
            this.innerRings[i] = this.convertToRing(lineRings[i]);
        }
    }

    @Override
    public DoubleRange getZoomRange() {
        // TODO Auto-generated method stub
        return WebMercatorUtil.ZOOM_RANGE;
    }

    @Override
    public PolygonRing getOuterRing() {
        return this.outerRing;
    }

    @Override
    public PolygonRing[] getInnerRings() {
        return this.innerRings;
    }

    @Override
    public Color getInnerColor() {
        // TODO Auto-generated method stub
        return Color.GREEN;
    }

    @Override
    public Color getContourColor() {
        // TODO Auto-generated method stub
        return Color.YELLOW;
    }

    private PolygonRing convertToRing(net.buildtheearth.terraplusplus.dataset.geojson.geometry.LineString line) {
        net.buildtheearth.terraplusplus.dataset.geojson.geometry.Point[] points = line.points();
        GeoPoint[] geoPoints = new GeoPoint[points.length];
        for(int i=0; i<points.length; i++) {
            geoPoints[i] = new GeoPoint(points[i].lon(), points[i].lat());
        }
        return new UnmutablePolygonRing(geoPoints);
    }
}
