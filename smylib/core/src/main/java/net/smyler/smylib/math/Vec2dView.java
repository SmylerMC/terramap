package net.smyler.smylib.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

/**
 * An unmodifiable two-dimensional double-precision floating point vector providing
 * a view onto a potentially-mutable delegate vector.
 * <br>
 * This lets classes that use mutable vectors internally expose their state without
 * risking the caller modifying it by calling a potentially stateful method.
 * Any operation on this vector that results in a vector will therefore leave the
 * delegate vector untouched and return a new {@link Vec2dImmutable} instance.
 *
 * @see Vec2dMutable#getReadOnlyView()
 *
 * @author Smyler
 */
public final class Vec2dView implements Vec2d {

    private final @NotNull Vec2d delegate;

    /**
     * Creates a new {@link Vec2dView} backed by a given delegate {@link Vec2d}.
     *
     * @param delegate the delegate {@link Vec2d}
     */
    public Vec2dView(@NotNull Vec2d delegate) {
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
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Vec2dImmutable scale(double factor) {
        return new Vec2dImmutable(this.delegate.x() * factor, this.delegate.y() * factor);
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Vec2dImmutable downscale(double factor) {
        return new Vec2dImmutable(this.delegate.x() / factor, this.delegate.y() / factor);
    }

    @Override
    @Contract(value = "-> new", pure = true)
    public @NotNull Vec2dImmutable normalize() {
        double norm = this.norm();
        if (norm == 0d) {
            throw new ArithmeticException("Cannot normalize null vector");
        }
        return this.scale(1d / norm);
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Vec2dImmutable add(@NotNull Vec2d other) {
        return new Vec2dImmutable(this.delegate.x() + other.x(), this.delegate.y() + other.y());
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public @NotNull Vec2dImmutable add(double x, double y) {
        return new Vec2dImmutable(this.delegate.x() + x, this.delegate.y() + y);
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Vec2dImmutable subtract(@NotNull Vec2d other) {
        return new Vec2dImmutable(this.delegate.x() - other.x(), this.delegate.y() - other.y());
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public @NotNull Vec2dImmutable subtract(double x, double y) {
        return new Vec2dImmutable(this.delegate.x() - x, this.delegate.y() - y);
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Vec2dImmutable hadamardProd(@NotNull Vec2d other) {
        return new Vec2dImmutable(this.delegate.x() * other.x(), this.delegate.y() * other.y());
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public @NotNull Vec2dImmutable hadamardProd(double x, double y) {
        return new Vec2dImmutable(this.delegate.x() * x, this.delegate.y() * y);
    }

    @Override
    public String toString() {
        return "View[" + this.delegate + "]";
    }

}
