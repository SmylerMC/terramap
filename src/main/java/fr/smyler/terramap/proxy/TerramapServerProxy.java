package fr.smyler.terramap.proxy;

import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.network.ProjectionSyncPacket;
import fr.smyler.terramap.network.TerramapPacketHandler;
import io.github.terra121.EarthGeneratorSettings;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapServerProxy extends TerramapProxy{

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapPacketHandler.registerHandlers(Side.SERVER);
		TerramapMod.logger.debug("Terramap server pre-init");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		TerramapMod.logger.debug("Terramap server init");
	}

	@Override
	public void onSyncProjection(EarthGeneratorSettings s) {
		// Should never be called on server
	}

	@Override
	public EarthGeneratorSettings getCurrentEarthGeneratorSettings(World world) {
		return ProjectionSyncPacket.getEarthGeneratorSettingsFromWorld(world);
	}

}
