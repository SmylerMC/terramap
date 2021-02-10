package fr.thesmyler.smylibgui;

public final class Utils {
	
	public static int adaptAlpha(int color, float amount) {
		if(amount >= 1) return color;
		float factor = saturate(amount);
		int alpha = color >>> 24;
		int alphalessColor = color & 0x00FFFFFF;
		int newAlpha = Math.round(factor * alpha);
		return (newAlpha << 24) | alphalessColor;
	}
	
	public static float saturate(float f) {
		return Math.max(Math.min(1, f), 0);
	}
	
	public static int hslToRgb(float h, float s, float l){
	    float r, g, b;

	    if(s == 0){
	        r = g = b = l; // achromatic
	    }else{
	        float q = l < 0.5 ? l * (1 + s) : l + s - l * s;
	        float p = 2 * l - q;
	        r = hue2rgb(p, q, h + 1f/3f);
	        g = hue2rgb(p, q, h);
	        b = hue2rgb(p, q, h - 1f/3f);
	    }

	    return (255<<24) + (Math.round(r * 255)<<16) + (Math.round(g * 255)<<8) + Math.round(b * 255);
	}
	
	public static float hue2rgb(float p, float q, float r){
		float t = r;
        if(t < 0) t += 1;
        if(t > 1) t -= 1;
        if(t < 1f/6f) return p + (q - p) * 6f * t;
        if(t < 1f/2f) return q;
        if(t < 2f/3f) return p + (q - p) * (2f/3f - t) * 6f;
        return p;
    }
	
	public static long clamp(long value, long min, long max) {
		return Math.min(Math.max(value, min), max);
	}
	
	public static double clamp(double value, double min, double max) {
		return Math.min(Math.max(value, min), max);
	}
	
	public static boolean doBoxesCollide(int x1, int y1, int width1, int height1, int x2, int y2, int width2, int height2) {
		return x1 <= x2 + width2 && x1 + width1 >= x2 && y1 <= y2 + height2 && y1 + height1 >= y2;
	}

}
