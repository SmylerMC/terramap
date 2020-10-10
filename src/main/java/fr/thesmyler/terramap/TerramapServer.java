package fr.thesmyler.terramap;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.thesmyler.terramap.config.TerramapClientPreferences;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.TerramapScreenSavedState;
import fr.thesmyler.terramap.network.S2CTerramapHelloPacket.PlayerSyncStatus;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import fr.thesmyler.terramap.network.mapsync.C2SRegisterForUpdatesPacket;
import fr.thesmyler.terramap.network.mapsync.TerramapLocalPlayer;
import fr.thesmyler.terramap.network.mapsync.TerramapPlayer;
import fr.thesmyler.terramap.network.mapsync.TerramapRemotePlayer;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Used to represent the server from the client
 * 
 * @author SmylerMC
 *
 */
public class TerramapServer {

	private static TerramapServer instance;

	private Map<UUID, TerramapRemotePlayer> remotePlayers = new HashMap<UUID, TerramapRemotePlayer>();
	private PlayerSyncStatus syncPlayers = PlayerSyncStatus.DISABLED;
	private PlayerSyncStatus syncSpectators = PlayerSyncStatus.DISABLED;
	private String serverVersion = null;
	private String sledgehammerVersion = null;
	private EarthGeneratorSettings genSettings = null;
	private GeographicProjection projection = null;
	private boolean isRegisteredForUpdates = false;
	private String tpCommand = null;

	private String serverIdentifier = "genericserver";

	public boolean isInstalledOnServer() {
		return this.serverVersion != null; 
	}

	public Map<UUID, TerramapPlayer> getPlayerMap() {
		Map<UUID, TerramapPlayer> players = new HashMap<UUID, TerramapPlayer>();
		if(this.isInstalledOnServer()) {
			players.putAll(this.remotePlayers);
		}
		players.putAll(this.getLocalPlayers());
		return players;
	}

	public Collection<TerramapPlayer> getPlayers() {
		return this.getPlayerMap().values();
	}

	public boolean hasPlayer(UUID uuid) {
		return this.remotePlayers.containsKey(uuid) || this.getLocalPlayers().containsKey(uuid);
	}

	private Map<UUID, TerramapPlayer> getLocalPlayers() {
		Map<UUID, TerramapPlayer> players = new HashMap<UUID, TerramapPlayer>();
		for(EntityPlayer player: Minecraft.getMinecraft().world.playerEntities) {
			players.put(player.getPersistentID(), new TerramapLocalPlayer(player));
		}
		return players;
	}

	public EarthGeneratorSettings getGeneratorSettings() {
		return this.genSettings;
	}

	public GeographicProjection getProjection() {
		if(this.projection == null && this.genSettings != null) {
			this.projection = this.genSettings.getProjection();
		}
		return this.projection;
	}

	public void setGeneratorSettings(EarthGeneratorSettings genSettings) {
		this.genSettings = genSettings;
		this.projection = null;
	}

	public void saveSettings() {
		try {
			TerramapClientPreferences.setServerGenSettings(this.getServerIdentifier(), this.genSettings.toString());
			TerramapClientPreferences.save();
		} catch(Exception e) {
			TerramapMod.logger.info("Failed to save server preference file");
			TerramapMod.logger.catching(e);
		}
	}

	public void syncPlayers(TerramapRemotePlayer[] players) {
		Set<TerramapRemotePlayer> toAdd = new HashSet<TerramapRemotePlayer>();
		Set<UUID> toRemove = new HashSet<UUID>();
		toRemove.addAll(this.remotePlayers.keySet());
		for(TerramapRemotePlayer player: players) {
			if(toRemove.remove(player.getUUID())) {
				TerramapRemotePlayer savedPlayer = this.remotePlayers.get(player.getUUID());
				savedPlayer.setDisplayName(player.getDisplayName());
				savedPlayer.setLongitude(player.getLongitude());
				savedPlayer.setLatitude(player.getLatitude());
				savedPlayer.setIsSpectator(player.isSpectator());
			} else toAdd.add(player);
		}
		for(UUID uid: toRemove) this.remotePlayers.remove(uid);
		for(TerramapRemotePlayer sp: toAdd) this.remotePlayers.put(sp.getUUID(), sp);
	}


	public List<Entity> getEntities() {
		return Minecraft.getMinecraft().world.loadedEntityList;
	}

	private String buildCurrentServerIdentifer() {
		ServerData servData = Minecraft.getMinecraft().getCurrentServerData();
		if(Minecraft.getMinecraft().isIntegratedServerRunning()) {
			return Minecraft.getMinecraft().getIntegratedServer().getFolderName() + "@integrated_server@localhost";
		} else if(servData != null){
			return servData.serverName + "@" + servData.serverIP;
		} else {
			return "noserver";
		}
	}

	public TerramapScreenSavedState getSavedScreenState() {
		return TerramapClientPreferences.getServerSavedScreen(this.getServerIdentifier());
	}

	public boolean hasSavedScreenState() {
		return TerramapClientPreferences.getServerSavedScreen(this.getServerIdentifier()) != null;
	}

	public void setSavedScreenState(TerramapScreenSavedState svd) {
		TerramapClientPreferences.setServerSavedScreen(this.getServerIdentifier(), svd);
	}

	public void registerForUpdates(boolean yesNo) {
		this.isRegisteredForUpdates = yesNo;
		if(this.isInstalledOnServer())TerramapNetworkManager.CHANNEL_MAPSYNC.sendToServer(new C2SRegisterForUpdatesPacket(this.isRegisteredForUpdates));
	}

	public String getTpCommand() {
		if(this.tpCommand == null) return TerramapConfig.tpllcmd;
		else return this.tpCommand;
	}

	public void setTpCommand(String tpCmd) {
		TerramapMod.logger.info("Setting tp command defined by server");
		this.tpCommand = tpCmd;
	}

	public boolean needsUpdate() {
		return this.isRegisteredForUpdates;
	}

	public String getServerIdentifier() {
		return this.serverIdentifier;
	}

	public void guessServerIdentifier() {
		this.setServerIdentifier(this.buildCurrentServerIdentifer());
	}

	public void setServerIdentifier(String identifier) {
		this.serverIdentifier = identifier;
		String sttgStr = TerramapClientPreferences.getServerGenSettings(this.getServerIdentifier());
		if(sttgStr.length() > 0) {
			this.genSettings = new EarthGeneratorSettings(sttgStr);
			TerramapMod.logger.info("Got generator settings from client preferences file");
		}
	}

	public PlayerSyncStatus arePlayersSynchronized() {
		return this.syncPlayers;
	}

	public PlayerSyncStatus areSpectatorsSynchronized() {
		return this.syncSpectators;
	}

	public void setPlayersSynchronized(PlayerSyncStatus status) {
		this.syncPlayers = status;
	}

	public void setSpectatorsSynchronized(PlayerSyncStatus status) {
		this.syncSpectators = status;
	}

	public void setServerVersion(String version) {
		this.serverVersion = version;
	}

	public void setSledgehammerVersion(String version) {
		this.sledgehammerVersion = version;
	}

	public boolean hasSledgehammer() {
		return this.sledgehammerVersion != null;
	}

	public String getSledgehammerVersion() {
		return this.sledgehammerVersion;
	}

	public static TerramapServer getServer() {
		if(TerramapServer.instance == null) TerramapServer.resetServer();
		return TerramapServer.instance;
	}

	public static void resetServer() {
		TerramapMod.logger.info("Reseting server information");
		TerramapServer.instance = new TerramapServer();
	}

}
