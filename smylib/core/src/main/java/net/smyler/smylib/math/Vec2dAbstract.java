package net.smyler.smylib.math;

abstract class Vec2dAbstract<T extends Vec2dAbstract<?>> implements Vec2d<T> {

    @Override
    public String toString() {
        return "Vector[" + this.x() + "; " + this.y() + "]";
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(this.x());
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(this.y());
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(!(obj instanceof Vec2dAbstract)) return false;
        Vec2dAbstract<?> other = (Vec2dAbstract<?>) obj;
        if(Double.doubleToLongBits(this.x()) != Double.doubleToLongBits(other.x())) return false;
        return Double.doubleToLongBits(this.y()) == Double.doubleToLongBits(other.y());
    }

}
