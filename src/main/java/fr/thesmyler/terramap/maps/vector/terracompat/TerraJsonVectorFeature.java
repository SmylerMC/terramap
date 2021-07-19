package fr.thesmyler.terramap.maps.vector.terracompat;

import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.Identifiers;
import net.buildtheearth.terraplusplus.dataset.geojson.Geometry;
import net.buildtheearth.terraplusplus.dataset.geojson.object.Feature;

public abstract class TerraJsonVectorFeature implements VectorFeature {
    
    private Feature delegate;
    private transient UUID uuid;
    
    TerraJsonVectorFeature(Feature feature) {
        this.delegate = feature;
    }

    @Override
    public String getDisplayName() {
        return this.delegate.properties().get("name");
    }

    @Override
    public UUID uid() {
        if(this.uuid == null) this.uuid = Identifiers.stringIdToUUID(this.delegate.id());
        return this.uuid;
    }

    @Override
    public Map<String, String> getMetadata() {
        return this.delegate.properties();
    }
    
    public static TerraJsonVectorFeature convert(Feature feature) {
        return convertGeometry(feature, feature.geometry());
    }
    
    private static final TerraJsonVectorFeature convertGeometry(Feature feature, Geometry geometry) {
        if(geometry instanceof net.buildtheearth.terraplusplus.dataset.geojson.geometry.GeometryCollection) {
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.GeometryCollection multiGeo = (net.buildtheearth.terraplusplus.dataset.geojson.geometry.GeometryCollection) geometry;
            net.buildtheearth.terraplusplus.dataset.geojson.Geometry[] geos = multiGeo.geometries();
            TerraJsonVectorFeature[] terraGeos = new TerraJsonVectorFeature[geos.length];
            for(int i=0; i<terraGeos.length; i++) {
                terraGeos[i] = convertGeometry(feature, geos[i]);
            }
            return new TerraJsonMultiGeometry(terraGeos, feature);
        } else if(geometry instanceof net.buildtheearth.terraplusplus.dataset.geojson.geometry.MultiPolygon) {
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.MultiPolygon multiPolys = (net.buildtheearth.terraplusplus.dataset.geojson.geometry.MultiPolygon) geometry;
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.Polygon[] polys = multiPolys.polygons();
            TerraJsonPolygon[] terraPolys = new TerraJsonPolygon[polys.length];
            for(int i=0; i<terraPolys.length; i++) {
                terraPolys[i] = new TerraJsonPolygon(polys[i], feature);
            }
            return new TerraJsonMultiPolygon(terraPolys, feature);
        } else if(geometry instanceof net.buildtheearth.terraplusplus.dataset.geojson.geometry.MultiLineString) {
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.MultiLineString multiLines = (net.buildtheearth.terraplusplus.dataset.geojson.geometry.MultiLineString) geometry;
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.LineString[] lines = multiLines.lines();
            TerraJsonLineString[] terraLines = new TerraJsonLineString[lines.length];
            for(int i=0; i<terraLines.length; i++) {
                terraLines[i] = new TerraJsonLineString(lines[i], feature);
            }
            return new TerraJsonMultiLineString(terraLines, feature);
        } else if(geometry instanceof net.buildtheearth.terraplusplus.dataset.geojson.geometry.MultiPoint) {
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.MultiPoint multiPoint = (net.buildtheearth.terraplusplus.dataset.geojson.geometry.MultiPoint) geometry;
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.Point[] points = multiPoint.points();
            TerraJsonPoint[] terraPoints = new TerraJsonPoint[points.length];
            for(int i=0; i<terraPoints.length; i++) {
                terraPoints[i] = new TerraJsonPoint(points[i], feature);
            }
            return new TerraJsonMultiPoint(terraPoints, feature);
        } else if(geometry instanceof net.buildtheearth.terraplusplus.dataset.geojson.geometry.Polygon) {
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.Polygon polygon = (net.buildtheearth.terraplusplus.dataset.geojson.geometry.Polygon) geometry;
            return new TerraJsonPolygon(polygon, feature);
        } else if(geometry instanceof net.buildtheearth.terraplusplus.dataset.geojson.geometry.LineString) {
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.LineString line = (net.buildtheearth.terraplusplus.dataset.geojson.geometry.LineString) geometry;
            return new TerraJsonLineString(line, feature);
        } else if(geometry instanceof net.buildtheearth.terraplusplus.dataset.geojson.geometry.Point) {
            net.buildtheearth.terraplusplus.dataset.geojson.geometry.Point point = (net.buildtheearth.terraplusplus.dataset.geojson.geometry.Point) geometry;
            return new TerraJsonPoint(point, feature);
        }
        throw new IllegalArgumentException("Unknown Terra++ GeoJson geometry!!");
    }

}
