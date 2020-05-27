package fr.smyler.terramap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;

import fr.smyler.terramap.config.TerramapServerPreferences;
import fr.smyler.terramap.forgeessentials.FeWarp;
import fr.smyler.terramap.gui.GuiTiledMap.SavedMapState;
import fr.smyler.terramap.network.C2SRegisterForUpdatesPacket;
import fr.smyler.terramap.network.TerramapLocalPlayer;
import fr.smyler.terramap.network.TerramapPacketHandlers;
import fr.smyler.terramap.network.TerramapPlayer;
import fr.smyler.terramap.network.TerramapRemotePlayer;
import io.github.terra121.EarthGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;

/**
 * Used to represent the server from the client
 * 
 * @author hector
 *
 */
public class TerramapServer {

	private static TerramapServer instance;

	private Map<UUID, TerramapRemotePlayer> remotePlayers = new HashMap<UUID, TerramapRemotePlayer>();
	private boolean syncPlayers = false;
	private boolean syncSpectators = false;
	private boolean serverHasFe = false;
	private String serverVersion = null;
	private EarthGeneratorSettings genSettings = null;
	private boolean isRegisteredForUpdates = false;
	

	public TerramapServer(String serverVersion, boolean syncPlayers, boolean syncSpectators, boolean hasFe, @Nullable EarthGeneratorSettings genSettings) {
		this.serverVersion = serverVersion;
		this.syncPlayers = syncPlayers;
		this.syncSpectators = syncSpectators;
		this.serverHasFe = hasFe;
		this.genSettings = genSettings;
		if(this.genSettings == null) {
			String sttgStr = TerramapServerPreferences.getServerGenSettings(this.getCurrentServerIdentifer());
			if(sttgStr.length() > 0) {
				this.genSettings = new EarthGeneratorSettings(sttgStr);
				TerramapMod.logger.info("Got generator settings from server preferences file");
			}
		}
	}
	
	public TerramapServer() {
		
	}
	
	public boolean isInstalledOnServer() {
		return this.serverVersion != null; 
	}
	
	public boolean arePlayersSynchronized() {
		return this.syncPlayers;
	}
	
	public Collection<TerramapPlayer> getPlayers() {
		Map<UUID, TerramapPlayer> players = new HashMap<UUID, TerramapPlayer>();
		if(this.isInstalledOnServer()) {
			players.putAll(this.remotePlayers);
		}
		players.putAll(this.getLocalPlayers());
		return players.values();
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
	
	public void setGeneratorSettings(EarthGeneratorSettings genSettings) {
		this.genSettings = genSettings;
	}
	
	public void saveSettings() {
		TerramapServerPreferences.setServerGenSettings(this.getCurrentServerIdentifer(), this.genSettings.toString());
		TerramapServerPreferences.save();
	}

	public boolean doesSyncFeStuff() {
		return this.serverHasFe;
	}
	
	public List<FeWarp> getFeWarps() {
		return new ArrayList<FeWarp>(); //TODO getFeWarps
	}

	public void syncPlayers(TerramapRemotePlayer[] players) {
		Set<TerramapRemotePlayer> toAdd = new HashSet<TerramapRemotePlayer>();
		Set<UUID> toRemove = new HashSet<UUID>();
		toRemove.addAll(this.remotePlayers.keySet());
		for(TerramapRemotePlayer player: players) {
			if(toRemove.remove(player.getUUID())) {
				TerramapRemotePlayer savedPlayer = this.remotePlayers.get(player.getUUID());
				savedPlayer.setDisplayName(player.getDisplayName());
				savedPlayer.setPosX(player.getPosX());
				savedPlayer.setPosZ(player.getPosZ());
			} else toAdd.add(player);
		}
		for(UUID uid: toRemove) this.remotePlayers.remove(uid);
		for(TerramapRemotePlayer sp: toAdd) this.remotePlayers.put(sp.getUUID(), sp);
	}
	
	public boolean syncSpectators() {
		return this.syncSpectators; //TODO Move and rename
	}
	
	
	public List<Entity> getEntities() {
		return Minecraft.getMinecraft().world.loadedEntityList;
	}
	
	public String getCurrentServerIdentifer() {
		ServerData servData = Minecraft.getMinecraft().getCurrentServerData();
		if(servData == null) return "wip@locahost"; //TODO Find something for single player
		return servData.serverName + "@" + servData.serverIP;
	}
	
	public SavedMapState getSavedMap() {
		return new SavedMapState(TerramapServerPreferences.getServerMapState(this.getCurrentServerIdentifer()));
	}
	
	public boolean hasSavedMap() {
		return TerramapServerPreferences.getServerMapState(this.getCurrentServerIdentifer()).length() != 0;
	}
	
	public void setSavedMap(SavedMapState svd) {
		TerramapServerPreferences.setServerMapState(this.getCurrentServerIdentifer(), svd.toString());
	}
	
	public void registerForUpdates(boolean yesNo) {
		this.isRegisteredForUpdates = yesNo;
		if(this.isInstalledOnServer())TerramapPacketHandlers.INSTANCE.sendToServer(new C2SRegisterForUpdatesPacket(this.isRegisteredForUpdates));
	}

	public static TerramapServer getServer() {
		if(TerramapServer.instance == null) TerramapServer.resetServer();
		return TerramapServer.instance;
	}
	
	public static void resetServer() {
		TerramapMod.logger.info("Reseting server information");
		TerramapServer.instance = new TerramapServer();
	}
	
	public static void setServer(TerramapServer server) {
		TerramapServer.instance = server;
	}

}
