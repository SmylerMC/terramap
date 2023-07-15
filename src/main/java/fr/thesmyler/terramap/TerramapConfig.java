package fr.thesmyler.terramap;

import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static fr.thesmyler.smylibgui.SmyLibGui.getGameContext;

/**
 * @author SmylerMC
 * 
 * Terramap's config
 * 
 */
@Config(modid=TerramapMod.MODID)
public class TerramapConfig {

    public static final class Minimap {

        @Config.Name("minimap_enable")
        @Config.LangKey("terramap.config.minimap.enable")
        @Config.Comment("Set to false to hide the minimap")
        public boolean enable = true;

        @Config.Name("minimap_style")
        @Config.LangKey("terramap.config.minimap.style")
        @Config.Comment("Which map style to use for the minimap. Press P when viewing the fullscreen map to get the styles' id to use here")
        public String style = "osm";

        @Config.Name("minimap_position_x")
        @Config.LangKey("terramap.config.minimap.pos_x")
        @Config.Comment("X position of the minimap on screen, in percents")
        @Config.RangeDouble(min=0, max=100)
        @Config.SlidingOption
        public float posX = 1;

        @Config.Name("minimap_position_y")
        @Config.LangKey("terramap.config.minimap.pos_y")
        @Config.Comment("Y position of the minimap on screen, in percents")
        @Config.RangeDouble(min=0, max=100)
        @Config.SlidingOption
        public float posY = 1;

        @Config.Name("minimap_width")
        @Config.LangKey("terramap.config.minimap.width")
        @Config.Comment("Minimap width, in percents")
        @Config.RangeDouble(min=0, max=100)
        @Config.SlidingOption
        public float width = 20;

        @Config.Name("minimap_height")
        @Config.LangKey("terramap.config.minimap.height")
        @Config.Comment("Minimap height, in percents")
        @Config.RangeDouble(min=0, max=100)
        @Config.SlidingOption
        public float height = 15;

        @Config.Name("minimap_zoom")
        @Config.LangKey("terramap.config.minimap.zoom")
        @Config.Comment("Minimap zoom level")
        @Config.RangeDouble(min=0, max=19)
        @Config.SlidingOption
        public float zoomLevel = 18;

        @Config.Name("minimap_show_entities")
        @Config.LangKey("terramap.config.minimap.show_entities")
        @Config.Comment("Set to true to show entities on the minimap")
        public boolean showEntities = false;

        @Config.Name("minimap_show_other_players")
        @Config.LangKey("terramap.config.minimap.show_players")
        @Config.Comment("Set to false to hide other players on the minimap")
        public boolean showOtherPlayers = true;

        @Config.Name("minimap_tile_scaling")
        @Config.LangKey("terramap.config.minimap.tile_scaling")
        @Config.Comment("Try lowering this value if you have pixelated map because of vanilla GUI scalling. This is for the minimap.")
        @Config.RangeDouble(min=0.0, max=8.0)
        public double tileScaling = 0.0;

        @Config.Name("minimap_player_directions")
        @Config.LangKey("terramap.config.minimap.direction")
        @Config.Comment("Whether or not player directions should show up on the minimap")
        public boolean playerDirections = true;

        @Config.Name("minimap_player_rotation")
        @Config.LangKey("terramap.config.minimap.rotation")
        @Config.Comment("Whether or not to turn the minimap as the player looks around")
        public boolean playerRotation = false;
        
        @Config.Name("minimap_chunks")
        @Config.LangKey("terramap.config.minimap.chunk")
        @Config.Comment("Whether or not to show Minecraft region and chunk borders on the minimap")
        public boolean chunksRender = false;

        public double getEffectiveTileScaling() {
            if(this.tileScaling == 0) {
                return getGameContext().getScaleFactor();
            } else {
                return CLIENT.minimap.tileScaling;
            }
        }

    }

    public static final class Compass {
        @Config.Name("compass_enable")
        @Config.LangKey("terramap.config.compass.visibility")
        @Config.Comment("Show the compass in the hud")
        public boolean enable = true;

        @Config.Name("compass_position_x")
        @Config.LangKey("terramap.config.compass.position.x")
        @Config.Comment("Compass X position, as % of the screen's width")
        @Config.RangeDouble(min=0, max=100)
        @Config.SlidingOption
        public float posX = 1;

        @Config.Name("compass_position_y")
        @Config.LangKey("terramap.config.compass.position.y")
        @Config.Comment("Compass Y position, as % of the screen's height")
        @Config.RangeDouble(min=0, max=100)
        @Config.SlidingOption
        public float posY = 16;

