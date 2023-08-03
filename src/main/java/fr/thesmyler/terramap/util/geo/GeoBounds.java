package fr.thesmyler.terramap.util.geo;

import java.util.Locale;

import static java.lang.Math.max;
import static java.lang.Math.min;


/**
 * A square bounding box, aligned with meridians and parallels.
 * Works across the antimeridian.
 * By convention, a {@link GeoBounds} which has a greater lower latitude than its upper latitude is empty.
 * All empty bounding boxes are considered equals.
 * 
 * @author SmylerMC
 *
 */
public class GeoBounds {
    
    public static final GeoBounds WORLD = new GeoBounds(new GeoPointImmutable(-180d, -90d), new GeoPointImmutable(180d, 90d));
    public static final GeoBounds NORTHERN_HEMISPHERE = new GeoBounds(new GeoPointImmutable(-180d, 0d), new GeoPointImmutable(180d, 90d));
    public static final GeoBounds SOUTHERN_HEMISPHERE = new GeoBounds(new GeoPointImmutable(-180d, -90d), new GeoPointImmutable(180d, 0d));
    public static final GeoBounds EASTERN_HEMISPHERE = new GeoBounds(new GeoPointImmutable(0d, -90d), new GeoPointImmutable(180d, 90d));
    public static final GeoBounds WESTERN_HEMISPHERE = new GeoBounds(new GeoPointImmutable(-180d, -90d), new GeoPointImmutable(0d, 90d));
    public static final GeoBounds EMPTY = new GeoBounds(GeoPointImmutable.NORTH_POLE, GeoPointImmutable.SOUTH_POLE);

    public final GeoPointImmutable lowerCorner, upperCorner;
    private final transient boolean crossesAntimeridian;
    private transient GeoBounds lowerPart, upperPart;

    /**
     * Constructs new bounds. These bounds are oriented,
     * which means that upperCorner and lowerCorner are not interchangeable.
     * 
     * @param lowerCorner the lower corner
     * @param upperCorner the upper corner
     */
    public GeoBounds(GeoPointImmutable lowerCorner, GeoPointImmutable upperCorner) {
        this.lowerCorner = lowerCorner;
        this.upperCorner = upperCorner;
        this.crossesAntimeridian = this.lowerCorner.longitude() > this.upperCorner.longitude();
    }

    /**
     * @param point a {@link GeoPointImmutable}
     * 
     * @return whether these bounds contain the given point
     */
    public boolean contains(GeoPoint<?> point) {
        double pointLat = point.latitude();
        double pointLong = point.longitude();
        double lowerLong = this.lowerCorner.longitude();
        double upperLong = this.upperCorner.longitude();
        if(this.lowerCorner.latitude() > pointLat || pointLat > this.upperCorner.latitude()) return false;
        if(this.crossesAntimeridian) {
            return pointLong >= lowerLong || pointLong <= upperLong;
        } else {
            // Deal with -180 / 180
            if(pointLong == -180d && lowerLong > -180d) {
                pointLong = 180d;
            } else if(pointLong == 180d && upperLong < 180d) {
                pointLong = -180d;
            }
            return lowerLong <= pointLong && pointLong <= upperLong;
        }
    }

    /**
     * @param other another {@link GeoBounds}
     * 
     * @return whether this box contains the other
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
     * @param other another {@link GeoBounds}
     * 
     * @return whether the other box contains this one
     */
    public boolean within(GeoBounds other) {
        return other.contains(this);
    }

    /**
     * @param other another {@link GeoBounds}
     * 
     * @return whether this box and the other intersects
     */
    public boolean intersects(GeoBounds other) {
        return this.contains(other.lowerCorner) || this.contains(other.upperCorner) || other.contains(this.lowerCorner) || other.contains(this.upperCorner);
    }


