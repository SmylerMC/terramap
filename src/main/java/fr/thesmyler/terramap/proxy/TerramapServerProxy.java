package fr.thesmyler.terramap.proxy;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.command.TerrashowCommand;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.eventhandlers.ServerTerramapEventHandler;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapServerProxy extends TerramapProxy {

	@Override
	public Side getSide() {
		return Side.SERVER;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapMod.logger.debug("Terramap server pre-init");
		TerramapNetworkManager.registerHandlers(Side.SERVER);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		TerramapMod.logger.debug("Terramap server init");
		MinecraftForge.EVENT_BUS.register(new ServerTerramapEventHandler());

	}

	@Override
	public double getGuiScaleForConfig() {
		// Don't care on server, this is just for the client config
		return 0;
	}

	@Override
	public void onServerStarting(FMLServerStartingEvent event) {
		
		/* 
		 * Unfortunately, Forge's ConfigManager does not let us modify our config when the game is still loading and 
		 * and calling ConfigManager::sync only injects the file's value into the fields instead of saving them to disk,
		 * which is why we have to do it once the game is fully loaded.
		 * 
		 * This is called on the physical client by TerramapClientEventHandler::onGuiScreenInit when the main title screen is shown.
		 */
	    TerramapConfig.update(); // Update if invalid values were left by old versions
	    
    	event.registerServerCommand(new TerrashowCommand());
	}

	@Override
	public GameType getGameMode(EntityPlayer e) {
		EntityPlayerMP player = (EntityPlayerMP)e;
		return player.interactionManager.getGameType();
	}

	@Override
	public void onConfigChanged(OnConfigChangedEvent event) {
	}

}
