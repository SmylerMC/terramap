package fr.thesmyler.terramap.util;

public final class Vec2d {
	
	public final double x;
	public final double y;
	
	public Vec2d(double x, double y) {
		this.x = x;
		this.y = y;
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
	
	public Vec2d add(Vec2d other) {
		return new Vec2d(this.x + other.x, this.y + other.y);
	}
	
	public Vec2d add(double x, double y) {
		return new Vec2d(this.x + x, this.y + y);
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

}
