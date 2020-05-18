package fr.smyler.terramap.network;

import fr.smyler.terramap.TerramapMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapPacketHandler {

	//The channel instance
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID);

	//Packet discriminator counter, should be increased for each packet type.
	private static int discriminator = 0;
	
	/**
	 * Registers the handlers for the given side.
	 * 
	 * @param side
	 */
	public static void registerHandlers(Side side){
		if(side.isClient()){
			registerClientHandlers();
		}
			registerServerHandlers();
	}
	
	
	/**
	 * Registers the server handlers
	 */
	private static void registerServerHandlers(){
		TerramapMod.logger.debug("Registering server network handlers");
	}
	
	/**
	 * Registers the client handlers
	 */
	private static void registerClientHandlers(){
		TerramapMod.logger.debug("Registering client network handlers");
		INSTANCE.registerMessage(ProjectionSyncPacket.ProjectionSyncPacketHandler.class, ProjectionSyncPacket.class, discriminator++, Side.CLIENT);
	}
}
