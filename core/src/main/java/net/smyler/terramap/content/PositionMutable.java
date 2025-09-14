package net.smyler.terramap.content;

import org.jetbrains.annotations.NotNull;

public class PositionMutable extends PositionAbstract {

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

    @Override
    public PositionMutable withX(double x) {
        this.x = x;
        return this;
    }

    @Override
    public PositionMutable withY(double y) {
        this.y = y;
        return this;
    }

    @Override
    public PositionMutable withZ(double z) {
        this.z = z;
        return this;
    }

    @Override
    public PositionMutable withYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    @Override
    public PositionMutable withPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    @Override
    public PositionMutable withXYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    @Override
    public PositionMutable withXZ(double x, double z) {
        this.x = x;
        this.z = z;
        return this;
    }

    public PositionMutable set(double x, double y, double z, float yaw, float pitch) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        return this;
    }

    public PositionMutable set(@NotNull Position other) {
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

}
