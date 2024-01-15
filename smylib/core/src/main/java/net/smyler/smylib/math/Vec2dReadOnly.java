package net.smyler.smylib.math;

/**
 * A vector that provides read only access to a delegate vector.
 *
 * @author SmylerMC
 */
public final class Vec2dReadOnly extends Vec2dAbstract<Vec2dImmutable> {

    private final Vec2d<?> delegate;

    public Vec2dReadOnly(Vec2d<?> delegate) {
        this.delegate = delegate;
    }

    @Override
    public double x() {
        return this.delegate.x();
    }

    @Override
    public double y() {
        return this.delegate.y();
    }

    @Override
    public Vec2dImmutable scale(double factor) {
        return new Vec2dImmutable(this.delegate.x() * factor, this.delegate.y() * factor);
    }

    @Override
    public Vec2dImmutable downscale(double factor) {
        return new Vec2dImmutable(this.delegate.x() / factor, this.delegate.y() / factor);
    }

    @Override
    public Vec2dImmutable normalize() {
        double norm = this.norm();
        if(norm == 0d) throw new ArithmeticException("Cannot normalize null vector");
        return this.scale(1d / norm);
    }

    @Override
    public Vec2dImmutable add(Vec2d<?> other) {
        return new Vec2dImmutable(this.delegate.x() + other.x(), this.delegate.y() + other.y());
    }

    @Override
    public Vec2dImmutable add(double x, double y) {
        return new Vec2dImmutable(this.delegate.x() + x, this.delegate.y() + y);
    }

    @Override
    public Vec2dImmutable subtract(Vec2d<?> other) {
        return new Vec2dImmutable(this.delegate.x() - other.x(), this.delegate.y() - other.y());
    }

    @Override
    public Vec2dImmutable subtract(double x, double y) {
        return new Vec2dImmutable(this.delegate.x() - x, this.delegate.y() - y);
    }

    @Override
    public Vec2dImmutable hadamardProd(Vec2d<?> other) {
        return new Vec2dImmutable(this.delegate.x()*other.x(), this.delegate.y()*other.y());
    }

    @Override
    public Vec2dImmutable hadamardProd(double x, double y) {
        return new Vec2dImmutable(this.delegate.x()*x, this.delegate.y()*y);
    }

}
