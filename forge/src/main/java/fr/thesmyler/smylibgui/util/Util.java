package fr.thesmyler.smylibgui.util;

public final class Util {

    public static float saturate(float f) {
        return Math.max(Math.min(1, f), 0);
    }

    public static long clamp(long value, long min, long max) {
        return Math.min(Math.max(value, min), max);
    }

    public static double clamp(double value, double min, double max) {
        return Math.min(Math.max(value, min), max);
    }

    public static boolean doBoxesCollide(float x1, float y1, float width1, float height1, float x2, float y2, float width2, float height2) {
        return x1 <= x2 + width2 && x1 + width1 >= x2 && y1 <= y2 + height2 && y1 + height1 >= y2;
    }

}
