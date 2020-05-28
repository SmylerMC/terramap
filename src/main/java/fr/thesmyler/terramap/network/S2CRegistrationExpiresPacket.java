package fr.thesmyler.terramap.network;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapServer;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CRegistrationExpiresPacket implements IMessage {

	public S2CRegistrationExpiresPacket() {}
	
	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	public static class S2CRegistrationExpiresPacketHandler implements IMessageHandler<S2CRegistrationExpiresPacket, C2SRegisterForUpdatesPacket> {

		@Override
		public C2SRegisterForUpdatesPacket onMessage(S2CRegistrationExpiresPacket message, MessageContext ctx) {
			if(TerramapServer.getServer().needsUpdate()) {
				TerramapMod.logger.debug("Renewing registration for map update to server");
				return new C2SRegisterForUpdatesPacket(true);
			}
			return null;
		}
		
	}
	
}
