package net.smyler.terramap.minecraft.world;

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

    public PositionMutable setX(double x) {
        this.x = x;
        return this;
    }

    public PositionMutable setY(double y) {
        this.y = y;
        return this;
    }

    public PositionMutable setZ(double z) {
        this.z = z;
        return this;
    }

    public PositionMutable setYaw(float yaw) {
        this.yaw = yaw;
        return this;
    }

    public PositionMutable setPitch(float pitch) {
        this.pitch = pitch;
        return this;
    }

    public PositionMutable setXYZ(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
        return this;
    }

    public PositionMutable setXZ(double x, double z) {
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


}
