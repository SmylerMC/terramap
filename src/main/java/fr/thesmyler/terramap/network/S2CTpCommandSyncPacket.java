package fr.thesmyler.terramap.network;

import java.nio.charset.Charset;

import fr.thesmyler.terramap.TerramapServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CTpCommandSyncPacket implements IMessage {

	public String cmd = "";
	
	public S2CTpCommandSyncPacket(String cmd) {
		this.cmd = cmd;
	}
	
	public S2CTpCommandSyncPacket() {}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		int cmdlen = buf.readInt();
		this.cmd = buf.readCharSequence(cmdlen, Charset.forName("utf-8")).toString();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.cmd.length());
		buf.writeCharSequence(this.cmd, Charset.forName("utf-8"));
	}
	
	public static class S2CTpCommandSyncPacketHandler implements IMessageHandler<S2CTpCommandSyncPacket, IMessage> {

		public S2CTpCommandSyncPacketHandler() {}
		
		@Override
		public IMessage onMessage(S2CTpCommandSyncPacket message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {TerramapServer.getServer().setTpCommand(message.cmd);});
			return null;
		}
		
	}

}
