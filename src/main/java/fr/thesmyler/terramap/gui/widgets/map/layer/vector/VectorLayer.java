package fr.thesmyler.terramap.gui.widgets.map.layer.vector;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.terramap.gui.widgets.map.MapLayer;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.maps.vector.features.LineString;
import fr.thesmyler.terramap.maps.vector.features.MultiGeometry;
import fr.thesmyler.terramap.maps.vector.features.MultiLineString;
import fr.thesmyler.terramap.maps.vector.features.MultiPoint;
import fr.thesmyler.terramap.maps.vector.features.MultiPolygon;
import fr.thesmyler.terramap.maps.vector.features.Point;
import fr.thesmyler.terramap.maps.vector.features.Polygon;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.math.Vec2d;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.profiler.Profiler;

//TODO Respect zoom range
public abstract class VectorLayer extends MapLayer {
    
    private static final int MAX_FEATURE_DEPTH = 50;
    
    private int pointsRendered = 0;
    private int linesRendered = 0;
    private int polygonsRendered = 0;

    public VectorLayer(double tileScaling) {
        super(tileScaling);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {

        MapWidget parentMap = (MapWidget) parent;
        Profiler profiler = parentMap.getProfiler();
        boolean debug = parentMap.isDebugMode();

        profiler.startSection("render-vector-layer_" + this.getId());

        // The width and height of the rotated map that covers the area of the non-rotated one
        double extendedWidth = this.getExtendedWidth();
        double extendedHeight = this.getExtendedHeight();

        GeoPoint lowerLocation = this.getRenderLocation(new Vec2d(0, extendedHeight));
        GeoPoint upperLocation = this.getRenderLocation(new Vec2d(extendedWidth, 0));
        GeoBounds renderBounds = new GeoBounds(lowerLocation, upperLocation);
        
        this.pointsRendered = 0;
        this.linesRendered = 0;
        this.polygonsRendered = 0;
        
        Iterator<VectorFeature> featureIterator = this.getVisibleFeatures(renderBounds);
        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);
        Map<UUID, VectorFeature> features = new HashMap<>();
        while(featureIterator.hasNext()) {
            VectorFeature feature = featureIterator.next();
            if(renderBounds.intersects(feature.bounds()))
                features.put(feature.uid(), feature);
        }
        
        for(VectorFeature feature: features.values()) this.drawFeature(feature, 0);
        
        if(debug) {
            Vec2d lowerCorner = this.getRenderPos(lowerLocation);
            Vec2d upperCorner = this.getRenderPos(upperLocation);
            RenderUtil.drawClosedStrokeLine(Color.YELLOW, 5f,
                    lowerCorner.x, lowerCorner.y,
                    upperCorner.x, lowerCorner.y,
                    upperCorner.x, upperCorner.y,
                    lowerCorner.x, upperCorner.y
                    );
        }
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
        } else if(feature instanceof Polygon) {
            this.drawPolygon((Polygon) feature);
        } else if(feature instanceof LineString) {
            this.drawLineString((LineString) feature);
        } else if(feature instanceof Point) {
            this.drawPoint((Point) feature);
        }
    }
    
    private void drawMultiGeometry(MultiGeometry geometries, int depth) {
        for(VectorFeature feature: geometries) this.drawFeature(feature, depth);
    }
    
    private void drawMultiPolyon(MultiPolygon polygons) {
        for(Polygon polygon: polygons) this.drawPolygon(polygon);
    }
    
    private void drawMultiLineString(MultiLineString lines) {
        for(LineString line: lines) this.drawLineString(line);
    }
    
    private void drawMultiPoint(MultiPoint points) {
        for(Point point: points) this.drawPoint(point);
    }
    
    private void drawPolygon(Polygon polygon) {
        //TODO
        this.polygonsRendered++;
    }
    
    private void drawLineString(LineString line) {
        //TODO
        GeoPoint[] geo = line.getPoints();
        double[] cart = new double[geo.length*2];
        for(int i=0; i<geo.length; i++) {
            Vec2d pos = this.getRenderPos(geo[i]);
            cart[i*2] = pos.x;
            cart[i*2 + 1] = pos.y;
        }
        Color color = line.getColor().withAlpha(this.getAlpha());
        RenderUtil.drawStrokeLine(color, line.getWidth(), cart);
        this.linesRendered++;
    }
    
    private void drawPoint(Point point) {
        Vec2d pos = this.getRenderPos(point.getPosition());
        RenderUtil.drawRect(pos.x - 1, pos.y - 1, pos.x + 1, pos.y + 1, point.getColor().withAlpha(this.getAlpha()));
        this.pointsRendered++;
    }
    
    private boolean checkDepth(int depth) {
        return depth <= MAX_FEATURE_DEPTH;
    }

    public int getPointsRendered() {
        return pointsRendered;
    }

    public int getLinesRendered() {
        return linesRendered;
    }

    public int getPolygonsRendered() {
        return polygonsRendered;
    }

}
