package fr.thesmyler.terramap.network.mapsync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.config.TerramapServerPreferences;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class RemoteSynchronizer {

	public static Map<UUID, RegisteredForUpdatePlayer> playersToUpdate = new HashMap<UUID, RegisteredForUpdatePlayer>();

	public static void syncPlayers(World world) {
		long ctime = System.currentTimeMillis();
		List<TerramapLocalPlayer> players = new ArrayList<TerramapLocalPlayer>();
		for(EntityPlayer player: world.playerEntities) {
			if(!TerramapServerPreferences.shouldDisplayPlayer(player.getPersistentID())) continue;
			TerramapLocalPlayer terraPlayer = new TerramapLocalPlayer(player);
			if(terraPlayer.isSpectator() && !TerramapConfig.ServerConfig.synchronizeSpectators) continue;
			players.add(terraPlayer);
		}
		IMessage pkt = new S2CPlayerSyncPacket(players.toArray(new TerramapLocalPlayer[players.size()]));
		for(RegisteredForUpdatePlayer player: RemoteSynchronizer.playersToUpdate.values()) {
			TerramapNetworkManager.CHANNEL_MAPSYNC.sendTo(pkt, player.player);
		}
		for(RegisteredForUpdatePlayer player: RemoteSynchronizer.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfig.ServerConfig.syncHeartbeatTimeout - 10000 && !player.noticeSent) {
				TerramapMod.logger.debug("Sending registration expires notice to " + player.player.getName());
				TerramapNetworkManager.CHANNEL_MAPSYNC.sendTo(new S2CRegistrationExpiresPacket(), player.player);
				player.noticeSent = true;
			}
		}
		for(RegisteredForUpdatePlayer player: RemoteSynchronizer.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfig.ServerConfig.syncHeartbeatTimeout) {
				TerramapMod.logger.debug("Unregistering " + player.player.getName() + "from map update as it did not renew its registration");
				RemoteSynchronizer.playersToUpdate.remove(player.player.getPersistentID());
				TerramapNetworkManager.CHANNEL_MAPSYNC.sendTo(new S2CRegistrationExpiresPacket(), player.player);
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
