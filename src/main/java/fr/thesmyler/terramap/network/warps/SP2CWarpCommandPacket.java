package fr.thesmyler.terramap.network.warps;

import fr.thesmyler.terramap.network.TerramapNetworkManager;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SP2CWarpCommandPacket implements IMessage {

    public String cmd = "";

    public SP2CWarpCommandPacket(String cmd) {
        this.cmd = cmd;
    }

    public SP2CWarpCommandPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.cmd = TerramapNetworkManager.decodeStringFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        TerramapNetworkManager.encodeStringToByteBuf(this.cmd, buf);
    }

    public static class SP2CWarpCommandPacketServerHandler implements IMessageHandler<SP2CWarpCommandPacket, IMessage> {

        public SP2CWarpCommandPacketServerHandler() {}

        @Override
        public IMessage onMessage(SP2CWarpCommandPacket message, MessageContext ctx) {
            return null;
        }

    }

    public static class SP2CWarpCommandPacketProxyHandler implements IMessageHandler<SP2CWarpCommandPacket, IMessage> {

        public SP2CWarpCommandPacketProxyHandler() {}

        @Override
        public IMessage onMessage(SP2CWarpCommandPacket message, MessageContext ctx) {
            return null;
        }

    }

}
