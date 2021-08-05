package fr.thesmyler.terramap.gui.widgets.map.layer.vector;

import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

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
import fr.thesmyler.terramap.maps.vector.simplified.SimplifiedVectorFeature;
import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.math.Vec2d;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.profiler.Profiler;

public abstract class VectorLayer extends MapLayer {

    private static final int MAX_FEATURE_DEPTH = 50;

    private int pointsRendered = 0;
    private int linesRendered = 0;
    private int linePointsRendered = 0;
    private int polygonsRendered = 0;
    private int polygonPointsRendered = 0;

    private final ExecutorService executor = Executors.newSingleThreadExecutor();
    private final ConcurrentMap<UUID, VectorFeature> features = new ConcurrentHashMap<>();
    private final ConcurrentMap<UUID, Future<VectorFeature>> loading = new ConcurrentHashMap<>();

    private GeoBounds lastBounds = GeoBounds.EMPTY;
    private double lastZoom = Double.MIN_VALUE;

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
        double zoom = this.getZoom();

        this.pointsRendered = 0;
        this.linesRendered = 0;
        this.linePointsRendered = 0;
        this.polygonsRendered = 0;
        this.polygonPointsRendered = 0;

        if(!this.lastBounds.equals(renderBounds)) {
            this.load(renderBounds, this.lastBounds, zoom, this.lastZoom);
        }
        this.removeLoaded();
        GlStateManager.pushMatrix();
        this.applyRotationGl(x, y);
        this.features.forEach((uid, feature) -> this.drawFeature(feature, 0));        
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
        this.lastBounds = renderBounds;
        this.lastZoom = zoom;
    }

    public abstract Iterable<CompletableFuture<Iterable<VectorFeature>>> getVisibleFeatures(GeoBounds bounds);

    private void load(GeoBounds currentBounds, GeoBounds formerBounds, double currentZoom, double formerZoom) {
        this.loading.forEach((u, f) -> f.cancel(true));
        this.loading.clear();
        for(CompletableFuture<Iterable<VectorFeature>> future: this.getVisibleFeatures(currentBounds)) {
            future.thenAcceptAsync(features -> {
                for(VectorFeature feature: features) {
                    this.simplifyAsync(feature, currentBounds, currentZoom, false);
                }
            });
        }
        this.reloadCurrentFeatures(currentBounds, currentZoom);
    }
    
    private void reloadCurrentFeatures(GeoBounds bounds, double zoom) {
        this.features.forEach((uuid, feature) -> {
            this.simplifyAsync(feature, bounds, zoom, true);
        });
    }
    
    private void simplifyAsync(VectorFeature feature, GeoBounds bounds, double zoom, boolean replaceIfExists) {
        UUID uuid = feature.uid();
        @SuppressWarnings("unchecked")
        Future<VectorFeature> f = (Future<VectorFeature>) this.executor.submit(() -> {
            if(!replaceIfExists && this.features.containsKey(uuid)) return;
            VectorFeature simplifiable;
            if(feature instanceof SimplifiedVectorFeature) {
                simplifiable = ((SimplifiedVectorFeature) feature).getOriginal();
            } else {
                simplifiable = feature;
            }
            VectorFeature simplified = SimplifiedVectorFeature.simplify(simplifiable, bounds, zoom, 5f);
            if(simplified != null) {
                if(replaceIfExists) {
                    this.features.put(uuid, simplified);
                } else {
                    this.features.putIfAbsent(uuid, simplified);
                }
            } else if(replaceIfExists) {
                this.features.remove(uuid);
            }
        });
        this.loading.put(uuid, f);
    }
    
    private void removeLoaded() {
        this.loading.forEach((uuid, future) -> {
            if(future.isDone()) this.loading.remove(uuid);
        });
    }

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
        //TODO draw Polygons
        this.polygonsRendered++;
    }

    private void drawLineString(LineString line) {
        GeoPoint[] geo = line.getPoints();
        double[] cart = new double[geo.length*2];
        for(int i=0; i<geo.length; i++) {
            Vec2d pos = this.getRenderPos(geo[i]);
            cart[i*2] = pos.x;
            cart[i*2 + 1] = pos.y;
            this.linePointsRendered++;
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

    /**
     * @return the number of points that were rendered the last time this layer was drawn
     */
    public int getPointsRendered() {
        return pointsRendered;
    }

    /**
     * @return the number of lines that were rendered the last time this layer was drawn
     */
    public int getLinesRendered() {
        return linesRendered;
    }
    
    /**
     * @return the number of vertices used to draw lines the last time this layer was drawn
     */
    public int getLinePointsRendered() {
        return this.linePointsRendered;
    }

    /**
     * @return the number of polygons that were rendered the last time this layer was drawn
     */
    public int getPolygonsRendered() {
        return polygonsRendered;
    }
    
    /**
     * @return the number of vertices used to draw polygons the last time this layer was drawn
     */
    public int getPolygonPointsRendered() {
        return this.polygonPointsRendered;
    }
    
    /**
     * @return the number of geometry update tasks currently waiting to be processed or being processed
     */
    public int getLoadingCount() {
        return this.loading.size();
    }

}
