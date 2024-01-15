package net.smyler.smylib.math;

public class DoubleRange {
    
    public static final DoubleRange REALS = new DoubleRange(Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY);
    public static final DoubleRange POSITIVE_REALS = new DoubleRange(0d, Double.POSITIVE_INFINITY);
    public static final DoubleRange NEGATIVE_REALS = new DoubleRange(Double.NEGATIVE_INFINITY, 0d);
    
    public final double lowerBound, upperBound;
    
    /**
     * Constructor
     * 
     * @param lowerBound of this range, inclusive
     * @param upperBound of this range, inclusive
     * 
     * @throws {@link IllegalArgumentException} if upperBound > lowerBound 
     */
    public DoubleRange(double lowerBound, double upperBound) {
        if(upperBound < lowerBound) throw new IllegalArgumentException("lowerBound > upperBound");
        if(Double.isNaN(lowerBound) || Double.isNaN(upperBound)) throw new IllegalArgumentException("NaN bounds are not permitted in DoubleRange");
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }
    
    /**
     * @param x
     * @return true if x is within this range (inclusive)
     */
    public boolean matches(double x) {
        return x >= this.lowerBound && x <= this.upperBound;
    }
    
    /**
     * @param x
     * @return true if x is strictly above this range (exclusive)
     */
    public boolean above(double x) {
        return x > this.upperBound;
    }
    
    /**
     * @param x
     * @return true if x is strictly below this range (exclusive)
     */
    public boolean below(double x) {
        return x < this.lowerBound;
    }
    
    /**
     * @param other
     * @return true if the other range is strictly above this range (if both of its bounds are), false otherwise or if other is null
     */
    public boolean above(DoubleRange other) {
        if(other == null) return false;
        return this.above(other.lowerBound) && this.above(other.upperBound);
    }
    
    /**
     * @param other
     * @return true if the other range is strictly below this range (if both of its bounds are), false otherwise or if other is null
     */
    public boolean below(DoubleRange other) {
        if(other == null) return false;
        return this.below(other.lowerBound) && this.below(other.upperBound);
    }
    
    /**
     * 
     * @param other
     * @return true if the other range intersects with this one (inclusive)
     */
    public boolean intersects(DoubleRange other) {
        if(other == null) return false;
        return this.matches(other.lowerBound) || this.matches(other.upperBound) || other.matches(this.lowerBound);
    }
    
    /**
     * @param amount
     * @return a new {@link DoubleRange} that has the same lower bound, but an upper bound increased by amount
     */
    public DoubleRange extendUp(double amount) {
        return new DoubleRange(this.lowerBound, this.upperBound + amount);
    }
    
    /**
     * @param amount
     * @return a new {@link DoubleRange} that has the same upper bound, but a lower bound lowered by amount
     */
    public DoubleRange extendDown(double amount) {
        return new DoubleRange(this.lowerBound - amount, this.upperBound);
    }
    
    /**
     * @param amount
     * @return a new {@link DoubleRange} that has the same lower bound, but an upper bound lowered by amount
     */
    public DoubleRange contractUp(double amount) {
        return new DoubleRange(this.lowerBound, this.upperBound - amount);
    }
    
    /**
     * @param amount
     * @return a new {@link DoubleRange} that has the same upper bound, but a lower bound increased by amount
     */
    public DoubleRange contractDown(double amount) {
        return new DoubleRange(this.lowerBound + amount, this.upperBound);
    }
    
    /**
    /**
     * @param amount
     * @return a new {@link DoubleRange} that has both its lower and upper bounds increased by amount
     */
    public DoubleRange shift(double amount) {
        return new DoubleRange(this.lowerBound + amount, this.upperBound + amount);
    }
    
    /**
     * @return the center of this range
     */
    public double center() {
        return (this.lowerBound + this.upperBound) / 2;
    }
    
    /**
     * @return the length of this range
     */
    public double size() {
        return this.upperBound - this.lowerBound;
    }

}
