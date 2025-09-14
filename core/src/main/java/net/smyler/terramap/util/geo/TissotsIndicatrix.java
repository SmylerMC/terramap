package net.smyler.terramap.util.geo;

import static java.lang.Math.toDegrees;

/**
 * The components of <a href="https://en.wikipedia.org/wiki/Tissot%27s_indicatrix">Tissot's indicatrix</a> at a given geographic location.
 *
 * @author Smyler
 */
public class TissotsIndicatrix {

    private double areaInflation, maxAngularDistortion, maxAngularDistortionDeg, maxScaleFactor, minScaleFactor;

    public TissotsIndicatrix(double areaInflation, double maxAngularDistortion, double minScaleFactor, double maxScaleFactor) {
        this.areaInflation = areaInflation;
        this.maxAngularDistortion = maxAngularDistortion;
        this.maxAngularDistortionDeg = toDegrees(maxAngularDistortion);
        this.maxScaleFactor = maxScaleFactor;
        this.minScaleFactor = minScaleFactor;
    }

    public TissotsIndicatrix() {
        this(1d, 0d, 1d, 1d);

    }

    public void set(double areaInflation, double maxAngularDistortion, double minScaleFactor, double maxScaleFactor) {
        this.areaInflation = areaInflation;
        this.maxAngularDistortion = maxAngularDistortion;
        this.maxAngularDistortionDeg = toDegrees(maxAngularDistortion);
        this.maxScaleFactor = maxScaleFactor;
        this.minScaleFactor = minScaleFactor;
    }

    public double areaInflation() {
        return this.areaInflation;
    }

    public double maxAngularDistortion() {
        return this.maxAngularDistortion;
    }

    public double maxAngularDistortionDegrees() {
        return this.maxAngularDistortionDeg;
    }

    public double maxScaleFactor() {
        return this.maxScaleFactor;
    }

    public double minScaleFactor() {
        return this.minScaleFactor;
    }

}
