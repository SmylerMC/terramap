package fr.thesmyler.terramap.maps.vector.simplified;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.thesmyler.terramap.maps.vector.features.LineString;
import fr.thesmyler.terramap.maps.vector.features.Point;
import fr.thesmyler.terramap.maps.vector.features.Polygon;
import fr.thesmyler.terramap.maps.vector.features.VectorFeature;
import fr.thesmyler.terramap.util.geo.GeoPoint;

/**
 * Helper class to construct simplified vector features.
 * It takes care of providing the right feature depending on the number of lines, polygons and points that were passed to it.
 * 
 * @author SmylerMC
 *
 */
//TODO Test unit
public class SimplifiedFeatureBuilder {

    private List<GeoPoint> points = new ArrayList<>();
    private List<GeoPoint[]> lines = new ArrayList<>();
    private List<GeoPoint[][]> polygons = new ArrayList<>();

    private List<GeoPoint> builingLine = new ArrayList<>();
    private List<GeoPoint> buildingRing = new ArrayList<>();
    private List<GeoPoint[]> buildingPolygon = new ArrayList<>();

    private final VectorFeature original;

    private boolean allowDemote = true;
    private boolean splitAtEdges = false;

    /**
     * @param original - the original feature bening simplified
     */
    public SimplifiedFeatureBuilder(VectorFeature original) {
        this.original = original;
    }
    
    /**
     * @return the simplified feature
     */
    public VectorFeature build() {
        this.endLine();
        this.endPolygonRing();
        this.endPolygon();
        if(this.allowDemote) {
            this.tryDemotingPolygons();
            this.tryDemotingLines();
        }
        int pointCount = this.points.size();
        int lineCount = this.lines.size();
        int polygonCount = this.polygons.size();
        if(pointCount == 0 && lineCount == 0 && polygonCount == 0) {
            return null;
        } else if(pointCount == 1 && lineCount == 0 && polygonCount == 0) {
            return this.makePoint(this.points.get(0));
        } else if(pointCount > 0 && lineCount == 0 && polygonCount == 0) {
            throw new IllegalStateException("Unexpected simplification!");
        } else if(pointCount == 0 && lineCount == 1 && polygonCount == 0) {
            return this.makeLineString(this.lines.get(0));
        } else if(pointCount == 0 && lineCount > 0 && polygonCount == 0) {
            LineString[] lines = new LineString[lineCount];
            for(int i=0; i<lineCount; i++) {
                lines[i] = this.makeLineString(this.lines.get(i));
            }
            return new SimplifiedMultiLineString(lines, original, splitAtEdges);
        } else if(pointCount == 0 && lineCount == 0 && polygonCount == 1) {
            return this.makePolygon(this.polygons.get(0));
        } else if(pointCount == 0 && lineCount == 0 && polygonCount > 0) {
            Polygon[] polys = new Polygon[polygonCount];
            for(int i=0; i<polygonCount; i++) {
                polys[i] = this.makePolygon(this.polygons.get(i));
            }
            return new SimplifiedMultiPolygon(polys, original, splitAtEdges);
        } else {
            VectorFeature[] features = new VectorFeature[pointCount + lineCount + polygonCount];
            int i = 0;
            for(GeoPoint point: this.points) features[i++] = this.makePoint(point);
            for(GeoPoint[] line: this.lines) features[i++] = this.makeLineString(line);
            for(GeoPoint[][] polygons: this.polygons) features[i++] = this.makePolygon(polygons);
            return new SimplifiedMultiGeometry(features, this.original, this.splitAtEdges);
        }
    }
    
    private Point makePoint(GeoPoint location) {
        if(this.original instanceof Point) {
            Point point = (Point) this.original;
            return new SimplifiedPoint(location, point, this.splitAtEdges);
        } else if(this.original instanceof LineString) {
            LineString line = (LineString) this.original;
            return new SimplifiedPointLineString(location, line, this.splitAtEdges);
        } else if(this.original instanceof Polygon) {
            Polygon polygon = (Polygon) this.original;
            return new SimplifiedPointPolygon(location, polygon, this.splitAtEdges);
        } else {
            throw new IllegalStateException("Unexpected vector feature type!");
        }
    }
    
