package fr.thesmyler.terramap.network;

import fr.thesmyler.terramap.TerramapRemote;
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
		this.cmd = TerramapNetworkManager.decodeStringFromByteBuf(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		TerramapNetworkManager.encodeStringToByteBuf(this.cmd, buf);
	}
	
	public static class S2CTpCommandSyncPacketHandler implements IMessageHandler<S2CTpCommandSyncPacket, IMessage> {

		public S2CTpCommandSyncPacketHandler() {}
		
		@Override
		public IMessage onMessage(S2CTpCommandSyncPacket message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(() -> {TerramapRemote.getRemote().setTpCommand(message.cmd);});
			return null;
		}
		
	}

}
