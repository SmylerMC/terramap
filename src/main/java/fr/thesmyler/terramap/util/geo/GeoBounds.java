package fr.thesmyler.terramap.util.geo;

import java.util.Locale;


/**
 * A square bounding box, aligned with meridians and parallels.
 * Works across the antimeridian.
 * By convention, a {@link GeoBounds} which has a greater lower latitude than it's upper latitude is empty.
 * All empty bounding boxes are considered equals.
 * 
 * @author SmylerMC
 *
 */
public class GeoBounds {
    
    public static final GeoBounds WORLD = new GeoBounds(new GeoPoint(-180d, -90d), new GeoPoint(180d, 90d));
    public static final GeoBounds NORTHERN_HEMISPHERE = new GeoBounds(new GeoPoint(-180d, 0d), new GeoPoint(180d, 90d));
    public static final GeoBounds SOUTHERN_HEMISPHERE = new GeoBounds(new GeoPoint(-180d, -90d), new GeoPoint(180d, 0d));
    public static final GeoBounds EASTERN_HEMISPHERE = new GeoBounds(new GeoPoint(0d, -90d), new GeoPoint(180d, 90d));
    public static final GeoBounds WESTERN_HEMISPHERE = new GeoBounds(new GeoPoint(-180d, -90d), new GeoPoint(0d, 90d));
    public static final GeoBounds EMPTY = new GeoBounds(GeoPoint.NORTH_POLE, GeoPoint.SOUTH_POLE);

    public final GeoPoint lowerCorner, upperCorner;
    private final transient boolean crossesAntimeridian;
    private transient GeoBounds lowerPart, upperPart;

    /**
     * Constructs new bounds. These bounds are oriented,
     * which means that upperCorner and lowerCorner are not interchangeable.
     * 
     * @param lowerCorner - the lower corner
     * @param upperCorner - the upper corner
     */
    public GeoBounds(GeoPoint lowerCorner, GeoPoint upperCorner) {
        this.lowerCorner = lowerCorner;
        this.upperCorner = upperCorner;
        this.crossesAntimeridian = this.lowerCorner.longitude > this.upperCorner.longitude;
    }

    /**
     * @param point - a {@link GeoPoint}
     * 
     * @return whether or not these bounds contains the given point
     */
    public boolean contains(GeoPoint point) {
        if(this.lowerCorner.latitude > point.latitude || point.latitude > this.upperCorner.latitude) return false;
        if(this.crossesAntimeridian) {
            return point.longitude >= this.lowerCorner.longitude || point.longitude <= this.upperCorner.longitude;
        } else {
            double lon; // Deal with -180 / 180
            if(point.longitude == -180d && this.lowerCorner.longitude > -180d) {
                lon = 180d;
            } else if(point.longitude == 180d && this.upperCorner.longitude < 180d) {
                lon = -180d;
            } else {
                lon = point.longitude;
            }
            return this.lowerCorner.longitude <= lon && lon <= this.upperCorner.longitude;
        }
    }

    /**
     * @param other - an other {@link GeoBounds}
     * 
     * @return whether or not this box contains the other
     */
    public boolean contains(GeoBounds other) {
        if(this.crossesAntimeridian) {
            this.splitAtAntimeridian(); // Ensure this#lowerPart and this#upperPart are not null
            return (this.lowerPart.contains(other.lowerCorner) && this.lowerPart.contains(other.upperCorner))
                    ||
                    (this.upperPart.contains(other.lowerCorner) && this.upperPart.contains(other.upperCorner))
                    || 
                    (this.upperPart.contains(other.lowerCorner) && this.lowerPart.contains(other.upperCorner));
        } else {
            return this.contains(other.lowerCorner) && this.contains(other.upperCorner);
        }
    }

    /**
     * @param other - an other {@link GeoBounds}
     * 
     * @return whether or not the other box contains this one
     */
    public boolean within(GeoBounds other) {
        return other.contains(this);
    }

