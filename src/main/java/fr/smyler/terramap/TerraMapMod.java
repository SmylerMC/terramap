package fr.smyler.terramap;

import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(modid = TerraMapMod.MODID, name = TerraMapMod.NAME, version = TerraMapMod.VERSION)
public class TerraMapMod {
	
    public static final String MODID = "terramap";
    public static final String NAME = "Terramap";
    public static final String VERSION = "0.0.0";

    private static Logger logger;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) {
        logger = event.getModLog();
    }

    @EventHandler
    public void init(FMLInitializationEvent event) {
    	TerraMapMod.logger.debug("Initializing terramap");
    }
}
