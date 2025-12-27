package net.smyler.terramap.util.geo;

import net.smyler.smylib.math.Vec2dImmutable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.Locale;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

public class GeoPointTest {

    @ParameterizedTest
    @MethodSource("inRangeCoordinateSource")
    void canConstructGeoPointImmutableWithImmediatelyValidCoordinates(double[] coordinates) {
        double longitude = coordinates[0];
        double latitude = coordinates[1];

        GeoPointImmutable point = new GeoPointImmutable(coordinates[0], coordinates[1]);
        assertEquals(longitude, point.longitude(), 0d);
        assertEquals(latitude, point.latitude(), 0d);

        point = new GeoPointImmutable(coordinates);
        assertEquals(longitude, point.longitude(), 0d);
        assertEquals(latitude, point.latitude(), 0d);

        point = new GeoPointImmutable(point);
        assertEquals(longitude, point.longitude(), 0d);
        assertEquals(latitude, point.latitude(), 0d);

        point = GeoPointImmutable.ORIGIN
                .withLongitude(longitude)
                .withLatitude(latitude);
        assertEquals(longitude, point.longitude(), 0d);
        assertEquals(latitude, point.latitude(), 0d);
    }

    @ParameterizedTest
    @MethodSource("inRangeCoordinateSource")
    void canConstructGeoPointMutableWithImmediatelyValidCoordinates(double[] coordinates) {
        double longitude = coordinates[0];
        double latitude = coordinates[1];

        GeoPointMutable point = new GeoPointMutable(coordinates[0], coordinates[1]);
        assertEquals(longitude, point.longitude(), 0d);
        assertEquals(latitude, point.latitude(), 0d);

        point = new GeoPointMutable(coordinates);
        assertEquals(longitude, point.longitude(), 0d);
        assertEquals(latitude, point.latitude(), 0d);

        point = new GeoPointMutable(point);
        assertEquals(longitude, point.longitude(), 0d);
        assertEquals(latitude, point.latitude(), 0d);

        point = new GeoPointMutable();
        assertEquals(0d, point.longitude());
        assertEquals(0d, point.latitude());
    }

    @ParameterizedTest
    @MethodSource("equalLongitudesSource")
    void canConstructGeoPointImmutableByBringingLongitudeInRange(double[] longitude) {
        double potentiallyOutOfRangeValue = longitude[0];
        double inRangeValue = longitude[1];

        GeoPointImmutable point = new GeoPointImmutable(potentiallyOutOfRangeValue, 0d);
        assertEquals(inRangeValue, point.longitude(), 0d);

        point = new GeoPointImmutable(new double[] {potentiallyOutOfRangeValue, 0d});
        assertEquals(inRangeValue, point.longitude(), 0d);

        point = GeoPointImmutable.ORIGIN.withLongitude(potentiallyOutOfRangeValue);
        assertEquals(inRangeValue, point.longitude(), 0d);
    }

    @ParameterizedTest
    @MethodSource("equalLongitudesSource")
    void canConstructGeoPointMutableByBringingLongitudeInRange(double[] longitude) {
        double potentiallyOutOfRangeValue = longitude[0];
        double inRangeValue = longitude[1];

        GeoPointMutable point = new GeoPointMutable(potentiallyOutOfRangeValue, 0d);
        assertEquals(inRangeValue, point.longitude(), 0d);

        point = new GeoPointMutable(new double[] {potentiallyOutOfRangeValue, 0d});
        assertEquals(inRangeValue, point.longitude(), 0d);

        point = new GeoPointMutable().setLongitude(potentiallyOutOfRangeValue);
        assertEquals(inRangeValue, point.longitude());
    }

    @ParameterizedTest
    @MethodSource("invalidCoordinateSource")
    void cannotConstructGeoPointMutableWithInvalidCoordinates(double[] coordinates) {
        double longitude = coordinates[0];
        double latitude = coordinates[1];

        assertThrows(IllegalArgumentException.class,
                () ->  new GeoPointMutable(longitude, latitude)
        );
        assertThrows(IllegalArgumentException.class,
                () ->  new GeoPointMutable(coordinates)
        );
        assertThrows(IllegalArgumentException.class,
                () ->  new GeoPointMutable(new double[] {0d, longitude, latitude})
        );
        assertThrows(IllegalArgumentException.class,
                () ->  new GeoPointMutable(new double[] {longitude})
        );
    }

