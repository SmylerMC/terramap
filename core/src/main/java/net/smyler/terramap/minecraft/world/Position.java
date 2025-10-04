package net.smyler.terramap.minecraft.world;

import net.smyler.smylib.Immutable;
import net.smyler.smylib.Mutable;
import org.jetbrains.annotations.NotNull;

import static java.lang.Math.*;
import static java.lang.Math.round;


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

    /**
     * @return the X coordinate of the block that contains this position
     */
    default long blockX() {
        return round(this.x());
    }

    /**
     * @return the Y coordinate of the block that contains this position
     */
    default long blockY() {
        return round(this.y());
    }

    /**
     * @return the Z coordinate of the block that contains this position
     */
    default long blockZ() {
        return round(this.z());
    }

    /**
     * @return the X coordinate of the chunk that contains this position
     */
    default long chunkX() {
        return floorDiv(round(this.x()), 16);
    }

    /**
     * @return the Z coordinate of the chunk that contains this position
     */
    default long chunkZ() {
        return floorDiv(round(this.z()), 16);
    }

    /**
     * @return the X coordinate of the region that contains this position.
     * Regions are square sections of the world saved in individual files, with a side of 512 blocks.
     */
    default long regionX() {
        return floorDiv(round(this.x()), 512);
    }

    /**
     * @return the Z coordinate of the region that contains this position.
     * Regions are square sections of the world saved in individual files, with a side of 512 blocks.
     */
    default long regionZ() {
        return floorDiv(round(this.z()), 512);
    }

    /**
     * @return the X coordinate of the CubicChunk 3D region that contains this position.
     * 3D Regions are cubic sections of CubicChunks worlds world saved in individual files, with a side of 256 blocks.
     */
    default long region3dX() {
        return floorDiv(round(this.x()), 256);
    }

    /**
     * @return the Y coordinate of the CubicChunk 3D region that contains this position.
     * 3D Regions are cubic sections of CubicChunks worlds world saved in individual files, with a side of 256 blocks.
     */
    default long region3dY() {
        return floorDiv(round(this.y()), 256);
    }

    /**
     * @return the Z coordinate of the CubicChunk 3D region that contains this position.
     * 3D Regions are cubic sections of CubicChunks worlds world saved in individual files, with a side of 256 blocks.
     */
    default long region3dZ() {
        return floorDiv(round(this.z()), 256);
    }

    @Override
    default PositionMutable getMutable() {
        return new PositionMutable(this.x(), this.y(), this.z(), this.yaw(), this.pitch());
    }

    @Override
    default PositionImmutable getImmutable() {
        return new PositionImmutable(this.x(), this.y(), this.z(), this.yaw(), this.pitch());
    }

}
