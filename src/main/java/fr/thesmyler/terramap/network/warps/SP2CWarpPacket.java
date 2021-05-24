package fr.thesmyler.terramap.network.warps;

import javax.annotation.Nullable;

import fr.thesmyler.terramap.warp.Warp;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SP2CWarpPacket implements IMessage {

    private long requestId;
    private WarpRequestStatus status;
    private Warp warp;

    public SP2CWarpPacket(long requestId, WarpRequestStatus status, @Nullable Warp warp) {
        this.requestId = requestId;
        this.status = status;
        this.warp = warp;
    }

    public SP2CWarpPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.requestId = buf.readLong();
        this.status = WarpRequestStatus.getFromNetworkCode(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.requestId);
        buf.writeByte(this.status.getNetworkCode());
        if(this.warp != null) {
            this.warp.encodeToByteBuf(buf);
        }
    }

    public static class SP2CWarpPacketServerHandler implements IMessageHandler<SP2CWarpPacket, IMessage> {

        @Override
        public IMessage onMessage(SP2CWarpPacket message, MessageContext ctx) {
            //TODO SP2CWarpPacketServerHandler
            return null;
        }

    }

    public static class SP2CWarpPacketProxyHandler implements IMessageHandler<SP2CWarpPacket, IMessage> {

        @Override
        public IMessage onMessage(SP2CWarpPacket message, MessageContext ctx) {
            //TODO SP2CWarpPacketProxyHandler
            return null;
        }

    }

}
