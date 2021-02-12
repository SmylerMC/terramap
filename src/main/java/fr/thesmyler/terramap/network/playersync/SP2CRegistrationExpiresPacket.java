package fr.thesmyler.terramap.network.playersync;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapClientContext;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SP2CRegistrationExpiresPacket implements IMessage {

	public SP2CRegistrationExpiresPacket() {}
	
	@Override
	public void fromBytes(ByteBuf buf) {
	}

	@Override
	public void toBytes(ByteBuf buf) {
	}

	public static class S2CRegistrationExpiresPacketHandler implements IMessageHandler<SP2CRegistrationExpiresPacket, C2SPRegisterForUpdatesPacket> {

		@Override
		public C2SPRegisterForUpdatesPacket onMessage(SP2CRegistrationExpiresPacket message, MessageContext ctx) {
			if(TerramapClientContext.getContext().needsUpdate()) {
				TerramapMod.logger.debug("Renewing registration for map update to server");
				return new C2SPRegisterForUpdatesPacket(true);
			}
			return null;
		}
		
	}
	
}