    /**
     * @param other - an other {@link GeoBounds}
     * 
     * @return whether or not this box and the other intersects
     */
    public boolean intersects(GeoBounds other) {
        if(other.isEmpty() || this.isEmpty()) return false;
        GeoBounds[] partsThis = this.splitAtAntimeridian();
        GeoBounds[] partsOther = other.splitAtAntimeridian();
        for(GeoBounds tpart: partsThis) for(GeoBounds opart: partsOther) {
            if(!(
                    tpart.lowerCorner.latitude > opart.upperCorner.latitude
                 || tpart.upperCorner.latitude < opart.lowerCorner.latitude
                 || tpart.lowerCorner.longitude > opart.upperCorner.longitude
                 || tpart.upperCorner.longitude < opart.lowerCorner.longitude))
                return true;
        }
        return false;
    }


    /**
     * @param other - an other {@link GeoBounds}
     * 
     * @return the intersections between this box and the other.
     * Most of the time the returned array will only contain zero or one intersection,
     * but there is a specific case where two intersections can exist if only one of the bounds crosses the antimeridian.
     */
    public GeoBounds[] intersections(GeoBounds other) {
        if(!this.intersects(other)) {
            return new GeoBounds[0];
        }
        if(this.crossesAntimeridian ^ other.crossesAntimeridian) {
            GeoBounds crossing;
            GeoBounds notCrossing;
            if(this.crossesAntimeridian) {
                crossing = this;
                notCrossing = other;
            } else {
                crossing = other;
                notCrossing = this;
            }
            crossing.splitAtAntimeridian();
            GeoBounds[] part1 = crossing.lowerPart.intersections(notCrossing);
            GeoBounds[] part2 = crossing.upperPart.intersections(notCrossing);
            boolean p1empty = part1.length == 0;
            boolean p2empty = part2.length == 0;
            if(!p1empty && !p2empty) {
                return new GeoBounds[] { part1[0], part2[0] };
            } else if(!p1empty) {
                return new GeoBounds[] { part1[0] };
            } else if(!p2empty) {
                return new GeoBounds[] { part2[0] };
            } else {
                throw new IllegalStateException("Bounds intersect but cannot find intersection!");
            }
        } else {
            double lowerLat = Math.max(this.lowerCorner.latitude, other.lowerCorner.latitude);
            double upperLat = Math.min(this.upperCorner.latitude, other.upperCorner.latitude);
            double lowerLon = Math.max(this.lowerCorner.longitude,  other.lowerCorner.longitude);
            double upperLon = Math.min(this.upperCorner.longitude, other.upperCorner.longitude);
            return new GeoBounds[] { new GeoBounds(new GeoPoint(lowerLon, lowerLat), new GeoPoint(upperLon, upperLat)) };
        }
    }
    
    /**
     * @param other - an other {@link GeoBounds}
     * 
     * @return the smallest bounds that contains all the intersections between these bounds and the others
     */
    public GeoBounds encompassingIntersection(GeoBounds other) {
        GeoBounds[] parts = this.intersections(other);
        if(parts.length == 0) return GeoBounds.EMPTY;
        if(parts.length == 1) return parts[0];
        if(parts.length == 2) return parts[0].smallestEncompassingSquare(parts[1]);
        throw new IllegalStateException(String.format("GeoBounds#intersections(GeoBounds) returned an array of %s objects (max is 2)", parts.length));
    }

    /**
     * @return whether or not this box crosses the antimeridian
     */
    public boolean crossesAntimeridian() {
        return this.crossesAntimeridian;
    }

    /**
     * Splits these bounds so they do not cross the antimeridian
     * 
     * @return an array of {@link GeoBounds} that does not cross the antimeridian
     */
    public GeoBounds[] splitAtAntimeridian() {
        if(!this.crossesAntimeridian) return new GeoBounds[] { this };
        if(this.lowerPart == null || this.upperPart == null) {
            this.lowerPart = new GeoBounds(this.lowerCorner.withLongitude(-180d), this.upperCorner.withLongitude(this.upperCorner.longitude));
            this.upperPart = new GeoBounds(this.lowerCorner, this.upperCorner.withLongitude(180d));

        }
        return new GeoBounds[] { this.lowerPart, this.upperPart };
    }