    /**
     * @param other another {@link GeoBounds}
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
            double lowerLat = max(this.lowerCorner.latitude(), other.lowerCorner.latitude());
            double upperLat = min(this.upperCorner.latitude(), other.upperCorner.latitude());
            double lowerLon = max(this.lowerCorner.longitude(),  other.lowerCorner.longitude());
            double upperLon = min(this.upperCorner.longitude(), other.upperCorner.longitude());
            return new GeoBounds[] { new GeoBounds(new GeoPointImmutable(lowerLon, lowerLat), new GeoPointImmutable(upperLon, upperLat)) };
        }
    }
    
    /**
     * @param other another {@link GeoBounds}
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
     * @return whether this box crosses the antimeridian
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
            this.lowerPart = new GeoBounds(this.lowerCorner.withLongitude(-180d), this.upperCorner.withLongitude(this.upperCorner.longitude()));
            this.upperPart = new GeoBounds(this.lowerCorner, this.upperCorner.withLongitude(180d));

        }
        return new GeoBounds[] { this.lowerPart, this.upperPart };
    }

    /**
     * @param other - an other {@link GeoBounds}
     * 
     * @return the smallest {@link GeoBounds} that contains both this bounds and the other
     */
    public GeoBounds smallestEncompassingSquare(GeoBounds other) {
        if(this.isEmpty()) return other;
        if(other.isEmpty()) return this;
        double lowerLat = min(this.lowerCorner.latitude(), other.lowerCorner.latitude());
        double upperLat = max(this.upperCorner.latitude(), other.upperCorner.latitude());
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
            if(parts[0].upperCorner.longitude() >= notCrossing.lowerCorner.longitude()) { // Intersects with lower part
                if(parts[0].upperCorner.longitude() >= notCrossing.upperCorner.longitude()) { // In lower part
                    lowerLong = crossing.lowerCorner.longitude();
                    upperLong = crossing.upperCorner.longitude();
                } else if(parts[1].lowerCorner.longitude() <= notCrossing.upperCorner.longitude()) { // In both lower and upper part
                    lowerLong = -180d;
                    upperLong = 180d;
                } else {
                    lowerLong = crossing.lowerCorner.longitude();
                    upperLong = notCrossing.upperCorner.longitude();
                }
            } else if(parts[1].lowerCorner.longitude() <= notCrossing.upperCorner.longitude()) { // Crosses with upper part
                if(parts[1].lowerCorner.longitude() <= notCrossing.lowerCorner.longitude()) { // In upper part
                    lowerLong = crossing.lowerCorner.longitude();
                    upperLong = crossing.upperCorner.longitude();
                } else { // Intersects with upper part
                    lowerLong = notCrossing.lowerCorner.longitude();
                    upperLong = crossing.upperCorner.longitude();
                }
            } else { // Does not intersect, will be choosing the smallest square
                double deltaLeft = notCrossing.lowerCorner.longitude() - parts[0].upperCorner.longitude();
                double deltaRight = parts[1].lowerCorner.longitude() - notCrossing.upperCorner.longitude();
                if(deltaLeft < deltaRight) {
                    lowerLong = crossing.lowerCorner.longitude();
                    upperLong = notCrossing.upperCorner.longitude();
                } else {
                    lowerLong = notCrossing.lowerCorner.longitude();
                    upperLong = crossing.upperCorner.longitude();
                }
            }
        } else if(this.crossesAntimeridian) { // Both cross
            lowerLong = min(this.lowerCorner.longitude(), other.lowerCorner.longitude());
            upperLong = max(this.upperCorner.longitude(), other.upperCorner.longitude());
        } else { // None cross
            GeoBounds lowest;
            GeoBounds highest;
            if(this.lowerCorner.longitude() <= other.lowerCorner.longitude()) {
                lowest = this;
            } else {
                lowest = other;
            }
            if(this.upperCorner.longitude() >= other.upperCorner.longitude()) {
                highest = this;
            } else {
                highest = other;
            }
            if(highest == lowest) { // Contains the other
                lowerLong = highest.lowerCorner.longitude();
                upperLong = highest.upperCorner.longitude();
            } else if(highest.upperCorner.longitude() - lowest.lowerCorner.longitude() <= 360d - (highest.lowerCorner.longitude() - lowest.upperCorner.longitude())){
                // Smallest does not cross
                lowerLong = lowest.lowerCorner.longitude();
                upperLong = highest.upperCorner.longitude();
            } else { // Smallest crosses
                lowerLong = highest.lowerCorner.longitude();
                upperLong = lowest.upperCorner.longitude();
            }
        }
        return new GeoBounds(
                    new GeoPointImmutable(lowerLong, lowerLat),
                    new GeoPointImmutable(upperLong, upperLat)
                );
    }

    /**
     * @return whether these bounds are considered empty
     */
    public boolean isEmpty() {
        return this.lowerCorner.latitude() > this.upperCorner.latitude();
    }

    /**
     * Clamps the latitude and longitude to make sure they are in the given bounds.
     * Both are clamped independently.
     * The longitude is clamped to the nearest side of the bounding box,
     * even though it might be on the other side of the antimeridian.
     *
     * @param   point a point to clamp
     * @return  the result of the clamping operation, applied on the input {@link GeoPoint}
     *
     * @param <T>   the type of {@link GeoPoint}
     */
    public <T extends GeoPoint<T>> GeoPoint<T> clamp(GeoPoint<T> point) {

        if (this.isEmpty()) {
            throw new UnsupportedOperationException("Cannot clamp within empty bounds");
        }

        double longitude = point.longitude();
        double latitude = point.latitude();

        if (latitude < this.lowerCorner.latitude()) {
            point = point.withLatitude(this.lowerCorner.latitude());
        } else if (latitude > this.upperCorner.latitude()) {
            point = point.withLatitude(this.upperCorner.latitude());
        }

        if (this.crossesAntimeridian) {
            if (longitude > this.upperCorner.longitude() && longitude < this.lowerCorner.longitude()) {
                if (longitude - this.upperCorner.longitude() < this.lowerCorner.longitude() - longitude) {
                    point = point.withLongitude(this.upperCorner.longitude());
                } else {
                    point = point.withLongitude(this.lowerCorner.longitude());
                }
            }
        } else if (longitude < this.lowerCorner.longitude() ) {
            if (this.lowerCorner.longitude() - longitude < longitude + 360d - this.upperCorner.longitude()) {
                point = point.withLongitude(this.lowerCorner.longitude());
            } else {
                point = point.withLongitude(this.upperCorner.longitude());
            }
        } else if (longitude > this.upperCorner.longitude()) {
            if (longitude - this.upperCorner.longitude() < this.lowerCorner.longitude() - longitude + 360d) {
                point = point.withLongitude(this.upperCorner.longitude());
            } else {
                point = point.withLongitude(this.lowerCorner.longitude());
            }
        }

        return point;
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
        return String.format(Locale.US, "GeoBoundsSquare{lower=%s, upper=%s}", this.lowerCorner, this.upperCorner);
    }

}