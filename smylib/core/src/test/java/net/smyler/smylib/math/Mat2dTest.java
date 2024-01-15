package net.smyler.smylib.math;

import net.smyler.smylib.math.Mat2d;
import net.smyler.smylib.math.Vec2dImmutable;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class Mat2dTest {

    @Test
    public void testConstructors() {
        Mat2d mat = new Mat2d(1, 2, 3, 4);
        assertEquals(mat.column1().x, mat.line1().x);
        assertEquals(mat.column1().y, mat.line2().x);
        assertEquals(mat.column2().x, mat.line1().y);
        assertEquals(mat.column2().y, mat.line2().y);
        assertEquals(1, mat.line1().x);
        assertEquals(2, mat.line1().y);
        assertEquals(3, mat.line2().x);
        assertEquals(4, mat.line2().y);
    }

    @Test
    public void testScale() {
        Mat2d mat = new Mat2d(1, 2, 3, 4);
        mat = mat.scale(2);
        assertEquals(2, mat.line1().x);
        assertEquals(4, mat.line1().y);
        assertEquals(6, mat.line2().x);
        assertEquals(8, mat.line2().y);
    }

    @Test
    public void testAdd() {
        Mat2d mat1 = new Mat2d(1, 2, 3, 4);
        Mat2d mat2 = new Mat2d(2, 3, 4, 5);
        Mat2d mat = mat1.add(mat2);
        assertEquals(3, mat.line1().x);
        assertEquals(5, mat.line1().y);
        assertEquals(7, mat.line2().x);
        assertEquals(9, mat.line2().y);
    }

    @Test
    public void testProd() {
        Mat2d mat1 = new Mat2d(1, 2,
                               3, 4);
        Mat2d mat2 = new Mat2d(2, 3,
                               4, 5);
        Mat2d mat = mat1.prod(mat2);
        assertEquals(10, mat.line1().x);
        assertEquals(13, mat.line1().y);
        assertEquals(22, mat.line2().x);
        assertEquals(29, mat.line2().y);
        Vec2dImmutable vec = new Vec2dImmutable(-1, 1);
        vec = mat.prod(vec);
        assertEquals(3, vec.x);
        assertEquals(7, vec.y);
    }

    @Test
    public void testDeterminant() {
        Mat2d mat = new Mat2d(1, 2,
                3, 4);
        assertEquals(-2, mat.determinant());
    }

    @Test
    public void testTranspose() {
        Mat2d mat = new Mat2d(1, 2, 3, 4);
        mat = mat.transpose();
        assertEquals(1, mat.line1().x);
        assertEquals(3, mat.line1().y);
        assertEquals(2, mat.line2().x);
        assertEquals(4, mat.line2().y);
    }

}