    /**
     * @param other - an other {@link GeoBounds}
     * 
     * @returns the smallest {@link GeoBounds} that contains both this bounds and the other
     */
    public GeoBounds smallestEncompassingSquare(GeoBounds other) {
        if(this.isEmpty()) return other;
        if(other.isEmpty()) return this;
        double lowerLat = Math.min(this.lowerCorner.latitude, other.lowerCorner.latitude);
        double upperLat = Math.max(this.upperCorner.latitude, other.upperCorner.latitude);
        double lowerLong, upperLong;
        if(this.crossesAntimeridian ^ other.crossesAntimeridian) { // Exactly one crosses
            GeoBounds crossing;
            GeoBounds notCrossing;
            if(this.crossesAntimeridian) {
                crossing = this;
                notCrossing = other;
            } else {
                crossing = other;
                notCrossing = this;
            }
            GeoBounds[] parts = crossing.splitAtAntimeridian(); // Ensure #lowerPart and #upperPart are not null
            if(parts[0].upperCorner.longitude >= notCrossing.lowerCorner.longitude) { // Intersects with lower part
                if(parts[0].upperCorner.longitude >= notCrossing.upperCorner.longitude) { // In lower part
                    lowerLong = crossing.lowerCorner.longitude;
                    upperLong = crossing.upperCorner.longitude;
                } else if(parts[1].lowerCorner.longitude <= notCrossing.upperCorner.longitude) { // In both lower and upper part
                    lowerLong = -180d;
                    upperLong = 180d;
                } else {
                    lowerLong = crossing.lowerCorner.longitude;
                    upperLong = notCrossing.upperCorner.longitude;
                }
            } else if(parts[1].lowerCorner.longitude <= notCrossing.upperCorner.longitude) { // Crosses with upper part
                if(parts[1].lowerCorner.longitude <= notCrossing.lowerCorner.longitude) { // In upper part
                    lowerLong = crossing.lowerCorner.longitude;
                    upperLong = crossing.upperCorner.longitude;
                } else { // Intersects with upper part
                    lowerLong = notCrossing.lowerCorner.longitude;
                    upperLong = crossing.upperCorner.longitude;
                }
            } else { // Does not intersect, will be choosing the smallest square
                double deltaLeft = notCrossing.lowerCorner.longitude - parts[0].upperCorner.longitude;
                double deltaRight = parts[1].lowerCorner.longitude - notCrossing.upperCorner.longitude;
                if(deltaLeft < deltaRight) {
                    lowerLong = crossing.lowerCorner.longitude;
                    upperLong = notCrossing.upperCorner.longitude;
                } else {
                    lowerLong = notCrossing.lowerCorner.longitude;
                    upperLong = crossing.upperCorner.longitude;
                }
            }
        } else if(this.crossesAntimeridian) { // Both cross
            lowerLong = Math.min(this.lowerCorner.longitude, other.lowerCorner.longitude);
            upperLong = Math.max(this.upperCorner.longitude, other.upperCorner.longitude);
        } else { // None cross
            GeoBounds lowest;
            GeoBounds highest;
            if(this.lowerCorner.longitude <= other.lowerCorner.longitude) {
                lowest = this;
            } else {
                lowest = other;
            }
            if(this.upperCorner.longitude >= other.upperCorner.longitude) {
                highest = this;
            } else {
                highest = other;
            }
            if(highest == lowest) { // Contains the other
                lowerLong = highest.lowerCorner.longitude;
                upperLong = highest.upperCorner.longitude;
            } else if(highest.upperCorner.longitude - lowest.lowerCorner.longitude <= 360d - (highest.lowerCorner.longitude - lowest.upperCorner.longitude)){
                // Smallest does not cross
                lowerLong = lowest.lowerCorner.longitude;
                upperLong = highest.upperCorner.longitude;
            } else { // Smallest crosses
                lowerLong = highest.lowerCorner.longitude;
                upperLong = lowest.upperCorner.longitude;
            }
        }
        return new GeoBounds(
                    new GeoPoint(lowerLong, lowerLat),
                    new GeoPoint(upperLong, upperLat)
                );
    }

