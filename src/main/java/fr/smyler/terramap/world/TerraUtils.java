package fr.smyler.terramap.world;

import io.github.terra121.EarthBiomeProvider;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;

public class TerraUtils {

	public static GeographicProjection getProjection() {
		Minecraft mc = Minecraft.getMinecraft();
		if(mc.isIntegratedServerRunning()) return((EarthBiomeProvider)Minecraft.getMinecraft().getIntegratedServer().getWorld(0).getBiomeProvider()).projection;
		return null;
	}
}
