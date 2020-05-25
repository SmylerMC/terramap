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

import fr.smyler.terramap.forgeessentials.FeWarp;
import fr.smyler.terramap.network.SyncedPlayer;
import io.github.terra121.EarthGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;

public class TerramapServer {

	private static TerramapServer instance;

	private Map<UUID, SyncedPlayer> players = new HashMap<UUID, SyncedPlayer>();
	private boolean installedOnServer = false;
	private boolean syncPlayers = false;
	private EarthGeneratorSettings genSettings = null;

	public TerramapServer(boolean installedOnServer, boolean syncPlayers, @Nullable EarthGeneratorSettings genSettings) {
		this.installedOnServer = installedOnServer;
		this.syncPlayers = syncPlayers;
		this.genSettings = genSettings;
	}
	
	public boolean isInstalledOnServer() {
		return this.installedOnServer; 
	}
	
	public boolean arePlayersSynchronized() {
		return this.syncPlayers;
	}
	
	public Collection<SyncedPlayer> getPlayers() {
		if(this.isInstalledOnServer()) {
			return this.players.values();
		} else {
			List<SyncedPlayer> players = new ArrayList<SyncedPlayer>();
			for(EntityPlayer player: Minecraft.getMinecraft().world.playerEntities) players.add(new SyncedPlayer(player));
			return players;
		}
	}

	public EarthGeneratorSettings getGeneratorSettings() {
		return this.genSettings;
	}

	public List<FeWarp> getFeWarps() {
		return null; //TODO GetFeWarps
	}

	public void syncPlayers(SyncedPlayer[] players) {
		TerramapMod.logger.info("Got player packet");
		Set<SyncedPlayer> toAdd = new HashSet<SyncedPlayer>();
		Set<UUID> toRemove = new HashSet<UUID>();
		toRemove.addAll(this.players.keySet());
		for(SyncedPlayer player: players) {
			if(toRemove.remove(player.getUUID())) {
				SyncedPlayer savedPlayer = this.players.get(player.getUUID());
				savedPlayer.setDisplayName(player.getDisplayName());
				savedPlayer.setPosX(player.getPosX());
				savedPlayer.setPosZ(player.getPosZ());
			} else toAdd.add(player);
		}
		for(UUID uid: toRemove) this.players.remove(uid);
		for(SyncedPlayer sp: toAdd) this.players.put(sp.getUUID(), sp);
	}
	
	public static TerramapServer getServer() {
		return TerramapServer.instance;
	}
	
	public static void resetServer() {
		TerramapServer.instance = new TerramapServer(false, false, null);
	}
	
	public static void setServer(TerramapServer server) {
		TerramapServer.instance = server;
	}

}
