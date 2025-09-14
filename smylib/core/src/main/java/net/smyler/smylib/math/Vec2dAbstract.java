package net.smyler.smylib.math;

abstract class Vec2dAbstract implements Vec2d {

    @Override
    public String toString() {
        return "Vector[" + this.x() + "; " + this.y() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + Double.hashCode(this.x());
        result = prime * result + Double.hashCode(this.y());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof Vec2dAbstract)) {
            return false;
        }
        Vec2dAbstract other = (Vec2dAbstract) obj;
        if (Double.doubleToLongBits(this.x()) != Double.doubleToLongBits(other.x())) {
            return false;
        }
        return Double.doubleToLongBits(this.y()) == Double.doubleToLongBits(other.y());
    }

}
