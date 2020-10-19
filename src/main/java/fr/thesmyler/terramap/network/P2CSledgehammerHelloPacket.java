package fr.thesmyler.terramap.network;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapRemote;
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
	public boolean globalMap = true; // Can we open the map on non-terra worlds? //TODO Implement
	public boolean globalSettings = false; // Should settings and preferences be saved for the whole network (true) or per server (false)
	public boolean hasWarpSupport = false;
	
	//TODO Warp support

	@Override
	public void fromBytes(ByteBuf buf) {
		this.sledgehammerVersion = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		this.syncPlayers = PlayerSyncStatus.getFromNetworkCode(buf.readByte());
		this.syncSpectators = PlayerSyncStatus.getFromNetworkCode(buf.readByte());
		this.globalMap = buf.readBoolean();
		this.globalSettings = buf.readBoolean();
		this.hasWarpSupport = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// This should never be sent by the server, only by the proxy
		TerramapNetworkManager.encodeStringToByteBuf("Terramap!", buf);
		buf.writeByte(this.syncPlayers.VALUE);
		buf.writeByte(this.syncSpectators.VALUE);
		buf.writeBoolean(this.globalMap);
		buf.writeBoolean(this.globalSettings);
	}
	
	public static class P2CSledgehammerHelloPacketHandler implements IMessageHandler<P2CSledgehammerHelloPacket, IMessage> {

		//Required by forge
		public P2CSledgehammerHelloPacketHandler(){}
		
		@Override
		public IMessage onMessage(P2CSledgehammerHelloPacket pkt, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {
				TerramapMod.logger.info("Got Sledgehammer hello, remote version is " + pkt.sledgehammerVersion);
				TerramapRemote.getRemote().setSledgehammerVersion(pkt.sledgehammerVersion);
			});
			return null;
		}
		

	}

}
