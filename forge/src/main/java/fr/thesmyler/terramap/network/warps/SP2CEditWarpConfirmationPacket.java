package fr.thesmyler.terramap.network.warps;

import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SP2CEditWarpConfirmationPacket implements IMessage {

    private long requestId;
    private WarpRequestStatus status;

    public SP2CEditWarpConfirmationPacket(long requestId, WarpRequestStatus status) {
        this.requestId = requestId;
        this.status = status;
    }

    public SP2CEditWarpConfirmationPacket() {}

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

    public static class SP2CEditWarpConfirmationPacketServerHandler implements IMessageHandler<SP2CEditWarpConfirmationPacket, IMessage> {

        @Override
        public IMessage onMessage(SP2CEditWarpConfirmationPacket message, MessageContext ctx) {
            //TODO SP2CEditWarpConfirmationPacketServerHandler
            return null;
        }

    }

    public static class SP2CEditWarpConfirmationPacketProxyHandler implements IMessageHandler<SP2CEditWarpConfirmationPacket, IMessage> {

        @Override
        public IMessage onMessage(SP2CEditWarpConfirmationPacket message, MessageContext ctx) {
            //TODO SP2CEditWarpConfirmationPacketProxyHandler
            return null;
        }

    }

}
