package net.smyler.smylib.math;


import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.Preconditions.checkArgument;

/**
 * A mutable implementation of {@link Vec2d}
 * Any method from this class that returns a {@link Vec2d} modifies this vector and returns itself.
 *
 * @author Smyler
 */
public final class Vec2dMutable extends Vec2dAbstract {

    public double x, y;
    private Vec2dView readOnly;

    /**
     * Creates a new vector with the given coordinates.
     *
     * @param x first component of the vector
     * @param y second component of the vector
     */
    public Vec2dMutable(double x, double y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Creates a new vector by extracting coordinates from a double array of length 2.
     *
     * @param coordinates the array to extract coordinates from
     *
     * @throws NullPointerException if the array is null
     * @throws IllegalArgumentException if the array is not of length 2
     */
    public Vec2dMutable(double @NotNull [] coordinates) {
        requireNonNull(coordinates);
        checkArgument(coordinates.length == 2, "Expected a double array of length 2");
        this.x = coordinates[0];
        this.y = coordinates[1];
    }

    /**
     * Creates a new vector with all components set to 0.
     */
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

    /**
     * Sets this vector's coordinates to the given values.
     *
     * @param x new value for this vector's X coordinate
     * @param y new value for this vector's Y coordinate
     *
     * @return this vector
     */
    public Vec2dMutable set(double x, double y) {
        this.x = x;
        this.y = y;
        return this;
    }

    /**
     * Sets this vector's coordinates to be the same as that of a given vector.
     *
     * @param vector the vector to copy
     *
     * @return this vector
     *
     * @throws NullPointerException if the given vector is null
     */
    public Vec2dMutable set(@NotNull Vec2d vector) {
        requireNonNull(vector, "vector is null");
        this.x = vector.x();
        this.y = vector.y();
        return this;
    }

    /**
     * Sets this vector's coordinates to the values extracted from a two-dimensional double array.
     *
     * @param coordinates the array to extract coordinates from
     *
     * @return this vector
     *
     * @throws NullPointerException if the given array is null
     * @throws IllegalArgumentException if the given array is not of length 2
     */
    public Vec2dMutable set(double @NotNull [] coordinates) {
        requireNonNull(coordinates);
        checkArgument(coordinates.length == 2, "Expected a double array of length 2");
        this.x = coordinates[0];
        this.y = coordinates[1];
        return this;
    }

    /**
     * Sets this vector's X coordinate.
     *
     * @param x the new value for the X coordinate
     *
     * @return this vector
     */
    public Vec2dMutable setX(double x) {
        this.x = x;
        return this;
    }

    /**
     * Sets this vector's Y coordinate.
     *
     * @param y the new value for the X coordinate
     *
     * @return this vector
     */
    public Vec2dMutable setY(double y) {
        this.y = y;
        return this;
    }

    @Override
    public @NotNull Vec2dMutable scale(double factor) {
        this.x *= factor;
        this.y *= factor;
        return this;
    }

    @Override
    public @NotNull Vec2dMutable downscale(double factor) {
        this.x /= factor;
        this.y /= factor;
        return this;
    }

    @Override
    public @NotNull Vec2dMutable add(@NotNull Vec2d vector) {
        this.x += vector.x();
        this.y += vector.y();
        return this;
    }

    @Override
    public @NotNull Vec2dMutable add(double x, double y) {
        this.x += x;
        this.y += y;
        return this;
    }

    @Override
    public @NotNull Vec2dMutable subtract(@NotNull Vec2d vector) {
        this.x -= vector.x();
        this.y -= vector.y();
        return this;
    }

    @Override
    public @NotNull Vec2dMutable subtract(double x, double y) {
        this.x -= x;
        this.y -= y;
        return this;
    }

    @Override
    public @NotNull Vec2dMutable hadamardProd(@NotNull Vec2d vector) {
        this.x *= vector.x();
        this.y *= vector.y();
        return this;
    }

    @Override
    public @NotNull Vec2dMutable hadamardProd(double x, double y) {
        this.x *= x;
        this.y *= y;
        return this;
    }

    /**
     * Applies a matrix transformation to this vector.
     *
     * @param matrix the transformation to apply as a two-by-two double matrix
     *
     * @return this vector, with the transformation applied
     */
    public Vec2dMutable apply(Mat2d matrix) {
        double x = this.x;
        this.x = x * matrix.line1().x + this.y * matrix.line1().y;
        this.y = x * matrix.line2().x + this.y * matrix.line2().y;
        return this;
    }

    @Override
    public @NotNull Vec2dMutable normalize() {
        double norm = this.norm();
        if (norm == 0d) {
            throw new ArithmeticException("Cannot normalize null vector");
        }
        return this.scale(1d / norm);
    }

    /**
     * Provides access to a read-only view over this mutable vector.
     * <br>
     * Changes to this vector will be reflected in the view.
     * Operations to the view that result in vectors will return new {@link Vec2dImmutable}
     * vectors and let this vector untouched.
     * <br>
     * The view is unique and cached for each object:
     * multiple calls to this method on one object will return the same {@link Vec2dView} object.
     *
     * @return the view over this vector
     */
    @Contract(pure = true)
    public Vec2dView getReadOnlyView() {
        if (this.readOnly == null) {
            this.readOnly = new Vec2dView(this);
        }
        return this.readOnly;
    }

    @Override
    public @NotNull Vec2dMutable getMutable() {
        return this;
    }

    public @NotNull Vec2dMutable copy() {
        return new Vec2dMutable(this.x, this.y);
    }

    @Override
    public String toString() {
        return "Vec2dMutable[" + this.x + "; " + this.y + "]";
    }

}
