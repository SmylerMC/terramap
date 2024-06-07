package net.smyler.terramap;

import net.fabricmc.api.ModInitializer;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;

public class TerramapFabricMod implements ModInitializer {

    public static final Logger LOGGER = getLogger("terramap");
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Terramap");
    }

}
