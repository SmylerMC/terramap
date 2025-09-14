package net.smyler.terramap.content;

public class PositionImmutable extends PositionAbstract {

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

    @Override
    public PositionImmutable withX(double x) {
        return new PositionImmutable(x, this.y, this.z, this.yaw, this.pitch);
    }

    @Override
    public PositionImmutable withY(double y) {
        return new PositionImmutable(this.x, y, this.z, this.yaw, this.pitch);
    }

    @Override
    public PositionImmutable withZ(double z) {
        return new PositionImmutable(this.x, this.y, z, this.yaw, this.pitch);
    }

    @Override
    public PositionImmutable withYaw(float yaw) {
        return new PositionImmutable(this.x, this.y, this.z, yaw, this.pitch);
    }

    @Override
    public PositionImmutable withPitch(float pitch) {
        return new PositionImmutable(this.x, this.y, this.z, this.yaw, pitch);
    }

    @Override
    public PositionImmutable withXYZ(double x, double y, double z) {
        return new PositionImmutable(x, y, z, this.yaw, this.pitch);
    }

    @Override
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

}
