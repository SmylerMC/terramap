package fr.smyler.terramap.config;

import fr.smyler.terramap.TerramapMod;
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
	public static String cachingDir = "Terramap_cache";
	
	@Config.Name("tpll_command")
	@Config.LangKey("terramap.config.tpllcmd")
	@Config.Comment("The base tpll command to use")
	public static String tpllcmd = "/tpll {latitude} {longitude}"; //TODO Save per server
	
	@Config.Name("tile_scaling")
	@Config.LangKey("terramap.config.tile_scaling")
	@Config.Comment("Try lowering this value if you have pixelated map because of vanilla GUI scalling. Powers of two such as 0.5, 0.25 etc should work best")
	@Config.RangeDouble(min=0.125, max=4)
	@Config.SlidingOption
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
	@Config.Comment("Show a warning on the map if the projection was not available and allows to manually set one")
	public static boolean ignoreProjectionWarning = false;
	
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
