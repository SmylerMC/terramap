package fr.smyler.terramap;

import fr.smyler.terramap.config.TerramapConfiguration;
import fr.smyler.terramap.network.PlayerSyncPacket;
import fr.smyler.terramap.network.ProjectionSyncPacket;
import fr.smyler.terramap.network.TerramapLocalPlayer;
import fr.smyler.terramap.network.TerramapPacketHandler;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.EarthWorldType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
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
		EarthGeneratorSettings settings = ProjectionSyncPacket.getEarthGeneratorSettingsFromWorld(world);
		if(settings == null) return;
		IMessage data = new ProjectionSyncPacket(settings);
		TerramapPacketHandler.INSTANCE.sendTo(data, player);

	}

	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) { //FIXME Never caled on Client
		TerramapMod.proxy.onPlayerLoggedOut(event);
	}

	@SubscribeEvent
	public static void onWorldTick(WorldTickEvent event) {
		//TODO Only sync players which changed
		if(!TerramapConfiguration.synchronizePlayers) return;
		if(!event.phase.equals(TickEvent.Phase.END)) return;
		 World world = event.world.getMinecraftServer().worlds[0];
		if(!(world.getWorldType() instanceof EarthWorldType)) return;
		if(tickCounter == 0) {
			TerramapLocalPlayer[] players = new TerramapLocalPlayer[world.playerEntities.size()];
			for(int i=0; i<players.length; i++) {
				players[i] = new TerramapLocalPlayer(world.playerEntities.get(i));
			}
			IMessage pkt = new PlayerSyncPacket(players);
			for(EntityPlayer player: world.playerEntities) TerramapPacketHandler.INSTANCE.sendTo(pkt, (EntityPlayerMP)player);
		}
		tickCounter = (tickCounter+1) % TerramapConfiguration.syncInterval;
	}

}
