package fr.thesmyler.terramap.util.geo;

import fr.thesmyler.terramap.util.math.Vec2dImmutable;
import net.buildtheearth.terraplusplus.util.geo.LatLng;
import org.junit.jupiter.api.Test;

import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

public class GeoPointTest {

    public static GeoPointImmutable PARIS = new GeoPointImmutable(2.350987d, 48.856667d);
    public static GeoPointImmutable NEW_YORK = new GeoPointImmutable(-74.005974d, 40.714268d);
    public static GeoPointImmutable LONDON = new GeoPointImmutable(-0.166670d, 51.500000d);
    public static GeoPointImmutable BEIJING = new GeoPointImmutable(116.397230d, 39.907500d);
    public static GeoPointImmutable SEATTLE = new GeoPointImmutable(-122.332070, 47.606210d);
    public static GeoPointImmutable SIDNEY = new GeoPointImmutable(151.208666d, -33.875113d);
    public static GeoPointImmutable ARC_DE_TRIOMPHE = new GeoPointImmutable(2.295026d, 48.87378100000001d);
    public static GeoPointImmutable ARCHE_DE_LA_DEFENSE = new GeoPointImmutable(2.236214, 48.8926507);
    public static GeoPointImmutable NOTRE_DAME_NORTH_TOWER = new GeoPointImmutable(2.349270d, 48.853474d);
    public static GeoPointImmutable NOTRE_DAME_SOUTH_TOWER = new GeoPointImmutable(2.348969d, 48.853065d);
    
    @Test
    public void validConstructionTest() {
        double[][] validCoords = {
                {0d, 0d},
                {45d, 45d},
                {-45d, 45d},
                {-45d, -45d},
                {45d, -45d},
                {-180d, 0d},
                {180d, 0d},
                {-180d, -90d},
                {-180d, 90d},
                {180d, -90d},
                {180d, 90d},
                {0d, 90d},
                {0d, -90d}
        };
        for(double[] coords: validCoords) {
            GeoPointImmutable point = new GeoPointImmutable(coords[0], coords[1]);
            assertEquals(coords[0], point.longitude(), 0d);
            assertEquals(coords[1], point.latitude(), 0d);
            point = new GeoPointImmutable(coords);
            assertEquals(coords[0], point.longitude(), 0d);
            assertEquals(coords[1], point.latitude(), 0d);
            point = new GeoPointImmutable(new LatLng(coords[1], coords[0]));
            assertEquals(coords[0], point.longitude(), 0d);
            assertEquals(coords[1], point.latitude(), 0d);
        }
    }
    
    @Test
    public void adaptConstructionTest() {
        double[][] longitudes = {
                {0d, 0d},
                {-180d, -180d},
                {180d, 180d},
                {270d, -90d},
                {-270d, 90d},
                {360d, 0d},
                {-360d, 0d},
                {-540d, -180d},
                {810d, 90d},
                {-810d, -90d}
        };
        for(double[] lons: longitudes) {
            GeoPointImmutable point = new GeoPointImmutable(lons[0], 0d);
            assertEquals(lons[1], point.longitude(), 0d);
            point = new GeoPointImmutable(new double[] {lons[0], 0d});
            assertEquals(lons[1], point.longitude(), 0d);
            point = new GeoPointImmutable(new LatLng(0d, lons[0]));
            assertEquals(lons[1], point.longitude(), 0d);
        }
    }
    
    @Test
    public void invalidConstructionTest() {
        double[][] invalidCoords = {
                {0d, 91d},
                {0d, -91d},
                {0d, 180d},
                {0d, -180d},
                {Double.NaN, 0d},
                {0d, Double.NaN},
                {Double.POSITIVE_INFINITY, 0d},
                {Double.NEGATIVE_INFINITY, 0d},
                {0d, Double.POSITIVE_INFINITY},
                {0d, Double.NEGATIVE_INFINITY}
        };
        Arrays.stream(invalidCoords).forEach(coordinates -> assertThrows(IllegalArgumentException.class,
                () ->  new GeoPointImmutable(coordinates[0], coordinates[1])
        ));
    }
    
