package fr.smyler.terramap;

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
	
	@Config.Comment("Where to cache our files")
	public static String cachingDir = "Terramap_cache";
	
	@Config.Comment("The base tpll command to use")
	public static String tpllcmd = "/tpll {latitude} {longitude}";
	
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
