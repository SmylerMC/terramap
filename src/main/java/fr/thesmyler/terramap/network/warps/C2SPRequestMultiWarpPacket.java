package fr.thesmyler.terramap.network.warps;

import fr.thesmyler.terramap.network.TerramapNetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SPRequestMultiWarpPacket implements IMessage {
	
	private long requestId;
	private AbstractWarpFilter[] filters;
	private String[] keys;
	
	public C2SPRequestMultiWarpPacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.requestId = buf.readLong();
		this.filters = new AbstractWarpFilter[buf.readInt()];
		for(int i=0; i<this.filters.length; i++) {
			AbstractWarpFilter filter = AbstractWarpFilter.readFromByteBuf(buf);
			if(filter == null) {
				this.filters = null;
				return;
			}
		}
		this.keys = new String[buf.readInt()];
		for(int i=0; i<this.keys.length; i++) {
			this.keys[i] = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeLong(this.requestId);
		buf.writeInt(this.filters.length);
		for(AbstractWarpFilter filter: this.filters) {
			filter.writeToByteBuf(buf);
		}
		buf.writeInt(this.keys.length);
		for(String key: this.keys) {
			TerramapNetworkManager.encodeStringToByteBuf(key, buf);
		}
	}
	
	public static class C2SPRequestMultiWarpPacketHandler implements IMessageHandler<C2SPRequestMultiWarpPacket, IMessage> {
		
		@Override
		public SP2CMultiWarpPacket onMessage(C2SPRequestMultiWarpPacket pkt, MessageContext ctx) {
			return new SP2CMultiWarpPacket(pkt.requestId, WarpRequestStatus.NOT_IMPLEMENTED);
		}
		
	}

}
