package fr.smyler.terramap.proxy;

import io.github.terra121.EarthGeneratorSettings;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public abstract class TerramapProxy {

	public abstract void preInit(FMLPreInitializationEvent event);
	public abstract void init(FMLInitializationEvent event);
	public abstract void onSyncProjection(EarthGeneratorSettings settings);
	public abstract EarthGeneratorSettings getCurrentEarthGeneratorSettings(World world);
	public abstract void onPlayerLoggedOut(PlayerLoggedOutEvent event);
	
}