    @ParameterizedTest
    @MethodSource("inRangeCoordinateSource")
    void canSetGeoPointMutableToValidCoordinates(double[] coordinates) {
        double longitude = coordinates[0];
        double latitude = coordinates[1];
        GeoPointMutable point = new GeoPointMutable();

        point.set(longitude, latitude);  // Test this method first because we use it later to reset
        assertEquals(longitude, point.longitude());
        assertEquals(latitude, point.latitude());

        point.setLongitude(longitude).setLatitude(latitude);
        assertEquals(longitude, point.longitude());
        assertEquals(latitude, point.latitude());

        point.set(0d, 0d).set(coordinates);
        assertEquals(longitude, point.longitude());
        assertEquals(latitude, point.latitude());

        point.set(0d, 0d).set(new GeoPointImmutable(coordinates));
        assertEquals(longitude, point.longitude());
        assertEquals(latitude, point.latitude());
    }

    @ParameterizedTest
    @MethodSource("invalidCoordinateSource")
    void cannotSetGeoPointMutableToInvalidCoordinates(double[] coordinates) {
        double longitude = coordinates[0];
        double latitude = coordinates[1];

        final GeoPointMutable point = new GeoPointMutable();

        assertThrows(IllegalArgumentException.class,
                () ->  point.set(longitude, latitude)
        );
        assertThrows(IllegalArgumentException.class,
                () ->  point.set(coordinates)
        );
        assertThrows(IllegalArgumentException.class,
                () ->  point.set(new double[] {0d, longitude, latitude})
        );
        assertThrows(IllegalArgumentException.class,
                () ->  point.set(new double[] {longitude})
        );
    }

    @ParameterizedTest
    @MethodSource("equalLongitudesSource")
    void canSetGeoPointMutableByBringingLongitudeInRange(double[] longitude) {
        double potentiallyOutOfRangeValue = longitude[0];
        double inRangeValue = longitude[1];
        GeoPointMutable point = new GeoPointMutable();

        point.setLongitude(potentiallyOutOfRangeValue);
        assertEquals(inRangeValue, point.longitude());
    }

    @Test
    void geoPointImmutablePreservesObjectIdentityWhenPossible() {
        GeoPointImmutable point = new GeoPointImmutable(78d, -23d);
        assertSame(point, point.getImmutable());
        assertSame(point, point.withLongitude(78d).withLatitude(-23d));
    }

    @ParameterizedTest
    @MethodSource("distanceFixtureSource")
    void canComputeCorrectGroundDistance(DistanceFixture distanceFixture) {
        String friendlyName = String.format("distance from %s to %s", distanceFixture.namePoint1, distanceFixture.namePoint2);
        double distance = distanceFixture.point1.distanceTo(distanceFixture.point2);
        assertEquals(distanceFixture.expectedDistance, distance, distanceFixture.maximumAcceptableDelta, friendlyName);
    }
   
    @ParameterizedTest
    @MethodSource("equalCoordinatesSource")
    void canCompareEqualGeoPointImmutables(double[][] coordinates) {
        GeoPointImmutable point1 = new GeoPointImmutable(coordinates[0]);
        GeoPointImmutable point2 = new GeoPointImmutable(coordinates[1]);

        assertEquals(point1, point2);
        assertEquals(point2, point1);
        assertEquals(point1.hashCode(), point2.hashCode());
        assertEquals(point1, point1);
        assertEquals(point2, point2);
    }

    @ParameterizedTest
    @MethodSource("unequalCoordinatesSource")
    @SuppressWarnings("SimplifiableAssertion")  // assertEquals(X, false) is not the same as assertTrue(X.equals())
    void canCompareUnequalGeoPointImmutables(double[][] coordinates) {
        GeoPointImmutable point1 = new GeoPointImmutable(coordinates[0]);
        GeoPointImmutable point2 = new GeoPointImmutable(coordinates[1]);

        assertNotEquals(point1, point2);

        // While hash collision here would technically be valid has per Java conventions,
        // hitting such a case in our limited test corpus would mean we have a crappy hashing function that needs fixing.
        assertNotEquals(point1.hashCode(), point2.hashCode());

        assertFalse(point1.equals(null));
        assertFalse(point2.equals(null));
        assertFalse(point2.equals(new Object()));
        assertFalse(point2.equals(new Object()));
    }

    @ParameterizedTest
    @MethodSource("equalCoordinatesSource")
    void canConvertGeoPointImmutableToArray(double[][] coordinates) {
        double longitude = coordinates[1][0], latitude = coordinates[1][1];
        GeoPointImmutable point = new GeoPointImmutable(coordinates[0]);

        double[] array = point.asArray();
        assertEquals(2, array.length);
        assertEquals(longitude, array[0], 0d);
        assertEquals(latitude, array[1], 0d);
    }

