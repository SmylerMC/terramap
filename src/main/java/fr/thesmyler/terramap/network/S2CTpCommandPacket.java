package fr.thesmyler.terramap.network;

import fr.thesmyler.terramap.TerramapClientContext;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CTpCommandPacket implements IMessage {

    public String cmd = "";

    public S2CTpCommandPacket(String cmd) {
        this.cmd = cmd;
    }

    public S2CTpCommandPacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.cmd = NetworkUtil.decodeStringFromByteBuf(buf);
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkUtil.encodeStringToByteBuf(this.cmd, buf);
    }

    public static class S2CTpCommandPacketHandler implements IMessageHandler<S2CTpCommandPacket, IMessage> {

        public S2CTpCommandPacketHandler() {}

        @Override
        public IMessage onMessage(S2CTpCommandPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> TerramapClientContext.getContext().setTpCommand(message.cmd));
            return null;
        }

    }

}
