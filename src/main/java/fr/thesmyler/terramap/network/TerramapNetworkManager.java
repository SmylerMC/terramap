package fr.thesmyler.terramap.network;

import java.nio.charset.Charset;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.network.S2CTerramapHelloPacket.S2CTerramapHelloPacketHandler;
import fr.thesmyler.terramap.network.S2CTpCommandSyncPacket.S2CTpCommandSyncPacketHandler;
import fr.thesmyler.terramap.network.mapsync.C2SRegisterForUpdatesPacket;
import fr.thesmyler.terramap.network.mapsync.C2SRegisterForUpdatesPacket.C2SRegisterForUpdatesPacketHandler;
import fr.thesmyler.terramap.network.mapsync.S2CPlayerSyncPacket;
import fr.thesmyler.terramap.network.mapsync.S2CPlayerSyncPacket.S2CPlayerSyncPacketHandler;
import fr.thesmyler.terramap.network.mapsync.S2CRegistrationExpiresPacket;
import fr.thesmyler.terramap.network.mapsync.S2CRegistrationExpiresPacket.S2CRegistrationExpiresPacketHandler;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.SimpleNetworkWrapper;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TerramapNetworkManager {

	// The channel instances
	public static final SimpleNetworkWrapper CHANNEL_TERRAMAP = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID + ":terramap");
	public static final SimpleNetworkWrapper CHANNEL_MAPSYNC = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID + ":mapsync");
	public static final SimpleNetworkWrapper CHANNEL_SLEDGEHAMMER = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID + ":sledgehammer");
	
	public static final Charset CHARSET = Charset.forName("utf-8");
		
	/**
	 * Registers the handlers
	 * 
	 * @param side
	 */
	public static void registerHandlers(Side side){
		registerTerramapS2C(S2C_TERRAMAP_HELLO_DISCRIMINATOR, S2CTerramapHelloPacketHandler.class, S2CTerramapHelloPacket.class);
		registerTerramapS2C(S2C_TERRAMAP_TPCMD_DISCRIMINATOR, S2CTpCommandSyncPacketHandler.class, S2CTpCommandSyncPacket.class);

		registerMapsyncCP2S(CP2S_MAPSYNC_REGISTER_DISCRIMINATOR, C2SRegisterForUpdatesPacketHandler.class, C2SRegisterForUpdatesPacket.class);
		registerMapsyncSP2C(SP2C_MAPSYNC_PLAYERSYNC_DISCRIMINATOR, S2CPlayerSyncPacketHandler.class, S2CPlayerSyncPacket.class);
		registerMapsyncSP2C(SP2C_MAPSYNC_REGISTRATION_EXPIRES_DISCRIMINATOR, S2CRegistrationExpiresPacketHandler.class, S2CRegistrationExpiresPacket.class);
	}
	
	// terramap:terramap
	private static final int S2C_TERRAMAP_HELLO_DISCRIMINATOR = 0;
	private static final int S2C_TERRAMAP_TPCMD_DISCRIMINATOR = 1;
	
	// terramap:mapsync
	private static final int CP2S_MAPSYNC_REGISTER_DISCRIMINATOR = 0;
	private static final int SP2C_MAPSYNC_PLAYERSYNC_DISCRIMINATOR = 1;
	private static final int SP2C_MAPSYNC_REGISTRATION_EXPIRES_DISCRIMINATOR = 2;
	
	private static <REQ extends IMessage, REPLY extends IMessage> void registerTerramapS2C(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
		CHANNEL_TERRAMAP.registerMessage(handlerclass, msgclass, discriminator, Side.CLIENT);
	}
	
	private static <REQ extends IMessage, REPLY extends IMessage> void registerTerramapC2S(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
		CHANNEL_TERRAMAP.registerMessage(handlerclass, msgclass, discriminator, Side.SERVER);
	}
	
	private static <REQ extends IMessage, REPLY extends IMessage> void registerMapsyncSP2C(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
		CHANNEL_MAPSYNC.registerMessage(handlerclass, msgclass, discriminator, Side.CLIENT);
	}
	
	private static <REQ extends IMessage, REPLY extends IMessage> void registerMapsyncCP2S(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
		CHANNEL_MAPSYNC.registerMessage(handlerclass, msgclass, discriminator, Side.SERVER);
	}
	
	private static <REQ extends IMessage, REPLY extends IMessage> void registerSledgehammerP2C(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
		CHANNEL_SLEDGEHAMMER.registerMessage(handlerclass, msgclass, discriminator, Side.CLIENT);
	}
	
	private static <REQ extends IMessage, REPLY extends IMessage> void registerSledgehammerCS2P(int discriminator, Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
		CHANNEL_SLEDGEHAMMER.registerMessage(handlerclass, msgclass, discriminator, Side.SERVER);
	}
	
	public static void encodeStringToByteBuf(String str, ByteBuf buf) {
		byte[] strBytes = str.getBytes(CHARSET);
		buf.writeInt(strBytes.length);
		buf.writeBytes(strBytes);
	}
	
	public static String decodeStringFromByteBuf(ByteBuf buf) {
		return buf.readCharSequence(buf.readInt(), CHARSET).toString();
	}
	
}
