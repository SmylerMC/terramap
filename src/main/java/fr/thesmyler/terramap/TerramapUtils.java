package fr.thesmyler.terramap;

import java.security.MessageDigest;
import java.util.Random;

import io.github.opencubicchunks.cubicchunks.core.server.CubeProviderServer;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.EarthTerrainProcessor;
import io.github.terra121.EarthWorldType;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;

/**
 * @author SmylerMC
 *
 *
 */
public abstract class TerramapUtils {
	
	private static Random random = new Random();
	
	public static final EarthGeneratorSettings BTE_GENERATOR_SETTINGS = new EarthGeneratorSettings("{\"projection\":\"bteairocean\",\"orentation\":\"upright\",\"scaleX\":7318261.522857145,\"scaleY\":7318261.522857145,\"smoothblend\":true,\"roads\":true,\"customcubic\":\"\",\"dynamicbaseheight\":true,\"osmwater\":true,\"buildings\":true}");
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
			if(Minecraft.getMinecraft().gameSettings.language.equalsIgnoreCase("fr_fr")) return true;
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
	
	/**
	 * This function is just a wrapper around GeographicProjection::toGeo
	 * It's here in order to handle mapping Minecraft 0/0 to geographic 0°/0° instead of NaN/NaN
	 * 
	 * https://github.com/orangeadam3/terra121/issues/136
	 * 
	 * @param proj
	 * @param x
	 * @param z
	 * @return double array [longitude, latitude]
	 */
	public static double[] toGeo(GeographicProjection proj, double x, double z) {
		if(x == 0d && z == 0d) return new double[] {0, 0};
		return proj.toGeo(x, z);
	}
	
	public static double[] fromGeo(GeographicProjection proj, double longitude, double latitude) {
		return proj.fromGeo(longitude, latitude);
	}
	
	public static boolean isEarthWorld(World world) {
		return world.getWorldType() instanceof EarthWorldType;
	}
	
	public static EarthGeneratorSettings getEarthGeneratorSettingsFromWorld(World world) {
		if(TerramapUtils.isEarthWorld(world)) {
			return ((EarthTerrainProcessor)((CubeProviderServer)world.getChunkProvider()).getCubeGenerator()).cfg;
		} else return null;
	}
	
	public static boolean isBteCompatible(EarthGeneratorSettings gen) {
		return 
				gen.settings.projection.equals(BTE_GENERATOR_SETTINGS.settings.projection) &&
				gen.settings.orentation.equals(BTE_GENERATOR_SETTINGS.settings.orentation) &&
				gen.settings.scaleX.equals(BTE_GENERATOR_SETTINGS.settings.scaleX) &&
				gen.settings.scaleY.equals(BTE_GENERATOR_SETTINGS.settings.scaleY);
	}
	
}
