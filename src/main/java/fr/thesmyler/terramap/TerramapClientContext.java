package fr.thesmyler.terramap;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.thesmyler.smylibgui.toast.TextureToast;
import fr.thesmyler.terramap.config.TerramapClientPreferences;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.HudScreenHandler;
import fr.thesmyler.terramap.gui.screens.TerramapScreen;
import fr.thesmyler.terramap.gui.screens.TerramapScreenSavedState;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.IRasterTiledMap;
import fr.thesmyler.terramap.maps.MapStylesLibrary;
import fr.thesmyler.terramap.maps.TiledMapProvider;
import fr.thesmyler.terramap.maps.imp.TerrainPreviewMap;
import fr.thesmyler.terramap.maps.imp.UrlTiledMap;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import fr.thesmyler.terramap.network.playersync.C2SPRegisterForUpdatesPacket;
import fr.thesmyler.terramap.network.playersync.PlayerSyncStatus;
import fr.thesmyler.terramap.network.playersync.TerramapLocalPlayer;
import fr.thesmyler.terramap.network.playersync.TerramapPlayer;
import fr.thesmyler.terramap.network.playersync.TerramapRemotePlayer;
import net.buildtheearth.terraplusplus.EarthWorldType;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraplusplus.generator.TerrainPreview;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.mercator.WebMercatorProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

/**
 * Client side context that store important information about the current server, world, proxy, etc.
 * 
 * @author SmylerMC
 *
 */
public class TerramapClientContext {

	private static TerramapClientContext instance;
	
	private static final GeographicProjection TERRAIN_PREVIEW_PROJECTION = new WebMercatorProjection(TerrainPreviewMap.BASE_ZOOM_LEVEL);

	private Map<UUID, TerramapRemotePlayer> remotePlayers = new HashMap<UUID, TerramapRemotePlayer>();
	private PlayerSyncStatus serverSyncPlayers = PlayerSyncStatus.DISABLED;
	private PlayerSyncStatus serverSyncSpectators = PlayerSyncStatus.DISABLED;
	private PlayerSyncStatus proxySyncPlayers = PlayerSyncStatus.DISABLED;
	private PlayerSyncStatus proxySyncSpectators = PlayerSyncStatus.DISABLED;
	private TerramapVersion serverVersion = null;
	private String sledgehammerVersion = null;
	private EarthGeneratorSettings genSettings = null;
	private GeographicProjection projection = null;
	private TerrainPreview terrainPreview = null;
	private boolean isRegisteredForUpdates = false;
	private String tpCommand = null;
	private Map<String, IRasterTiledMap> serverMaps = new HashMap<>();
	private Map<String, IRasterTiledMap> proxyMaps = new HashMap<>();
	private boolean proxyHasWarpSupport = false;
	private boolean serverHasWarpSupport = false;
	private boolean allowPlayerRadar = true;
	private boolean allowAnimalRadar = true;
	private boolean allowMobRadar = true;
	private boolean allowDecoRadar = true;
	private boolean proxyForcesGlobalMap = false;
	private boolean proxyForceGlobalSettings = false;
	private UUID worldUUID = null;
	private UUID proxyUUID = null;

	private String serverIdentifier = "genericserver";

	public boolean isInstalledOnServer() {
		return this.serverVersion != null; 
	}

	public Map<UUID, TerramapPlayer> getPlayerMap() {
		Map<UUID, TerramapPlayer> players = new HashMap<UUID, TerramapPlayer>();
		if(this.arePlayersSynchronized()) {
			players.putAll(this.remotePlayers);
		}
		if(this.getProjection() != null) {
			players.putAll(this.getLocalPlayers());
		}
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
		if(this.genSettings == null && this.hasSledgehammer() && this.isOnEarthWorld()) {
			return TerramapUtils.BTE_GENERATOR_SETTINGS; // Sledgehammer is installed and this is an Earth world, it should be safe to assume a BTE world
		}
		return this.genSettings;
	}

