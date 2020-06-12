package fr.thesmyler.terramap;

import fr.thesmyler.terramap.config.TerramapConfiguration;
import fr.thesmyler.terramap.config.TerramapServerPreferences;
import fr.thesmyler.terramap.network.S2CTerramapHelloPacket;
import fr.thesmyler.terramap.network.S2CTpCommandSyncPacket;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.EarthWorldType;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * The event subscriber for generic server events
 * 
 * @author Smyler
 *
 */
@Mod.EventBusSubscriber(modid=TerramapMod.MODID)
public final class TerramapEventHandler {

	private static long tickCounter = 0;

	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event){
		//Send world data to the client
		TerramapMod.proxy.onPlayerLoggedIn(event);
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		World world = player.getEntityWorld();
		if(!(world.getWorldType() instanceof EarthWorldType)) return;
		EarthGeneratorSettings settings = S2CTerramapHelloPacket.getEarthGeneratorSettingsFromWorld(world);
		if(settings == null) return;
		IMessage data = new S2CTerramapHelloPacket(TerramapMod.getVersion(), settings, TerramapConfiguration.synchronizePlayers, TerramapConfiguration.syncSpectators, false);
		TerramapNetworkManager.CHANNEL.sendTo(data, player);
		if(TerramapConfiguration.forceClientTpCmd) TerramapNetworkManager.CHANNEL.sendTo(new S2CTpCommandSyncPacket(TerramapConfiguration.tpllcmd), player);
	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		TerramapMod.proxy.onPlayerLoggedOut(event);
		TerramapNetworkManager.playersToUpdate.remove(event.player.getPersistentID());
	}

	@SubscribeEvent
	public static void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
		TerramapServer.resetServer();
	}

	@SubscribeEvent
	public static void onClientConnected(ClientConnectedToServerEvent event) {
	}


	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		if(event.phase.equals(TickEvent.Phase.END)) return;
		World world = event.world.getMinecraftServer().worlds[0]; //event.world has no entity or players
		if(TerramapConfiguration.synchronizePlayers
				&& world.getWorldType() instanceof EarthWorldType
				&& tickCounter == 0) {
			TerramapNetworkManager.syncPlayers(world);
		}
		tickCounter = (tickCounter+1) % TerramapConfiguration.syncInterval;
	}
	
	@SubscribeEvent
	public static void onWorldSave(WorldEvent.Save event) {
		TerramapServerPreferences.save();
	}

}
