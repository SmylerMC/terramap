package net.smyler.terramap.world;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import static java.util.Objects.requireNonNull;

public class PositionMutable implements Position {

    private double x, y, z;
    private float yaw, pitch;

    public PositionMutable(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public PositionMutable(double x, double y, double z) {
        this(x, y, z, 0f, 0f);
    }

    public PositionMutable() {
        this(0d, 0d, 0d, 0f, 0f);
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
    public double z() {
        return this.z;
    }

    @Override
    public float yaw() {
        return this.yaw;
    }

    @Override
    public float pitch() {
        return this.pitch;
    }

    /**
     * Sets the X coordinate of this position.
     *
     * @param x the new X coordinate for this position
     * @return this position
     */
    @Contract("_ -> this")
    public PositionMutable setX(double x) {
        this.x = x;
        return this;
    }

    /**
     * Sets the Y coordinate of this position.
     *
     * @param y the new Y coordinate for this position
     * @return this position
     */
    @Contract("_ -> this")
    public PositionMutable setY(double y) {
        this.y = y;
        return this;
    }

    /**
     * Sets the Z coordinate of this position.
     *
     * @param z the new Z coordinate for this position
     * @return this position
     */
    @Contract("_ -> this")
    public PositionMutable setZ(double z) {
        this.z = z;
        return this;
    }

    /**
     * Sets the yaw of this position.
     *
     * @param yaw the new yaw of this position
     * @return this position
     */
    @Contract("_ -> this")
    public PositionMutable setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    /**
     * Sets the pitch of this position.
     *
     * @param pitch the new pitch of this position
     * @return this position
     */
    @Contract("_ -> this")
    public PositionMutable withPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    /**
     * Sets the X, Y and Z coordinates of this position.
     *
     * @param x the new X coordinate for this position
     * @param y the new Y coordinate for this position
     * @param z the new Z coordinate for this position
     * @return this position
     */
    @Contract("_, _, _ -> this")
    public PositionMutable setXYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    /**
     * Sets the horizontal coordinates of this position.
     *
     * @param x the new X coordinate for this position
     * @param z the new Z coordinate for this position
     * @return this position
     */
    @Contract("_, _ -> this")
    public PositionMutable setXZ(double x, double z) {
        this.x = x;
        this.z = z;
        return this;
    }

    /**
     * Sets the coordinates of this position.
     *
     * @param x the new X coordinate for this position
     * @param y the new X coordinate for this position
     * @param z the new X coordinate for this position
     * @param yaw the new yaw for this position
     * @param pitch the new pitch for this position
     * @return this position
     */
    public PositionMutable set(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    /**
     * Sets the coordinates of this position by copying another.
     *
     * @param other the other position to copy
     * @return this position
     */
    public PositionMutable set(@NotNull Position other) {
        requireNonNull(other);
        this.x = other.x();
        this.y = other.y();
        this.z = other.z();
        this.yaw = other.yaw();
        this.pitch = other.pitch();
        return this;
    }

    @Override
    public PositionMutable getMutable() {
        return this;
    }

    @Override
    public PositionImmutable getImmutable() {
        return new PositionImmutable(this.x(), this.y(), this.z(), this.yaw, this.pitch);
    }

    @Override
    public String toString() {
        return "PositionMutable[x=" + this.x() + ";y=" + this.y() + ";yaw=" + this.yaw() + ";pitch=" + this.pitch() + "]";
    }

}
