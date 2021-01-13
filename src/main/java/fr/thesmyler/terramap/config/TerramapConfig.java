package fr.thesmyler.terramap.config;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.terramap.TerramapMod;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author SmylerMC
 * 
 * Terramap's config
 * 
 */
@Config(modid=TerramapMod.MODID)
public class TerramapConfig {

	@Config.Name("minimap_enable")
	@Config.LangKey("terramap.config.minimap.enable")
	@Config.Comment("Set to false to hide the minimap")
	public static boolean minimapEnable = true;

	@Config.Name("minimap_style")
	@Config.LangKey("terramap.config.minimap.style")
	@Config.Comment("Which map style to use for the minimap. Press P when viewing the fullscreen map to get the styles' id to use here")
	public static String minimapStyle = "osm";

	@Config.Name("minimap_position_x")
	@Config.LangKey("terramap.config.minimap.pos_x")
	@Config.Comment("X position of the minimap on screen, in percents")
	@Config.RangeInt(min=0, max=100)
	@Config.SlidingOption
	public static int minimapPosX = 1;

	@Config.Name("minimap_position_y")
	@Config.LangKey("terramap.config.minimap.pos_y")
	@Config.Comment("Y position of the minimap on screen, in percents")
	@Config.RangeInt(min=0, max=100)
	@Config.SlidingOption
	public static int minimapPosY = 1;

	@Config.Name("minimap_width")
	@Config.LangKey("terramap.config.minimap.width")
	@Config.Comment("Minimap width, in percents")
	@Config.RangeInt(min=0, max=100)
	@Config.SlidingOption
	public static int minimapWidth = 20;

	@Config.Name("minimap_height")
	@Config.LangKey("terramap.config.minimap.height")
	@Config.Comment("Minimap height, in percents")
	@Config.RangeInt(min=0, max=100)
	@Config.SlidingOption
	public static int minimapHeight = 15;

	@Config.Name("minimap_zoom")
	@Config.LangKey("terramap.config.minimap.zoom")
	@Config.Comment("Minimap zoom level")
	@Config.RangeInt(min=0, max=19)
	@Config.SlidingOption
	public static int minimapZoomLevel = 18;

	@Config.Name("minimap_show_entities")
	@Config.LangKey("terramap.config.minimap.show_entities")
	@Config.Comment("Set to true to show entities on the minimap")
	public static boolean minimapShowEntities = false;

	@Config.Name("minimap_show_other_players")
	@Config.LangKey("terramap.config.minimap.show_players")
	@Config.Comment("Set to false to hide other players on the minimap")
	public static boolean minimapShowOtherPlayers = true;
	
	@Config.Name("compass_visibility")
	@Config.LangKey("terramap.config.compass.visibility") //TODO Localize
	@Config.Comment("Show the compass in the hud")
	public static boolean compassVisibility = true;
	
	@Config.Name("compass_position_x")
	@Config.LangKey("terramap.config.compass.position.x") //TODO Localize
	@Config.Comment("Compass X position, as % of the screen's width")
	@Config.RangeDouble(min=0, max=100)
	@Config.SlidingOption
	public static double compassX = 0; // TODO Better default value
	
	@Config.Name("compass_position_y")
	@Config.LangKey("terramap.config.compass.position.y") //TODO Localize
	@Config.Comment("Compass Y position, as % of the screen's height")
	@Config.RangeDouble(min=0, max=100)
	@Config.SlidingOption
	public static double compassY = 0; // TODO Better default value
	
	@Config.Name("compass_width")
	@Config.LangKey("terramap.config.compass.width") //TODO Localize
	@Config.Comment("Compass width, as % of the screen's width")
	@Config.RangeDouble(min=0, max=100)
	@Config.SlidingOption
	public static double compassWidth = 0; // TODO Better default value

	@Config.Name("tile_scaling")
	@Config.LangKey("terramap.config.tile_scaling")
	@Config.Comment("Try lowering this value if you have pixelated map because of vanilla GUI scalling. Powers of two such as 0.5, 0.25 etc should work best")
	@Config.RangeDouble(min=1.0, max=8.0)
	public static double tileScaling = TerramapMod.proxy.getGuiScaleForConfig();

	@Config.Name("auto_tile_scaling")
	@Config.LangKey("terramap.config.auto_tile_scaling")
	@Config.Comment("If this is set to true, tile scaling will be automatically adjusted to counter Minecraft GUI scaling and the tile_scaling option will be bypassed")
	public static boolean autoTileScaling = true;

	@Config.Name("max_tile_load")
	@Config.LangKey("terramap.config.max_tile_load")
	@Config.Comment("This is the maximum number of tiles to keep loaded. A lower number implies lower memory usage, however, if this is lower than the number of tiles displayed on your screen at once you will experience a huge performance drop. Change for a higher value if you experience lag when displaying a map on a large display")
	@Config.RangeInt(min=128, max=2048)
	@Config.SlidingOption
	public static int maxTileLoad = 256;
	
