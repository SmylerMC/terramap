package fr.smyler.terramap.proxy;

import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public abstract class TerramapProxy {

	public abstract void preInit(FMLPreInitializationEvent event);
	public abstract void init(FMLInitializationEvent event);
}
