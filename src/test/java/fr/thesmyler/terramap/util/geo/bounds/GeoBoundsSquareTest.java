package fr.thesmyler.terramap.util.geo.bounds;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map;

import org.junit.Test;

import fr.thesmyler.terramap.util.HashMapBuilder;
import fr.thesmyler.terramap.util.geo.GeoBounds;
import fr.thesmyler.terramap.util.geo.GeoPoint;

public class GeoBoundsSquareTest {
    
    @Test
    public void constructionAndCrossesAntimeridianTest() {
        Map<GeoBounds, Boolean> squares = new HashMapBuilder<GeoBounds, Boolean>()
                .put(GeoBounds.WORLD, false)
                .put(GeoBounds.SOUTHERN_HEMISPHERE, false)
                .put(GeoBounds.NORTHERN_HEMISPHERE, false)
                .put(GeoBounds.WESTERN_HEMISPHERE, false)
                .put(GeoBounds.EASTERN_HEMISPHERE, false)
                .put(new GeoBounds(GeoPoint.ORIGIN, GeoPoint.NORTH_POLE.withLongitude(-1d)), true)
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
                new GeoPoint(150d, -60d),
                new GeoPoint(-15d, 56d)
        );
        GeoBounds[] split = sq.splitAtAntimeridian();
        assertEquals(2, split.length);
        assertEquals(-180d, split[0].lowerCorner.longitude, 0d);
        assertEquals(-15d, split[0].upperCorner.longitude, 0d);
        assertEquals(-60d, split[0].lowerCorner.latitude, 0d);
        assertEquals(56d, split[0].upperCorner.latitude, 0d);
        assertEquals(150d, split[1].lowerCorner.longitude, 0d);
        assertEquals(180d, split[1].upperCorner.longitude, 0d);
        assertEquals(-60d, split[1].lowerCorner.latitude, 0d);
        assertEquals(56d, split[1].upperCorner.latitude, 0d);
    }
    
    @Test
    public void encompassingOtherSquareTest() {
        GeoPoint rightPosMid = new GeoPoint(35d, 45d);
        GeoPoint rightUp = new GeoPoint(35d, 85d);
        GeoPoint rightNegMid = new GeoPoint(35d, -50d);
        GeoPoint rightDown = new GeoPoint(35d, -88d);
        GeoPoint farRightPosMid = new GeoPoint(158d, 45d);
        GeoPoint farRightUp = new GeoPoint(158d, 85d);
        GeoPoint farRightNegMid = new GeoPoint(158d, -50d);
        GeoPoint farRightDown = new GeoPoint(158d, -88d);
        GeoPoint leftPosMid = new GeoPoint(-40d, 45d);
        GeoPoint leftUp = new GeoPoint(-40d, 85d);
        GeoPoint leftNegMid = new GeoPoint(-40d, -50d);
        GeoPoint leftDown = new GeoPoint(-40d, -88d);
        GeoPoint farLeftPosMid = new GeoPoint(-140d, 45d);
        GeoPoint farLeftUp = new GeoPoint(-140d, 85d);
        GeoPoint farLeftNegMid = new GeoPoint(-140d, -50d);
        GeoPoint farLeftDown = new GeoPoint(-140d, -88d);
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
                    new GeoBounds(farLeftDown, rightUp.withLongitude(farRightPosMid.longitude))
                },
                {
                    // Squares do not intersect, none cross, smallest encompassing does not cross
                    new GeoBounds(leftDown, GeoPoint.ORIGIN),
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
                    new GeoBounds(new GeoPoint(-180d, farLeftDown.latitude), new GeoPoint(180d, leftUp.latitude))
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
                    new GeoBounds(farRightDown, rightPosMid.withLatitude(leftUp.latitude))
                },
                {
                    // First square crosses the antimeridian, second one intersects with upper part
                    new GeoBounds(rightDown, farLeftUp),
                    new GeoBounds(leftNegMid, rightPosMid),
                    new GeoBounds(leftNegMid.withLatitude(rightDown.latitude), farLeftUp)
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
                new GeoPoint(-67d, -78d), new GeoPoint(169d, 35d)
            );
        assertTrue(notCrossing.contains(notCrossing.lowerCorner));
        assertTrue(notCrossing.contains(notCrossing.upperCorner));
        assertTrue(notCrossing.contains(new GeoPoint(-67d, -6d)));
        assertTrue(notCrossing.contains(new GeoPoint(169d, 15d)));
        assertTrue(notCrossing.contains(new GeoPoint(0d, -78d)));
        assertTrue(notCrossing.contains(new GeoPoint(-5d, 35d)));
        assertTrue(notCrossing.contains(GeoPoint.ORIGIN));
        assertTrue(notCrossing.contains(new GeoPoint(-24d, 18d)));
        assertFalse(notCrossing.contains(new GeoPoint(-69d, 0d)));
        assertFalse(notCrossing.contains(new GeoPoint(179d, 0d)));
        assertFalse(notCrossing.contains(new GeoPoint(0d, -85d)));
        assertFalse(notCrossing.contains(new GeoPoint(0d, 40d)));
        GeoBounds crossing = new GeoBounds(
                new GeoPoint(169d, -35d), new GeoPoint(-67d, 78d)
            );
        assertTrue(crossing.contains(crossing.lowerCorner));
        assertTrue(crossing.contains(crossing.upperCorner));
        assertTrue(crossing.contains(new GeoPoint(-67d, -6d)));
        assertTrue(crossing.contains(new GeoPoint(169d, 15d)));
        assertTrue(crossing.contains(new GeoPoint(180d, 78d)));
        assertTrue(crossing.contains(new GeoPoint(-180d, -35d)));
        assertTrue(crossing.contains(new GeoPoint(170d, 25d)));
        assertTrue(crossing.contains(new GeoPoint(-80d, -5d)));
        assertFalse(crossing.contains(new GeoPoint(-5d, 35d)));
        assertFalse(crossing.contains(GeoPoint.ORIGIN));
    }
    
    @Test
    public void containsSquareTest() {
        GeoPoint rightPosMid = new GeoPoint(35d, 45d);
        GeoPoint rightUp = new GeoPoint(35d, 85d);
        GeoPoint rightNegMid = new GeoPoint(35d, -50d);
        GeoPoint rightDown = new GeoPoint(35d, -88d);
        GeoPoint farRightPosMid = new GeoPoint(158d, 45d);
        GeoPoint farRightUp = new GeoPoint(158d, 85d);
        GeoPoint farRightNegMid = new GeoPoint(158d, -50d);
        GeoPoint farRightDown = new GeoPoint(158d, -88d);
        GeoPoint leftPosMid = new GeoPoint(-40d, 45d);
        GeoPoint leftUp = new GeoPoint(-40d, 85d);
        GeoPoint leftNegMid = new GeoPoint(-40d, -50d);
        GeoPoint leftDown = new GeoPoint(-40d, -88d);
        GeoPoint farLeftPosMid = new GeoPoint(-140d, 45d);
        GeoPoint farLeftUp = new GeoPoint(-140d, 85d);
        GeoPoint farLeftNegMid = new GeoPoint(-140d, -50d);
        GeoPoint farLeftDown = new GeoPoint(-140d, -88d);
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
                    new GeoBounds(leftDown, GeoPoint.ORIGIN),
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
        GeoPoint rightPosMid = new GeoPoint(35d, 45d);
        GeoPoint rightUp = new GeoPoint(35d, 85d);
        GeoPoint rightNegMid = new GeoPoint(35d, -50d);
        GeoPoint rightDown = new GeoPoint(35d, -88d);
        GeoPoint farRightPosMid = new GeoPoint(158d, 45d);
        GeoPoint farRightUp = new GeoPoint(158d, 85d);
        GeoPoint farRightNegMid = new GeoPoint(158d, -50d);
        GeoPoint farRightDown = new GeoPoint(158d, -88d);
        GeoPoint leftPosMid = new GeoPoint(-40d, 45d);
        GeoPoint leftUp = new GeoPoint(-40d, 85d);
        GeoPoint leftNegMid = new GeoPoint(-40d, -50d);
        GeoPoint leftDown = new GeoPoint(-40d, -88d);
        GeoPoint farLeftPosMid = new GeoPoint(-140d, 45d);
        GeoPoint farLeftUp = new GeoPoint(-140d, 85d);
        GeoPoint farLeftNegMid = new GeoPoint(-140d, -50d);
        GeoPoint farLeftDown = new GeoPoint(-140d, -88d);
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
                    new GeoBounds(leftDown, rightUp.withLatitude(farRightPosMid.latitude))
                },
                {
                    // Squares do not intersect, none cross, smallest encompassing does not cross
                    new GeoBounds(leftDown, GeoPoint.ORIGIN),
                    new GeoBounds(rightDown, farRightUp),
                    GeoBounds.EMPTY
                },
                {
                    // Squares do not intersect, none cross, smallest encompassing crosses
                    new GeoBounds(farLeftDown.withLongitude(-180d), farLeftUp),
                    new GeoBounds(rightDown, farRightUp),
                    GeoBounds.EMPTY
                },
                {
                    // Both squares cross
                    new GeoBounds(rightDown, farLeftUp),
                    new GeoBounds(farRightNegMid, leftPosMid),
                    new GeoBounds(farRightNegMid, leftPosMid.withLongitude(farLeftUp.longitude))
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
                    GeoBounds.EMPTY
                },
                {
                    // First square crosses the antimeridian, second one does not intersets, smallest gap is on the right
                    new GeoBounds(farRightDown, farLeftUp),
                    new GeoBounds(leftDown.withLongitude(0d), rightPosMid),
                    GeoBounds.EMPTY
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
                    new GeoBounds(rightDown.withLatitude(leftNegMid.latitude), farRightPosMid)
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
            if(GeoBounds.EMPTY.equals(inter)) {
                assertFalse(square1.intersects(square2));
                assertFalse(square2.intersects(square1));
                assertEquals(0, square1.intersections(square2).length);
                assertEquals(0, square2.intersections(square1).length);
                assertEquals(GeoBounds.EMPTY, square1.encompassingIntersection(square2));
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
    public void equalsAndHashCodeTest() {
        GeoBounds square1 = new GeoBounds(new GeoPoint(45d, 23d), new GeoPoint(60d, 67d));
        GeoBounds square2 = new GeoBounds(new GeoPoint(45d, 23d), new GeoPoint(60d, 67d));
        GeoBounds square3 = new GeoBounds(new GeoPoint(46d, 23d), new GeoPoint(60d, 67d));
        assertEquals(square1, square2);
        assertEquals(square1.hashCode(), square2.hashCode());
        assertNotEquals(square1, square3);
        assertNotEquals(square1.hashCode(), square3.hashCode());
        GeoBounds empty1 = new GeoBounds(new GeoPoint(45d, 23d), new GeoPoint(60d, -67d));
        GeoBounds empty2 = new GeoBounds(new GeoPoint(-28d, 38d), new GeoPoint(125d, 1d));
        assertEquals(empty1.hashCode(), empty2.hashCode());
        assertEquals(empty1, empty2);
        assertNotEquals(empty1, square1);
    }
    
    @Test
    public void toStringTest() {
        GeoBounds square = new GeoBounds(new GeoPoint(45d, -16d), new GeoPoint(-64d, 18d));
        assertEquals("GeoBoundsSquare{lower=GeoPoint{lon=45.0°, lat=-16.0°}, upper=GeoPoint{lon=-64.0°, lat=18.0°}}", square.toString());
    }

}