    @ParameterizedTest
    @MethodSource("equalCoordinatesSource")
    void canConvertGeoPointImmutableToVec2d(double[][] coordinates) {
        GeoPointImmutable point = new GeoPointImmutable(coordinates[0]);
        assertEquals(new Vec2dImmutable(coordinates[1]), point.asVec2d());
    }

    @ParameterizedTest
    @MethodSource("equalCoordinatesSource")
    void canConvertMutableToImmutable(double[][] coordinates) {
        GeoPointMutable mutable = new GeoPointMutable(coordinates[0]);
        GeoPointImmutable immutable = mutable.getImmutable();
        assertEquals(coordinates[1][0], immutable.longitude());
        assertEquals(coordinates[1][1], immutable.latitude());
    }

    @ParameterizedTest
    @MethodSource("equalCoordinatesSource")
    void canConvertImmutableToMutable(double[][] coordinates) {
        GeoPointImmutable immutable = new GeoPointImmutable(coordinates[0]);
        GeoPointMutable mutable = immutable.getMutable();
        assertEquals(coordinates[1][0], mutable.longitude());
        assertEquals(coordinates[1][1], mutable.latitude());
    }

    @ParameterizedTest
    @MethodSource("equalCoordinatesSource")
    void canGetReadOnlyViewFromMutable(double[][] coordinates) {
        GeoPointMutable mutable = new GeoPointMutable(coordinates[0]);
        GeoPointView view = mutable.getReadOnlyView();
        assertEquals(coordinates[1][0], view.longitude());
        assertEquals(coordinates[1][1], view.latitude());
        assertSame(view, mutable.getReadOnlyView());
    }

    @Test
    void geoPointImmutableToStringIsAccurate() {
        assertEquals(
                "GeoPointImmutable[lon=78.0°, lat=-45.0°]",
                new GeoPointImmutable(78d, -45).toString()
            );
    }

    @Test
    void geoPointMutableToStringIsAccurate() {
        assertEquals(
                "GeoPointMutable[lon=78.0°, lat=-45.0°]",
                new GeoPointMutable(78d, -45).toString()
        );
    }

    @Test
    void geoPointViewToStringIsAccurate() {
        assertEquals(
                "GeoPointView[GeoPointMutable[lon=78.0°, lat=-45.0°]]",
                new GeoPointView(new GeoPointMutable(78d, -45)).toString()
        );
    }

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

    static Stream<double[]> inRangeCoordinateSource() {
        return Stream.of(
                new double[] {-78.52671d, 37.02498d},
                new double[] {75.77848d, 13.84583d},
                new double[] {0.66979, 32.51799},
                new double[] {0d, 0d},
                new double[] {45d, 45d},
                new double[] {-45d, 45d},
                new double[] {-45d, -45d},
                new double[] {45d, -45d},
                new double[] {-180d, 0d},
                new double[] {180d, 0d},
                new double[] {-180d, -90d},
                new double[] {-180d, 90d},
                new double[] {180d, -90d},
                new double[] {180d, 90d},
                new double[] {0d, 90d},
                new double[] {0d, -90d}
        );
    }

    static Stream<double[][]> equalCoordinatesSource() {
        return Stream.of(
                new double[][] {
                        new double[]{0d, 0d},
                        new double[]{0d, 0d}
                },
                new double[][]{
                        new double[]{-0d, 0d},
                        new double[]{0d, 0d}
                },
                new double[][]{
                        new double[]{0d, -0d},
                        new double[]{0d, 0d}
                },
                new double[][]{
                        new double[]{-0d, -0d},
                        new double[]{0d, 0d}
                },
                new double[][]{
                        new double[]{-180d, 0d},
                        new double[]{-180d, 0d}
                },
                new double[][]{
                        new double[]{180d, 0d},
                        new double[]{180d, 0d}
                },
                new double[][]{
                        new double[]{270d, 0d},
                        new double[]{-90d, 0d}
                },
                new double[][]{
                        new double[]{-270d, 0d},
                        new double[]{90d, 0d}
                },
                new double[][]{
                        new double[]{360d, 0d},
                        new double[]{0d, 0d}
                },
                new double[][]{
                        new double[]{-360d, 0d},
                        new double[]{0d, 0d}
                },
                new double[][]{
                        new double[]{-540d, 0d},
                        new double[]{-180d, 0d}
                },
                new double[][]{
                        new double[]{810d, 0d},
                        new double[]{90d, 0d}
                },
                new double[][]{
                        new double[]{-810d, 0d},
                        new double[]{-90, 0d}
                },
                new double[][]{
                        new double[]{45d, 45d},
                        new double[]{45d, 45d}
                }
        );
    }

