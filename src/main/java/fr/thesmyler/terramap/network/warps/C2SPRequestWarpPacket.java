package fr.thesmyler.terramap.network.warps;

import fr.thesmyler.terramap.network.TerramapNetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SPRequestWarpPacket implements IMessage {

    private long requestId;
    private String warpId;

    public C2SPRequestWarpPacket(long id, String warpId) {
        this.requestId = id;
        this.warpId = warpId;
    }

    public C2SPRequestWarpPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.requestId = buf.readLong();
        this.warpId = TerramapNetworkManager.decodeStringFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.requestId);
        TerramapNetworkManager.encodeStringToByteBuf(this.warpId, buf);
    }

    public static class C2SPRequestWarpPacketHandler implements IMessageHandler<C2SPRequestWarpPacket, SP2CWarpPacket> {

        @Override
        public SP2CWarpPacket onMessage(C2SPRequestWarpPacket pkt, MessageContext ctx) {
            return new SP2CWarpPacket(pkt.requestId, WarpRequestStatus.NOT_IMPLEMENTED, null);
        }

    }

}
