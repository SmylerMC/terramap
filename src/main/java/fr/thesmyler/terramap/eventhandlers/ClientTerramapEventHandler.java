package fr.thesmyler.terramap.eventhandlers;

import java.util.HashMap;
import java.util.Map;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.screen.TestScreen;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.gui.widgets.map.MapWidget;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.AnimalMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.MobMarkerController;
import fr.thesmyler.terramap.gui.widgets.markers.controllers.OtherPlayerMarkerController;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.MapStyleRegistry;
import fr.thesmyler.terramap.maps.TiledMap;
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
		TerramapRemote.resetRemote();
	}

	@SubscribeEvent
	public void onClientConnected(ClientConnectedToServerEvent event) {
		TerramapRemote.getRemote().guessRemoteIdentifier();
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
			
			if(TerramapConfig.Minimap.enable) {
				MapWidget map = new MapWidget(10, MapStyleRegistry.getTiledMaps().values().toArray(new TiledMap[0])[0], MapContext.MINIMAP, TerramapConfig.ClientAdvanced.getEffectiveTileScaling());
				map.setInteractive(false);
				map.setX((int) (TerramapConfig.Minimap.posX * 0.01 * screen.getWidth()));
				map.setY((int) (TerramapConfig.Minimap.posX * 0.01 * screen.getWidth()));
				map.setWidth((int) (TerramapConfig.Minimap.width * 0.01 * screen.getWidth()));
				map.setHeight((int) (TerramapConfig.Minimap.height * 0.01 * screen.getWidth()));
				Map<String, Boolean> markerVisibility = new HashMap<String, Boolean>();
				markerVisibility.put(AnimalMarkerController.ID, TerramapConfig.Minimap.showEntities);
				markerVisibility.put(MobMarkerController.ID, TerramapConfig.Minimap.showEntities);
				markerVisibility.put(OtherPlayerMarkerController.ID, TerramapConfig.Minimap.showOtherPlayers);
				map.setMarkersVisibility(markerVisibility);
				Map<String, TiledMap> styles = MapStyleRegistry.getTiledMaps();
				TiledMap bg = styles.getOrDefault(TerramapConfig.Minimap.style, styles.get("osm"));
				map.setBackground(bg);
				int zoomLevel = Math.max(bg.getMinZoom(), TerramapConfig.Minimap.zoomLevel);
				zoomLevel = Math.min(bg.getMaxZoom(), TerramapConfig.Minimap.zoomLevel);
				map.setZoom(zoomLevel);
				map.setZoom(TerramapConfig.Minimap.zoomLevel);
				map.setCopyrightVisibility(false);
				map.setScaleVisibility(false);
				screen.addWidget(map);
				screen.scheduleAtUpdate(() -> {
					if(TerramapRemote.getRemote().isInstalledOnServer()) {
						map.track(map.getMainPlayerMarker());
					}
				});
			}
			
		}
	}
	
}
