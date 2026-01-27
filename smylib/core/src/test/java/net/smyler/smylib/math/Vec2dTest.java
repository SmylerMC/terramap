package net.smyler.smylib.math;

import org.junit.jupiter.api.Test;

import static java.lang.Math.sqrt;
import static net.smyler.smylib.math.Vec2dImmutable.*;
import static org.junit.jupiter.api.Assertions.*;

public class Vec2dTest {

    @Test
    public void canCreateAndSetVec2dMutable() {
        Vec2dMutable vec = new Vec2dMutable();
        assertVec2d(0d, 0d, vec);

        vec.set(-6d, 12.6d);
        assertVec2d(-6d, 12.6d, vec);

        vec.set(new Vec2dImmutable(16d, 78.9d));
        assertVec2d(16d, 78.9d, vec);

        vec.set(new double[] {20d, -43d});
        assertVec2d(20d, -43d, vec);

        vec.setX(34d);
        assertVec2d(34d, -43d, vec);

        vec.setY(56d);
        assertVec2d(34d, 56d, vec);

        assertVec2d(20d, -43d, new Vec2dMutable(new double[] {20d, -43d}));

        assertThrows(IllegalArgumentException.class, () -> vec.set(new double[] {1d, 2d, 3d}));
        assertThrows(IllegalArgumentException.class, () -> vec.set(new double[] {1d}));
        assertThrows(IllegalArgumentException.class, () -> new Vec2dMutable(new double[] {1d}));
        assertThrows(IllegalArgumentException.class, () -> new Vec2dMutable(new double[] {1d, 2d, 3d}));
    }

    @Test
    public void canCreateVec2dImmutable() {
        assertVec2d(2d, 4d, new Vec2dImmutable(2d, 4d));
        assertVec2d(2d, 4d, new Vec2dImmutable(new double[]{2d, 4d}));

        assertVec2d(3d, 4d, new Vec2dImmutable(2d, 4d).withX(3d));
        assertVec2d(2d, 6d, new Vec2dImmutable(2d, 4d).withY(6d));

        assertThrows(IllegalArgumentException.class, () -> new Vec2dImmutable(new double[] {1d}));
        assertThrows(IllegalArgumentException.class, () -> new Vec2dImmutable(new double[] {1d, 2d, 3d}));
    }

    @Test
    public void canComputeVec2dNorms() {
        Vec2dMutable vec = new Vec2dMutable(-5d, 8.5d);
        assertEquals(sqrt(5d*5d + 8.5d*8.5d), vec.norm());
        assertEquals(5d*5d + 8.5d*8.5d, vec.normSquared());
        assertEquals(5d + 8.5d, vec.taxicabNorm());
        assertEquals(8.5d, vec.maximumNorm());
    }

    @Test
    public void canAddToVec2dMutable() {
        Vec2dMutable vec = new Vec2dMutable(-5d, 8.5d);

        vec.add(7d, -6d);
        assertVec2d(2d, 2.5d, vec);

        vec.add(new Vec2dImmutable(9d, .5d));
        assertVec2d(11d, 3d, vec);
    }

    @Test
    public void canAddToVec2dImmutable() {
        Vec2dImmutable vec = new Vec2dImmutable(-5d, 8.5d);

        vec = vec.add(7d, -6d);
        assertVec2d(2d, 2.5d, vec);

        vec = vec.add(new Vec2dMutable(9d, .5d));
        assertVec2d(11d, 3d, vec);
    }

    @Test
    public void canAddToVec2dView() {
        Vec2dMutable mutable = new Vec2dMutable(-5d, 8.5d);
        Vec2dView vec = mutable.getReadOnlyView();

        assertVec2d(2d, 2.5d, vec.add(7d, -6d));

        mutable.add(7d, -6d);
        assertVec2d(11d, 3d, vec.add(new Vec2dMutable(9d, .5d)));
    }

    @Test
    public void canSubtractFromVec2dMutable() {
        Vec2dMutable vec = new Vec2dMutable(-5d, 8.5d);

        vec.subtract(7d, -6d);
        assertVec2d(-12d, 14.5d, vec);

        vec.subtract(new Vec2dImmutable(9d, .5d));
        assertVec2d(-21d, 14d, vec);
    }

    @Test
    public void canSubtractFromVec2dImmutable() {
        Vec2dImmutable vec = new Vec2dImmutable(-5d, 8.5d);

        vec = vec.subtract(7d, -6d);
        assertVec2d(-12d, 14.5d, vec);

        vec = vec.subtract(new Vec2dImmutable(9d, .5d));
        assertVec2d(-21d, 14d, vec);
    }