        @Config.Name("compass_width")
        @Config.LangKey("terramap.config.compass.width")
        @Config.Comment("Compass width, as % of the screen's width")
        @Config.RangeDouble(min=0, max=100)
        @Config.SlidingOption
        public float width = 20;
    }

    public static final class Client {

        @Config.Name("Minimap")
        @Config.Comment("Everything related to the minimap")
        @Config.LangKey("terramap.config.minimap")
        public final Minimap minimap = new Minimap();

        @Config.Name("Compass")
        @Config.Comment("Everything related to the compass")
        @Config.LangKey("terramap.config.compass")
        public final Compass compass = new Compass();

        @Config.Name("tile_scaling")
        @Config.LangKey("terramap.config.tile_scaling")
        @Config.Comment("Try lowering this value if you have pixelated map because of vanilla GUI scalling. This is for the full-screen map.")
        @Config.RangeDouble(min=0.0, max=8.0)
        public double tileScaling = 0;

        @Config.Ignore public final int TILE_LOAD_MIN = 128;
        @Config.Ignore public final int TILE_LOAD_DEFAULT = 512;
        @Config.Ignore public final int TILE_LOAD_MAX = 4096;
        @Config.Name("max_tile_load")
        @Config.LangKey("terramap.config.max_tile_load")
        @Config.Comment("This is the maximum number of tiles to keep loaded. A lower number implies lower memory usage, however, if this is lower than the number of tiles displayed on your screen at once you will experience a huge performance drop. Change for a higher value if you experience lag when displaying a map on a large display")
        @Config.RangeInt(min=TILE_LOAD_MIN, max=TILE_LOAD_MAX)
        @Config.SlidingOption
        public int maxTileLoad = TILE_LOAD_DEFAULT;

        @Config.Ignore public final int LOW_ZOOM_LEVEL_MIN = 0;
        @Config.Ignore public final int LOW_ZOOM_LEVEL_DEFAULT = 2;
        @Config.Ignore public final int LOW_ZOOM_LEVEL_MAX = 3;
        @Config.Name("low_zoom_level")
        @Config.LangKey("terramap.config.low_zoom_level")
        @Config.Comment("Tiles bellow this zoom level will be loaded when the map loads for the first time and then will never be unloaded. Number of tiles to keep loaded per zoom level:\n"
                + "\t0 -> 1\n"
                + "\t1 -> 5\n"
                + "\t2 -> 21\n"
                + "\t3 -> 85\n"
                + "Honnestly, you shouldn't be changing that option, it's mostly for testing purposes")
        @Config.RangeInt(min=LOW_ZOOM_LEVEL_MIN, max=LOW_ZOOM_LEVEL_MAX)
        @Config.SlidingOption
        public int lowZoomLevel = LOW_ZOOM_LEVEL_DEFAULT;

        @Config.Name("unlock_zoom")
        @Config.LangKey("terramap.config.unlock_zoom")
        @Config.Comment("Set this to true to allow zoom level up to 25, even on maps where the tile server doesn't support it")
        public boolean unlockZoom = false;

        @Config.Ignore public final int DOUBLE_CLICK_DELAY_MIN = 10;
        @Config.Ignore public final int DOUBLE_CLICK_DELAY_DEFAULT = 500;
        @Config.Ignore public final int DOUBLE_CLICK_DELAY_MAX = 2000;
        @Config.Name("double_click_delay")
        @Config.LangKey("terramap.config.double_click_delay")
        @Config.Comment("Double click delay to use in guis, in milliscondes")
        @Config.RangeInt(min=DOUBLE_CLICK_DELAY_MIN, max=DOUBLE_CLICK_DELAY_MAX)
        @Config.SlidingOption
        public int doubleClickDelay = DOUBLE_CLICK_DELAY_DEFAULT;

        @Config.Name("save_ui_state")
        @Config.LangKey("terramap.config.save_ui_state")
        @Config.Comment("Whether or not to save the map ui state when closing the full-screen map. Enable to save F1 mode and debug mode.")
        public boolean saveUiState = false;

        @Config.Name("chat_on_map")
        @Config.LangKey("terramap.config.chat_on_map")
        @Config.Comment("Whether or not to show the chat over the full screen map")
        public boolean chatOnMap = true;

        @Config.Name("force_terra_world")
        @Config.LangKey("terramap.config.force_terra_world")
        @Config.Comment("Set this to true to force terramap to treat all worlds as Terra worlds")
        public boolean forceTerraWorld = false;