	public GeographicProjection getProjection() {
		EarthGeneratorSettings gen = this.getGeneratorSettings();
		if(this.projection == null && gen != null) {
			this.projection = gen.projection();
		}
		return this.projection;
	}
	
	public TerrainPreview getTerrainPreview() {
		EarthGeneratorSettings gen = this.getGeneratorSettings();
		if(this.terrainPreview == null && gen != null) {			
			this.terrainPreview = new TerrainPreview(gen.withProjection(TERRAIN_PREVIEW_PROJECTION));
		}
		return this.terrainPreview;
	}

	public void setGeneratorSettings(EarthGeneratorSettings genSettings) {
		if(genSettings != null && this.hasSledgehammer() && !TerramapUtils.isBteCompatible(genSettings)) {
			TerramapMod.logger.error("Terrramap server is reporting a projection which is not compatible with BTE, yet Sledgehammer is installer on the proxy!!");
			TerramapMod.logger.error("The proxy will be assuming a BTE projection, things will not work!");
			//TODO Warning on the GUI
		}
		this.genSettings = genSettings;
		this.projection = null;
		this.terrainPreview = null;
	}

	public void saveSettings() {
		try {
			if(!this.isInstalledOnServer() && this.genSettings != null) {
				TerramapClientPreferences.setServerGenSettings(this.getContextIdentifier(), this.genSettings.toString());
			}
			TerramapClientPreferences.save();
		} catch(Exception e) {
			TerramapMod.logger.info("Failed to save client preference file");
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
				savedPlayer.setAzimut(player.getAzimut());
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
			if(this.proxyUUID != null) {
				return "proxy:" + this.proxyUUID.toString();
			}
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
	
	public void addServerMapStyle(UrlTiledMap map) {
		this.serverMaps.put(map.getId(), map);
        HudScreenHandler.updateMinimap();
	}
	
	public void addProxyMapStyle(UrlTiledMap map) {
		this.proxyMaps.put(map.getId(), map);
		HudScreenHandler.updateMinimap();
	}
	
	public void resetWorld() {
		this.setGeneratorSettings(null);
		this.serverVersion = null;
		this.serverMaps.clear();
		HudScreenHandler.updateMinimap();
	}
	
	public Map<String, IRasterTiledMap> getServerMapStyles() {
		return this.serverMaps;
	}
	
	public Map<String, IRasterTiledMap> getProxyMapStyles() {
		return this.proxyMaps;
	}
	
	/**
	 * @return a new Map containing all available mapstyles
	 */
	public Map<String, IRasterTiledMap> getMapStyles() {
		Map<String, IRasterTiledMap> maps = new HashMap<>();
		maps.putAll(MapStylesLibrary.getBaseMaps());
		maps.putAll(this.proxyMaps);
		maps.putAll(this.serverMaps);
		maps.putAll(MapStylesLibrary.getUserMaps());
		return maps;
	}

	public TerramapScreenSavedState getSavedScreenState() {
		return TerramapClientPreferences.getServerSavedScreen(this.getContextIdentifier());
	}

	public boolean hasSavedScreenState() {
		return TerramapClientPreferences.getServerSavedScreen(this.getContextIdentifier()) != null;
	}

	public void setSavedScreenState(TerramapScreenSavedState svd) {
		TerramapClientPreferences.setServerSavedScreen(this.getContextIdentifier(), svd);
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

	public String getContextIdentifier() {
		return this.serverIdentifier;
	}

	public void setRemoteIdentifier() {
		this.setRemoteIdentifier(this.buildCurrentServerIdentifer());
	}

	private void setRemoteIdentifier(String identifier) {
		this.serverIdentifier = identifier;
		String sttgStr = TerramapClientPreferences.getServerGenSettings(this.getContextIdentifier());
		if(sttgStr.length() > 0) {
			this.genSettings = EarthGeneratorSettings.parse(sttgStr);
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

	public void setServerVersion(TerramapVersion version) {
		this.serverVersion = version;
	}
	
	public TerramapVersion getServerVersion() {
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
		return this.proxyForcesGlobalMap;
	}
	
	public void setProxyForceMinimap(boolean yesNo) {
		this.proxyForcesGlobalMap = yesNo;
	}
	
	public void setProxyForceGlobalSettings(boolean yesNo) {
		this.proxyForceGlobalSettings = yesNo;
		this.setRemoteIdentifier();
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
	
	/**
	 * Tells whether or not the map should be accessible in the given context
	 * 
	 * The fullscreen map is accessible if one of the following is true:
	 *  - The proxy forces the map
	 *  - The generation settings are set
	 *  - We are on a Earth world and Terramap is not installed on the server
	 *  
	 *  The minimap is accessible if one of the following is true:
	 *   - We are on an earth world
	 *   - The generation settings are set
	 * 
	 * @param context
	 * @return true if the map should be accessible
	 */
	public boolean allowsMap(MapContext context) {
		switch(context) {
		case FULLSCREEN:
			return this.proxyForcesGlobalMap || this.getGeneratorSettings() != null || (!this.isInstalledOnServer() && this.isOnEarthWorld());
		case MINIMAP:
			return this.getGeneratorSettings() != null;
		default:
			return false;
		}
	}
	
	/**
	 * Checks if the player is on the overworld of an EarthWorld (client side check)
	 * 
	 * @return
	 */
	public boolean isOnEarthWorld() {
		WorldClient world = Minecraft.getMinecraft().world;
		EntityPlayerSP player = Minecraft.getMinecraft().player;
		return world != null && player != null && world.getWorldType() instanceof EarthWorldType && player.dimension == 0;
	}
	
	public void setupMaps() {
		for(IRasterTiledMap map: this.getMapStyles().values()) {
			map.setup();
		}
	}
	
	public boolean shouldShowWelcomeToast() {
		if(!this.allowsMap(MapContext.FULLSCREEN)) return false;
		if(!(Minecraft.getMinecraft().currentScreen == null)) return false;
		return !TerramapClientPreferences.getServerHasShownWelcome(this.getContextIdentifier());
	}
	
	public void setHasShownWelcomeMessage(boolean yesNo) {
		TerramapClientPreferences.setServerHasShownWelcome(this.getContextIdentifier(), yesNo);
		TerramapClientPreferences.save();
	}
	
	public void tryShowWelcomeToast() {
		if(this.shouldShowWelcomeToast()) {
			//FIXME often shows up twice
			String key = KeyBindings.OPEN_MAP.getKeyDescription();
			Minecraft.getMinecraft().getToastGui().add(new TextureToast(I18n.format("terramap.toasts.welcome.title"), I18n.format("terramap.toasts.welcome.text", key), new ResourceLocation(TerramapMod.MODID, "logo/50.png")));
			this.setHasShownWelcomeMessage(true);
		}
	}
	
	public void openMap() {
		Minecraft.getMinecraft().displayGuiScreen(new TerramapScreen(Minecraft.getMinecraft().currentScreen, TerramapClientContext.getContext().getMapStyles(), TerramapClientContext.getContext().getSavedScreenState()));
	}
	
	public void openMapAt(double zoom, double lon, double lat) {
		TerramapScreenSavedState state = this.getSavedScreenState();
		state.centerLongitude = lon;
		state.centerLatitude = lat;
		state.zoomLevel = zoom;
		state.trackedMarker = null;
		Minecraft.getMinecraft().displayGuiScreen(new TerramapScreen(Minecraft.getMinecraft().currentScreen, TerramapClientContext.getContext().getMapStyles(), state));
	}

	public static TerramapClientContext getContext() {
		if(TerramapClientContext.instance == null) TerramapClientContext.resetContext();
		return TerramapClientContext.instance;
	}

	public static void resetContext() {
		TerramapMod.logger.info("Reseting client context");
		TiledMapProvider.SERVER.setLastError(null);
		TiledMapProvider.PROXY.setLastError(null);
		TerramapClientContext.instance = new TerramapClientContext();
	}

}
