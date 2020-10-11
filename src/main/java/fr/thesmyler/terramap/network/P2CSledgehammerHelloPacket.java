package fr.thesmyler.terramap.network;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapServer;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class P2CSledgehammerHelloPacket implements IMessage {
	
	public String sledgehammerVersion;

	@Override
	public void fromBytes(ByteBuf buf) {
		this.sledgehammerVersion = TerramapNetworkManager.decodeStringFromByteBuf(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		// This should never be sent by the server, only by the proxy
		TerramapNetworkManager.encodeStringToByteBuf("Terramap!", buf);
	}
	
	public static class P2CSledgehammerHelloPacketHandler implements IMessageHandler<P2CSledgehammerHelloPacket, IMessage> {

		//Required by forge
		public P2CSledgehammerHelloPacketHandler(){}
		
		@Override
		public IMessage onMessage(P2CSledgehammerHelloPacket pkt, MessageContext ctx) {
			TerramapMod.logger.info("Gor Sledgehammer hello, remote version is " + pkt.sledgehammerVersion);
			TerramapServer.getServer().setSledgehammerVersion(pkt.sledgehammerVersion);
			return null;
		}
		

	}

}