    @Test
    public void canSubtractFromVec2dView() {
        Vec2dMutable mutable = new Vec2dMutable(-5d, 8.5d);
        Vec2dView vec = mutable.getReadOnlyView();

        assertVec2d(-12d, 14.5d, vec.subtract(7d, -6d));

        mutable.subtract(7d, -6d);
        assertVec2d(-21d, 14d, vec.subtract(new Vec2dMutable(9d, .5d)));
    }

    @Test
    public void canScaleVec2dMutable() {
        Vec2dMutable vec = new Vec2dMutable(1d, -1d);

        vec.scale(7d);
        assertVec2d(7d, -7d, vec);

        vec.downscale(2);
        assertVec2d(3.5d, -3.5d, vec);
    }

    @Test
    public void canScaleVec2dView() {
        Vec2dMutable mutable = new Vec2dMutable(1d, -1d);
        Vec2dView vec = mutable.getReadOnlyView();

        assertVec2d(7d, -7d, vec.scale(7d));

        mutable.scale(7d);
        assertVec2d(3.5d, -3.5d, vec.downscale(2d));
    }

    @Test
    public void canScaleVec2dImmutable() {
        Vec2dImmutable vec = new Vec2dImmutable(1d, -1d);

        vec = vec.scale(7d);
        assertVec2d(7d, -7d, vec);

        vec = vec.downscale(2);
        assertVec2d(3.5d, -3.5d, vec);
    }

    @Test
    public void canComputeVec2dDotProduct() {
        Vec2dMutable vec = new Vec2dMutable(8d, -9d);
        assertEquals(-20d, vec.dotProd(2d, 4d));
        assertEquals(-20d, vec.dotProd(new Vec2dImmutable(2d, 4d)));
    }

    @Test
    public void canComputeVec2dCrossProduct() {
        Vec2dMutable vec = new Vec2dMutable(8d, -9d);
        assertEquals(50d, vec.crossProd(2d, 4d));
        assertEquals(50d, vec.crossProd(new Vec2dImmutable(2d, 4d)));
    }

    @Test
    public void canComputeHadamardProductOfVec2dMutable() {
        Vec2dMutable vec = new Vec2dMutable(8d, -6d);

        vec.hadamardProd(5d, -3d);
        assertVec2d(40d, 18d, vec);

        vec.hadamardProd(new Vec2dImmutable(-.5d, 3d));
        assertVec2d(-20d, 54d, vec);
    }

    @Test
    public void canComputeHadamardProductOfVec2dImmutable() {
        Vec2dImmutable vec = new Vec2dImmutable(8d, -6d);

        vec = vec.hadamardProd(5d, -3d);
        assertVec2d(40d, 18d, vec);

        vec = vec.hadamardProd(new Vec2dImmutable(-.5d, 3d));
        assertVec2d(-20d, 54d, vec);
    }

    @Test
    public void canComputeHadamardProductOfVec2dView() {
        Vec2dMutable mutable = new Vec2dMutable(8d, -6d);
        Vec2dView vec = mutable.getReadOnlyView();

        assertVec2d(40d, 18d, vec.hadamardProd(5d, -3d));

        mutable.hadamardProd(5d, -3d);
        assertVec2d(-20d, 54d, vec.hadamardProd(new Vec2dImmutable(-.5d, 3d)));
    }

    @Test
    public void canNormalizeVec2dMutable() {
        Vec2dMutable vec = new Vec2dMutable(-1d, 1d);

        vec.normalize();

        final double halfSqrt2 = sqrt(2d) / 2d;
        assertEquals(-halfSqrt2, vec.x(), 1e-8d);
        assertEquals(halfSqrt2, vec.y(), 1e-8d);
        assertThrows(ArithmeticException.class, () -> vec.set(Vec2dImmutable.NULL).normalize());
    }

    @Test
    public void canNormalizeVec2dImmutable() {
        Vec2dImmutable vec = new Vec2dImmutable(-1d, 1d);

        vec = vec.normalize();

        final double halfSqrt2 = sqrt(2d) / 2d;
        assertEquals(-halfSqrt2, vec.x(), 1e-8d);
        assertEquals(halfSqrt2, vec.y(), 1e-8d);
        assertThrows(ArithmeticException.class, Vec2dImmutable.NULL::normalize);
    }

