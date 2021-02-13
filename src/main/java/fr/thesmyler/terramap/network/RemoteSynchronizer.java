package fr.thesmyler.terramap.network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapUtils;
import fr.thesmyler.terramap.TerramapVersion;
import fr.thesmyler.terramap.TerramapVersion.InvalidVersionString;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.config.TerramapServerPreferences;
import fr.thesmyler.terramap.maps.MapStylesLibrary;
import fr.thesmyler.terramap.maps.imp.UrlTiledMap;
import fr.thesmyler.terramap.network.playersync.PlayerSyncStatus;
import fr.thesmyler.terramap.network.playersync.SP2CPlayerSyncPacket;
import fr.thesmyler.terramap.network.playersync.SP2CRegistrationExpiresPacket;
import fr.thesmyler.terramap.network.playersync.TerramapLocalPlayer;
import fr.thesmyler.terramap.permissions.Permission;
import fr.thesmyler.terramap.permissions.PermissionManager;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public abstract class RemoteSynchronizer {

	public static Map<UUID, RegisteredForUpdatePlayer> playersToUpdate = new HashMap<UUID, RegisteredForUpdatePlayer>();

	public static void syncPlayers(WorldServer world) {
		if(playersToUpdate.size() <= 0) return;
		long ctime = System.currentTimeMillis();
		List<TerramapLocalPlayer> players = new ArrayList<TerramapLocalPlayer>();
		for(EntityPlayer player: world.playerEntities) {
			if(!TerramapServerPreferences.shouldDisplayPlayer(world, player.getPersistentID())) continue;
			TerramapLocalPlayer terraPlayer = new TerramapLocalPlayer(player);
			if(terraPlayer.isSpectator() && !TerramapConfig.SERVER.synchronizeSpectators) continue;
			players.add(terraPlayer);
		}
		IMessage pkt = new SP2CPlayerSyncPacket(players.toArray(new TerramapLocalPlayer[players.size()]));
		for(RegisteredForUpdatePlayer player: RemoteSynchronizer.playersToUpdate.values()) {
			TerramapNetworkManager.CHANNEL_MAPSYNC.sendTo(pkt, player.player);
		}
		for(RegisteredForUpdatePlayer player: RemoteSynchronizer.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfig.SERVER.syncHeartbeatTimeout - 10000 && !player.noticeSent) {
				TerramapMod.logger.debug("Sending registration expires notice to " + player.player.getName());
				TerramapNetworkManager.CHANNEL_MAPSYNC.sendTo(new SP2CRegistrationExpiresPacket(), player.player);
				player.noticeSent = true;
			}
		}
		for(RegisteredForUpdatePlayer player: RemoteSynchronizer.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfig.SERVER.syncHeartbeatTimeout) {
				TerramapMod.logger.debug("Unregistering " + player.player.getName() + "from map update as it did not renew its registration");
				RemoteSynchronizer.playersToUpdate.remove(player.player.getPersistentID());
				TerramapNetworkManager.CHANNEL_MAPSYNC.sendTo(new SP2CRegistrationExpiresPacket(), player.player);
			}
		}
	}

	public static void registerPlayerForUpdates(EntityPlayerMP player) {
		if(PermissionManager.hasPermission(player, Permission.RADAR_PLAYERS)) {
			TerramapMod.logger.debug("Registering player for map updates: " + player.getDisplayNameString());
			RemoteSynchronizer.playersToUpdate.put(player.getPersistentID(), new RegisteredForUpdatePlayer(player, System.currentTimeMillis()));
		}
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
				TerramapMod.getVersion().toString(),
				settings,
				TerramapServerPreferences.getWorldUUID(player.getServerWorld()),
				PlayerSyncStatus.getFromBoolean(TerramapConfig.SERVER.synchronizePlayers),
				PlayerSyncStatus.getFromBoolean(TerramapConfig.SERVER.synchronizeSpectators),
				PermissionManager.hasPermission(player, Permission.RADAR_PLAYERS),
				PermissionManager.hasPermission(player, Permission.RADAR_ANIMALS),
				PermissionManager.hasPermission(player, Permission.RADAR_MOBS),
				true,
				//TODO Implement warps
				false);
		TerramapNetworkManager.CHANNEL_TERRAMAP.sendTo(data, player);
	}

	public static void sendTpCommandToClient(EntityPlayerMP player) {
		if(TerramapConfig.SERVER.forceClientTpCmd)
			TerramapNetworkManager.CHANNEL_TERRAMAP.sendTo(new S2CTpCommandPacket(TerramapConfig.tpllcmd), player);
	}

	public static void sendMapStylesToClient(EntityPlayerMP player) {
		if(TerramapConfig.SERVER.sendCusomMapsToClient) {
			for(UrlTiledMap map: MapStylesLibrary.getUserMaps().values()) {
				if(!TerramapConfig.enableDebugMaps && map.isDebug()) continue;
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
		TerramapClientContext ctx = TerramapClientContext.getContext();

		try {
			ctx.setServerVersion(new TerramapVersion(pkt.serverVersion));
			ctx.setGeneratorSettings(pkt.worldSettings);
			if(pkt.worldUUID.getLeastSignificantBits() != 0 || pkt.worldUUID.getMostSignificantBits() != 0) {
				ctx.setWorldUUID(pkt.worldUUID);
			}
			ctx.setPlayersSynchronizedByServer(pkt.syncPlayers);
			ctx.setSpectatorsSynchronizedByServer(pkt.syncSpectators);
			ctx.setAllowsPlayerRadar(pkt.enablePlayerRadar);
			ctx.setAllowsAnimalRadar(pkt.enableAnimalRadar);
			ctx.setAllowsMobRadar(pkt.enableMobRadar);
			ctx.setAllowsDecoRadar(pkt.enableDecoRadar);
			ctx.setServerWarpSupport(pkt.hasWarpSupport);
		} catch (InvalidVersionString e) {
			TerramapMod.logger.warn("Failed to parse server version! will act as if the server did not have Terramap installed");
		}
		ctx.tryShowWelcomeToast();
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
