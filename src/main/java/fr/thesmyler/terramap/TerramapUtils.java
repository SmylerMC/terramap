package fr.thesmyler.terramap;

import java.util.Random;

import io.github.opencubicchunks.cubicchunks.api.world.ICubeProvider;
import io.github.opencubicchunks.cubicchunks.core.server.CubeProviderServer;
import net.buildtheearth.terraplusplus.EarthWorldType;
import net.buildtheearth.terraplusplus.generator.EarthGenerator;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.World;

/**
 * @author SmylerMC
 *
 *
 */
public abstract class TerramapUtils {
	
	private static final Random RANDOM = new Random();
	
	public static final EarthGeneratorSettings BTE_GENERATOR_SETTINGS = EarthGeneratorSettings.parse(EarthGeneratorSettings.BTE_DEFAULT_SETTINGS);
	public static final long EARTH_CIRCUMFERENCE = 40075017;

	
	public static char pickChar(char[] chars) {
		return chars[RANDOM.nextInt(chars.length)];
	}
	
	public static boolean isServerEarthWorld(World world) {
		if(!(world.getWorldType() instanceof EarthWorldType)) return false; // Is this a terra save?
		if(!(world.getChunkProvider() instanceof ICubeProvider)) return false; // Is a CC world (could be a different dimension)
		ICubeProvider provider = (ICubeProvider) world.getChunkProvider();
		if(!(provider instanceof CubeProviderServer)) return false; // Are we on server ?
		return ((CubeProviderServer) provider).getCubeGenerator() instanceof EarthGenerator; // Is it the overworld ?
	}
	
	public static boolean isOnEarthWorld(EntityPlayer player) {
		return player.getEntityWorld().getWorldType() instanceof EarthWorldType && player.dimension == 0;
	}
	
	public static EarthGeneratorSettings getEarthGeneratorSettingsFromWorld(World world) {
		if(TerramapUtils.isServerEarthWorld(world)) {
			ICubeProvider provider = (ICubeProvider) world.getChunkProvider();
			EarthGenerator generator = (EarthGenerator)((CubeProviderServer) provider).getCubeGenerator();
			return generator.settings;
		} else return null;
	}
	
	public static boolean isBteCompatible(EarthGeneratorSettings gen) {
		return 
				gen != null &&
				gen.projection().equals(BTE_GENERATOR_SETTINGS.projection()); //TODO Be a bit more laxist
	}
	
}
