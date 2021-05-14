package fr.thesmyler.terramap.util;

/**
 * A 2 by 2 double matrix 
 * 
 * @author SmylerMC
 *
 */
public class Mat2d {
	
	public static final Mat2d INDENTITY = new Mat2d(1d, 0d, 0d, 1d);
	public static final Mat2d NULL = new Mat2d(0d, 0d, 0d, 0d);

	private double x11;
	private double x12;
	private double x21;
	private double x22;
	
	public Mat2d(double x11, double x12, double x21, double x22) {
		super();
		this.x11 = x11;
		this.x12 = x12;
		this.x21 = x21;
		this.x22 = x22;
	}
	
	public Mat2d scale(double factor) {
		return new Mat2d(
				this.x11*factor, this.x12*factor,
				this.x21*factor, this.x22*factor
			);
	}
	
	public Mat2d add(Mat2d other) {
		return new Mat2d(
				this.x11 + other.x11, this.x12 + other.x12,
				this.x21 + other.x21, this.x22 + other.x22
			);
	}
	
	public Mat2d prod(Mat2d other) {
		return new Mat2d(
				this.x11*other.x11 + this.x12*other.x21, this.x11*other.x12 + this.x12*other.x22,
				this.x21*other.x11 + this.x22*other.x21, this.x21*other.x12 + this.x22*other.x22
			);
	}
	
	public Vec2d prod(Vec2d vec) {
		return new Vec2d(
				this.x11*vec.x + this.x12*vec.y,
				this.x21*vec.x + this.x22*vec.y
			);
	}
	
	public double determinant() {
		return this.x11*this.x22 - this.x12*this.x21;
	}
	
	public Mat2d inverse() {
		double det = this.determinant();
		if(det == 0) throw new IllegalStateException("Matrix has no inverse: determinant is 0");
		return new Mat2d(
				this.x22, -this.x21,
				-this.x12, this.x11)
		.scale(1d / det);
	}
	
	public Mat2d transpose() {
		return new Mat2d(
				x11, x21,
				x12, x22);
	}
	
	public Vec2d column1() {
		return new Vec2d(this.x11, this.x21);
	}
	
	public Vec2d column2() {
		return new Vec2d(this.x12, this.x22);
	}
	
	public Vec2d line1() {
		return new Vec2d(this.x11, this.x12);
	}
	
	public Vec2d line2() {
		return new Vec2d(this.x21, this.x22);
	}
	
	public static Mat2d forRotation(double radAngle) {
		double c = Math.cos(radAngle);
		double s = Math.sin(radAngle);
		return new Mat2d(
				c, -s,
				s, c
			);
	}
	
	public static Mat2d forSymetry(double radAngle) {
		double c = Math.cos(radAngle);
		double s = Math.sin(radAngle);
		return new Mat2d(
				c, s,
				s, -c
			);
	}

}
