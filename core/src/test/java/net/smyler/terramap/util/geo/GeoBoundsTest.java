package net.smyler.terramap.util.geo;

import java.util.Map;

import net.smyler.terramap.util.collections.HashMapBuilder;
import net.smyler.terramap.util.geo.GeoBounds;
import net.smyler.terramap.util.geo.GeoPointImmutable;
import org.junit.jupiter.api.Test;

import static net.smyler.terramap.util.geo.GeoBounds.*;
import static net.smyler.terramap.util.geo.GeoPointImmutable.NORTH_POLE;
import static org.junit.jupiter.api.Assertions.*;

public class GeoBoundsTest {
    
    @Test
    public void constructionAndCrossesAntimeridianTest() {
        Map<GeoBounds, Boolean> squares = new HashMapBuilder<GeoBounds, Boolean>()
                .put(GeoBounds.WORLD, false)
                .put(GeoBounds.SOUTHERN_HEMISPHERE, false)
                .put(GeoBounds.NORTHERN_HEMISPHERE, false)
                .put(GeoBounds.WESTERN_HEMISPHERE, false)
                .put(GeoBounds.EASTERN_HEMISPHERE, false)
                .put(new GeoBounds(GeoPointImmutable.ORIGIN, NORTH_POLE.withLongitude(-1d)), true)
            .build();
        for(GeoBounds square: squares.keySet()) {
            boolean crossesAntimeridian = squares.get(square);
            assertEquals(crossesAntimeridian, square.crossesAntimeridian());
            GeoBounds[] split = square.splitAtAntimeridian();
            assertEquals(crossesAntimeridian ? 2: 1, split.length);
            if(split.length == 1) {
                assertEquals(square, split[0]);
            } else {
                assertFalse(split[0].crossesAntimeridian());
                assertFalse(split[1].crossesAntimeridian());
                GeoBounds rebuilt = split[0].smallestEncompassingSquare(split[1]);
                assertEquals(square, rebuilt);
            }
        }
    }

    @Test
    public void splitAtAntimeridianTest() {
        GeoBounds sq = new GeoBounds(
                new GeoPointImmutable(150d, -60d),
                new GeoPointImmutable(-15d, 56d)
        );
        GeoBounds[] split = sq.splitAtAntimeridian();
        assertEquals(2, split.length);
        assertEquals(-180d, split[0].lowerCorner.longitude(), 0d);
        assertEquals(-15d, split[0].upperCorner.longitude(), 0d);
        assertEquals(-60d, split[0].lowerCorner.latitude(), 0d);
        assertEquals(56d, split[0].upperCorner.latitude(), 0d);
        assertEquals(150d, split[1].lowerCorner.longitude(), 0d);
        assertEquals(180d, split[1].upperCorner.longitude(), 0d);
        assertEquals(-60d, split[1].lowerCorner.latitude(), 0d);
        assertEquals(56d, split[1].upperCorner.latitude(), 0d);
    }
    