	@Config.Name("low_zoom_level")
	@Config.LangKey("terramap.config.low_zoom_level")
	@Config.Comment("Tiles bellow this zoom level will be loaded when the map loads for the first time and then will never be unloaded. Number of tiles to keep loaded per zoom level:\n"
			+ "\t0 -> 1\n"
			+ "\t1 -> 5\n"
			+ "\t2 -> 21\n"
			+ "\t3 -> 85\n"
			+ "Honnestly, you shouldn't be changing that option, it's mostly for testing purposes")
	@Config.RangeInt(min=0, max=3)
	@Config.SlidingOption
	public static int lowZoomLevel = 1;
	
	@Config.Name("unlock_zoom")
	@Config.LangKey("terramap.config.unlock_zoom")
	@Config.Comment("Set this to true to allow zoom level up to 25, even on maps where the tile server doesn't support it")
	public static boolean unlockZoom = false;

	@Config.Name("double_click_delay")
	@Config.LangKey("terramap.config.double_click_delay")
	@Config.Comment("Double click delay to use in guis, in milliscondes")
	@Config.RangeInt(min=10, max=1000)
	@Config.SlidingOption
	public static int doubleClickDelay = 500;

	@Config.Name("tpll_command")
	@Config.LangKey("terramap.config.tpllcmd")
	@Config.Comment("The base tpll command to use")
	public static String tpllcmd = "/tpll {latitude} {longitude}";

	@Config.Name("force_client_tp_cmd")
	@Config.LangKey("terramap.config.forcetpllcmd")
	@Config.Comment("If set to true, the server will force clients to use its own tp command, if false, client will use their own configuration")
	public static boolean forceClientTpCmd = false;

	@Config.Name("synchronize_players")
	@Config.LangKey("terramap.config.sync_players")
	@Config.Comment("Wether or not to synchronize players from server to client so everyone appears on the map, no matter the distance")
	public static boolean synchronizePlayers = true;

	@Config.Name("sync_spectators")
	@Config.LangKey("terramap.config.sync_spec")
	@Config.Comment("Synchronize spectator players or not (players still need to be synchronized)")
	public static boolean synchronizeSpectators = true;
	
	@Config.Name("sync_interval")
	@Config.LangKey("terramap.config.sync_interval")
	@Config.Comment("Synchronization time interval, int ticks, higher means better server perfomance but a map which lags behind a bit more")
	@Config.RangeInt(min=1, max=100)
	@Config.SlidingOption
	public static int syncInterval = 10;

	@Config.Name("sync_hearthbeet_timeout")
	@Config.LangKey("terramap.config.sync_heartbeet")
	@Config.RangeInt(min=20000)
	@Config.Comment("If a client keeps its map open more than this time, the server asks the client to confirm that it still needs map updates. This is in milliseconds")
	public static int syncHeartbeatTimeout = 120000;

	@Config.Name("players_opt_in_to_display_default")
	@Config.LangKey("terramap.config.players_opt_in_to_display_default")
	@Config.Comment("If player sync is enabled, sould players be displayed by default (true) or should they opt-in (false)")
	public static boolean playersDisplayDefault = true;

	@Config.Name("sync_custom_maps")
	@Config.LangKey("terramap.config.sync_custom_maps")
	@Config.Comment("Set to false if you do not want to send custom maps to clients. This is only for testing, as if you don't want to send map styles to client, the first thing to do is to not configure any.")
	public static boolean sendCusomMapsToClient = true;
	
	@Config.Name("join_without_mod_message")
	@Config.LangKey("terramap.config.join_without_mod_message")
	@Config.Comment("A message to display to players who do not have Terramap installed, as a json text (https://minecraft.tools/en/json_text.php). Leave blank for no message.")
	public static String joinWithoutModMessage = "";
	
	@Config.Name("join_with_outdated_mod_message")
	@Config.LangKey("terramap.config.join_with_outdated_mod_message")
	@Config.Comment("A message to display to players with an incompatible version of Terramap installed, as a json text (https://minecraft.tools/en/json_text.php). Leave blank for no message.")
	public static String joinWithOutdatedModMessage = "[\"\",{\"text\":\"You are using an old and outdated version of Terramap, it will not work on this server. You can update \",\"color\":\"red\"},{\"text\":\"here\",\"underlined\":true,\"color\":\"red\",\"clickEvent\":{\"action\":\"open_url\",\"value\":\"https://www.curseforge.com/minecraft/mc-mods/terramap\"},\"hoverEvent\":{\"action\":\"show_text\",\"value\":\"Click to open link.\"}},{\"text\":\".\",\"color\":\"red\"}]";

	public static double getEffectiveTileScaling() {
		if(autoTileScaling) {
			return SmyLibGui.getMinecraftGuiScale();
		} else {
			return tileScaling;
		}
	}
	
	public static void update() {
		boolean sync = false;
		if(tileScaling < 1) {
			tileScaling = 1.0d / tileScaling;
			TerramapMod.logger.debug("Updated tile scaling from an invalid value that was probably left over by an old version of the mod");
			sync = true;
		}
		if(sync) {
			sync();
			TerramapMod.logger.info("Synchronized configuration after incorrect values were updated");
		}
	}

	public static void override() {

	}

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
