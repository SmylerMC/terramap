package net.smyler.terramap.world;

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
     * Exposes this position's X coordinate.
     *
     * @return the x component of this position
     */
    double x();

    /**
     * Exposes this position's Y coordinate.
     *
     * @return the y component of this position
     */
    double y();

    /**
     * Exposes this position's Z coordinate.
     *
     * @return the z component of this position
     */
    double z();

    /**
     * Exposes this position's yaw.
     *
     * @return the yaw rotational component of this position
     */
    float yaw();

    /**
     * Exposes this position's pitch.
     *
     * @return the pitch rotational component of this position
     */
    float pitch();

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