    @Test
    public void encompassingOtherSquareTest() {
        GeoPointImmutable rightPosMid = new GeoPointImmutable(35d, 45d);
        GeoPointImmutable rightUp = new GeoPointImmutable(35d, 85d);
        GeoPointImmutable rightNegMid = new GeoPointImmutable(35d, -50d);
        GeoPointImmutable rightDown = new GeoPointImmutable(35d, -88d);
        GeoPointImmutable farRightPosMid = new GeoPointImmutable(158d, 45d);
        GeoPointImmutable farRightUp = new GeoPointImmutable(158d, 85d);
        GeoPointImmutable farRightNegMid = new GeoPointImmutable(158d, -50d);
        GeoPointImmutable farRightDown = new GeoPointImmutable(158d, -88d);
        GeoPointImmutable leftPosMid = new GeoPointImmutable(-40d, 45d);
        GeoPointImmutable leftUp = new GeoPointImmutable(-40d, 85d);
        GeoPointImmutable leftNegMid = new GeoPointImmutable(-40d, -50d);
        GeoPointImmutable leftDown = new GeoPointImmutable(-40d, -88d);
        GeoPointImmutable farLeftPosMid = new GeoPointImmutable(-140d, 45d);
        GeoPointImmutable farLeftUp = new GeoPointImmutable(-140d, 85d);
        GeoPointImmutable farLeftNegMid = new GeoPointImmutable(-140d, -50d);
        GeoPointImmutable farLeftDown = new GeoPointImmutable(-140d, -88d);
        GeoBounds[][] tests = {
                {
                    // Square 1 contains square 2, none cross
                    new GeoBounds(farLeftDown, rightUp),
                    new GeoBounds(leftDown, rightPosMid),
                    new GeoBounds(farLeftDown, rightUp) // Expected result
                },
                {
                    // Squares intersect, none cross
                    new GeoBounds(farLeftDown, rightUp),
                    new GeoBounds(leftDown, farRightPosMid),
                    new GeoBounds(farLeftDown, rightUp.withLongitude(farRightPosMid.longitude()))
                },
                {
                    // Squares do not intersect, none cross, smallest encompassing does not cross
                    new GeoBounds(leftDown, GeoPointImmutable.ORIGIN),
                    new GeoBounds(rightDown, farRightUp),
                    new GeoBounds(leftDown, farRightUp)
                },
                {
                    // Squares do not intersect, none cross, smallest encompassing crosses
                    new GeoBounds(farLeftDown.withLongitude(-180d), farLeftUp),
                    new GeoBounds(rightDown, farRightUp),
                    new GeoBounds(rightDown, farLeftUp)
                },
                {
                    // Both squares cross
                    new GeoBounds(rightDown, farLeftUp),
                    new GeoBounds(farRightNegMid, leftPosMid),
                    new GeoBounds(rightDown, leftUp)
                },
                {
                    // Both squares cross, first one contains second
                    new GeoBounds(rightDown, leftUp),
                    new GeoBounds(farRightNegMid, farLeftPosMid),
                    new GeoBounds(rightDown, leftUp)
                },
                {
                    // First square crosses the antimeridian, second does not but has both points inside of the other
                    new GeoBounds(rightDown, leftUp),
                    new GeoBounds(farLeftDown, farRightPosMid),
                    new GeoBounds(new GeoPointImmutable(-180d, farLeftDown.latitude()), new GeoPointImmutable(180d, leftUp.latitude()))
                },
                {
                    // First square crosses the antimeridian, second one does not intersets, smallest gap is on the left
                    new GeoBounds(farRightDown, farLeftUp),
                    new GeoBounds(leftDown, rightPosMid),
                    new GeoBounds(farRightDown, rightPosMid.withLatitude(85d))
                },
                {
                    // First square crosses the antimeridian, second one does not intersets, smallest gap is on the right
                    new GeoBounds(farRightDown, farLeftUp),
                    new GeoBounds(leftDown.withLongitude(0d), rightPosMid),
                    new GeoBounds(farRightDown.withLongitude(0d), farLeftUp)
                },
                {
                    // First square crosses the antimeridian, second one intersects with lower part
                    new GeoBounds(farRightDown, leftUp),
                    new GeoBounds(farLeftNegMid, leftPosMid),
                    new GeoBounds(farRightDown, leftUp)
                },
                {
                    // First square crosses the antimeridian, second one is in lower part
                    new GeoBounds(farRightDown, rightUp),
                    new GeoBounds(farLeftDown.withLongitude(0d), rightPosMid),
                    new GeoBounds(farRightDown, rightPosMid.withLatitude(leftUp.latitude()))
                },
                {
                    // First square crosses the antimeridian, second one intersects with upper part
                    new GeoBounds(rightDown, farLeftUp),
                    new GeoBounds(leftNegMid, rightPosMid),
                    new GeoBounds(leftNegMid.withLatitude(rightDown.latitude()), farLeftUp)
                },
                {
                    // First square crosses the antimeridian, second one is in the upper part
                    new GeoBounds(leftDown, farLeftUp),
                    new GeoBounds(rightNegMid, farRightPosMid),
                    new GeoBounds(leftDown, farLeftUp)
                }
        };
        for(GeoBounds[] bounds: tests) {
            assertEquals(bounds[2], bounds[0].smallestEncompassingSquare(bounds[1]));
            assertEquals(bounds[2], bounds[1].smallestEncompassingSquare(bounds[0]));
        }
    }
    
