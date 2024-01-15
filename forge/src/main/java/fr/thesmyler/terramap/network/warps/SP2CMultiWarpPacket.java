package fr.thesmyler.terramap.network.warps;

import fr.thesmyler.terramap.warp.Warp;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SP2CMultiWarpPacket implements IMessage {

    private long requestId;
    private WarpRequestStatus status;
    private Warp[] warps;

    public SP2CMultiWarpPacket(long requestId, WarpRequestStatus status, Warp... warps) {
        this.requestId = requestId;
        this.status = status;
        this.warps = warps;
    }

    public SP2CMultiWarpPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.requestId = buf.readLong();
        this.status = WarpRequestStatus.getFromNetworkCode(buf.readByte());
        if(status.isSuccess()) {
            this.warps = new Warp[buf.readInt()];
            for(int i=0; i < this.warps.length; i++) {
                this.warps[i] = Warp.readWarpFromByteBuf(buf);
            }
        } else {
            this.warps = new Warp[0];
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.requestId);
        buf.writeByte(this.status.getNetworkCode());
        buf.writeInt(this.warps.length);
        for(Warp warp: this.warps) {
            warp.encodeToByteBuf(buf);
        }
    }

    public static class SP2CMultiWarpPacketServerHandler implements IMessageHandler<SP2CMultiWarpPacket, IMessage> {

        @Override
        public IMessage onMessage(SP2CMultiWarpPacket message, MessageContext ctx) {
            //TODO SP2CMultiWarpPacketServerHandler
            return null;
        }

    }

    public static class SP2CMultiWarpPacketProxyHandler implements IMessageHandler<SP2CMultiWarpPacket, IMessage> {

        @Override
        public IMessage onMessage(SP2CMultiWarpPacket message, MessageContext ctx) {
            //TODO SP2CMultiWarpPacketProxyHandler
            return null;
        }

    }

}
