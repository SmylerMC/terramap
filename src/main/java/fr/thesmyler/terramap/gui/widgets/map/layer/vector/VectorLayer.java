package fr.thesmyler.terramap.gui.widgets.map.layer.vector;

import java.util.Iterator;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.maps.vector.Geometry;
import fr.thesmyler.terramap.maps.vector.LineString;
import fr.thesmyler.terramap.maps.vector.MultiGeometry;
import fr.thesmyler.terramap.maps.vector.MultiLineString;
import fr.thesmyler.terramap.maps.vector.MultiPoint;
import fr.thesmyler.terramap.maps.vector.MultiPolygon;
import fr.thesmyler.terramap.maps.vector.Point;
import fr.thesmyler.terramap.maps.vector.Polygon;
import fr.thesmyler.terramap.maps.vector.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.math.Vec2d;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.profiler.Profiler;

//TODO Respect zoom range
public abstract class VectorLayer extends MapLayer {
    
    private static final int MAX_FEATURE_DEPTH = 50;

    public VectorLayer(double tileScaling) {
        super(tileScaling);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {

        MapWidget parentMap = (MapWidget) parent;
        Profiler profiler = parentMap.getProfiler();

        profiler.startSection("render-vector-layer_" + this.getId());

        // The width and height of the rotated map that covers the area of the non-rotated one
        double extendedWidth = this.getExtendedWidth();
        double extendedHeight = this.getExtendedHeight();

        Vec2d upperLeft = this.getUpperLeftRenderCorner();
        GeoPoint lowerLocation = this.getRenderLocation(upperLeft.add(0, extendedHeight));
        GeoPoint upperLocation = this.getRenderLocation(upperLeft.add(extendedWidth, 0));
        GeoBounds renderBounds = new GeoBounds(lowerLocation, upperLocation);
        
        Iterator<VectorFeature> features = this.getVisibleFeatures(renderBounds);
        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);
        while(features.hasNext()) this.drawFeature(features.next(), 0);
        GlStateManager.popMatrix();
        profiler.endSection();
    }
    
    public abstract Iterator<VectorFeature> getVisibleFeatures(GeoBounds bounds);
    
    private void drawFeature(VectorFeature feature, int depth) {
        if(feature instanceof MultiGeometry) {
            if(this.checkDepth(depth)) this.drawMultiGeometry((MultiGeometry) feature, depth++);
        } else if(feature instanceof MultiPolygon) {
            this.drawMultiPolyon((MultiPolygon) feature);
        } else if(feature instanceof MultiLineString) {
            this.drawMultiLineString((MultiLineString) feature);
        } else if(feature instanceof MultiPoint) {
            this.drawMultiPoint((MultiPoint) feature);
        } else if(feature instanceof Geometry) {
            if(this.checkDepth(depth)) this.drawGeometry((Geometry) feature, depth++);
        } else if(feature instanceof Polygon) {
            this.drawPolygon((Polygon) feature);
        } else if(feature instanceof LineString) {
            this.drawLineString((LineString) feature);
        } else if(feature instanceof Point) {
            this.drawPoint((Point) feature);
        }
    }
    
    private void drawMultiGeometry(MultiGeometry geometries, int depth) {
        Iterator<Geometry> iterator = geometries.geometries();
        while(iterator.hasNext()) {
            this.drawGeometry(iterator.next(), depth);
        }
    }
    
    private void drawMultiPolyon(MultiPolygon polygons) {
        Iterator<Polygon> iterator = polygons.polygons();
        while(iterator.hasNext()) {
            this.drawPolygon(iterator.next());
        }
    }
    
    private void drawMultiLineString(MultiLineString lines) {
        Iterator<LineString> iterator = lines.lineStrings();
        while(iterator.hasNext()) {
            this.drawLineString(iterator.next());
        }
    }
    
    private void drawMultiPoint(MultiPoint points) {
        Iterator<Point> iterator = points.points();
        while(iterator.hasNext()) {
            this.drawPoint(iterator.next());
        }
    }
    
    private void drawGeometry(Geometry geometry, int depth) {
        Iterator<VectorFeature> features = geometry.features();
        while(features.hasNext()) {
            this.drawFeature(features.next(), depth);
        }
    }
    
    private void drawPolygon(Polygon polygon) {
        //TODO
    }
    
    private void drawLineString(LineString line) {
        //TODO
    }
    
    private void drawPoint(Point point) {
        //TODO
    }
    
    private boolean checkDepth(int depth) {
        return depth <= MAX_FEATURE_DEPTH;
    }

}
