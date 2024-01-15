package fr.thesmyler.terramap.network.warps;

import fr.thesmyler.terramap.warp.Warp;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SPCreateWarpPacket implements IMessage {

    private long requestId;
    private Warp warp;

    public C2SPCreateWarpPacket(long requestId, Warp warp) {
        this.requestId = requestId;
        this.warp = warp;
    }
    public C2SPCreateWarpPacket() {}

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

    public static class C2SPCreateWarpPacketHandler implements IMessageHandler<C2SPCreateWarpPacket, IMessage> {

        @Override
        public SP2CCreateWarpConfirmationPacket onMessage(C2SPCreateWarpPacket pkt, MessageContext ctx) {
            return new SP2CCreateWarpConfirmationPacket(pkt.requestId, WarpRequestStatus.NOT_IMPLEMENTED);
        }

    }

}
