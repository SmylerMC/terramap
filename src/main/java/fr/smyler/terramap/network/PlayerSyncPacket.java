package fr.smyler.terramap.network;

import java.nio.charset.Charset;
import java.util.UUID;

import fr.smyler.terramap.TerramapServer;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PlayerSyncPacket implements IMessage {
	
	protected SyncedPlayer[] players;
	
	public PlayerSyncPacket() {} //Required by forge
	
	public PlayerSyncPacket(SyncedPlayer[] players) {
		this.players = players;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.players = new SyncedPlayer[buf.readInt()];
		for(int i=0; i<this.players.length; i++) {
			long leastUUID = buf.readLong();
			long mostUUID = buf.readLong();
			UUID uuid = new UUID(leastUUID, mostUUID); //TODO Make sure this is not in reverse
			double x = buf.readDouble();
			double z = buf.readDouble();
			int nameLen = buf.readInt();
			String name = buf.readCharSequence(nameLen, Charset.forName("utf-8")).toString();
			this.players[i] = new SyncedPlayer(uuid, name, x, z);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.players.length);
		for(SyncedPlayer player: this.players) {
			buf.writeLong(player.getUUID().getLeastSignificantBits());
			buf.writeLong(player.getUUID().getMostSignificantBits());
			buf.writeDouble(player.posX);
			buf.writeDouble(player.posZ);
			String playerDisplayName = player.getDisplayName();
			buf.writeInt(playerDisplayName.length());
			buf.writeCharSequence(playerDisplayName, Charset.forName("utf-8"));
		}
	}
	
	public static class PlayerSyncPacketHandler implements IMessageHandler<PlayerSyncPacket, IMessage> {

		//Required by forge ?
		public PlayerSyncPacketHandler(){}
		
		@Override
		public IMessage onMessage(PlayerSyncPacket message, MessageContext ctx) {
			if(TerramapServer.getServer() != null) {
				TerramapServer.getServer().syncPlayers(message.players);
			}
			return null;
		}
		

	}

}
