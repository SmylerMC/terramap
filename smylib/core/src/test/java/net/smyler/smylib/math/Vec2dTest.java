package net.smyler.smylib.math;

import net.smyler.smylib.math.Mat2d;
import net.smyler.smylib.math.Vec2d;
import net.smyler.smylib.math.Vec2dImmutable;
import net.smyler.smylib.math.Vec2dMutable;
import org.junit.jupiter.api.Test;

import static java.lang.Math.sqrt;
import static org.junit.jupiter.api.Assertions.*;

public class Vec2dTest {

    @Test
    public void testGetSet() {
        Vec2dMutable calc = new Vec2dMutable(0d, 0d);
        assertEquals(0d, calc.x());
        assertEquals(0d, calc.y());
        assertArrayEquals(new double[] {0d, 0d}, calc.asArray());
        assertEquals(new Vec2dImmutable(0d, 0d), calc);
        calc.set(-6d, 12.6d);
        assertEquals(-6d, calc.x());
        assertEquals(12.6d, calc.y());
        assertArrayEquals(new double[] {-6d, 12.6d}, calc.asArray());
        assertEquals(new Vec2dImmutable(-6d, 12.6d), calc);
        calc.set(new Vec2dImmutable(16d, 78.9d));
        assertEquals(16d, calc.x());
        assertEquals(78.9, calc.y());
        assertArrayEquals(new double[] {16d, 78.9d}, calc.asArray());
        assertEquals(new Vec2dImmutable(16d, 78.9d), calc);
        calc.set(new double[] {20d, -43d});
        assertEquals(20d, calc.x());
        assertEquals(-43d, calc.y());
        assertArrayEquals(new double[] {20d, -43d}, calc.asArray());
        assertEquals(new Vec2dImmutable(20d, -43d), calc);
        assertThrows(IllegalArgumentException.class, () -> calc.set(new double[] {1d, 2d, 3d}));
        assertThrows(IllegalArgumentException.class, () -> calc.set(new double[] {1d}));
    }

    @Test
    public void testNorms() {
        Vec2dMutable calc = new Vec2dMutable(-5d, 8.5d);
        assertEquals(sqrt(5d*5d + 8.5d*8.5d), calc.norm());
        assertEquals(5d*5d + 8.5d*8.5d, calc.normSquared());
        assertEquals(5d + 8.5d, calc.taxicabNorm());
        assertEquals(8.5d, calc.maximumNorm());
    }

    @Test
    public void testAdd() {
        Vec2dMutable calc = new Vec2dMutable(-5d, 8.5d);
        calc.add(7d, -6d);
        assertEquals(2d, calc.x());
        assertEquals(2.5d, calc.y());
        calc.add(new Vec2dImmutable(9d, .5d));
        assertEquals(11d, calc.x());
        assertEquals(3d, calc.y());
    }

    @Test
    public void testSubtract() {
        Vec2dMutable calc = new Vec2dMutable(-5d, 8.5d);
        calc.subtract(7d, -6d);
        assertEquals(-12d, calc.x());
        assertEquals(14.5d, calc.y());
        calc.subtract(new Vec2dImmutable(9d, .5d));
        assertEquals(-21d, calc.x());
        assertEquals(14d, calc.y());
    }

    @Test
    public void testScaling() {
        Vec2dMutable calc = new Vec2dMutable(1d, -1d);
        calc.scale(7d);
        assertEquals(7d, calc.x());
        assertEquals(-7d, calc.y());
        calc.downscale(2);
        assertEquals(3.5d, calc.x());
        assertEquals(-3.5d, calc.y());
    }

    @Test
    public void testDotProd() {
        Vec2dMutable calc = new Vec2dMutable(8d, -9d);
        assertEquals(-20d, calc.dotProd(2d, 4d));
        assertEquals(-20d, calc.dotProd(new Vec2dImmutable(2d, 4d)));
    }

    @Test
    public void testCrossProd() {
        Vec2dMutable calc = new Vec2dMutable(8d, -9d);
        assertEquals(50d, calc.crossProd(2d, 4d));
        assertEquals(50d, calc.crossProd(new Vec2dImmutable(2d, 4d)));
    }

    @Test
    public void testHadamardProd() {
        Vec2dMutable calc = new Vec2dMutable(8d, -6d);
        calc.hadamardProd(5d, -3d);
        assertEquals(40d, calc.x());
        assertEquals(18d, calc.y());
        calc.hadamardProd(new Vec2dImmutable(-.5d, 3d));
        assertEquals(-20d, calc.x());
        assertEquals(54d, calc.y());
    }

    @Test
    public void testNormalize() {
        Vec2dMutable calc = new Vec2dMutable(-1d, 1d);
        calc.normalize();
        final double halfSqrt2 = sqrt(2d) / 2d;
        assertEquals(-halfSqrt2, calc.x(), 1e-8d);
        assertEquals(halfSqrt2, calc.y(), 1e-8d);
        assertThrows(ArithmeticException.class, () -> calc.set(Vec2dImmutable.NULL).normalize());
    }

    @Test
    public void testApplyMatrix() {
        Mat2d matrix = new Mat2d(1d, 2d, 3d, 4d);
        Vec2dMutable calc =  new Vec2dMutable(-2d, 3d).apply(matrix);
        assertEquals(4d, calc.x());
        assertEquals(6d, calc.y());
    }

    @Test
    public void testDistanceTo() {
        Vec2dMutable calc = new Vec2dMutable(-2d, 1d);
        assertEquals(5d, calc.distanceTo(new Vec2dImmutable(2d, 4d)));
    }

    @Test
    public void testEqualsAndHashcode() {
        Vec2d imu = new Vec2dImmutable(4d, -6d);
        Vec2d mut = new Vec2dMutable(4d, -6d);
        assertEquals(imu, mut);
        assertEquals(imu.hashCode(), mut.hashCode());
    }

}