    @Test
    public void containsPointTest() {
        GeoBounds notCrossing = new GeoBounds(
                new GeoPointImmutable(-67d, -78d), new GeoPointImmutable(169d, 35d)
            );
        assertTrue(notCrossing.contains(notCrossing.lowerCorner));
        assertTrue(notCrossing.contains(notCrossing.upperCorner));
        assertTrue(notCrossing.contains(new GeoPointImmutable(-67d, -6d)));
        assertTrue(notCrossing.contains(new GeoPointImmutable(169d, 15d)));
        assertTrue(notCrossing.contains(new GeoPointImmutable(0d, -78d)));
        assertTrue(notCrossing.contains(new GeoPointImmutable(-5d, 35d)));
        assertTrue(notCrossing.contains(GeoPointImmutable.ORIGIN));
        assertTrue(notCrossing.contains(new GeoPointImmutable(-24d, 18d)));
        assertFalse(notCrossing.contains(new GeoPointImmutable(-69d, 0d)));
        assertFalse(notCrossing.contains(new GeoPointImmutable(179d, 0d)));
        assertFalse(notCrossing.contains(new GeoPointImmutable(0d, -85d)));
        assertFalse(notCrossing.contains(new GeoPointImmutable(0d, 40d)));
        GeoBounds crossing = new GeoBounds(
                new GeoPointImmutable(169d, -35d), new GeoPointImmutable(-67d, 78d)
            );
        assertTrue(crossing.contains(crossing.lowerCorner));
        assertTrue(crossing.contains(crossing.upperCorner));
        assertTrue(crossing.contains(new GeoPointImmutable(-67d, -6d)));
        assertTrue(crossing.contains(new GeoPointImmutable(169d, 15d)));
        assertTrue(crossing.contains(new GeoPointImmutable(180d, 78d)));
        assertTrue(crossing.contains(new GeoPointImmutable(-180d, -35d)));
        assertTrue(crossing.contains(new GeoPointImmutable(170d, 25d)));
        assertTrue(crossing.contains(new GeoPointImmutable(-80d, -5d)));
        assertFalse(crossing.contains(new GeoPointImmutable(-5d, 35d)));
        assertFalse(crossing.contains(GeoPointImmutable.ORIGIN));
    }
    
