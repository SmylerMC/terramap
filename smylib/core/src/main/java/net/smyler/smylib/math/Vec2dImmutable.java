package net.smyler.smylib.math;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.Preconditions.checkArgument;

/**
 * An immutable implementation of {@link Vec2d}.
 * <br>
 * Any operation on an immutable vector that results in a vector
 * will return a new instance of {@link Vec2dImmutable} and leave the original untouched.
 *
 * @author Smyler
 */
public final class Vec2dImmutable implements Vec2d {

    /**
     * The null vector
     */
    public static final Vec2dImmutable NULL = new Vec2dImmutable(0d, 0d);

    /**
     * A unit vector along the X axis
     */
    public static final Vec2dImmutable UNIT_X = new Vec2dImmutable(1d, 0d);

    /**
     * A unit vector along the Y axis
     */
    public static final Vec2dImmutable UNIT_Y = new Vec2dImmutable(0d, 1d);

    public final double x;
    public final double y;

    /**
     * Creates a new {@link Vec2dImmutable} with the given coordinates.
     *
     * @param x X coordinate component
     * @param y Y coordinate component
     */
    public Vec2dImmutable(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new {@link Vec2dImmutable} with coordinates extracted from a two-dimensional double array..
     *
     * @param coordinates the array to extract coordinates from
     *
     * @throws NullPointerException if the array is null
     * @throws IllegalArgumentException if the array is not of length 2
     */
    public Vec2dImmutable(double @NotNull [] coordinates) {
        requireNonNull(coordinates);
        checkArgument(coordinates.length == 2, "Expected a double array of length 2");
        this.x = coordinates[0];
        this.y = coordinates[1];
    }

    @Override
    public double x() {
        return this.x;
    }

    @Override
    public double y() {
        return this.y;
    }

    /**
     * Creates a new {@link Vec2dImmutable} with the same Y component as this one and
     * a new X component.
     *
     * @param x the new X component
     *
     * @return a new {@link Vec2dImmutable}
     */
    @Contract(value = "_ -> new", pure = true)
    public Vec2dImmutable withX(double x) {
        return new Vec2dImmutable(x, this.y);
    }

    /**
     * Creates a new {@link Vec2dImmutable} with the same X component as this one and
     * a new Y component.
     *
     * @param y the new Y component
     *
     * @return a new {@link Vec2dImmutable}
     */
    @Contract(value = "_ -> new", pure = true)
    public Vec2dImmutable withY(double y) {
        return new Vec2dImmutable(this.x, y);
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Vec2dImmutable scale(double factor) {
        return new Vec2dImmutable(this.x * factor, this.y * factor);
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Vec2dImmutable downscale(double factor) {
        return new Vec2dImmutable(this.x / factor, this.y / factor);
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
        return new Vec2dImmutable(this.x + other.x(), this.y + other.y());
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public @NotNull Vec2dImmutable add(double x, double y) {
        return new Vec2dImmutable(this.x + x, this.y + y);
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Vec2dImmutable subtract(@NotNull Vec2d other) {
        return new Vec2dImmutable(this.x - other.x(), this.y - other.y());
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public @NotNull Vec2dImmutable subtract(double x, double y) {
        return new Vec2dImmutable(this.x - x, this.y - y);
    }

    @Override
    @Contract(value = "_ -> new", pure = true)
    public @NotNull Vec2dImmutable hadamardProd(@NotNull Vec2d other) {
        return new Vec2dImmutable(this.x * other.x(), this.y * other.y());
    }

    @Override
    @Contract(value = "_, _ -> new", pure = true)
    public @NotNull Vec2dImmutable hadamardProd(double x, double y) {
        return new Vec2dImmutable(this.x * x, this.y * y);
    }

    @Override
    public @NotNull Vec2dImmutable getImmutable() {
        return this;
    }

    @Override
    public String toString() {
        return "Vec2dImmutable[" + this.x + "; " + this.y + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Double.hashCode(this.x());
        result = prime * result + Double.hashCode(this.y());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!obj.getClass().equals(Vec2dImmutable.class)) {
            return false;
        }
        Vec2dImmutable other = (Vec2dImmutable) obj;
        if (Double.doubleToLongBits(this.x()) != Double.doubleToLongBits(other.x())) {
            return false;
        }
        return Double.doubleToLongBits(this.y()) == Double.doubleToLongBits(other.y());
    }

}
