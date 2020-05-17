package fr.smyler.terramap.maps.utils;

/**
 * @author SmylerMC
 *
 * TODO Type comment
 *
 */
public class TerramapUtils {
	
	public static int modulus(int a, int b) {
		int mod = a%b;
		return mod<0?b+mod:mod ;
	}

	/**
	 * 
	 * @param x
	 * @return The integer just before x
	 */
	public static int roudSmaller(float x){
		return x >= 0? (int) x: (int) x - 1;
	}
	
	/**
	 * 
	 * @param x
	 * @return The integer just before x
	 */
	public static int roudSmaller(double x){
		return x >= 0? (int) x: (int) x - 1;
	}
	
}