    @Test
    public void containsSquareTest() {
        GeoPointImmutable rightPosMid = new GeoPointImmutable(35d, 45d);
        GeoPointImmutable rightUp = new GeoPointImmutable(35d, 85d);
        GeoPointImmutable rightNegMid = new GeoPointImmutable(35d, -50d);
        GeoPointImmutable rightDown = new GeoPointImmutable(35d, -88d);
        GeoPointImmutable farRightPosMid = new GeoPointImmutable(158d, 45d);
        GeoPointImmutable farRightUp = new GeoPointImmutable(158d, 85d);
        GeoPointImmutable farRightNegMid = new GeoPointImmutable(158d, -50d);
        GeoPointImmutable farRightDown = new GeoPointImmutable(158d, -88d);
        GeoPointImmutable leftPosMid = new GeoPointImmutable(-40d, 45d);
        GeoPointImmutable leftUp = new GeoPointImmutable(-40d, 85d);
        GeoPointImmutable leftNegMid = new GeoPointImmutable(-40d, -50d);
        GeoPointImmutable leftDown = new GeoPointImmutable(-40d, -88d);
        GeoPointImmutable farLeftPosMid = new GeoPointImmutable(-140d, 45d);
        GeoPointImmutable farLeftUp = new GeoPointImmutable(-140d, 85d);
        GeoPointImmutable farLeftNegMid = new GeoPointImmutable(-140d, -50d);
        GeoPointImmutable farLeftDown = new GeoPointImmutable(-140d, -88d);
        GeoBounds[][] contains = {
                {
                    // Square 1 contains square 2, none cross
                    new GeoBounds(farLeftDown, rightUp),
                    new GeoBounds(leftDown, rightPosMid),
                },
                {
                    // Both squares cross, first one contains second
                    new GeoBounds(rightDown, leftUp),
                    new GeoBounds(farRightNegMid, farLeftPosMid),
                },
                {
                    // First square crosses the antimeridian, second one is in lower part
                    new GeoBounds(farRightDown, rightUp),
                    new GeoBounds(farLeftDown.withLongitude(0d), rightPosMid),
                },
                {
                    // First square crosses the antimeridian, second one is in the upper part
                    new GeoBounds(leftDown, farLeftUp),
                    new GeoBounds(rightNegMid, farRightPosMid),
                }
        };
        GeoBounds[][] notContains = {
                {
                    // Squares intersect, none cross
                    new GeoBounds(farLeftDown, rightUp),
                    new GeoBounds(leftDown, farRightPosMid),
                },
                {
                    // Squares do not intersect, none cross, smallest encompassing does not cross
                    new GeoBounds(leftDown, GeoPointImmutable.ORIGIN),
                    new GeoBounds(rightDown, farRightUp),
                },
                {
                    // Squares do not intersect, none cross, smallest encompassing crosses
                    new GeoBounds(farLeftDown.withLongitude(-180d), farLeftUp),
                    new GeoBounds(rightDown, farRightUp),
                },
                {
                    // Both squares cross
                    new GeoBounds(rightDown, farLeftUp),
                    new GeoBounds(farRightNegMid, leftPosMid),
                },
                {
                    // First square crosses the antimeridian, second does not but has both points inside of the other
                    new GeoBounds(rightDown, leftUp),
                    new GeoBounds(farLeftDown, farRightPosMid),
                },
                {
                    // First square crosses the antimeridian, second one does not intersets, smallest gap is on the left
                    new GeoBounds(farRightDown, farLeftUp),
                    new GeoBounds(leftDown, rightPosMid),
                },
                {
                    // First square crosses the antimeridian, second one does not intersets, smallest gap is on the right
                    new GeoBounds(farRightDown, farLeftUp),
                    new GeoBounds(leftDown.withLongitude(0d), rightPosMid),
                },
                {
                    // First square crosses the antimeridian, second one intersects with lower part
                    new GeoBounds(farRightDown, leftUp),
                    new GeoBounds(farLeftNegMid, leftPosMid.withLongitude(-30d)),
                },{
                    // First square crosses the antimeridian, second one intersects with upper part
                    new GeoBounds(rightDown, farLeftUp),
                    new GeoBounds(leftNegMid, rightPosMid),
                }
        };
        for(GeoBounds[] squares: contains) {
            assertTrue(squares[0].contains(squares[0]));
            assertTrue(squares[1].contains(squares[1]));
            assertTrue(squares[0].contains(squares[1]));
            assertFalse(squares[1].contains(squares[0]));
        }
        for(GeoBounds[] squares: notContains) {
            assertTrue(squares[0].contains(squares[0]));
            assertTrue(squares[1].contains(squares[1]));
            assertFalse(squares[0].contains(squares[1]));
            assertFalse(squares[1].contains(squares[0]));
        }
    }
    
