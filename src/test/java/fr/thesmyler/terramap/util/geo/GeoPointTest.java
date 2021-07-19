package fr.thesmyler.terramap.util.geo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import fr.thesmyler.terramap.util.math.Vec2d;
import net.buildtheearth.terraplusplus.util.geo.LatLng;
import net.minecraft.util.math.Vec3d;

public class GeoPointTest {
    
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
            GeoPoint point = new GeoPoint(coords[0], coords[1]);
            assertEquals(coords[0], point.longitude, 0d);
            assertEquals(coords[1], point.latitude, 0d);
            point = new GeoPoint(coords);
            assertEquals(coords[0], point.longitude, 0d);
            assertEquals(coords[1], point.latitude, 0d);
            point = new GeoPoint(new LatLng(coords[1], coords[0]));
            assertEquals(coords[0], point.longitude, 0d);
            assertEquals(coords[1], point.latitude, 0d);
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
            GeoPoint point = new GeoPoint(lons[0], 0d);
            assertEquals(lons[1], point.longitude, 0d);
            point = new GeoPoint(new double[] {lons[0], 0d});
            assertEquals(lons[1], point.longitude, 0d);
            point = new GeoPoint(new LatLng(0d, lons[0]));
            assertEquals(lons[1], point.longitude, 0d);
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
        for(double[] coords: invalidCoords) {
            try {
                new GeoPoint(coords[0], coords[1]);
                fail(String.format("Illegal geographic point created: longitude=%s latitude=%s", coords[0], coords[1]));
            } catch(IllegalArgumentException silenced) {}
            try {
                new GeoPoint(coords);
                fail(String.format("Illegal geographic point created: longitude=%s latitude=%s", coords[0], coords[1]));
            } catch(IllegalArgumentException silenced) {}
            try {
                new GeoPoint(new LatLng(coords[1], coords[0]));
                fail(String.format("Illegal geographic point created: longitude=%s latitude=%s", coords[0], coords[1]));
            } catch(IllegalArgumentException silenced) {}
        }
    }
    
    @Test
    public void distanceTest() {
        GeoPoint paris = new GeoPoint(2.350987d, 48.856667d);
        GeoPoint newYork = new GeoPoint(-74.005974d, 40.714268d);
        GeoPoint london = new GeoPoint(-0.166670d, 51.500000d);
        GeoPoint beijing = new GeoPoint(116.397230d, 39.907500d);
        GeoPoint seattle = new GeoPoint(-122.332070, 47.606210d);
        GeoPoint sidney = new GeoPoint(151.208666d, -33.875113d);
        GeoPoint arcDeTriomphe = new GeoPoint(2.295026d, 48.87378100000001d);
        GeoPoint archeLaDefense = new GeoPoint(2.236214, 48.8926507);
        GeoPoint notreDameNorthTower = new GeoPoint(2.349270d, 48.853474d);
        GeoPoint notreDameSouthTower = new GeoPoint(2.348969d, 48.853065d);
        assertEquals(12470810d, sidney.distanceTo(seattle), 1000d);
        assertEquals(8689000d, beijing.distanceTo(seattle), 1000d);
        assertEquals(5837000d, paris.distanceTo(newYork), 1000d);
        assertEquals(344240d, paris.distanceTo(london), 100d);
        assertEquals(4785d, arcDeTriomphe.distanceTo(archeLaDefense), 10d);
        assertEquals(51d, notreDameNorthTower.distanceTo(notreDameSouthTower), 1d);
        assertEquals(0d, paris.distanceTo(paris), 0d);
        assertEquals(0d, newYork.distanceTo(newYork), 0d);
        assertEquals(0d, sidney.distanceTo(sidney), 0d);
        assertEquals(0d, beijing.distanceTo(beijing), 0d);
    }
   
    @Test
    public void equalsAndHashCodeTest() {
        GeoPoint[][] same = {
                {new GeoPoint(0d, 0d), new GeoPoint(0d, 0d)},
                {new GeoPoint(0d, 0d), new GeoPoint(0d, -0d)},
                {new GeoPoint(0d, 0d), new GeoPoint(-0d, 0d)},
                {new GeoPoint(0d, 0d), new GeoPoint(-0d, -0d)},
                {new GeoPoint(45d, 45d), new GeoPoint(45d, 45d)},
                {new GeoPoint(0d, 90d), new GeoPoint(-54.4d, 90d)},
                {new GeoPoint(78.73d, -90d), new GeoPoint(-65.44d, -90d)},
                {new GeoPoint(180d, 47d), new GeoPoint(-180, 47d)},
                {new GeoPoint(180d, 90d), new GeoPoint(-180, 90d)},
                {new GeoPoint(180d, -90d), new GeoPoint(-180, -90d)}
        };
        for(GeoPoint[] points: same) {
            assertEquals(points[0].hashCode(), points[1].hashCode());
            assertEquals(points[0], points[1]);
            assertEquals(points[1], points[0]);
        }
        GeoPoint[][] diff = {
                {new GeoPoint(0d, 0d), new GeoPoint(0d, 1d)},
                {new GeoPoint(0d, 45d), new GeoPoint(0d, -45d)},
                {new GeoPoint(-45d, 45d), new GeoPoint(45d, 45d)}
        };
        for(GeoPoint[] points: diff) {
            assertNotEquals(points[0].hashCode(), points[1].hashCode()); // A hash collision here should be extremely rare
            assertNotEquals(points[0], points[1]);
            assertNotEquals(points[1], points[0]);
        }
        assertFalse(new GeoPoint(168.4d, -26d).equals(null));
    }
    
    @Test
    public void withTest() {
        GeoPoint point1 = new GeoPoint(-56d, 37d);
        GeoPoint point2 = point1.withLatitude(-67d);
        GeoPoint point3 = point1.withLongitude(89d);
        assertEquals(-56d, point2.longitude, 0d);
        assertEquals(-67d, point2.latitude, 0d);
        assertEquals(89d, point3.longitude, 0d);
        assertEquals(37d, point3.latitude, 0d);
    }
    
    @Test
    public void asTest() {
        GeoPoint point = new GeoPoint(18d, 39d);
        double[] arr = point.asArray();
        assertEquals(2, arr.length);
        assertEquals(18d, arr[0], 0d);
        assertEquals(39d, arr[1], 0d);
        assertEquals(point.asVec2d(), new Vec2d(18d, 39d));
        LatLng tppPos = point.asLatLng();
        assertEquals(18d, tppPos.getLng(), 0d);
        assertEquals(39d, tppPos.getLat(), 0d);
    }
    
    @Test
    public void unitCartesianPositionTest() {
        GeoPoint point = new GeoPoint(36d, -67d);
        Vec3d vec = point.unitCartesianPosition();
        assertEquals(0.3161081232, vec.x, 1e-10);
        assertEquals(0.229665995, vec.y, 1e-10);
        assertEquals(-0.9205048535, vec.z, 1e-10);
    }
    
    @Test
    public void toStringTest() {
        assertEquals(
                "GeoPoint{lon=78.0°, lat=-45.0°}",
                new GeoPoint(78d, -45).toString()
            );
    }

}
