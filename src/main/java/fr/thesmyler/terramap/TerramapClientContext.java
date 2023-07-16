package fr.thesmyler.terramap;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.toast.TextureToast;
import fr.thesmyler.terramap.saving.client.ClientSaveManager;
import fr.thesmyler.terramap.saving.client.SavedClientState;
import fr.thesmyler.terramap.gui.HudScreenHandler;
import fr.thesmyler.terramap.gui.screens.SavedMainScreenState;
import fr.thesmyler.terramap.gui.screens.TerramapScreen;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.raster.IRasterTiledMap;
import fr.thesmyler.terramap.maps.raster.MapStylesLibrary;
import fr.thesmyler.terramap.maps.raster.TiledMapProvider;
import fr.thesmyler.terramap.maps.raster.imp.TerrainPreviewMap;
import fr.thesmyler.terramap.maps.raster.imp.UrlTiledMap;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import fr.thesmyler.terramap.network.playersync.C2SPRegisterForUpdatesPacket;
import fr.thesmyler.terramap.network.playersync.PlayerSyncStatus;
import fr.thesmyler.terramap.network.playersync.TerramapLocalPlayer;
import fr.thesmyler.terramap.network.playersync.TerramapPlayer;
import fr.thesmyler.terramap.network.playersync.TerramapRemotePlayer;
import fr.thesmyler.terramap.util.TerramapUtil;
import net.buildtheearth.terraplusplus.EarthWorldType;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.buildtheearth.terraplusplus.generator.TerrainPreview;
import net.buildtheearth.terraplusplus.projection.GeographicProjection;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.buildtheearth.terraplusplus.projection.mercator.WebMercatorProjection;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nonnull;

/**
 * Client side context that store important information about the current server, world, proxy, etc.
 * 
 * @author SmylerMC
 *
 */
public class TerramapClientContext {

    private static TerramapClientContext instance;

    private static final GeographicProjection TERRAIN_PREVIEW_PROJECTION = new WebMercatorProjection(TerrainPreviewMap.BASE_ZOOM_LEVEL);

    private final Map<UUID, TerramapRemotePlayer> remotePlayers = new HashMap<>();
    private PlayerSyncStatus serverSyncPlayers = PlayerSyncStatus.DISABLED;
    private PlayerSyncStatus serverSyncSpectators = PlayerSyncStatus.DISABLED;
    private PlayerSyncStatus proxySyncPlayers = PlayerSyncStatus.DISABLED;
    private final PlayerSyncStatus proxySyncSpectators = PlayerSyncStatus.DISABLED;
    private TerramapVersion serverVersion = null;
    private String sledgehammerVersion = null;
    private GeographicProjection projection = null;
    private TerrainPreview terrainPreview = null;
    private boolean isRegisteredForUpdates = false;
    private String tpCommand = null;
    private final Map<String, IRasterTiledMap> serverMaps = new HashMap<>();
    private final Map<String, IRasterTiledMap> proxyMaps = new HashMap<>();
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

    private SavedClientState state;

    private final ClientSaveManager saveManager;

    public TerramapClientContext() {
        this.saveManager = new ClientSaveManager(Minecraft.getMinecraft().gameDir.toPath().resolve("terramap"));
        try {
            this.saveManager.createDirectoryIfNecessary();
        } catch (IOException exception) {
            TerramapMod.logger.error("An error occurred when preparing Terramap's save directory");
            TerramapMod.logger.catching(exception);
        }
        this.reloadState();
    }

    public boolean isInstalledOnServer() {
        return this.serverVersion != null; 
    }

    public Map<UUID, TerramapPlayer> getPlayerMap() {
        Map<UUID, TerramapPlayer> players = new HashMap<>();
        if(this.arePlayersSynchronized()) {
            players.putAll(this.remotePlayers);
        }
        if(this.getProjection() != null) {
            players.putAll(this.getLocalPlayersMap());
        }
        return players;
    }

    public Collection<TerramapPlayer> getPlayers() {
        return this.getPlayerMap().values();
    }

    public boolean hasPlayer(UUID uuid) {
        return this.remotePlayers.containsKey(uuid) || this.getLocalPlayersMap().containsKey(uuid);
    }