        public double getEffectiveTileScaling() {
            if(this.tileScaling == 0) {
                return getGameContext().getScaleFactor();
            } else {
                return CLIENT.tileScaling;
            }
        }

    }

    public static final class Server {
        @Config.Name("force_client_tp_cmd")
        @Config.LangKey("terramap.config.forcetpllcmd")
        @Config.Comment("If set to true, the server will force clients to use its own tp command, if false, client will use their own configuration")
        public boolean forceClientTpCmd = false;

        @Config.Name("synchronize_players")
        @Config.LangKey("terramap.config.sync_players")
        @Config.Comment("Wether or not to synchronize players from server to client so everyone appears on the map, no matter the distance")
        public boolean synchronizePlayers = true;

        @Config.Name("sync_spectators")
        @Config.LangKey("terramap.config.sync_spec")
        @Config.Comment("Synchronize spectator players or not (players still need to be synchronized)")
        public boolean synchronizeSpectators = true;

        @Config.Name("sync_interval")
        @Config.LangKey("terramap.config.sync_interval")
        @Config.Comment("Synchronization time interval, int ticks, higher means better server perfomance but a map which lags behind a bit more")
        @Config.RangeInt(min=1, max=100)
        @Config.SlidingOption
        public int syncInterval = 10;

        @Config.Name("sync_hearthbeet_timeout")
        @Config.LangKey("terramap.config.sync_heartbeet")
        @Config.RangeInt(min=20000)
        @Config.Comment("If a client keeps its map open more than this time, the server asks the client to confirm that it still needs map updates. This is in milliseconds")
        public int syncHeartbeatTimeout = 120000;

        @Config.Name("players_opt_in_to_display_default")
        @Config.LangKey("terramap.config.players_opt_in_to_display_default")
        @Config.Comment("If player sync is enabled, sould players be displayed by default (true) or should they opt-in (false)")
        public boolean playersDisplayDefault = true;

        @Config.Name("sync_custom_maps")
        @Config.LangKey("terramap.config.sync_custom_maps")
        @Config.Comment("Set to false if you do not want to send custom maps to clients. This is only for testing, as if you don't want to send map styles to client, the first thing to do is to not configure any.")
        public boolean sendCusomMapsToClient = true;

        @Config.Name("join_without_mod_message")
        @Config.LangKey("terramap.config.join_without_mod_message")
        @Config.Comment("A message to display to players who do not have Terramap installed, as a json text (https://minecraft.tools/en/json_text.php). Leave blank for no message.")
        public String joinWithoutModMessage = "";

        @Config.Name("join_with_outdated_mod_message")
        @Config.LangKey("terramap.config.join_with_outdated_mod_message")
        @Config.Comment("A message to display to players with an incompatible version of Terramap installed, as a json text (https://minecraft.tools/en/json_text.php). Leave blank for no message.")
        public String joinWithOutdatedModMessage = "[\"\",{\"text\":\"You are using an old and outdated version of Terramap, it will not work on this server. You can update \",\"color\":\"red\"},{\"text\":\"here\",\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.curseforge.com/minecraft/mc-mods/terramap\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open link.\"}},{\"text\":\".\",\"color\":\"red\"}]";

    }

    @Config.Name("client")
    @Config.Comment("Client side Terramap options")
    @Config.LangKey("terramap.config.client")
    public static final Client CLIENT = new Client();

    @Config.Name("server")
    @Config.Comment("Server side Terramap options")
    @Config.LangKey("terramap.config.server")
    public static final Server SERVER = new Server();

    @Config.Name("tpll_command")
    @Config.LangKey("terramap.config.tpllcmd")
    @Config.Comment("The base tpll command to use")
    public static String tpllcmd = "/tpll {latitude} {longitude}";

    @Config.Name("enable_debug_maps")
    @Config.LangKey("terramap.config.enable_debug_maps")
    @Config.Comment("Set this to true to enable debugging map styles")
    public static boolean enableDebugMaps = false;

    public static void sync() {
        ConfigManager.sync(TerramapMod.MODID, Config.Type.INSTANCE);
    }

    @Mod.EventBusSubscriber(modid = TerramapMod.MODID)
    private static class EventHandler {

        /**
         * Inject the new values and save to the config file when the config has been changed from the GUI.
         * 
         * @param event The event
         */
        @SubscribeEvent
        public static void onConfigChanged(final ConfigChangedEvent.OnConfigChangedEvent event) {
            if (event.getModID().equals(TerramapMod.MODID)) {
                TerramapConfig.sync();
            }
            TerramapMod.proxy.onConfigChanged(event);
        }

    }

}
