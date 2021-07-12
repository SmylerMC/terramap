package fr.thesmyler.terramap.util.math;

import net.buildtheearth.terraplusplus.dep.net.daporkchop.lib.common.util.PValidation;

public final class Vec2d {
    
    public static final Vec2d NULL = new Vec2d(0d, 0d);
    public static final Vec2d UNIT_X = new Vec2d(1d, 0d);
    public static final Vec2d UNIT_Y = new Vec2d(0d, 1d);

    public final double x;
    public final double y;

    public Vec2d(double x, double y) {
        this.x = x;
        this.y = y;
    }
    
    public Vec2d(double[] coords) {
        PValidation.checkArg(coords.length == 2, "Excpected a double array of length 2");
        this.x = coords[0];
        this.y = coords[1];
    }

    public double normSquared() {
        return this.dotProd(this);
    }

    public double norm() {
        return Math.sqrt(this.normSquared());
    }

    public Vec2d scale(double factor) {
        return new Vec2d(this.x*factor, this.y*factor);
    }
    
    public Vec2d downscale(double factor) {
        return new Vec2d(this.x / factor, this.y / factor);
    }

    public Vec2d add(Vec2d other) {
        return new Vec2d(this.x + other.x, this.y + other.y);
    }

    public Vec2d add(double x, double y) {
        return new Vec2d(this.x + x, this.y + y);
    }
    
    public Vec2d substract(Vec2d other) {
        return new Vec2d(this.x - other.x, this.y - other.y);
    }
    
    public Vec2d substract(double x, double y) {
        return new Vec2d(this.x - x, this.y - y);
    }

    public double dotProd(Vec2d other) {
        return this.x*other.x + this.y*other.y;
    }

    public double crossProd(Vec2d other) {
        return this.x*other.y - this.y*other.x;
    }

    public Vec2d hadamardProd(Vec2d other) {
        return new Vec2d(this.x*other.x, this.y*other.y);
    }

    public double taxicabNorm() {
        return Math.abs(this.x) + Math.abs(this.y); 
    }

    public double maximumNorm() {
        return Math.max(Math.abs(this.x), Math.abs(this.y));
    }

    public Vec2d normalize() {
        double norm = this.norm();
        if(norm == 0d) throw new ArithmeticException("Cannot normalize null vector");
        return this.scale(1d / norm);
    }
    
    public double distanceTo(Vec2d other) {
        return this.substract(other).norm();
    }

    public double[] asArray() {
        return new double[] {this.x, this.y};
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        long temp;
        temp = Double.doubleToLongBits(x);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(y);
        result = prime * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if(this == obj) return true;
        if(obj == null) return false;
        if(getClass() != obj.getClass()) return false;
        Vec2d other = (Vec2d) obj;
        if(Double.doubleToLongBits(x) != Double.doubleToLongBits(other.x)) return false;
        if(Double.doubleToLongBits(y) != Double.doubleToLongBits(other.y)) return false;
        return true;
    }

}
