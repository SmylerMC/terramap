package fr.thesmyler.terramap.util.math;

import fr.thesmyler.terramap.util.Immutable;
import fr.thesmyler.terramap.util.Mutable;

import static java.lang.Math.*;

/**
 * An interface representing a 2D vector of doubles.
 *
 * @param <T> the type returned by operation performed on this vector that should return another vector.
 */
public interface Vec2d<T extends Vec2d<?>> extends Mutable<Vec2dImmutable>, Immutable<Vec2dMutable> {

    /**
     * @return the X component of this vector.
     */
    double x();

    /**
     * @return the Y component of this vector.
     */
    double y();

    /**
     * Multiply this vector by a given scalar.
     *
     * @param factor a scalar by which to multiply this vector.
     * @return a vector scaled accordingly to the given scalar.
     */
    T scale(double factor);

    /**
     * Divides this vector by a given scalar.
     *
     * @param factor a scalar by which to divide this vector.
     * @return a vector scaled accordingly to the given scalar.
     */
    T downscale(double factor);

    /**
     * Normalizes this vector so the result has a length of 1 according to the euclidean norm.
     *
     * @return the normalized vector.
     * @throws ArithmeticException if this vector is the null vector.
     */
    T normalize();

    /**
     * Adds another vector to this one.
     *
     * @param other a vector to add with this one.
     * @return the resulting vector.
     */
    T add(Vec2d<?> other);

    /**
     * Adds another vector to this one.
     *
     * @param x the X coordinate of the vector to add with this one.
     * @param y the Y coordinate of the vector to add with this one.
     * @return the resulting vector.
     */
    T add(double x, double y);

    /**
     * Subtracts another vector from this one.
     *
     * @param other a vector to subtract from this one.
     * @return the resulting vector.
     */
    T subtract(Vec2d<?> other);

    /**
     * Subtracts another vector from this one.
     *
     * @param x the X coordinate of the vector to subtract from this one.
     * @param y the Y coordinate of the vector to subtract from this one.
     * @return the resulting vector.
     */
    T subtract(double x, double y);

    /**
     * Computes the hadamard product of two vectors.
     *
     * @param other another to compute the product with this one.
     * @return the resulting vector.
     */
    T hadamardProd(Vec2d<?> other);

    /**
     * Computes the hadamard product of two vectors.
     *
     * @param x the X coordinate of the vector to compute the product with.
     * @param y the Y coordinate of the vector to compute the product with.
     * @return the resulting vector.
     */
    T hadamardProd(double x, double y);

    /**
     * Computes the dot product of a vector with this one.
     *
     * @param other another vector to compute the dot product with.
     * @return the resulting scalar.
     */
    default double dotProd(Vec2d<?> other) {
        return this.x()*other.x() + this.y()*other.y();
    }

    /**
     * Computes the dot product of a vector with this one.
     *
     * @param x the X coordinates of the  vector to compute the dot product with.
     * @param y the Y coordinates of the  vector to compute the dot product with.
     * @return the resulting scalar.
     */
    default double dotProd(double x, double y) {
        return this.x()*x + this.y()*y;
    }

    /**
     * Computes the cross product of a vector with this one.
     * @param other another vector to compute the cross product with.
     * @return the resulting scalar.
     */
    default double crossProd(Vec2d<?> other) {
        return this.x()*other.y() - this.y()*other.x();
    }

    /**
     * Computes the cross product of a vector with this one.
     * @param x the X coordinates of the  vector to compute the dot product with.
     * @param y the Y coordinates of the  vector to compute the dot product with.
     * @return the resulting scalar.
     */
    default double crossProd(double x, double y) {
        return this.x()*y - this.y()*x;
    }

    /**
     * @return the square of the euclidean norm of this vector.
     */
    default double normSquared() {
        double x = this.x();
        double y = this.y();
        return x * x + y * y;
    }

    /**
     * @return the euclidean norm of this vector.
     */
    default double norm() {
        double x = this.x();
        double y = this.y();
        return sqrt(x * x + y * y);
    }

    /**
     * @return the taxicab norm of this vector.
     */
    default double taxicabNorm() {
        return abs(this.x()) + abs(this.y());
    }

    /**
     * @return the maximum norm of this vector.
     */
    default double maximumNorm() {
        return max(abs(this.x()), abs(this.y()));
    }

    /**
     * @param other - another vector to compute the distance with.
     * @return the euclidean between this vector and the other.
     */
    default double distanceTo(Vec2d<?> other) {
        double dx = this.x() - other.x();
        double dy = this.y() - other.y();
        return sqrt(dx * dx + dy * dy);
    }

    /**
     * @param x - the X coordinate of the vector to compute the distance with.
     * @param y - the Y coordinate of the vector to compute the distance with.
     * @return the euclidean between this vector and the other.
     */
    default double distanceTo(double x, double y) {
        double dx = this.x() - x;
        double dy = this.y() - y;
        return sqrt(dx * dx + dy * dy);
    }

    /**
     * @return a double array with this vector coordinates.
     */
    default double[] asArray() {
        return new double[] {this.x(), this.y()};
    }

    /**
     * @return a mutable version of this vector.
     */
    @Override
    default Vec2dMutable getMutable() {
        return new Vec2dMutable(this.x(), this.y());
    }

    /**
     * @return a immutable version of this vector.
     */
    @Override
    default Vec2dImmutable getImmutable() {
        return new Vec2dImmutable(this.x(), this.y());
    }

    /**
     * @return whether both components of this vector are finite
     */
    default boolean isFinite() {
        return Double.isFinite(this.x()) && Double.isFinite(this.y());
    }

}
