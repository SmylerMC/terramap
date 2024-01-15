package fr.thesmyler.terramap.util.math;

import net.buildtheearth.terraplusplus.dep.net.daporkchop.lib.common.util.PValidation;

/**
 * An unmutable implementation of {@link Vec2d}.
 *
 * @author SmylerMC
 */
public final class Vec2dImmutable extends Vec2dAbstract<Vec2dImmutable> {
    
    public static final Vec2dImmutable NULL = new Vec2dImmutable(0d, 0d);
    public static final Vec2dImmutable UNIT_X = new Vec2dImmutable(1d, 0d);
    public static final Vec2dImmutable UNIT_Y = new Vec2dImmutable(0d, 1d);

    public final double x;
    public final double y;

    public Vec2dImmutable(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vec2dImmutable(double[] coords) {
        PValidation.checkArg(coords.length == 2, "Expected a double array of length 2");
        this.x = coords[0];
        this.y = coords[1];
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    @Override
    public Vec2dImmutable scale(double factor) {
        return new Vec2dImmutable(this.x*factor, this.y*factor);
    }

    @Override
    public Vec2dImmutable downscale(double factor) {
        return new Vec2dImmutable(this.x / factor, this.y / factor);
    }

    @Override
    public Vec2dImmutable normalize() {
        double norm = this.norm();
        if(norm == 0d) throw new ArithmeticException("Cannot normalize null vector");
        return this.scale(1d / norm);
    }

    @Override
    public Vec2dImmutable add(Vec2d<?> other) {
        return new Vec2dImmutable(this.x + other.x(), this.y + other.y());
    }

    @Override
    public Vec2dImmutable add(double x, double y) {
        return new Vec2dImmutable(this.x + x, this.y + y);
    }

    @Override
    public Vec2dImmutable subtract(Vec2d<?> other) {
        return new Vec2dImmutable(this.x - other.x(), this.y - other.y());
    }

    @Override
    public Vec2dImmutable subtract(double x, double y) {
        return new Vec2dImmutable(this.x - x, this.y - y);
    }

    @Override
    public Vec2dImmutable hadamardProd(Vec2d<?> other) {
        return new Vec2dImmutable(this.x*other.x(), this.y*other.y());
    }

    @Override
    public Vec2dImmutable hadamardProd(double x, double y) {
        return new Vec2dImmutable(this.x*x, this.y*y);
    }

    @Override
    public Vec2dImmutable getImmutable() {
        return this;
    }

}
