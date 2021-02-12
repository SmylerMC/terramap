package fr.thesmyler.terramap.network;

import java.util.UUID;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.network.playersync.PlayerSyncStatus;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class P2CSledgehammerHelloPacket implements IMessage {
	
	public String sledgehammerVersion;
	public PlayerSyncStatus syncPlayers = PlayerSyncStatus.DISABLED;
	public PlayerSyncStatus syncSpectators = PlayerSyncStatus.DISABLED;
	public boolean globalMap = true; // Can we open the map on non-terra worlds?
	public boolean globalSettings = false; // Should settings and preferences be saved for the whole network (true) or per server (false)
	public boolean hasWarpSupport = false;
	public UUID proxyUUID = new UUID(0, 0);
	
	//TODO Warp support

	@Override
	public void fromBytes(ByteBuf buf) {
		this.sledgehammerVersion = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		this.syncPlayers = PlayerSyncStatus.getFromNetworkCode(buf.readByte());
		this.syncSpectators = PlayerSyncStatus.getFromNetworkCode(buf.readByte());
		this.globalMap = buf.readBoolean();
		this.globalSettings = buf.readBoolean();
		this.hasWarpSupport = buf.readBoolean();
		long leastUUID = buf.readLong();
		long mostUUID = buf.readLong();
		this.proxyUUID = new UUID(mostUUID, leastUUID);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// This should never be sent by the server, only by the proxy
		TerramapNetworkManager.encodeStringToByteBuf("Terramap!", buf);
		buf.writeByte(this.syncPlayers.VALUE);
		buf.writeByte(this.syncSpectators.VALUE);
		buf.writeBoolean(this.globalMap);
		buf.writeBoolean(this.globalSettings);
		buf.writeBoolean(this.hasWarpSupport);
	}
	
	public static class P2CSledgehammerHelloPacketHandler implements IMessageHandler<P2CSledgehammerHelloPacket, IMessage> {

		//Required by forge
		public P2CSledgehammerHelloPacketHandler(){}
		
		@Override
		public IMessage onMessage(P2CSledgehammerHelloPacket pkt, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TerramapMod.logger.info("Got Sledgehammer hello, remote version is " + pkt.sledgehammerVersion);
				TerramapClientContext.getContext().setSledgehammerVersion(pkt.sledgehammerVersion);
				TerramapClientContext.getContext().setPlayersSynchronizedByProxy(pkt.syncPlayers);
				TerramapClientContext.getContext().setSpectatorsSynchronizedByProxy(pkt.syncSpectators);
				TerramapClientContext.getContext().setProxyForceMinimap(pkt.globalMap);
				TerramapClientContext.getContext().setProxyForceGlobalSettings(pkt.globalSettings);
				TerramapClientContext.getContext().setProxyWarpSupport(pkt.hasWarpSupport);
				TerramapClientContext.getContext().setProxyUUID(pkt.proxyUUID);
			});
			return null;
		}
		

	}

}
