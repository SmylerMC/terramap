package fr.thesmyler.terramap.util.math;


import net.buildtheearth.terraplusplus.dep.net.daporkchop.lib.common.util.PValidation;

/**
 * A mutable implementation of {@link Vec2d}
 * Any method from this class that returns a {@link Vec2d} modifies this vector and returns itself.
 *
 * @author SmylerMC
 */
public final class Vec2dMutable extends Vec2dAbstract<Vec2dMutable> {

    public double x, y;
    private Vec2dReadOnly readOnly;

    public Vec2dMutable(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public Vec2dMutable(double[] coordinates) {
        PValidation.checkArg(coordinates.length == 2, "Expected a double array of length 2");
        this.x = coordinates[0];
        this.y = coordinates[1];
    }

    public Vec2dMutable() {
        this(0, 0);
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    public Vec2dMutable set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    public Vec2dMutable set(Vec2d<?> vector) {
        this.x = vector.x();
        this.y = vector.y();
        return this;
    }

    public Vec2dMutable set(double[] array) {
        if (array.length != 2) {
            throw new IllegalArgumentException("Only a double array of length 2 can be used a 2d vector");
        }
        this.x = array[0];
        this.y = array[1];
        return this;
    }

    @Override
    public Vec2dMutable scale(double factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }

    @Override
    public Vec2dMutable downscale(double factor) {
        this.x /= factor;
        this.y /= factor;
        return this;
    }

    @Override
    public Vec2dMutable add(Vec2d<?> vector) {
        this.x += vector.x();
        this.y += vector.y();
        return this;
    }

    @Override
    public Vec2dMutable add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public Vec2dMutable subtract(Vec2d<?> vector) {
        this.x -= vector.x();
        this.y -= vector.y();
        return this;
    }

    @Override
    public Vec2dMutable subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    @Override
    public Vec2dMutable hadamardProd(Vec2d<?> vector) {
        this.x *= vector.x();
        this.y *= vector.y();
        return this;
    }

    @Override
    public Vec2dMutable hadamardProd(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    public Vec2dMutable apply(Mat2d matrix) {
        double x = this.x;
        this.x = x * matrix.line1().x + this.y * matrix.line1().y;
        this.y = x * matrix.line2().x + this.y * matrix.line2().y;
        return this;
    }

    @Override
    public Vec2dMutable normalize() {
        double norm = this.norm();
        if(norm == 0d) throw new ArithmeticException("Cannot normalize null vector");
        return this.scale(1d / norm);
    }

    public Vec2dReadOnly getReadOnly() {
        if (this.readOnly == null) this.readOnly = new Vec2dReadOnly(this);
        return this.readOnly;
    }

    @Override
    public Vec2dMutable getMutable() {
        return this;
    }

}
