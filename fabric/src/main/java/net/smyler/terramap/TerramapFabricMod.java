package net.smyler.terramap;

import net.fabricmc.api.ModInitializer;
import net.minecraft.client.Minecraft;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.game.WrappedMinecraft;
import org.apache.logging.log4j.Logger;

import static org.apache.logging.log4j.LogManager.getLogger;

public class TerramapFabricMod implements ModInitializer {

    public static final Logger LOGGER = getLogger("terramap");
    @Override
    public void onInitialize() {
        LOGGER.info("Initializing Terramap");
        SmyLib.initializeGameClient(new WrappedMinecraft(Minecraft.getInstance()), LOGGER);
    }

}
