package fr.thesmyler.terramap.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.TerramapUtils;
import fr.thesmyler.terramap.command.Permission;
import fr.thesmyler.terramap.command.PermissionManager;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.config.TerramapServerPreferences;
import fr.thesmyler.terramap.maps.MapStyleRegistry;
import fr.thesmyler.terramap.maps.TiledMap;
import fr.thesmyler.terramap.network.playersync.PlayerSyncStatus;
import fr.thesmyler.terramap.network.playersync.SP2CPlayerSyncPacket;
import fr.thesmyler.terramap.network.playersync.SP2CRegistrationExpiresPacket;
import fr.thesmyler.terramap.network.playersync.TerramapLocalPlayer;
import io.github.terra121.EarthGeneratorSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class RemoteSynchronizer {

	public static Map<UUID, RegisteredForUpdatePlayer> playersToUpdate = new HashMap<UUID, RegisteredForUpdatePlayer>();

	public static void syncPlayers(World world) {
		if(playersToUpdate.size() <= 0) return;
		long ctime = System.currentTimeMillis();
		List<TerramapLocalPlayer> players = new ArrayList<TerramapLocalPlayer>();
		for(EntityPlayer player: world.playerEntities) {
			if(!TerramapServerPreferences.shouldDisplayPlayer(player.getPersistentID())) continue;
			TerramapLocalPlayer terraPlayer = new TerramapLocalPlayer(player);
			if(terraPlayer.isSpectator() && !TerramapConfig.synchronizeSpectators) continue;
			players.add(terraPlayer);
		}
		IMessage pkt = new SP2CPlayerSyncPacket(players.toArray(new TerramapLocalPlayer[players.size()]));
		for(RegisteredForUpdatePlayer player: RemoteSynchronizer.playersToUpdate.values()) {
			TerramapNetworkManager.CHANNEL_MAPSYNC.sendTo(pkt, player.player);
		}
		for(RegisteredForUpdatePlayer player: RemoteSynchronizer.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfig.syncHeartbeatTimeout - 10000 && !player.noticeSent) {
				TerramapMod.logger.debug("Sending registration expires notice to " + player.player.getName());
				TerramapNetworkManager.CHANNEL_MAPSYNC.sendTo(new SP2CRegistrationExpiresPacket(), player.player);
				player.noticeSent = true;
			}
		}
		for(RegisteredForUpdatePlayer player: RemoteSynchronizer.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfig.syncHeartbeatTimeout) {
				TerramapMod.logger.debug("Unregistering " + player.player.getName() + "from map update as it did not renew its registration");
				RemoteSynchronizer.playersToUpdate.remove(player.player.getPersistentID());
				TerramapNetworkManager.CHANNEL_MAPSYNC.sendTo(new SP2CRegistrationExpiresPacket(), player.player);
			}
		}
	}
	
	public static void registerPlayerForUpdates(EntityPlayerMP player) {
		TerramapMod.logger.debug("Registering player for map updates: " + player.getDisplayNameString());
		RemoteSynchronizer.playersToUpdate.put(player.getPersistentID(), new RegisteredForUpdatePlayer(player, System.currentTimeMillis()));
	}
	
	public static void unregisterPlayerForUpdates(EntityPlayerMP player) {
		TerramapMod.logger.debug("Unregistering player for map updates: " + player.getDisplayNameString());
		RemoteSynchronizer.playersToUpdate.remove(player.getPersistentID());
	}
	
	public static void sendHelloToClient(EntityPlayerMP player) {
		// Send world data to the client
		World world = player.getEntityWorld();
		if(!TerramapUtils.isServerEarthWorld(world)) return;
		EarthGeneratorSettings settings = TerramapUtils.getEarthGeneratorSettingsFromWorld(world);
		IMessage data = new S2CTerramapHelloPacket(
				TerramapMod.getVersion(),
				settings,
				new UUID(0, 0), //TODO Implement world uuids
				PlayerSyncStatus.getFromBoolean(TerramapConfig.synchronizePlayers),
				PlayerSyncStatus.getFromBoolean(TerramapConfig.synchronizeSpectators),
				PermissionManager.hasPermission(player, Permission.RADAR_PLAYERS),
				PermissionManager.hasPermission(player, Permission.RADAR_ANIMALS),
				PermissionManager.hasPermission(player, Permission.RADAR_MOBS),
				true,
				//TODO Implement warps
				false);
		TerramapNetworkManager.CHANNEL_TERRAMAP.sendTo(data, player);
	}
	
	public static void sendTpCommandToClient(EntityPlayerMP player) {
		if(TerramapConfig.forceClientTpCmd)
			TerramapNetworkManager.CHANNEL_TERRAMAP.sendTo(new S2CTpCommandSyncPacket(TerramapConfig.tpllcmd), player);
	}
	
	public static void sendMapStylesToClient(EntityPlayerMP player) {
		if(TerramapConfig.sendCusomMapsToClient) {
			for(TiledMap map: MapStyleRegistry.getTiledMaps().values()) {
				SP2CMapStylePacket pkt = new SP2CMapStylePacket(map);
				TerramapNetworkManager.CHANNEL_TERRAMAP.sendTo(pkt, player);
			}
		}
	}
	
	public static void onServerHello(S2CTerramapHelloPacket pkt) {
		TerramapMod.logger.info("Got server hello, remote version is " + pkt.serverVersion);
		String jsonWorldSettings = null;
		if(pkt.worldSettings != null) {
			jsonWorldSettings = pkt.worldSettings.toString();
		}
		TerramapMod.logger.debug(
				"Server version: " + pkt.serverVersion + "\t" +
				"Server worldSettings: " + jsonWorldSettings + "\t" +
				"Server UUID: " + pkt.worldUUID + "\t" +
				"Sync players: " + pkt.syncPlayers + "\t" +
				"Sync spectators: " + pkt.syncSpectators + "\t" +
				"Enable player radar: " + pkt.enablePlayerRadar + "\t" +
				"Enable animal radar: " + pkt.enableAnimalRadar + "\t" +
				"Enable mob radar: " + pkt.enableMobRadar + "\t" +
				"Enable deco radar: " + pkt.enableDecoRadar + "\t" +
				"Warp support: " + pkt.hasWarpSupport + "\t"
			);
		TerramapRemote srv = TerramapRemote.getRemote();
		srv.setServerVersion(pkt.serverVersion);
		srv.setGeneratorSettings(pkt.worldSettings);
		if(pkt.worldUUID.getLeastSignificantBits() != 0 || pkt.worldUUID.getMostSignificantBits() != 0) {
			srv.guessRemoteIdentifier();
			srv.setRemoteIdentifier(srv.getRemoteIdentifier() + pkt.worldUUID.toString());
		}
		srv.setPlayersSynchronizedByServer(pkt.syncPlayers);
		srv.setSpectatorsSynchronizedByServer(pkt.syncSpectators);
		srv.setAllowsPlayerRadar(pkt.enablePlayerRadar);
		srv.setAllowsAnimalRadar(pkt.enableAnimalRadar);
		srv.setAllowsMobRadar(pkt.enableMobRadar);
		srv.setAllowsDecoRadar(pkt.enableDecoRadar);
		srv.setServerWarpSupport(pkt.hasWarpSupport);
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
