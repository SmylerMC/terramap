package net.smyler.terramap.content;

import java.util.Objects;

public abstract class PositionAbstract implements Position {

    @Override
    public String toString() {
        return "Position[x=" + this.x() + ";y=" + this.y() + ";yaw=" + this.yaw() + ";pitch=" + this.pitch() + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof Position)) {
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

}
