package fr.thesmyler.terramap.maps.vector.simplified;

import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.maps.vector.features.LineString;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.WebMercatorUtil;
import fr.thesmyler.terramap.util.math.DoubleRange;
import fr.thesmyler.terramap.util.math.Vec2d;


public abstract class SimplifiedVectorFeature implements VectorFeature {

    private final boolean splitAtEdges;

    SimplifiedVectorFeature(boolean splitAtEdges) {
        this.splitAtEdges = splitAtEdges;
    }

    public abstract VectorFeature getOriginal();

    public boolean isSplitAtEdges() {
        return splitAtEdges;
    }

    @Override
    public String getDisplayName() {
        return this.getOriginal().getDisplayName();
    }

    @Override
    public DoubleRange getZoomRange() {
        return this.getOriginal().getZoomRange();
    }

    @Override
    public UUID uid() {
        return this.getOriginal().uid();
    }

    @Override
    public Map<String, String> getMetadata() {
        return this.getOriginal().getMetadata();
    }

    public static VectorFeature simplify(VectorFeature feature, GeoBounds viewPort, double zoom, float distance) {
        if(!feature.getZoomRange().matches(zoom)) return null;
        if(Thread.interrupted()) return null;
        if(!viewPort.intersects(feature.bounds())) return null;
        if(Thread.interrupted()) return null;
        if(feature instanceof LineString) return simplifyLine((LineString) feature, viewPort, zoom, distance);
        //TODO Simplify more
        return feature;
    }

    //TODO Test
    private static VectorFeature simplifyLine(LineString line, GeoBounds viewPort, double zoom, float distance) {
        SimplifiedFeatureBuilder builder = new SimplifiedFeatureBuilder(line);
        GeoPoint[] points = line.getPoints();
        if(points.length <= 0) return null;
        GeoPoint lastPoint = points[0];
        Vec2d lastPos = WebMercatorUtil.fromGeo(points[0], zoom);
        boolean in = viewPort.contains(lastPoint);
        if(in) builder.addPointToLine(lastPoint);
        for(int i=1; i < points.length; i++) {
            GeoPoint point = points[i];
            Vec2d pos = WebMercatorUtil.fromGeo(point, zoom);
            int interCount = countEdgeIntersections(lastPoint, point, viewPort);
            boolean isLast = i == points.length - 1;
            if(in && interCount == 0 && (pos.substract(lastPos).taxicabNorm() >= distance || isLast)) {
                builder.addPointToLine(point);
                lastPos = pos;
                lastPoint = point;
            } else if(in && interCount > 0) {
                builder.addPointToLine(point);
                builder.endLine();
                lastPos = pos;
                lastPoint = point;
            } else if(!in){
                if(interCount > 0) {
                    builder.addPointToLine(lastPoint);
                    builder.addPointToLine(point);
                }
                lastPos = pos;
                lastPoint = point;
            }
            in = viewPort.contains(point);
        }
        return builder.endLine().build();
    }

    private static int countEdgeIntersections(GeoPoint point1, GeoPoint point2, GeoBounds bounds) {
        double dLon = point2.longitude - point1.longitude;
        double dLat = point2.latitude - point1.latitude;
        double tLeft = (bounds.lowerCorner.longitude - point1.longitude) / dLon;
        double tRight = (bounds.upperCorner.longitude - point1.longitude) / dLon;
        double tTop = (bounds.upperCorner.latitude - point1.latitude) / dLat;
        double tBottom = (bounds.lowerCorner.latitude - point1.latitude) / dLat;
        int crosses = 0;
        if(0 <= tLeft && tLeft <= 1) {
            double yCross = tLeft*dLat + point1.latitude;
            crosses += bounds.lowerCorner.latitude <= yCross && yCross <= bounds.upperCorner.latitude ? 1: 0;
        }
        if(0 <= tRight && tRight <= 1) {
            double yCross = tRight*dLat + point1.latitude;
            crosses += bounds.lowerCorner.latitude <= yCross && yCross <= bounds.upperCorner.latitude ? 1: 0;
        }
        if(0 <= tTop && tTop <= 1) {
            double xCross = tTop*dLon + point1.longitude;
            if(bounds.crossesAntimeridian()) {
                crosses += xCross < bounds.upperCorner.longitude || xCross > bounds.lowerCorner.longitude ? 1: 0;
            } else {
                crosses += bounds.lowerCorner.longitude < xCross && xCross < bounds.upperCorner.longitude ? 1: 0;
            }
        }
        if(0 <= tBottom && tBottom <= 1) {
            double xCross = tBottom*dLon + point1.longitude;
            if(bounds.crossesAntimeridian()) {
                crosses += xCross < bounds.upperCorner.longitude || xCross > bounds.lowerCorner.longitude ? 1: 0;
            } else {
                crosses += bounds.lowerCorner.longitude < xCross && xCross < bounds.upperCorner.longitude ? 1: 0;
            }
        }
        return crosses;
    }

}
