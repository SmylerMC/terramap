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
@Config(modid=TerramapMod.MODID, name="General")
@Config.LangKey("General category") //TODO Localize
public class TerramapConfig {
	
	@Config(modid=TerramapMod.MODID, category="Minimap", name="Minimap")
	@Config.LangKey("Minimap configuration") //TODO Localize
	public static class Minimap {
		
		@Config.Name("enable")
		@Config.LangKey("terramap.config.minimap.enable") //TODO Localize
		@Config.Comment("") //TODO Config comment
		public static boolean enable = true;
		
		@Config.Name("style")
		@Config.LangKey("terramap.config.minimap.style") //TODO Localize
		@Config.Comment("") //TODO Config comment
		public static String style = "osm";
		
		@Config.Name("position_x")
		@Config.LangKey("terramap.config.minimap.pos_x") //TODO Localize
		@Config.Comment("") //TODO Config comment
		@Config.RangeInt(min=0, max=100)
		@Config.SlidingOption
		public static int posX = 1;
		
		@Config.Name("position_y")
		@Config.LangKey("terramap.config.minimap.pos_y") //TODO Localize
		@Config.Comment("") //TODO Config comment
		@Config.RangeInt(min=0, max=100)
		@Config.SlidingOption
		public static int posY = 1;
		
		@Config.Name("width")
		@Config.LangKey("terramap.config.minimap.width") //TODO Localize
		@Config.Comment("") //TODO Config comment
		@Config.RangeInt(min=0, max=100)
		@Config.SlidingOption
		public static int width = 15;
		
		@Config.Name("height")
		@Config.LangKey("terramap.config.minimap.height") //TODO Localize
		@Config.Comment("") //TODO Config comment
		@Config.RangeInt(min=0, max=100)
		@Config.SlidingOption
		public static int height = 10;
		
		@Config.Name("zoom")
		@Config.LangKey("terramap.config.minimap.zoom") //TODO Localize
		@Config.Comment("") //TODO Config comment
		@Config.RangeInt(min=0, max=19)
		@Config.SlidingOption
		public static int zoomLevel = 18;
		
		@Config.Name("show_entities")
		@Config.LangKey("terramap.config.minimap.show_entities") //TODO Localize
		@Config.Comment("") //TODO Config comment
		public static boolean showEntities = false;
		
		@Config.Name("show_other_players")
		@Config.LangKey("terramap.config.minimap.show_players") //TODO Localize
		@Config.Comment("") //TODO Config comment
		public static boolean showOtherPlayers = true;
		
		
	}

	@Config(modid=TerramapMod.MODID, category="Client Advanced", name="Client Advanced")
	@Config.LangKey("Advanced client configuration") //TODO Localize
	public static class ClientAdvanced {
		
		@Config.Name("cache_directory")
		@Config.LangKey("terramap.config.cache_dir")
		@Config.Comment("Where to cache our files")
		@Config.RequiresMcRestart
		public static String cachingDir = "Terramap_cache";
		
		@Config.Name("tile_scaling")
		@Config.LangKey("terramap.config.tile_scaling")
		@Config.Comment("Try lowering this value if you have pixelated map because of vanilla GUI scalling. Powers of two such as 0.5, 0.25 etc should work best")
		@Config.RangeDouble(min=1.0, max=8.0)
		public static double tileScaling = TerramapMod.proxy.getGuiScaleForConfig();
		
		@Config.Name("auto_tile_scaling")
		//@Config.LangKey("") //TODO Localize
		@Config.Comment("If this is set to true, tile scaling will be automatically adjusted to counter Minecraft GUI scaling.")
		public static boolean autoTileScaling = true;
		
		@Config.Name("max_tile_load")
		@Config.LangKey("terramap.config.max_tile_load")
		@Config.Comment("This is the maximum number of tiles to keep loaded. A lower number implies lower memory usage, however, if this is lower than the number of tiles displayed on your screen at once you will experience a huge performance drop. Change for a higher value if you experience lag when displaying a map on a large display")
		@Config.RangeInt(min=16, max=256)
		@Config.SlidingOption
		public static int maxTileLoad = 128;
		
		@Config.Name("double_click_delay")
		@Config.LangKey("terramap.config.double_click_delay")
		@Config.Comment("Double click delay to use in guis, in milliscondes")
		@Config.RangeInt(min=10, max=1000)
		@Config.SlidingOption
		public static int doubleClickDelay = 500; //TODO use
		
		public static double getEffectiveTileScaling() {
			if(autoTileScaling) {
				return SmyLibGui.getMinecraftGuiScale();
			} else {
				return tileScaling;
			}
		}
		
	}
	
	@Config.Name("tpll_command")
	@Config.LangKey("terramap.config.tpllcmd")
	@Config.Comment("The base tpll command to use")
	public static String tpllcmd = "/tpll {latitude} {longitude}";
	
	@Config(modid=TerramapMod.MODID, category="Server", name="Server")
	@Config.LangKey("Server configuration") //TODO Localize
	public static class ServerConfig {
		
		@Config.Name("force_client_tp_cmd")
		@Config.LangKey("terramap.config.forcetpllcmd")
		@Config.Comment("If set to true, the server will force clients to use its own tp command, if false, client will use their own configuration")
		public static boolean forceClientTpCmd = false;
		
		@Config.Name("synchronize_players")
		@Config.LangKey("terramap.config.sync_players")
		@Config.Comment("Wether or not to synchronize players from server to client so everyone appears on the map, no matter the distance")
		public static boolean synchronizePlayers = true;
		
		@Config.Name("sync_interval")
		@Config.LangKey("terramap.config.sync_interval")
		@Config.Comment("Synchronization time interval, int ticks, higher means better server perfomance but a map which lags behind a bit more")
		@Config.RangeInt(min=1, max=100)
		@Config.SlidingOption
		public static int syncInterval = 10;
		
		@Config.Name("sync_spectators")
		@Config.LangKey("terramap.config.sync_spec")
		@Config.Comment("Synchronize spectator players or not (players still need to be synchronized)")
		public static boolean synchronizeSpectators = true;
		
		@Config.Name("sync_hearthbeet_timeout")
		@Config.LangKey("terramap.config.sync_heartbeet")
		@Config.RangeInt(min=20000)
		@Config.Comment("If a client keeps its map open more than this time, the server asks the client to confirm that it still needs map updates. This is in milliseconds")
		public static int syncHeartbeatTimeout = 120000;
		
		@Config.Name("players_opt_in_to_display_default")
		@Config.LangKey("terramap.config.players_opt_in_to_display_default")
		@Config.Comment("If player sync is enabled, sould players be displayed by default (true) or should they opt-in (false)")
		public static boolean playersDisplayDefault = true;
		
	}
	
	public static void update() {
		boolean sync = false;
		if(ClientAdvanced.tileScaling < 1) {
			ClientAdvanced.tileScaling = 1.0d / ClientAdvanced.tileScaling;
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
		}
		
	}

}
