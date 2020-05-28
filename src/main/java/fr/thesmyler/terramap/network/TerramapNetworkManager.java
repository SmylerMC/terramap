package fr.thesmyler.terramap.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfiguration;
import fr.thesmyler.terramap.network.S2CRegistrationExpiresPacket.S2CRegistrationExpiresPacketHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TerramapNetworkManager {

	// The channel instance
	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID);

	// Packet discriminator counter, should be increased for each packet type.
	private static int discriminator = 0;
	
	public static Map<UUID, RegisteredForUpdatePlayer> playersToUpdate= new HashMap<UUID, RegisteredForUpdatePlayer>(); //TODO Remove player after some time
	
	/**
	 * Registers the handlers
	 * 
	 * @param side
	 */
	public static void registerHandlers(Side side){
		CHANNEL.registerMessage(S2CTerramapHelloPacket.S2CTerramapHelloPacketHandler.class, S2CTerramapHelloPacket.class, discriminator++, Side.CLIENT);
		CHANNEL.registerMessage(S2CPlayerSyncPacket.S2CPlayerSyncPacketHandler.class, S2CPlayerSyncPacket.class, discriminator++, Side.CLIENT);
		CHANNEL.registerMessage(C2SRegisterForUpdatesPacket.C2SRegisterForUpdatesPacketHandler.class, C2SRegisterForUpdatesPacket.class, discriminator++, Side.SERVER);
		CHANNEL.registerMessage(S2CRegistrationExpiresPacketHandler.class, S2CRegistrationExpiresPacket.class, discriminator++, Side.CLIENT);
	}
	
	public static void syncPlayers(World world) {
		long ctime = System.currentTimeMillis();
		List<TerramapLocalPlayer> players = new ArrayList<TerramapLocalPlayer>();
		for(EntityPlayer player: world.playerEntities) {
			TerramapLocalPlayer terraPlayer = new TerramapLocalPlayer(player);
			if(terraPlayer.isSpectator() && !TerramapConfiguration.syncSpectators) continue;
			players.add(terraPlayer);
		}
		IMessage pkt = new S2CPlayerSyncPacket(players.toArray(new TerramapLocalPlayer[players.size()]));
		for(RegisteredForUpdatePlayer player: TerramapNetworkManager.playersToUpdate.values()) {
			TerramapNetworkManager.CHANNEL.sendTo(pkt, player.player);
		}
		for(RegisteredForUpdatePlayer player: TerramapNetworkManager.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfiguration.syncHeartbeatTimeout - 10000 && !player.noticeSent) {
				TerramapMod.logger.debug("Sending registration expires notice to " + player.player.getName());
				CHANNEL.sendTo(new S2CRegistrationExpiresPacket(), player.player);
				player.noticeSent = true;
			}
		}
		for(RegisteredForUpdatePlayer player: TerramapNetworkManager.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfiguration.syncHeartbeatTimeout) {
				TerramapMod.logger.debug("Unregistering " + player.player.getName() + "from map update as it did not renew its registration");
				TerramapNetworkManager.playersToUpdate.remove(player.player.getPersistentID());
				CHANNEL.sendTo(new S2CRegistrationExpiresPacket(), player.player);
			}
		}
	}
	
	public static class RegisteredForUpdatePlayer {
		
		public EntityPlayerMP player;
		public long lastRegisterTime;
		boolean noticeSent = false;
		
		public RegisteredForUpdatePlayer(EntityPlayerMP player, long time) {
			this.player = player;
			this.lastRegisterTime = time;
		}
	}
	
}
