package fr.thesmyler.terramap;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.terramap.config.TerramapClientPreferences;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.TerramapScreenSavedState;
import fr.thesmyler.terramap.maps.MapStyleRegistry;
import fr.thesmyler.terramap.maps.TiledMap;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import fr.thesmyler.terramap.network.playersync.C2SPRegisterForUpdatesPacket;
import fr.thesmyler.terramap.network.playersync.PlayerSyncStatus;
import fr.thesmyler.terramap.network.playersync.TerramapLocalPlayer;
import fr.thesmyler.terramap.network.playersync.TerramapPlayer;
import fr.thesmyler.terramap.network.playersync.TerramapRemotePlayer;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.projection.GeographicProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.common.MinecraftForge;

/**
 * Used to represent the server from the client
 * 
 * @author SmylerMC
 *
 */
public class TerramapRemote {

	private static TerramapRemote instance;

	private Map<UUID, TerramapRemotePlayer> remotePlayers = new HashMap<UUID, TerramapRemotePlayer>();
	private PlayerSyncStatus serverSyncPlayers = PlayerSyncStatus.DISABLED;
	private PlayerSyncStatus serverSyncSpectators = PlayerSyncStatus.DISABLED;
	private PlayerSyncStatus proxySyncPlayers = PlayerSyncStatus.DISABLED;
	private PlayerSyncStatus proxySyncSpectators = PlayerSyncStatus.DISABLED;
	private String serverVersion = null;
	private String sledgehammerVersion = null;
	private EarthGeneratorSettings genSettings = null;
	private GeographicProjection projection = null;
	private boolean isRegisteredForUpdates = false;
	private String tpCommand = null;
	private Map<String, TiledMap> serverMaps = new HashMap<String, TiledMap>();
	private Map<String, TiledMap> proxyMaps = new HashMap<String, TiledMap>();
	private boolean proxyHasWarpSupport = false;
	private boolean serverHasWarpSupport = false;
	private boolean allowPlayerRadar = true;
	private boolean allowAnimalRadar = true;
	private boolean allowMobRadar = true;
	private boolean allowDecoRadar = true;
	private boolean proxyForceGlobalMap = false;
	private boolean proxyForceGlobalSettings = false;
	private UUID worldUUID = null;
	private UUID proxyUUID = null;

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
		if(genSettings != null && this.hasSledgehammer() && !TerramapUtils.isBteCompatible(genSettings)) {
			TerramapMod.logger.error("Terrramap server is reporting a projection which is not compatible with BTE, yet Sledgehammer is installer on the proxy!!");
			TerramapMod.logger.error("The proxy will be assuming a BTE projection, things will not work!");
			//TODO Warning on the GUI
		}
		this.genSettings = genSettings;
		this.projection = null;
	}

	public void saveSettings() {
		try {
			if(this.genSettings != null) {
				TerramapClientPreferences.setServerGenSettings(this.getRemoteIdentifier(), this.genSettings.toString());
				TerramapClientPreferences.save();
			}
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
				savedPlayer.setGamemode(player.getGamemode());
			} else toAdd.add(player);
		}
		for(UUID uid: toRemove) this.remotePlayers.remove(uid);
		for(TerramapRemotePlayer sp: toAdd) this.remotePlayers.put(sp.getUUID(), sp);
	}


	public List<Entity> getEntities() {
		return Minecraft.getMinecraft().world.loadedEntityList;
	}

	private String buildCurrentServerIdentifer() {
		if(this.proxyForceGlobalSettings && this.proxyUUID != null) {
			return "proxy:" + this.proxyUUID.toString();
		} else if(this.worldUUID != null) {
			return "server:" + this.worldUUID.toString();
		} else {
			ServerData servData = Minecraft.getMinecraft().getCurrentServerData();
			if(Minecraft.getMinecraft().isIntegratedServerRunning()) {
				return Minecraft.getMinecraft().getIntegratedServer().getFolderName() + "@integrated_server@localhost";
			} else if(servData != null){
				return servData.serverName + "@" + servData.serverIP;
			} else {
				return "noserver";
			}
		}
	}
	
	public void addServerMapStyle(TiledMap map) {
		this.serverMaps.put(map.getId(), map);
		
		// Update the minimap
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Pre(SmyLibGui.getHudScreen(), new ArrayList<GuiButton>()));
	}
	
	public void addProxyMapStyle(TiledMap map) {
		this.proxyMaps.put(map.getId(), map);
		
		// Update the minimap
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Pre(SmyLibGui.getHudScreen(), new ArrayList<GuiButton>()));
	}
	
	public void resetServerMapStyles() {
		this.serverMaps.clear();
		// Update the minimap
        MinecraftForge.EVENT_BUS.post(new GuiScreenEvent.InitGuiEvent.Pre(SmyLibGui.getHudScreen(), new ArrayList<GuiButton>()));
	}
	
	public Map<String, TiledMap> getMapStyles() {
		Map<String, TiledMap> maps = new HashMap<String, TiledMap>();
		maps.putAll(MapStyleRegistry.getTiledMaps());
		maps.putAll(this.proxyMaps);
		maps.putAll(this.serverMaps);
		return maps;
	}

	public TerramapScreenSavedState getSavedScreenState() {
		return TerramapClientPreferences.getServerSavedScreen(this.getRemoteIdentifier());
	}

	public boolean hasSavedScreenState() {
		return TerramapClientPreferences.getServerSavedScreen(this.getRemoteIdentifier()) != null;
	}

	public void setSavedScreenState(TerramapScreenSavedState svd) {
		TerramapClientPreferences.setServerSavedScreen(this.getRemoteIdentifier(), svd);
	}

	public void registerForUpdates(boolean yesNo) {
		this.isRegisteredForUpdates = yesNo;
		if(this.isInstalledOnServer() && this.arePlayersSynchronized()) TerramapNetworkManager.CHANNEL_MAPSYNC.sendToServer(new C2SPRegisterForUpdatesPacket(this.isRegisteredForUpdates));
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

	public String getRemoteIdentifier() {
		return this.serverIdentifier;
	}

	public void setRemoteIdentifier() {
		this.setRemoteIdentifier(this.buildCurrentServerIdentifer());
	}

	private void setRemoteIdentifier(String identifier) {
		this.serverIdentifier = identifier;
		String sttgStr = TerramapClientPreferences.getServerGenSettings(this.getRemoteIdentifier());
		if(sttgStr.length() > 0) {
			this.genSettings = new EarthGeneratorSettings(sttgStr);
			TerramapMod.logger.info("Got generator settings from client preferences file");
		}
	}

	public boolean arePlayersSynchronized() {
		return this.proxySyncPlayers.equals(PlayerSyncStatus.ENABLED) || this.serverSyncPlayers.equals(PlayerSyncStatus.ENABLED);
	}

	public boolean areSpectatorsSynchronized() {
		return this.proxySyncSpectators.equals(PlayerSyncStatus.ENABLED) || this.proxySyncPlayers.equals(PlayerSyncStatus.ENABLED);
	}
	
	public PlayerSyncStatus doesServerSyncPlayers() {
		return this.serverSyncPlayers;
	}
	
	public PlayerSyncStatus doesServerSyncSpectators() {
		return this.serverSyncSpectators;
	}
	
	public PlayerSyncStatus doesProxySyncPlayers() {
		return this.proxySyncPlayers;
	}
	
	public PlayerSyncStatus doesProxySyncSpectators() {
		return this.proxySyncSpectators;
	}

	public void setPlayersSynchronizedByServer(PlayerSyncStatus status) {
		this.serverSyncPlayers = status;
	}

	public void setSpectatorsSynchronizedByServer(PlayerSyncStatus status) {
		this.serverSyncSpectators = status;
	}
	
	public void setPlayersSynchronizedByProxy(PlayerSyncStatus status) {
		this.proxySyncPlayers = status;
	}

	public void setSpectatorsSynchronizedByProxy(PlayerSyncStatus status) {
		this.proxySyncPlayers = status;
	}

	public void setServerVersion(String version) {
		this.serverVersion = version;
	}
	
	public String getServerVersion() {
		return this.serverVersion;
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

	public boolean doesProxyHaveWarpSupport() {
		return proxyHasWarpSupport;
	}

	public void setProxyWarpSupport(boolean proxyHasWarpSupport) {
		this.proxyHasWarpSupport = proxyHasWarpSupport;
	}

	public boolean doesServerHaveWarpSupport() {
		return serverHasWarpSupport;
	}

	public void setServerWarpSupport(boolean serverHasWarpSupport) {
		this.serverHasWarpSupport = serverHasWarpSupport;
	}

	public boolean allowsPlayerRadar() {
		return allowPlayerRadar;
	}

	public void setAllowsPlayerRadar(boolean allowPlayerRadar) {
		this.allowPlayerRadar = allowPlayerRadar;
	}

	public boolean allowsAnimalRadar() {
		return allowAnimalRadar;
	}

	public void setAllowsAnimalRadar(boolean allowAnimalRadar) {
		this.allowAnimalRadar = allowAnimalRadar;
	}

	public boolean allowsMobRadar() {
		return allowMobRadar;
	}

	public void setAllowsMobRadar(boolean allowMobRadar) {
		this.allowMobRadar = allowMobRadar;
	}

	public boolean allowsDecoRadar() {
		return allowDecoRadar;
	}

	public void setAllowsDecoRadar(boolean allowDecoRadar) {
		this.allowDecoRadar = allowDecoRadar;
	}
	
	public boolean doesProxyForceMinimap() {
		return this.proxyForceGlobalMap;
	}
	
	public void setProxyForceMinimap(boolean yesNo) {
		this.proxyForceGlobalMap = yesNo;
	}
	
	public void setProxyForceGlobalSettings(boolean yesNo) {
		this.proxyForceGlobalSettings = yesNo;
	}

	public UUID getWorldUUID() {
		return worldUUID;
	}

	public void setWorldUUID(UUID worldUUID) {
		this.worldUUID = worldUUID;
		this.setRemoteIdentifier();
	}
	
	public UUID getProxyUUID() {
		return proxyUUID;
	}

	public void setProxyUUID(UUID worldUUID) {
		this.proxyUUID = worldUUID;
		this.setRemoteIdentifier();
	}

	public static TerramapRemote getRemote() {
		if(TerramapRemote.instance == null) TerramapRemote.resetRemote();
		return TerramapRemote.instance;
	}

	public static void resetRemote() {
		TerramapMod.logger.info("Reseting server information");
		TerramapRemote.instance = new TerramapRemote();
	}

}
