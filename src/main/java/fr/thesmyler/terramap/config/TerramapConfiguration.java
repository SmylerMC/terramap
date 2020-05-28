package fr.thesmyler.terramap.config;

import fr.thesmyler.terramap.TerramapMod;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

/**
 * @author SmylerMC
 * 
 * IRLW's config
 *
 */
@Config(modid = TerramapMod.MODID)
public class TerramapConfiguration{

	@Config.Name("cache_directory")
	@Config.LangKey("terramap.config.cache_dir")
	@Config.Comment("Where to cache our files")
	@Config.RequiresMcRestart
	public static String cachingDir = "Terramap_cache";

	@Config.Name("tpll_command")
	@Config.LangKey("terramap.config.tpllcmd")
	@Config.Comment("The base tpll command to use")
	public static String tpllcmd = "/tpll {latitude} {longitude}"; //TODO Save per server
	
	@Config.Name("force_client_tp_cmd")
	@Config.LangKey("terramap.config.forcetpllcmd")
	@Config.Comment("If set to true, the server will force clients to use its own tp command, if false, client will use their own configuration")
	public static boolean forceClientTpCmd = false;

	@Config.Name("tile_scaling")
	@Config.LangKey("terramap.config.tile_scaling")
	@Config.Comment("Try lowering this value if you have pixelated map because of vanilla GUI scalling. Powers of two such as 0.5, 0.25 etc should work best")
	@Config.RangeDouble(min=0.125, max=4)
	public static double tileScaling = TerramapMod.proxy.getDefaultGuiSize();

	@Config.Name("max_tile_load")
	@Config.LangKey("terramap.config.max_tile_load")
	@Config.Comment("This is the maximum number of tiles to keep loaded. A lower number implies lower memory usage, however, if this is lower than the number of tiles displayed on your screen at once you will experience a huge performance drop. Change for a higher value if you experience lag when displaying a map on a large display")
	@Config.RangeInt(min=16, max=256)
	@Config.SlidingOption
	public static int maxTileLoad = 128;

	@Config.Name("show_entities")
	@Config.LangKey("terramap.config.show_entities")
	@Config.Comment("Set to true if you want the entities to be displayed on the map")
	public static boolean showEntities = false;

	@Config.Name("double_click_delay")
	@Config.LangKey("terramap.config.double_click_delay")
	@Config.Comment("Double click delay to use in guis, in milliscondes")
	@Config.RangeInt(min=10, max=1000)
	@Config.SlidingOption
	public static int doubleClickDelay = 500;

	@Config.Name("ignore_projection_warning")
	@Config.LangKey("terramap.config.ignore_projection_warning")
	@Config.Comment("Do not show a warning when the projection is not available")
	public static boolean ignoreProjectionWarning = false;
	
	@Config.Name("synchronize_players")
	@Config.LangKey("terramap.config.sync_players")
	@Config.Comment("Wether or not to synchronize players from server to client so everyone appears on the map, no matter the distance")
	public static boolean synchronizePlayers = false;
	
	@Config.Name("sync_interval")
	@Config.LangKey("terramap.config.sync_interval")
	@Config.Comment("Synchronization time interval, int ticks, higher means better server perfomance but a map which lags behind a bit more")
	@Config.RangeInt(min=1, max=100)
	@Config.SlidingOption
	public static int syncInterval = 10;
	
	@Config.Name("sync_spectators")
	@Config.LangKey("terramap.config.sync_spec")
	@Config.Comment("Synchronize spectator players or not (players still need to be synchronized)")
	public static boolean syncSpectators = true;
	
	@Config.Name("sync_hearthbeet_timeout")
	@Config.LangKey("terramap.config.sync_heartbeet")
	@Config.RangeInt(min=20000)
	@Config.Comment("If a client keeps its map open more than this time, the server asks the client to confirm that it still needs map updates. This is in milliseconds")
	public static int syncHeartbeatTimeout = 120000;

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
				TerramapConfiguration.sync();
			}
		}
		
	}

}
