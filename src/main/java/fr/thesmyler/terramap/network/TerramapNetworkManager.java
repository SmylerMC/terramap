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

	// The channel instance
	public static final SimpleNetworkWrapper CHANNEL = NetworkRegistry.INSTANCE.newSimpleChannel(TerramapMod.MODID);
	public static final Charset CHARSET = Charset.forName("utf-8");

	// Packet discriminator counter, should be increased for each packet type.
	private static int discriminator = 0;
		
	/**
	 * Registers the handlers
	 * 
	 * @param side
	 */
	public static void registerHandlers(Side side){
		registerS2C(S2CTerramapHelloPacketHandler.class, S2CTerramapHelloPacket.class);
		registerS2C(S2CPlayerSyncPacketHandler.class, S2CPlayerSyncPacket.class);
		registerS2C(S2CRegistrationExpiresPacketHandler.class, S2CRegistrationExpiresPacket.class);
		registerS2C(S2CTpCommandSyncPacketHandler.class, S2CTpCommandSyncPacket.class);
		registerC2S(C2SRegisterForUpdatesPacketHandler.class, C2SRegisterForUpdatesPacket.class);
	}
	
	private static <REQ extends IMessage, REPLY extends IMessage> void registerS2C(Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
		CHANNEL.registerMessage(handlerclass, msgclass, discriminator++, Side.CLIENT);
	}
	
	private static <REQ extends IMessage, REPLY extends IMessage> void registerC2S(Class<? extends IMessageHandler<REQ, REPLY>> handlerclass, Class<REQ> msgclass) {
		CHANNEL.registerMessage(handlerclass, msgclass, discriminator++, Side.SERVER);
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
