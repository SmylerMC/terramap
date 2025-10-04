package fr.thesmyler.terramap;

import net.smyler.terramap.geo.GeoPoint;
import net.smyler.smylib.math.Vec2d;

/**
 * Useful assertions for Terramap specific objects.
 *
 * @author Smlyer
 */
public final class Assertions {

    /**
     * Asserts the distance between two {@link GeoPoint} is small enough that they can be considered equals.
     *
     * @param expected  expected point
     * @param actual    actual point
     * @param distance  acceptable distance between the points, in meters
     */
    public static void assertEquals(Vec2d expected, Vec2d actual, double distance) {
        org.junit.jupiter.api.Assertions.assertEquals(0d, expected.distanceTo(actual), distance,
                "Vectors are different. Expected: " + expected + " Actual: " + actual);
    }

    /**
     * Asserts the distance between two {@link GeoPoint} is small enough that they can be considered equals.
     *
     * @param expected  expected point
     * @param actual    actual point
     * @param distance  acceptable distance between the points, in meters
     */
    public static void assertEquals(GeoPoint expected, GeoPoint actual, double distance) {
        org.junit.jupiter.api.Assertions.assertEquals(0d, expected.distanceTo(actual), distance,
                "GeoPoints are different. Expected: " + expected + " Actual: " + actual);
    }

}