    public Map<UUID, TerramapPlayer> getLocalPlayersMap() {
        Map<UUID, TerramapPlayer> players = new HashMap<>();
        for(EntityPlayer player: Minecraft.getMinecraft().world.playerEntities) {
            players.put(player.getPersistentID(), new TerramapLocalPlayer(player));
        }
        return players;
    }

    public EarthGeneratorSettings getGeneratorSettings() {
        SavedClientState savedClientState = this.getSavedState();
        if(savedClientState.generatorSettings == null && this.hasSledgehammer() && this.isOnEarthWorld()) {
            return TerramapUtil.BTE_GENERATOR_SETTINGS; // Sledgehammer is installed and this is an Earth world, it should be safe to assume a BTE world
        }
        return savedClientState.generatorSettings;
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
        if(genSettings != null && this.hasSledgehammer() && !TerramapUtil.isBteCompatible(genSettings)) {
            TerramapMod.logger.error("Terramap server is reporting a projection which is not compatible with BTE, yet Sledgehammer is installer on the proxy!!");
            TerramapMod.logger.error("The proxy will be assuming a BTE projection, things will not work!");
            //TODO Warning on the GUI
        }
        this.getSavedState().generatorSettings = genSettings;
        this.projection = null;
        this.terrainPreview = null;
        this.saveState();
    }

    public void syncPlayers(TerramapRemotePlayer[] players) {
        Set<TerramapRemotePlayer> toAdd = new HashSet<>();
        Set<UUID> toRemove = new HashSet<>(this.remotePlayers.keySet());
        for(TerramapRemotePlayer player: players) {
            if(toRemove.remove(player.getUUID())) {
                TerramapRemotePlayer savedPlayer = this.remotePlayers.get(player.getUUID());
                savedPlayer.setDisplayName(player.getDisplayName());
                try {
                    savedPlayer.setLocationAndAzimuth(player.getLocation(), player.getAzimuth());
                } catch (OutOfProjectionBoundsException e) {
                    savedPlayer.setOutOfProjection();
                }
                savedPlayer.setGamemode(player.getGamemode());
            } else toAdd.add(player);
        }
        for(UUID uid: toRemove) this.remotePlayers.remove(uid);
        for(TerramapRemotePlayer sp: toAdd) this.remotePlayers.put(sp.getUUID(), sp);
    }