    @Test
    public void intesectionTest() {
        GeoPointImmutable rightPosMid = new GeoPointImmutable(35d, 45d);
        GeoPointImmutable rightUp = new GeoPointImmutable(35d, 85d);
        GeoPointImmutable rightNegMid = new GeoPointImmutable(35d, -50d);
        GeoPointImmutable rightDown = new GeoPointImmutable(35d, -88d);
        GeoPointImmutable farRightPosMid = new GeoPointImmutable(158d, 45d);
        GeoPointImmutable farRightUp = new GeoPointImmutable(158d, 85d);
        GeoPointImmutable farRightNegMid = new GeoPointImmutable(158d, -50d);
        GeoPointImmutable farRightDown = new GeoPointImmutable(158d, -88d);
        GeoPointImmutable leftPosMid = new GeoPointImmutable(-40d, 45d);
        GeoPointImmutable leftUp = new GeoPointImmutable(-40d, 85d);
        GeoPointImmutable leftNegMid = new GeoPointImmutable(-40d, -50d);
        GeoPointImmutable leftDown = new GeoPointImmutable(-40d, -88d);
        GeoPointImmutable farLeftPosMid = new GeoPointImmutable(-140d, 45d);
        GeoPointImmutable farLeftUp = new GeoPointImmutable(-140d, 85d);
        GeoPointImmutable farLeftNegMid = new GeoPointImmutable(-140d, -50d);
        GeoPointImmutable farLeftDown = new GeoPointImmutable(-140d, -88d);
        GeoBounds[][] tests = {
                {
                    // Square 1 contains square 2, none cross
                    new GeoBounds(farLeftDown, rightUp),
                    new GeoBounds(leftDown, rightPosMid),
                    new GeoBounds(leftDown, rightPosMid) // Expected result
                },
                {
                    // Squares intersect, none cross
                    new GeoBounds(farLeftDown, rightUp),
                    new GeoBounds(leftDown, farRightPosMid),
                    new GeoBounds(leftDown, rightUp.withLatitude(farRightPosMid.latitude()))
                },
                {
                    // Squares do not intersect, none cross, smallest encompassing does not cross
                    new GeoBounds(leftDown, GeoPointImmutable.ORIGIN),
                    new GeoBounds(rightDown, farRightUp),
                    EMPTY
                },
                {
                    // Squares do not intersect, none cross, smallest encompassing crosses
                    new GeoBounds(farLeftDown.withLongitude(-180d), farLeftUp),
                    new GeoBounds(rightDown, farRightUp),
                    EMPTY
                },
                {
                    // Both squares cross
                    new GeoBounds(rightDown, farLeftUp),
                    new GeoBounds(farRightNegMid, leftPosMid),
                    new GeoBounds(farRightNegMid, leftPosMid.withLongitude(farLeftUp.longitude()))
                },
                {
                    // Both squares cross, first one contains second
                    new GeoBounds(rightDown, leftUp),
                    new GeoBounds(farRightNegMid, farLeftPosMid),
                    new GeoBounds(farRightNegMid, farLeftPosMid),
                },
                {
                    // First square crosses the antimeridian, second one does not intersets, smallest gap is on the left
                    new GeoBounds(farRightDown, farLeftUp),
                    new GeoBounds(leftDown, rightPosMid),
                    EMPTY
                },
                {
                    // First square crosses the antimeridian, second one does not intersets, smallest gap is on the right
                    new GeoBounds(farRightDown, farLeftUp),
                    new GeoBounds(leftDown.withLongitude(0d), rightPosMid),
                    EMPTY
                },
                {
                    // First square crosses the antimeridian, second one intersects with lower part
                    new GeoBounds(farRightDown, leftUp),
                    new GeoBounds(farLeftNegMid, rightPosMid),
                    new GeoBounds(farLeftNegMid, leftPosMid)
                },
                {
                    // First square crosses the antimeridian, second one is in lower part
                    new GeoBounds(farRightDown, rightUp),
                    new GeoBounds(farLeftNegMid, leftNegMid),
                    new GeoBounds(farLeftNegMid, leftNegMid),
                },
                {
                    // First square crosses the antimeridian, second one intersects with upper part
                    new GeoBounds(rightDown, farLeftUp),
                    new GeoBounds(leftNegMid, farRightPosMid),
                    new GeoBounds(rightDown.withLatitude(leftNegMid.latitude()), farRightPosMid)
                },
                {
                    // First square crosses the antimeridian, second one is in the upper part
                    new GeoBounds(leftDown, farLeftUp),
                    new GeoBounds(rightNegMid, farRightPosMid),
                    new GeoBounds(rightNegMid, farRightPosMid),
                }
        };
        for(GeoBounds[] bounds: tests) {
            GeoBounds square1 = bounds[0];
            GeoBounds square2 = bounds[1];
            GeoBounds inter = bounds[2];
            if(EMPTY.equals(inter)) {
                assertFalse(square1.intersects(square2));
                assertFalse(square2.intersects(square1));
                assertEquals(0, square1.intersections(square2).length);
                assertEquals(0, square2.intersections(square1).length);
                assertEquals(EMPTY, square1.encompassingIntersection(square2));
            } else {
                assertTrue(square1.intersects(square2));
                assertTrue(square2.intersects(square1));
                GeoBounds[] res = square1.intersections(square2);
                assertEquals(1, res.length);
                assertEquals(inter, res[0]);
                res = square2.intersections(square1);
                assertEquals(1, res.length);
                assertEquals(inter, res[0]);
                assertEquals(inter, square1.encompassingIntersection(square2));
            }
            assertTrue(square1.intersects(square1));
            assertTrue(square2.intersects(square2));
            GeoBounds[] res = square1.intersections(square1);
            assertEquals(1, res.length);
            assertEquals(square1, res[0]);
            res = square2.intersections(square2);
            assertEquals(1, res.length);
            assertEquals(square2, res[0]);
        }
        // First square crosses the antimeridian, second does not but has both points inside of the other
        GeoBounds square1 = new GeoBounds(rightDown, leftUp);
        GeoBounds square2 = new GeoBounds(farLeftNegMid, farRightPosMid);
        GeoBounds res1 = new GeoBounds(farLeftNegMid, leftPosMid);
        GeoBounds res2 = new GeoBounds(rightNegMid, farRightPosMid);
        GeoBounds[] inte = square1.intersections(square2);
        assertEquals(2, inte.length);
        assertTrue(
                (inte[0].equals(res1) && inte[1].equals(res2))
                ||
                (inte[1].equals(res1) && inte[0].equals(res2)));
        assertEquals(inte[0].smallestEncompassingSquare(inte[1]), square1.encompassingIntersection(square2));
    }

