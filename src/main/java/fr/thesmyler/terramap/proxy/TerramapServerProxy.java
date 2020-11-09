package fr.thesmyler.terramap.proxy;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.IllegalFormatException;
import java.util.Map;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.command.TerrashowCommand;
import fr.thesmyler.terramap.command.TilesetReloadCommand;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.eventhandlers.ServerTerramapEventHandler;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapServerProxy extends TerramapProxy {
	
	private static final String LANG_FILE = "assets/terramap/lang/en_us.lang";
	private final Map<String, String> translationMappings = new HashMap<String, String>();

	@Override
	public Side getSide() {
		return Side.SERVER;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapMod.logger.debug("Terramap server pre-init");
		this.loadTranslationMappings();
		TerramapNetworkManager.registerHandlers(Side.SERVER);
	}

	@Override
	public void init(FMLInitializationEvent event) {
		TerramapMod.logger.debug("Terramap server init");
		MinecraftForge.EVENT_BUS.register(new ServerTerramapEventHandler());
	}

	@Override
	public double getGuiScaleForConfig() {
		// Don't care on server, this is just for the client config
		return 0;
	}

	@Override
	public void onServerStarting(FMLServerStartingEvent event) {
		
		/* 
		 * Unfortunately, Forge's ConfigManager does not let us modify our config when the game is still loading and 
		 * and calling ConfigManager::sync only injects the file's value into the fields instead of saving them to disk,
		 * which is why we have to do it once the game is fully loaded.
		 * 
		 * This is called on the physical client by TerramapClientEventHandler::onGuiScreenInit when the main title screen is shown.
		 */
	    TerramapConfig.update(); // Update if invalid values were left by old versions
	    
    	event.registerServerCommand(new TerrashowCommand());
    	event.registerServerCommand(new TilesetReloadCommand());
	}

	@Override
	public GameType getGameMode(EntityPlayer e) {
		EntityPlayerMP player = (EntityPlayerMP)e;
		return player.interactionManager.getGameType();
	}

	@Override
	public void onConfigChanged(OnConfigChangedEvent event) {
	}
	
	private void loadTranslationMappings() {
		String path = LANG_FILE;
		try {
			// https://github.com/MinecraftForge/MinecraftForge/issues/5713
			InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(path);
			try(BufferedReader txtReader = new BufferedReader(new InputStreamReader(in))) {
				String line = txtReader.readLine();
				while(line != null) {
					try {
						int delimiter = line.indexOf("=");
						if(delimiter == -1 || delimiter >= line.length() - 1) continue;
						String key = line.substring(0, delimiter);
						String value = line.substring(delimiter + 1);
						this.translationMappings.put(key, value);
					} catch(Exception e) {
						TerramapMod.logger.warn("Failed to parse translation line " + line + " : " + e.getLocalizedMessage());
					}
				}
			}
		} catch(Exception e) {
			TerramapMod.logger.fatal("Failed to read english translation file!");
			TerramapMod.logger.fatal("Path: " + path);
			TerramapMod.logger.catching(e);
		}
	}

	@Override
	public String localize(String key, Object... parameters) {
		String str = this.translationMappings.getOrDefault(key, key);
        try {
            return String.format(str, parameters);
        } catch (IllegalFormatException e) {
            return "Format error: " + str;
        }
	}

}
