package fr.smyler.terramap;

import java.security.MessageDigest;
import java.util.Random;

import net.minecraft.client.Minecraft;

/**
 * @author SmylerMC
 *
 *
 */
public abstract class TerramapUtils {
	
	private static Random random = new Random();
	
	public static final long EARTH_CIRCUMFERENCE = 40075017;
	
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
	
	public static char pickChar(char[] chars) {
		return chars[random.nextInt(chars.length)];
	}
	
	public static boolean isBaguette() {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(Minecraft.getMinecraft().getCurrentServerData().serverIP.getBytes());
			byte[] theBaguette = {88, 65, -17, 73, 13, 8, 81, 32, 53, 2, 27, 14, 2, -3, -36, 11, -75, 79, 60, -103, -62, 80, 99, 30, 102, -84, 89, 44, 112, 53, 94, 36};
			for(int i=0; i<hash.length; i++) if(hash[i] != theBaguette[i]) return false;
			return true;
		} catch(Exception e) {
			return false;
		}
	}
	
	public static boolean isPirate() {
		try {
			return Minecraft.getMinecraft().gameSettings.language.equalsIgnoreCase("en_PT");
		} catch(Exception e) {
			return false;
		}
	}
	
}