    private LineString makeLineString(GeoPoint[] points) {
        if(this.original instanceof LineString) {
            LineString line = (LineString) this.original;
            return new SimplifiedLineString(points, line, this.splitAtEdges);
        } else if(this.original instanceof Polygon) {
            Polygon polygon = (Polygon) this.original;
            return new SimplifiedLineStringPolygon(points, polygon, this.splitAtEdges);
        } else {
            throw new IllegalStateException("Unexpected vector feature type!");
        }
    }
    
    private Polygon makePolygon(GeoPoint[][] rings) {
        if(this.original instanceof Polygon) {
            Polygon polygon = (Polygon) this.original;
            return new SimplifiedPolygon(rings, polygon, this.splitAtEdges);
        } else {
            throw new IllegalStateException("Unexpected vector feature type!");
        }
    }
    
    private void tryDemotingPolygons() {
        for(int i = this.polygons.size() - 1; i >= 0; i--) {
            GeoPoint[][] polygon = this.polygons.get(i);
            if(polygon.length > 0) {
                GeoPoint[] outerRing = polygon[0];
                if(outerRing.length < 4) {
                    if(outerRing.length == 3) {
                        // Make it a line
                        this.lines.add(new GeoPoint[] {outerRing[0], outerRing[1]});
                    } else if(outerRing.length == 2) {
                        // Make it a point
                        this.points.add(outerRing[0]);
                    }
                    this.polygons.remove(i);
                }
                List<GeoPoint[]> innerRings = new ArrayList<>();
                for(int j = 1; i<polygon.length; j++) { // Check rings' validity
                    GeoPoint[] ring = polygon[j];
                    if(ring.length > 3) innerRings.add(ring);
                }
                if(innerRings.size() != polygon.length - 1) {
                    innerRings.add(0, outerRing);
                    this.polygons.set(i, innerRings.toArray(new GeoPoint[0][0]));
                }
            } else {
                this.polygons.remove(i);
            }
        }
    }
    
    private void tryDemotingLines() {
        for(int i = this.lines.size() - 1; i >= 0; i--) {
            GeoPoint[] line = this.lines.get(i);
            if(line.length > 0) {
                if(line.length == 1) {
                    this.lines.remove(i);
                    this.points.add(line[0]);
                }
            } else {
                this.lines.remove(i);
            }
        }
    }

    public SimplifiedFeatureBuilder addPoint(GeoPoint point) {
        this.points.add(point);
        return this;
    }

    public SimplifiedFeatureBuilder addPoints(GeoPoint... points) {
        Collections.addAll(this.points, points);
        return this;
    }

    public SimplifiedFeatureBuilder addLine(GeoPoint... line) {
        this.lines.add(line);
        return this;
    }

    public SimplifiedFeatureBuilder addLines(GeoPoint[]... lines) {
        Collections.addAll(this.lines, lines);
        return this;
    }

    public SimplifiedFeatureBuilder addPointToLine(GeoPoint point) {
        this.builingLine.add(point);
        return this;
    }

    public SimplifiedFeatureBuilder addPointsToLine(GeoPoint... points) {
        Collections.addAll(this.builingLine, points);
        return this;
    }

    public SimplifiedFeatureBuilder endLine() {
        if(this.builingLine.size() > 0) {
            GeoPoint[] line = this.builingLine.toArray(new GeoPoint[0]);
            this.builingLine.clear();
            this.addLine(line);
        }
        return this;
    }

    public SimplifiedFeatureBuilder addPolygon(GeoPoint[][] points) {
        this.polygons.add(points);
        return this;
    }

    public SimplifiedFeatureBuilder addPointToPolygon(GeoPoint point) {
        this.buildingRing.add(point);
        return this;
    }

    public SimplifiedFeatureBuilder endPolygonRing() {
        if(this.buildingRing.size() > 0) {
            GeoPoint[] ring = this.buildingRing.toArray(new GeoPoint[0]);
            this.buildingRing.clear();
            this.buildingPolygon.add(ring);
        }
        return this;
    }

    public SimplifiedFeatureBuilder endPolygon() {
        if(this.buildingPolygon.size() > 0) {
            GeoPoint[][] polygon = this.buildingPolygon.toArray(new GeoPoint[0][0]);
            this.buildingPolygon.clear();
            this.addPolygon(polygon);
        }
        return this;
    }
    
    public SimplifiedFeatureBuilder setAllowDemote(boolean yesNo) {
        this.allowDemote = yesNo;
        return this;
    }

    public SimplifiedFeatureBuilder setSplitAtEdges() {
        this.splitAtEdges = true;
        return this;
    }
}
