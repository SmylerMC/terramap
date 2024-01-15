package fr.thesmyler.terramap.network.warps;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SP2CCreateWarpConfirmationPacket implements IMessage {

    private long requestId;
    private WarpRequestStatus status;

    public SP2CCreateWarpConfirmationPacket(long requestId, WarpRequestStatus status) {
        this.requestId = requestId;
        this.status = status;
    }

    public SP2CCreateWarpConfirmationPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.requestId = buf.readLong();
        this.status = WarpRequestStatus.getFromNetworkCode(buf.readByte());
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeLong(this.requestId);
        buf.writeByte(this.status.getNetworkCode());
    }

    public static class SP2CCreateWarpConfirmationPacketServerHandler implements IMessageHandler<SP2CCreateWarpConfirmationPacket, IMessage> {

        @Override
        public IMessage onMessage(SP2CCreateWarpConfirmationPacket message, MessageContext ctx) {
            //TODO SP2CCreateWarpConfirmationPacketHandler
            return null;
        }

    }

    public static class SP2CCreateWarpConfirmationPacketProxyHandler implements IMessageHandler<SP2CCreateWarpConfirmationPacket, IMessage> {

        @Override
        public IMessage onMessage(SP2CCreateWarpConfirmationPacket message, MessageContext ctx) {
            //TODO SP2CCreateWarpConfirmationPacketProxyHandler
            return null;
        }

    }

}
