package fr.thesmyler.terramap.eventhandlers;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.screen.TestScreen;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.TiledMaps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

/**
 * Event handler for the physical client
 *
 */
@SideOnly(Side.CLIENT)
public class ClientTerramapEventHandler {
	
	private boolean testScreenWasShown = false;
	private boolean configWasFixed = false;

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
    	KeyBindings.checkBindings();
    }
    
	@SubscribeEvent
	public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
		TerramapServer.resetServer();
	}

	@SubscribeEvent
	public void onClientConnected(ClientConnectedToServerEvent event) {
	}
	
	@SubscribeEvent
	public void onGuiScreenInit(InitGuiEvent event) {
		if(event.getGui() instanceof GuiMainMenu && !configWasFixed) {
			/* 
			 * Unfortunately, Forge's ConfigManager does not let us modify our config when the game is still loading and 
			 * and calling ConfigManager::sync only injects the file's value into the fields instead of saving them to disk,
			 * which is why we have to do it once the game is fully loaded.
			 * 
			 * This is called on the physical server by TerramapServerProxy::onServerStarting .
			 * 
			 */
		    TerramapConfig.update(); // Update if invalid values were left by old versions
		    configWasFixed = true;
		}
		if(SmyLibGui.debug && !testScreenWasShown && !(event.getGui() instanceof Screen)) {
			Minecraft.getMinecraft().displayGuiScreen(new TestScreen(event.getGui()));
			this.testScreenWasShown = true;
		} else if(event.getGui() instanceof HudScreen) {
			//TODO Customizable minimap
			HudScreen screen = (HudScreen) event.getGui();
			screen.removeAllWidgets();
			screen.cancellAllScheduled();
			MapWidget map = new MapWidget(10, TiledMaps.OSM, MapContext.MINIMAP, TerramapConfig.getEffectiveTileScaling());
			map.setInteractive(false);
			map.setX(5);
			map.setY(5);
			map.setWidth(300);
			map.setHeight(200);
			map.setZoom(18);
			map.setCopyrightVisibility(false);
			map.setScaleVisibility(false);
			screen.addWidget(map);
			screen.scheduleAtUpdate(() -> {
				if(TerramapServer.getServer().isInstalledOnServer()) {
					map.track(map.getMainPlayerMarker());
				}
			});
		}
	}
	
}
