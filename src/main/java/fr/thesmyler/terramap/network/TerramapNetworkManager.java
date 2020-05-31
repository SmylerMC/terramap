package fr.thesmyler.terramap.network;

import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfiguration;
import fr.thesmyler.terramap.network.S2CTerramapHelloPacket.S2CTerramapHelloPacketHandler;
import fr.thesmyler.terramap.network.S2CTpCommandSyncPacket.S2CTpCommandSyncPacketHandler;
import fr.thesmyler.terramap.network.mapsync.C2SRegisterForUpdatesPacket;
import fr.thesmyler.terramap.network.mapsync.C2SRegisterForUpdatesPacket.C2SRegisterForUpdatesPacketHandler;
import fr.thesmyler.terramap.network.mapsync.S2CPlayerSyncPacket;
import fr.thesmyler.terramap.network.mapsync.S2CPlayerSyncPacket.S2CPlayerSyncPacketHandler;
import fr.thesmyler.terramap.network.mapsync.S2CRegistrationExpiresPacket;
import fr.thesmyler.terramap.network.mapsync.S2CRegistrationExpiresPacket.S2CRegistrationExpiresPacketHandler;
import fr.thesmyler.terramap.network.mapsync.TerramapLocalPlayer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
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
	
	public static Map<UUID, RegisteredForUpdatePlayer> playersToUpdate = new HashMap<UUID, RegisteredForUpdatePlayer>();
	public static Map<UUID, Boolean> playersWithDisplayPreferences = new HashMap<UUID, Boolean>();
	
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
	
	public static void syncPlayers(World world) {
		long ctime = System.currentTimeMillis();
		List<TerramapLocalPlayer> players = new ArrayList<TerramapLocalPlayer>();
		for(EntityPlayer player: world.playerEntities) {
			if(playersWithDisplayPreferences.getOrDefault(player.getUniqueID(), TerramapConfiguration.playersOptInToDisplayDefault)) continue;
			TerramapLocalPlayer terraPlayer = new TerramapLocalPlayer(player);
			if(terraPlayer.isSpectator() && !TerramapConfiguration.syncSpectators) continue;
			players.add(terraPlayer);
		}
		IMessage pkt = new S2CPlayerSyncPacket(players.toArray(new TerramapLocalPlayer[players.size()]));
		for(RegisteredForUpdatePlayer player: TerramapNetworkManager.playersToUpdate.values()) {
			TerramapNetworkManager.CHANNEL.sendTo(pkt, player.player);
		}
		for(RegisteredForUpdatePlayer player: TerramapNetworkManager.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfiguration.syncHeartbeatTimeout - 10000 && !player.noticeSent) {
				TerramapMod.logger.debug("Sending registration expires notice to " + player.player.getName());
				CHANNEL.sendTo(new S2CRegistrationExpiresPacket(), player.player);
				player.noticeSent = true;
			}
		}
		for(RegisteredForUpdatePlayer player: TerramapNetworkManager.playersToUpdate.values()) {
			if(ctime - player.lastRegisterTime > TerramapConfiguration.syncHeartbeatTimeout) {
				TerramapMod.logger.debug("Unregistering " + player.player.getName() + "from map update as it did not renew its registration");
				TerramapNetworkManager.playersToUpdate.remove(player.player.getPersistentID());
				CHANNEL.sendTo(new S2CRegistrationExpiresPacket(), player.player);
			}
		}
	}
	
	public static void encodeStringToByteBuf(String str, ByteBuf buf) {
		byte[] strBytes = str.getBytes(CHARSET);
		buf.writeInt(strBytes.length);
		buf.writeBytes(strBytes);
	}
	
	public static String decodeStringFromByteBuf(ByteBuf buf) {
		return buf.readCharSequence(buf.readInt(), CHARSET).toString();
	}
	
	public static class RegisteredForUpdatePlayer {
		
		public EntityPlayerMP player;
		public long lastRegisterTime;
		boolean noticeSent = false;
		
		public RegisteredForUpdatePlayer(EntityPlayerMP player, long time) {
			this.player = player;
			this.lastRegisterTime = time;
		}
		
	}
	
}