    @Test
    public void distanceTest() {
        assertEquals(12470810d, SIDNEY.distanceTo(SEATTLE), 1000d);
        assertEquals(8689000d, BEIJING.distanceTo(SEATTLE), 1000d);
        assertEquals(5837000d, PARIS.distanceTo(NEW_YORK), 1000d);
        assertEquals(344240d, PARIS.distanceTo(LONDON), 100d);
        assertEquals(4785d, ARC_DE_TRIOMPHE.distanceTo(ARCHE_DE_LA_DEFENSE), 10d);
        assertEquals(51d, NOTRE_DAME_NORTH_TOWER.distanceTo(NOTRE_DAME_SOUTH_TOWER), 1d);
        assertEquals(0d, PARIS.distanceTo(PARIS), 0d);
        assertEquals(0d, NEW_YORK.distanceTo(NEW_YORK), 0d);
        assertEquals(0d, SIDNEY.distanceTo(SIDNEY), 0d);
        assertEquals(0d, BEIJING.distanceTo(BEIJING), 0d);
    }
   
    @Test
    public void equalsAndHashCodeTest() {
        GeoPointImmutable[][] same = {
                {new GeoPointImmutable(0d, 0d), new GeoPointImmutable(0d, 0d)},
                {new GeoPointImmutable(0d, 0d), new GeoPointImmutable(0d, -0d)},
                {new GeoPointImmutable(0d, 0d), new GeoPointImmutable(-0d, 0d)},
                {new GeoPointImmutable(0d, 0d), new GeoPointImmutable(-0d, -0d)},
                {new GeoPointImmutable(45d, 45d), new GeoPointImmutable(45d, 45d)},
                {new GeoPointImmutable(0d, 90d), new GeoPointImmutable(-54.4d, 90d)},
                {new GeoPointImmutable(78.73d, -90d), new GeoPointImmutable(-65.44d, -90d)},
                {new GeoPointImmutable(180d, 47d), new GeoPointImmutable(-180, 47d)},
                {new GeoPointImmutable(180d, 90d), new GeoPointImmutable(-180, 90d)},
                {new GeoPointImmutable(180d, -90d), new GeoPointImmutable(-180, -90d)}
        };
        for(GeoPointImmutable[] points: same) {
            assertEquals(points[0].hashCode(), points[1].hashCode());
            assertEquals(points[0], points[1]);
            assertEquals(points[1], points[0]);
        }
        GeoPointImmutable[][] diff = {
                {new GeoPointImmutable(0d, 0d), new GeoPointImmutable(0d, 1d)},
                {new GeoPointImmutable(0d, 45d), new GeoPointImmutable(0d, -45d)},
                {new GeoPointImmutable(-45d, 45d), new GeoPointImmutable(45d, 45d)}
        };
        for(GeoPointImmutable[] points: diff) {
            assertNotEquals(points[0].hashCode(), points[1].hashCode()); // A hash collision here should be extremely rare
            assertNotEquals(points[0], points[1]);
            assertNotEquals(points[1], points[0]);
        }
        assertNotEquals(null, new GeoPointImmutable(168.4d, -26d));
    }
    
    @Test
    public void withTest() {
        GeoPointImmutable point1 = new GeoPointImmutable(-56d, 37d);
        GeoPointImmutable point2 = point1.withLatitude(-67d);
        GeoPointImmutable point3 = point1.withLongitude(89d);
        assertEquals(-56d, point2.longitude(), 0d);
        assertEquals(-67d, point2.latitude(), 0d);
        assertEquals(89d, point3.longitude(), 0d);
        assertEquals(37d, point3.latitude(), 0d);
    }
    
    @Test
    public void asTest() {
        GeoPointImmutable point = new GeoPointImmutable(18d, 39d);
        double[] arr = point.asArray();
        assertEquals(2, arr.length);
        assertEquals(18d, arr[0], 0d);
        assertEquals(39d, arr[1], 0d);
        assertEquals(point.asVec2d(), new Vec2dImmutable(18d, 39d));
        LatLng tppPos = point.asLatLng();
        assertEquals(18d, tppPos.getLng(), 0d);
        assertEquals(39d, tppPos.getLat(), 0d);
    }
    
    @Test
    public void toStringTest() {
        assertEquals(
                "GeoPoint{lon=78.0°, lat=-45.0°}",
                new GeoPointImmutable(78d, -45).toString()
            );
    }

}
