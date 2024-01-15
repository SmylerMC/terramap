package net.smyler.smylib.math;

import static java.lang.Math.cos;
import static java.lang.Math.sin;

/**
 * A 2 by 2 double matrix 
 * 
 * @author SmylerMC
 *
 */
public class Mat2d {

    public static final Mat2d IDENTITY = new Mat2d(1d, 0d, 0d, 1d);
    public static final Mat2d NULL = new Mat2d(0d, 0d, 0d, 0d);

    private final Vec2dImmutable col1;
    private final Vec2dImmutable col2;
    private final Vec2dImmutable lig1;
    private final Vec2dImmutable lig2;


    public Mat2d(double xx, double xy, double yx, double yy) {
        this.col1 = new Vec2dImmutable(xx, yx);
        this.col2 = new Vec2dImmutable(xy, yy);
        this.lig1 = new Vec2dImmutable(xx, xy);
        this.lig2 = new Vec2dImmutable(yx, yy);
    }

    public Mat2d scale(double factor) {
        return new Mat2d(
                this.lig1.x * factor, this.lig1.y * factor,
                this.lig2.x * factor, this.lig2.y * factor
                );
    }

    public Mat2d add(Mat2d other) {
        return new Mat2d(
                this.lig1.x + other.lig1.x, this.lig1.y + other.lig1.y,
                this.lig2.x + other.lig2.x, this.lig2.y + other.lig2.y
                );
    }

    public Mat2d prod(Mat2d other) {
        return new Mat2d(
                this.lig1.x*other.lig1.x + this.lig1.y*other.lig2.x, this.lig1.x*other.lig1.y + this.lig1.y*other.lig2.y,
                this.lig2.x*other.lig1.x + this.lig2.y*other.lig2.x, this.lig2.x*other.lig1.y + this.lig2.y*other.lig2.y
                );
    }

    public Vec2dImmutable prod(Vec2dImmutable vec) {
        return new Vec2dImmutable(
                this.lig1.x*vec.x + this.lig1.y*vec.y,
                this.lig2.x*vec.x + this.lig2.y*vec.y
                );
    }

    public double determinant() {
        return this.lig1.x*this.lig2.y - this.lig1.y*this.lig2.x;
    }

    public Mat2d inverse() {
        double det = this.determinant();
        if(det == 0) throw new IllegalStateException("Matrix has no inverse: determinant is 0");
        return new Mat2d(
                this.lig2.y, -this.lig2.x,
                -this.lig1.y, this.lig1.x)
                .scale(1d / det);
    }

    public Mat2d transpose() {
        return new Mat2d(
                this.lig1.x, this.lig2.x,
                this.lig1.y, this.lig2.y);
    }

    public Vec2dImmutable column1() {
        return this.col1;
    }

    public Vec2dImmutable column2() {
        return this.col2;
    }

    public Vec2dImmutable line1() {
        return this.lig1;
    }

    public Vec2dImmutable line2() {
        return this.lig2;
    }

    public static Mat2d forRotation(double radAngle) {
        double c = cos(radAngle);
        double s = sin(radAngle);
        return new Mat2d(
                c, -s,
                s, c
                );
    }

    public static Mat2d forSymmetry(double radAngle) {
        double c = cos(radAngle);
        double s = sin(radAngle);
        return new Mat2d(
                c, s,
                s, -c
                );
    }

}
