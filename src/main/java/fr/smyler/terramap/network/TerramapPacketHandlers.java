package fr.smyler.terramap.network;

import fr.smyler.terramap.TerramapMod;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TerramapPacketHandlers {

	// The channel instance
	public static final SimpleNetworkWrapper INSTANCE = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID);

	// Packet discriminator counter, should be increased for each packet type.
	private static int discriminator = 0;
	
	/**
	 * Registers the handlers
	 * 
	 * @param side
	 */
	public static void registerHandlers(Side side){
		INSTANCE.registerMessage(S2CTerramapHelloPacket.S2CTerramapHelloPacketHandler.class, S2CTerramapHelloPacket.class, discriminator++, Side.CLIENT);
		INSTANCE.registerMessage(S2CPlayerSyncPacket.S2CPlayerSyncPacketHandler.class, S2CPlayerSyncPacket.class, discriminator++, Side.CLIENT);
		INSTANCE.registerMessage(C2SRegisterForUpdatesPacket.C2SRegisterForUpdatesPacketHandler.class, C2SRegisterForUpdatesPacket.class, discriminator++, Side.SERVER);
	}
	
}
