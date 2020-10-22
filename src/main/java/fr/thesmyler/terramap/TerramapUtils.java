package fr.thesmyler.terramap;

import java.util.Random;

import io.github.opencubicchunks.cubicchunks.api.world.ICubeProvider;
import io.github.opencubicchunks.cubicchunks.core.server.CubeProviderServer;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.EarthTerrainProcessor;
import io.github.terra121.EarthWorldType;
import net.minecraft.world.World;

/**
 * @author SmylerMC
 *
 *
 */
public abstract class TerramapUtils {
	
	private static final Random RANDOM = new Random();
	
	public static final EarthGeneratorSettings BTE_GENERATOR_SETTINGS = new EarthGeneratorSettings("{\"projection\":\"bteairocean\",\"orentation\":\"upright\",\"scaleX\":7318261.522857145,\"scaleY\":7318261.522857145,\"smoothblend\":true,\"roads\":true,\"customcubic\":\"\",\"dynamicbaseheight\":true,\"osmwater\":true,\"buildings\":true}");
	public static final long EARTH_CIRCUMFERENCE = 40075017;

	
	public static char pickChar(char[] chars) {
		return chars[RANDOM.nextInt(chars.length)];
	}
	
	public static boolean isServerEarthWorld(World world) {
		if(world.getWorldType() instanceof EarthWorldType) { // Is this a terra save?
			ICubeProvider provider = (ICubeProvider) world.getChunkProvider();
			if(provider instanceof CubeProviderServer) { // Are we on server ?
				return ((CubeProviderServer) provider).getCubeGenerator() instanceof EarthTerrainProcessor; // Is it the overworld ?
			}
		}
		return  false;
	}
	
	public static EarthGeneratorSettings getEarthGeneratorSettingsFromWorld(World world) {
		if(TerramapUtils.isServerEarthWorld(world)) {
			return ((EarthTerrainProcessor)((CubeProviderServer)world.getChunkProvider()).getCubeGenerator()).cfg;
		} else return null;
	}
	
	public static boolean isBteCompatible(EarthGeneratorSettings gen) {
		return 
				gen != null &&
				gen.settings.projection.equals(BTE_GENERATOR_SETTINGS.settings.projection) &&
				gen.settings.orentation.equals(BTE_GENERATOR_SETTINGS.settings.orentation) &&
				gen.settings.scaleX.equals(BTE_GENERATOR_SETTINGS.settings.scaleX) &&
				gen.settings.scaleY.equals(BTE_GENERATOR_SETTINGS.settings.scaleY);
	}
	
}
