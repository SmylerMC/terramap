package net.smyler.smylib.math;

/**
 * Static Math class because for some reason Java is missing some pretty useful stuff like clamp methods.
 */
public final class Math {

    private Math() {}

    public static int clamp(int val, final int min, final int max) {
        if (val < min) val = min;
        if (val > max) val = max;
        return val;
    }

    public static long clamp(long val, final long min, final long max) {
        if (val < min) val = min;
        if (val > max) val = max;
        return val;
    }

    public static float clamp(float val, float min, float max) {
        if (val < min) val = min;
        if (val > max) val = max;
        return val;
    }

    public static double clamp(double val, double min, double max) {
        if (val < min) val = min;
        if (val > max) val = max;
        return val;
    }

}
