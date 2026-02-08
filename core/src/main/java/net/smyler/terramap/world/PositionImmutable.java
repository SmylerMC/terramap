package net.smyler.terramap.world;

import java.util.Objects;
import org.jetbrains.annotations.Contract;

public class PositionImmutable implements Position {

    private final double x, y, z;
    private final float yaw, pitch;

    public PositionImmutable(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
    }

    public PositionImmutable(double x, double y, double z) {
        this(x, y, z, 0f, 0f);
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
     * Returns a position with its X component set to the given value.
     *
     * @param x the new value for X
     *
     * @return a {@link Position} with its X component sets to the given value
     */
    @Contract(pure = true)
    public PositionImmutable withX(double x) {
        return new PositionImmutable(x, this.y, this.z, this.yaw, this.pitch);
    }

    /**
     * Returns a position with its Y component set to the given value.
     *
     * @param y the new value for Y
     *
     * @return a {@link Position} with its Y component sets to the given value
     */
    @Contract(pure = true)
    public PositionImmutable withY(double y) {
        return new PositionImmutable(this.x, y, this.z, this.yaw, this.pitch);
    }

    /**
     * Returns a position with its Z component set to the given value.
     *
     * @param z the new value for Z
     *
     * @return a {@link Position} with its Z component sets to the given value
     */
    @Contract(pure = true)
    public PositionImmutable withZ(double z) {
        return new PositionImmutable(this.x, this.y, z, this.yaw, this.pitch);
    }

    /**
     * Returns a position with its yaw component set to the given value.
     *
     * @param yaw the new value for yaw
     *
     * @return a {@link Position} with its yaw component sets to the given value
     */
    @Contract(pure = true)
    public PositionImmutable withYaw(float yaw) {
        return new PositionImmutable(this.x, this.y, this.z, yaw, this.pitch);
    }

    /**
     * Returns a position with its pitch component set to the given value.
     *
     * @param pitch the new value for pitch
     *
     * @return a {@link Position} with its pitch component sets to the given value
     */
    @Contract(pure = true)
    public PositionImmutable withPitch(float pitch) {
        return new PositionImmutable(this.x, this.y, this.z, this.yaw, pitch);
    }

    /**
     * Returns a position with its X, Y and Z component set to the given values.
     *
     * @param x the new value for X
     * @param y the new value for Y
     * @param z the new value for Z
     *
     * @return a {@link Position} with its X, Y and Z component sets to the given values
     */
    @Contract(pure = true)
    public PositionImmutable withXYZ(double x, double y, double z) {
        return new PositionImmutable(x, y, z, this.yaw, this.pitch);
    }

    /**
     * Returns a position with its X and Z component set to the given value.
     *
     * @param x the new value for X
     * @param z the new value for z
     *
     * @return a {@link Position} with its X and Z components sets to the given values
     */
    @Contract(pure = true)
    public PositionImmutable withXZ(double x, double z) {
        return new PositionImmutable(x, this.y, z, this.yaw, this.pitch);
    }

    @Override
    public PositionImmutable getImmutable() {
        return this;
    }

    @Override
    public PositionMutable getMutable() {
        return new PositionMutable(this.x, this.y, this.z, this.yaw, this.pitch);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || !PositionImmutable.class.equals(o.getClass())) {
            return false;
        }
        Position other =  (Position)o;
        return this.x() == other.x()
                && this.y() == other.y()
                && this.z() == other.z()
                && this.yaw() == other.yaw()
                && this.pitch() == other.pitch();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.x(), this.y(), this.z(), this.yaw(), this.pitch());
    }

    @Override
    public String toString() {
        return "PositionImmutable[x=" + this.x() + ";y=" + this.y() + ";yaw=" + this.yaw() + ";pitch=" + this.pitch() + "]";
    }

}