    /**
     * @return whether these bounds are considered empty
     */
    public boolean isEmpty() {
        return this.lowerCorner.latitude > this.upperCorner.latitude;
    }

    @Override
    public boolean equals(Object other) {
        if(other == this) return true;
        if(!(other instanceof GeoBounds)) return false;
        GeoBounds otherSquare = (GeoBounds) other;
        if(this.isEmpty() && otherSquare.isEmpty()) return true;
        return otherSquare.lowerCorner.equals(this.lowerCorner) && otherSquare.upperCorner.equals(this.upperCorner);
    }

    @Override
    public int hashCode() {
        if(this.isEmpty() && this != GeoBounds.EMPTY) return GeoBounds.EMPTY.hashCode();
        return this.lowerCorner.hashCode() ^ this.upperCorner.hashCode();
    }
    
    @Override
    public String toString() {
        return String.format(Locale.US, "GeoBounds{lower=%s, upper=%s}", this.lowerCorner, this.upperCorner);
    }
    
    /**
     * Utility class to construct a {@link GeoBounds} that contains a list of points
     * 
     * @author SmylerMC
     *
     */
    public static class Builder {
        
        private double minLat = Double.NaN;
        private double minLon = Double.NaN;
        private double maxLat = Double.NaN;
        private double maxLon = Double.NaN;
        
        /**
         * Add a point that needs to be in the bounds
         * 
         * @param point - point to add
         * 
         * @return this builder, for chaining
         */
        public Builder addPoint(GeoPoint point) {
            if(Double.isNaN(this.minLat)) {
                this.minLat = this.maxLat = point.latitude;
                this.minLon = this.maxLon = point.longitude;
            } else {
                this.minLat = Math.min(this.minLat, point.latitude);
                this.maxLat = Math.max(this.maxLat, point.latitude);
                if(this.minLon > this.maxLon) { // Crosses antimeridian
                    if(point.longitude > this.maxLon && this.minLon > point.longitude) { // Not inside
                        double distanceLeft = point.longitude - this.maxLon;
                        double distanceRight = this.minLon - point.longitude;
                        if(distanceLeft >= distanceRight) {
                            this.minLon = point.longitude;
                        } else {
                            this.maxLon = point.longitude;
                        }
                    }
                } else if(this.minLon > point.longitude) { // Does not cross the antimeridian, West side
                    double distanceLeft = this.minLon - point.longitude;
                    double distanceRight = 360d - this.maxLon + point.longitude;
                    if(distanceLeft >= distanceRight) {
                        this.maxLon = point.longitude;
                    } else {
                        this.minLon = point.longitude;
                    }
                } else if(this.maxLon < point.longitude) { // Does not cross the antimeridian, East side
                    double distanceLeft = 360d - point.longitude + this.minLon;
                    double distanceRight = point.longitude - this.maxLon;
                    if(distanceLeft >= distanceRight) {
                        this.maxLon = point.longitude;
                    } else {
                        this.minLon = point.longitude;
                    }
                }
            }
            return this;
        }
        
        public Builder addAllPoints(Iterable<GeoPoint> iterable) {
            for(GeoPoint point: iterable) this.addPoint(point);
            return this;
        }
        
        /**
         * Rests this builder (forgets about all previously added points)
         */
        public void reset() {
            this.minLat = this.minLon = this.maxLat = this.maxLon = Double.NaN;
        }
        
        /**
         * Constructs a {@link GeoBounds} that contains all the points passed to the builder via {@link #addPoint(GeoPoint)}
         * and resets this builder.
         * 
         * @return bounds that contains all points
         * 
         * @see #reset()
         */
        public GeoBounds build() {
            if(Double.isNaN(this.minLat)) return GeoBounds.EMPTY;
            GeoBounds bounds = new GeoBounds(new GeoPoint(this.minLon, this.minLat), new GeoPoint(this.maxLon, this.maxLat));
            this.reset();
            return bounds;
        }
        
    }

}
