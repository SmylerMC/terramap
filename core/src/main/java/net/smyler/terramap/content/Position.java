package net.smyler.terramap.content;

import net.smyler.smylib.Immutable;
import net.smyler.smylib.Mutable;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.abs;
import static java.lang.Math.sqrt;


/**
 * A position and a rotation in a Minecraft world.
 *
 * @author Smyler
 */
public interface Position extends Immutable<PositionMutable>, Mutable<PositionImmutable> {

    /**
     * @return the x component of this position
     */
    double x();

    /**
     * @return the y component of this position
     */
    double y();

    /**
     * @return the z component of this position
     */
    double z();

    /**
     * @return the yaw rotational component of this position
     */
    float yaw();

    /**
     * @return the pitch rotational component of this position
     */
    float pitch();

    /**
     * Returns a position with its X component set to the given value.
     * If this position is mutable, it may change its own X component and return itself.
     *
     * @param x the new value for X
     *
     * @return a {@link Position} with its X component sets to the given value, possibly this
     */
    Position withX(double x);

    /**
     * Returns a position with its Y component set to the given value.
     * If this position is mutable, it may change its own Y component and return itself.
     *
     * @param y the new value for Y
     *
     * @return a {@link Position} with its Y component sets to the given value, possibly this
     */
    Position withY(double y);

    /**
     * Returns a position with its Z component set to the given value.
     * If this position is mutable, it may change its own Z component and return itself.
     *
     * @param z the new value for Z
     *
     * @return a {@link Position} with its Z component sets to the given value, possibly this
     */
    Position withZ(double z);

    /**
     * Returns a position with its yaw component set to the given value.
     * If this position is mutable, it may change its own yaw component and return itself.
     *
     * @param yaw the new value for yaw
     *
     * @return a {@link Position} with its yaw component sets to the given value, possibly this
     */
    Position withYaw(float yaw);

    /**
     * Returns a position with its pitch component set to the given value.
     * If this position is mutable, it may change its own pitch component and return itself.
     *
     * @param pitch the new value for pitch
     *
     * @return a {@link Position} with its pitch component sets to the given value, possibly this
     */
    Position withPitch(float pitch);

    /**
     * Returns a position with its X, Y and Z component set to the given values.
     * If this position is mutable, it may change its own components and return itself.
     *
     * @param x the new value for X
     * @param y the new value for Y
     * @param z the new value for Z
     *
     * @return a {@link Position} with its X, Y and Z component sets to the given value, possibly this
     */
    Position withXYZ(double x, double y, double z);

    /**
     * Returns a position with its X and Z component set to the given value.
     * If this position is mutable, it may change its own components and return itself.
     *
     * @param x the new value for X
     * @param z the new value for z
     *
     * @return a {@link Position} with its X and Z components sets to the given value, possibly this
     */
    Position withXZ(double x, double z);

    /**
     * Computes the distance between this position and another one.
     *
     * @param other the {@link Position position} to compute the distance to
     *
     * @return the distance between the positions
     */
    default double distanceTo(@NotNull Position other) {
        double dX = this.x() - other.x();
        double dY = this.y() - other.y();
        double dZ = this.z() - other.z();
        return sqrt(dX * dX +  dY * dY + dZ * dZ);
    }

    /**
     * Computes the horizontal distance between this position and another one,
     * meaning their two-dimensional distance when ignoring their Y component.
     *
     * @param other the {@link Position position} to compute the distance to
     *
     * @return the horizontal distance between the positions
     */
    default double horizontalDistanceTo(@NotNull Position other) {
        double dX = this.x() - other.x();
        double dZ = this.z() - other.z();
        return sqrt(dX * dX + dZ * dZ);
    }

    /**
     * Computes the vertical distance between this position and another one,
     * meaning their one-dimensional distance when ignoring their X and Z component.
     *
     * @param other the {@link Position position} to compute the distance to
     *
     * @return the vertical distance between the positions
     */
    default double verticalDistanceTo(@NotNull Position other) {
        return abs(this.y() - other.y());
    }

}