    @Test
    public void clampGeoPointTest() {
        GeoBounds transatlanticBounds = new GeoBounds(
                new GeoPointImmutable(-90, -45),
                new GeoPointImmutable(15, 45)
        );

        GeoBounds transpacificBounds = new GeoBounds(
                new GeoPointImmutable(120, -45),
                new GeoPointImmutable(-120, 45)
        );

        // Cannot clamp with empty bounds
        assertThrows(UnsupportedOperationException.class, () -> EMPTY.clamp(NORTH_POLE));

        // Simplest case, clamping with bounds that do not cross the antimeridian
        assertEquals(new GeoPointImmutable(-90, 10), transatlanticBounds.clamp(new GeoPointImmutable(-92, 10)));
        assertEquals(new GeoPointImmutable(15, 10), transatlanticBounds.clamp(new GeoPointImmutable(20, 10)));
        assertEquals(new GeoPointImmutable(10, -45), transatlanticBounds.clamp(new GeoPointImmutable(10, -60)));
        assertEquals(new GeoPointImmutable(10, 45), transatlanticBounds.clamp(new GeoPointImmutable(10, 70)));

        // More complicated case, clamping with bounds that cross the antimeridian
        assertEquals(new GeoPointImmutable(120, 20), transpacificBounds.clamp(new GeoPointImmutable(118, 20)));
        assertEquals(new GeoPointImmutable(-120, 20), transpacificBounds.clamp(new GeoPointImmutable(-118, 20)));
        assertEquals(new GeoPointImmutable(130, -45), transpacificBounds.clamp(new GeoPointImmutable(130, -60)));
        assertEquals(new GeoPointImmutable(130, 45), transpacificBounds.clamp(new GeoPointImmutable(130, 70)));

        // Most complicated: closest boundary is on the other side of the antimeridian
        assertEquals(new GeoPointImmutable(-180, 0), WESTERN_HEMISPHERE.clamp(new GeoPointImmutable(170, 0)));
        assertEquals(new GeoPointImmutable(180, 0), EASTERN_HEMISPHERE.clamp(new GeoPointImmutable(-170, 0)));
    }

    @Test
    public void equalsAndHashCodeTest() {
        GeoBounds square1 = new GeoBounds(new GeoPointImmutable(45d, 23d), new GeoPointImmutable(60d, 67d));
        GeoBounds square2 = new GeoBounds(new GeoPointImmutable(45d, 23d), new GeoPointImmutable(60d, 67d));
        GeoBounds square3 = new GeoBounds(new GeoPointImmutable(46d, 23d), new GeoPointImmutable(60d, 67d));
        assertEquals(square1, square2);
        assertEquals(square1.hashCode(), square2.hashCode());
        assertNotEquals(square1, square3);
        assertNotEquals(square1.hashCode(), square3.hashCode());
        GeoBounds empty1 = new GeoBounds(new GeoPointImmutable(45d, 23d), new GeoPointImmutable(60d, -67d));
        GeoBounds empty2 = new GeoBounds(new GeoPointImmutable(-28d, 38d), new GeoPointImmutable(125d, 1d));
        assertEquals(empty1.hashCode(), empty2.hashCode());
        assertEquals(empty1, empty2);
        assertNotEquals(empty1, square1);
    }
    
    @Test
    public void toStringTest() {
        GeoBounds square = new GeoBounds(new GeoPointImmutable(45d, -16d), new GeoPointImmutable(-64d, 18d));
        assertEquals("GeoBoundsSquare{lower=GeoPoint{lon=45.0°, lat=-16.0°}, upper=GeoPoint{lon=-64.0°, lat=18.0°}}", square.toString());
    }

}
