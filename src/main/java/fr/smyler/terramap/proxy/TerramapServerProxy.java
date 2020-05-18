package fr.smyler.terramap.proxy;

import fr.smyler.terramap.TerramapMod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class TerramapServerProxy extends TerramapProxy{

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapMod.logger.debug("Terramap server pre-init");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		TerramapMod.logger.debug("Terramap server init");
	}

}
