package net.smyler.terramap.util.math;

import static java.lang.Math.abs;
import static java.lang.Math.round;
import static net.smyler.smylib.Preconditions.checkArgument;

/**
 * Snaps values to given values at fixed interval if they are closed enough.
 *
 * @author SmylerMC
 */
public class Snapper {

    private double period;
    private double distance;

    public Snapper(double period, double distance) {
        this.period = period;
        this.distance = distance;
    }

    public Snapper(double period) {
        this(period, Double.MAX_VALUE);
    }

    public double snap(double value) {
        double closetsPeriod = round(value / this.period) * this.period;
        if (abs(closetsPeriod - value) < this.distance) {
            value = closetsPeriod;
        }
        return value;
    }

    public float snap(float value) {
        return (float) this.snap((double)value);
    }

    public double getPeriod() {
        return this.period;
    }

    public void setPeriod(double period) {
        checkArgument(period >= 0, "Snapping period shall be positive, not " + period);
        this.period = period;
    }

    public double getDistance() {
        return this.distance;
    }

    public void setDistance(double distance) {
        checkArgument(distance > 0, "Snapping distance must be positive, not " + distance);
        this.distance = distance;
    }

}
