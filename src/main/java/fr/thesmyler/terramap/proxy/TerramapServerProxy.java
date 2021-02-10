package fr.thesmyler.terramap.proxy;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.command.TerrashowCommand;
import fr.thesmyler.terramap.command.TilesetReloadCommand;
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
	public void onServerStarting(FMLServerStartingEvent event) {
    	event.registerServerCommand(new TerrashowCommand());
    	event.registerServerCommand(new TilesetReloadCommand());
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