    static Stream<double[][]> unequalCoordinatesSource() {
        return Stream.of(
                new double[][]{
                        new double[]{0d, 0d},
                        new double[]{0d, 1d}
                },
                new double[][]{
                        new double[]{0d, 45d},
                        new double[]{0d, -45d}
                },
                new double[][]{
                        new double[]{-45d, 45d},
                        new double[]{45d, 45d}
                },
                new double[][]{
                        new double[]{180d, 0d},
                        new double[]{-180d, 0d}
                },
                new double[][]{
                        new double[]{0d, 90d},
                        new double[]{-180d, 90d}
                },
                new double[][]{
                        new double[]{-180d, 0d},
                        new double[]{180d, 0d}
                },
                new double[][]{
                        new double[]{180d, 0d},
                        new double[]{-180d, 0d}
                },
                new double[][]{
                        new double[]{180d, 90d},
                        new double[]{-180d, 90d}
                },
                new double[][]{
                        new double[]{180d, -90d},
                        new double[]{-180d, -90d}
                },
                new double[][]{
                        new double[]{180d, -45d},
                        new double[]{-180d, -45d}
                }
        );
    }

    static Stream<double[]> equalLongitudesSource() {
        return equalCoordinatesSource().map(coordinates -> new double[] { coordinates[0][0], coordinates[1][0] });
    }

    static Stream<double[]> invalidCoordinateSource() {
        return Stream.of(
                new double[] {0d, 91d},
                new double[] {0d, -91d},
                new double[] {0d, 180d},
                new double[] {0d, -180d},
                new double[] {Double.NaN, 0d},
                new double[] {0d, Double.NaN},
                new double[] {Double.POSITIVE_INFINITY, 0d},
                new double[] {Double.NEGATIVE_INFINITY, 0d},
                new double[] {0d, Double.POSITIVE_INFINITY},
                new double[] {0d, Double.NEGATIVE_INFINITY}
        );
    }

    static class DistanceFixture {
        GeoPoint point1, point2;
        String namePoint1, namePoint2;
        double expectedDistance, maximumAcceptableDelta;

        @Override
        public String toString() {
            return String.format(
                    Locale.US,
                    "%s to %s is %.3fm (±%.2fm) (%s to %s)",
                    this.namePoint1, this.namePoint2,
                    this.expectedDistance, this.maximumAcceptableDelta,
                    this.point1, this.point2
            );
        }
    }

    static DistanceFixture distance(String name1, GeoPoint point1, String name2, GeoPoint point2, double expectedDistance, double maximumAcceptableDelta) {
        DistanceFixture f = new DistanceFixture();
        f.point1 = point1;
        f.point2 = point2;
        f.namePoint1 = name1;
        f.namePoint2 = name2;
        f.expectedDistance = expectedDistance;
        f.maximumAcceptableDelta = maximumAcceptableDelta;
        return f;
    }

    static Stream<DistanceFixture> distanceFixtureSource() {
        return Stream.of(
                distance("Sidney", SIDNEY, "Seattle" ,SEATTLE, 12470810d, 1000d),
                distance("Beijing", BEIJING, "Seattle", SEATTLE, 8689000d, 1000d),
                distance("Paris", PARIS, "New York", NEW_YORK, 5837000d, 1000d),
                distance("Paris", PARIS, "London", LONDON, 344240d, 100d),
                distance("Arc de triomphe", ARC_DE_TRIOMPHE, "Grande arche", ARCHE_DE_LA_DEFENSE, 4785d, 10d),
                distance("Notre Dame north tower", NOTRE_DAME_NORTH_TOWER, "Notre Dame south tower", NOTRE_DAME_SOUTH_TOWER, 51d, 1d),
                distance("Paris", PARIS, "Paris", PARIS, 0d, 0d),
                distance("New York", NEW_YORK, "New York", NEW_YORK, 0d, 0d),
                distance("Sidney", SIDNEY, "Sidney", SIDNEY, 0d, 0d),
                distance("Beijing", BEIJING, "Beijing", BEIJING, 0d, 0d)
        );
    }

}
