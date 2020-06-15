package fr.thesmyler.terramap.eventhandlers;

import fr.thesmyler.terramap.TerramapServer;
import fr.thesmyler.terramap.input.KeyBindings;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;

/**
 * Event handler for the physical client
 *
 */
public class ClientTerramapEventHandler {

    @SubscribeEvent
    public void onKeyInput(InputEvent.KeyInputEvent event) {
    	KeyBindings.checkBindings();
    }
    
	//TODO This is too late
	@SubscribeEvent
	public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
		TerramapServer.resetServer();
	}

	@SubscribeEvent
	public void onClientConnected(ClientConnectedToServerEvent event) {
	}
	
}
