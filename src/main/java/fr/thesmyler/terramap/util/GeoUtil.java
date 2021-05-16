package fr.thesmyler.terramap.util;

/**
 * A collection of algorithm to solve geographic problems
 * 
 * @author SmylerMC
 *
 */
public class GeoUtil {
	
	public static double getLongitudeInRange(double longitude) {
		if(!Double.isFinite(longitude)) throw new RuntimeException("longitude cannot be infinite");
		double l = longitude;
		while(l> 180d) l -= 360d;
		while(l<-180d) l += 360d;
		return l;
	}

	public static double getLatitudeInRange(double latitude) {
		if(!Double.isFinite(latitude)) throw new RuntimeException("longitude cannot be infinite");
		double l = latitude;
		while(l> 90d) l -= 180d;
		while(l<-90d) l += 180d;
		return l;
	}
	
	public static float getAzimuthInRange(float azimuth) {
		if(!Float.isFinite(azimuth)) throw new RuntimeException("azimuth cannot be infinite");
		while(azimuth >= 360f) azimuth -= 360f;
		while(azimuth < 0f) azimuth += 360f;
		return azimuth;
	}

}