    @Test
    public void canNormalizeVec2dView() {
        Vec2dMutable mutable = new Vec2dMutable(-1d, 1d);
        Vec2dView vec = mutable.getReadOnlyView();

        Vec2dImmutable normalized = vec.normalize();

        final double halfSqrt2 = sqrt(2d) / 2d;
        assertEquals(-halfSqrt2, normalized.x(), 1e-8d);
        assertEquals(halfSqrt2, normalized.y(), 1e-8d);

        mutable.set(0d, 0d);
        assertThrows(ArithmeticException.class, vec::normalize);
    }

    @Test
    public void canApply2dMatrixToVec2dMutable() {
        Mat2d matrix = new Mat2d(1d, 2d, 3d, 4d);
        Vec2dMutable vec =  new Vec2dMutable(-2d, 3d).apply(matrix);
        assertVec2d(4d, 6d, vec);
    }

    @Test
    public void canComputeDistanceBetweenVec2d() {
        Vec2dMutable vec = new Vec2dMutable(-2d, 1d);
        assertEquals(5d, vec.distanceTo(new Vec2dImmutable(2d, 4d)));
        assertEquals(5d, vec.distanceTo(2d, 4d));
    }

    @Test
    public void canUseEqualsAndHashcodeOnVec2dImmutable() {
        Vec2d imu1 = new Vec2dImmutable(4d, -6d);
        Vec2d imu2 = new Vec2dImmutable(4d, -6d);
        Vec2d imu3 = new Vec2dImmutable(4d, -8d);
        Vec2d imu4 = new Vec2dImmutable(7d, -6d);
        Vec2d imu5 = new Vec2dImmutable(9d, -0d);
        Vec2d mut = new Vec2dMutable(4d, -6d);

        assertEquals(imu1, imu1);
        assertEquals(imu1, imu2);
        assertNotEquals(imu1, mut);
        assertNotEquals(imu1, imu3);
        assertNotEquals(imu1, imu4);
        assertNotEquals(imu1, imu5);
        assertEquals(imu1.hashCode(), imu2.hashCode());
        assertFalse(imu1.equals(null));
    }

    @Test
    public void canCheckThatVec2dIsFinite() {
        Vec2dMutable vec = new Vec2dMutable(-2d, 3d);
        assertTrue(vec.isFinite());

        vec.set(Double.NaN, 0d);
        assertFalse(vec.isFinite());

        vec.set(10d, Double.POSITIVE_INFINITY);
        assertFalse(vec.isFinite());

        vec.set(Double.NaN, Double.POSITIVE_INFINITY);
        assertFalse(vec.isFinite());
    }

    @Test
    public void vec2dMutableCachesItsReadOnlyView() {
        Vec2dMutable mutable = new Vec2dMutable(-2d, 3d);
        Vec2dView view1 = mutable.getReadOnlyView();
        mutable.set(8d, 3d);
        Vec2dView view2 = mutable.getReadOnlyView();

        assertSame(view1, view2);
    }

    @Test
    public void wellKnownVectorsHaveCorrectValues() {
        assertVec2d(0d, 0d, NULL);
        assertVec2d(1d, 0d, UNIT_X);
        assertVec2d(0d, 1d, UNIT_Y);
    }

    @Test
    public void toStringReturnsExpectedString() {
        assertEquals("Vec2dMutable[0.0; 0.0]", new Vec2dMutable(0d, 0d).toString());
        assertEquals("Vec2dImmutable[0.0; 0.0]", new Vec2dImmutable(0d, 0d).toString());
        assertEquals("View[Vec2dMutable[0.0; 0.0]]", new Vec2dMutable(0d, 0d).getReadOnlyView().toString());
    }

    @Test
    public void canCopyVec2dMutable() {
        Vec2dMutable vec1 = new Vec2dMutable(-2d, 3d);
        Vec2dMutable vec2 = vec1.copy();
        vec1.set(0d, 0d);

        assertVec2d(-2d, 3d, vec2.getReadOnlyView());
    }

    private static void assertVec2d(double expectedX, double expectedY, Vec2d actual) {
        assertEquals(expectedX, actual.x(), "vector X component did not match");
        assertEquals(expectedY, actual.y(), "vector Y component did not match");
        assertArrayEquals(new double[] {expectedX, expectedY}, actual.asArray(), "vector array did not match");
        assertEquals(new Vec2dImmutable(expectedX, expectedY), actual.getImmutable(), "mutable vector did not match");
    }

}