    public List<Entity> getEntities() {
        return Minecraft.getMinecraft().world.loadedEntityList;
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

    public void registerForUpdates(boolean yesNo) {
        this.isRegisteredForUpdates = yesNo;
        if(this.arePlayersSynchronized()) TerramapNetworkManager.CHANNEL_MAPSYNC.sendToServer(new C2SPRegisterForUpdatesPacket(this.isRegisteredForUpdates));
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

    public void reloadState() {
        ServerData servData = Minecraft.getMinecraft().getCurrentServerData();
        if(this.proxyForceGlobalSettings && this.proxyUUID != null) {
            this.state = this.saveManager.loadProxyState(this.proxyUUID);
            TerramapMod.logger.debug("Loaded proxy saved state for UUID {} (forced by proxy)", this.proxyUUID);
        } else if(this.worldUUID != null) {
            this.state = this.saveManager.loadWorldState(this.worldUUID);
            TerramapMod.logger.debug("Loaded world saved state for UUID {}", this.worldUUID);
        } else if(this.proxyUUID != null) {
            this.state = this.saveManager.loadProxyState(this.proxyUUID);
            TerramapMod.logger.debug("Loaded proxy saved state for UUID {} (world unknown)", this.proxyUUID);
        } else if (servData != null) {
            this.state = this.saveManager.loadServerState(servData);
            TerramapMod.logger.debug("Loaded server saved state for server {} ({})",servData.serverName, servData.serverIP);
        } else {
            this.state = this.saveManager.getDefaultState();
            TerramapMod.logger.debug("Went back to default state");
        }
    }

    public void saveState() {
        ServerData servData = Minecraft.getMinecraft().getCurrentServerData();
        if(this.proxyForceGlobalSettings && this.proxyUUID != null) {
            this.saveManager.saveProxyState(this.proxyUUID, this.state);
            TerramapMod.logger.debug("Saved proxy state for UUID {} (forced by proxy)", this.proxyUUID);
        } else if(this.worldUUID != null) {
            this.saveManager.saveWorldState(this.worldUUID, this.state);
            TerramapMod.logger.debug("Saved world state for UUID {}", this.worldUUID);
        } else if(this.proxyUUID != null) {
            this.saveManager.saveProxyState(this.proxyUUID, this.state);
            TerramapMod.logger.debug("Saved proxy state for UUID {} (world unknown)", this.proxyUUID);
        } else if (servData != null) {
            this.saveManager.saveServerState(servData, this.state);
            TerramapMod.logger.debug("Saved server state for server {} ({})",servData.serverName, servData.serverIP);
        } else {
            TerramapMod.logger.debug("Did not save state for unreliable context");
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
        this.reloadState();
    }

    public UUID getWorldUUID() {
        return worldUUID;
    }

    public void setWorldUUID(UUID worldUUID) {
        this.worldUUID = worldUUID;
        this.reloadState();
    }

    public UUID getProxyUUID() {
        return proxyUUID;
    }

    public void setProxyUUID(UUID worldUUID) {
        this.proxyUUID = worldUUID;
        this.reloadState();
    }

    /**
     * Tells whether the map should be accessible in the given context
     * <br>
     * The fullscreen map is accessible if one of the following is true:
     *  - The proxy forces the map
     *  - The force terra world client side configuration is set
     *  - The generation settings are set
     *  - We are on an Earth world and Terramap is not installed on the server
     * <br>
     *  The minimap is accessible if one of the following is true:
     *   - We are on an earth world
     *   - The generation settings are set
     * 
     * @param context   the context in which the map would be opened
     * @return true if the map should be accessible
     */
    public boolean allowsMap(MapContext context) {
        switch(context) {
            case FULLSCREEN:
                return this.proxyForcesGlobalMap
                        || TerramapConfig.CLIENT.forceTerraWorld
                        || this.getGeneratorSettings() != null
                        || (!this.isInstalledOnServer() && this.isOnEarthWorld());
            case MINIMAP:
                return this.getGeneratorSettings() != null;
            default:
                return false;
        }
    }

    /**
     * Checks if the player is on the overworld of an EarthWorld (client side check).
     * This check may be bypassed with {@link TerramapConfig.Client#forceTerraWorld}.
     *
     * @return true if the current world is a Terra world
     */
    public boolean isOnEarthWorld() {
        if (TerramapConfig.CLIENT.forceTerraWorld) return true;
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
        return !this.getSavedState().hasShownWelcome;
    }

    public void setHasShownWelcomeMessage(boolean yesNo) {
        this.getSavedState().hasShownWelcome = yesNo;
        this.saveState();
    }

    public SavedClientState getSavedState() {
        return this.state;
    }

    public void tryShowWelcomeToast() {
        if(this.shouldShowWelcomeToast()) {
            String key = KeyBindings.OPEN_MAP.getDisplayName();
            Minecraft.getMinecraft().getToastGui().add(
                    new TextureToast(
                            SmyLibGui.getTranslator().format("terramap.toasts.welcome.title"),
                            SmyLibGui.getTranslator().format("terramap.toasts.welcome.text", key),
                            new ResourceLocation(TerramapMod.MODID, "logo/50.png")));
            this.setHasShownWelcomeMessage(true);
        }
    }

    public void openMap() {
        Minecraft.getMinecraft().displayGuiScreen(new TerramapScreen(Minecraft.getMinecraft().currentScreen, this.getSavedState().mainScreen));
    }

    public void openMapAt(double zoom, double lon, double lat) {
        SavedMainScreenState state = this.getSavedState().mainScreen;
        state.map.center.set(lon, lat);
        state.map.zoom = zoom;
        state.map.trackedMarker = null;
        Minecraft.getMinecraft().displayGuiScreen(new TerramapScreen(Minecraft.getMinecraft().currentScreen, state));
    }

    @Nonnull
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
