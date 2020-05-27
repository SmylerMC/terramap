package fr.smyler.terramap.network;

import fr.smyler.terramap.TerramapEventHandler;
import fr.smyler.terramap.TerramapMod;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SRegisterForUpdatesPacket implements IMessage {

	public boolean update = false;
	
	public C2SRegisterForUpdatesPacket() {}
	
	public C2SRegisterForUpdatesPacket(boolean update) {
		this.update = update;
	}
	
	@Override
	public void fromBytes(ByteBuf buf) {
		this.update = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeBoolean(this.update);
	}
	
	public static class C2SRegisterForUpdatesPacketHandler implements IMessageHandler<C2SRegisterForUpdatesPacket, IMessage>{

		@Override
		public C2SRegisterForUpdatesPacket onMessage(C2SRegisterForUpdatesPacket message, MessageContext ctx) {
			EntityPlayerMP player = ctx.getServerHandler().player;
			WorldServer world = player.getServerWorld();
			if(message.update) world.addScheduledTask(()->{registerPlayer(player);});
			else world.addScheduledTask(()->{unregisterPlayer(player);});
			return null;
		}
		
		public void registerPlayer(EntityPlayerMP player) {
			TerramapMod.logger.debug("Registering player for map updates: " + player.getDisplayNameString()); //TODO Set to debug
			TerramapEventHandler.playersToUpdate.put(player.getPersistentID(), player);
		}
		
		public void unregisterPlayer(EntityPlayerMP player) {
			TerramapMod.logger.debug("Unregistering player for map updates: " + player.getDisplayNameString()); //TODO Set to debug
			TerramapEventHandler.playersToUpdate.remove(player.getPersistentID());
		}
		
	}

}
