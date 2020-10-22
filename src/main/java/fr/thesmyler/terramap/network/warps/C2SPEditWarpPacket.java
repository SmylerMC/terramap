package fr.thesmyler.terramap.network.warps;

import fr.thesmyler.terramap.warp.Warp;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SPEditWarpPacket implements IMessage {
	
	private long requestId;
	private Warp warp;
	
	public C2SPEditWarpPacket(long requestId, Warp warp) {
		this.requestId = requestId;
		this.warp = warp;
	}
	public C2SPEditWarpPacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.requestId = buf.readLong();
		this.warp = Warp.readWarpFromByteBuf(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.requestId);
		this.warp.encodeToByteBuf(buf);
	}
	
	public static class C2SPEditWarpPacketHandler implements IMessageHandler<C2SPEditWarpPacket, IMessage> {
		
		@Override
		public SP2CEditWarpConfirmationPacket onMessage(C2SPEditWarpPacket pkt, MessageContext ctx) {
			return new SP2CEditWarpConfirmationPacket(pkt.requestId, WarpRequestStatus.NOT_IMPLEMENTED);
		}
		
	}

}
