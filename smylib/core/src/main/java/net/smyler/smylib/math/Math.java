package net.smyler.smylib.math;

/**
 * Static Math class because for some reason Java is missing some pretty useful stuff like clamp methods.
 */
public final class Math {

    private Math() {}

    public static int clamp(int val, int min, int max) {
        if (val < min) val = min;
        if (val > max) val = max;
        return val;
    }

    public static long clamp(long val, long min, long max) {
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

    public static float saturate(float val) {
        if (val < 0) return 0f;
        if (val > 1) return 1f;
        return val;
    }

    public static double saturate(double val) {
        if (val < 0) return 0f;
        if (val > 1) return 1f;
        return val;
    }

    public static boolean doBoxesCollide(float x1, float y1, float width1, float height1, float x2, float y2, float width2, float height2) {
        return x1 <= x2 + width2 && x1 + width1 >= x2 && y1 <= y2 + height2 && y1 + height1 >= y2;
    }

}
