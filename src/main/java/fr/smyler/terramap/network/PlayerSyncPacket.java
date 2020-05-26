package fr.smyler.terramap.network;

import java.nio.charset.Charset;
import java.util.UUID;

import fr.smyler.terramap.TerramapServer;
import io.netty.buffer.ByteBuf;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class PlayerSyncPacket implements IMessage {
	
	protected TerramapLocalPlayer[] localPlayers;
	protected TerramapRemotePlayer[] remotePlayers;
	
	public PlayerSyncPacket() {} //Required by forge
	
	public PlayerSyncPacket(TerramapLocalPlayer[] players) {
		this.localPlayers = players;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.remotePlayers = new TerramapRemotePlayer[buf.readInt()];
		for(int i=0; i<this.remotePlayers.length; i++) {
			long leastUUID = buf.readLong();
			long mostUUID = buf.readLong();
			UUID uuid = new UUID(mostUUID, leastUUID);
			double x = buf.readDouble();
			double z = buf.readDouble();
			int nameLen = buf.readInt();
			String name = buf.readCharSequence(nameLen, Charset.forName("utf-8")).toString();
			this.remotePlayers[i] = new TerramapRemotePlayer(uuid, name, x, z);
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(this.localPlayers.length);
		for(TerramapPlayer player: this.localPlayers) {
			buf.writeLong(player.getUUID().getLeastSignificantBits());
			buf.writeLong(player.getUUID().getMostSignificantBits());
			buf.writeDouble(player.getPosX());
			buf.writeDouble(player.getPosZ());
			String playerDisplayName = player.getDisplayName();
			buf.writeInt(playerDisplayName.length());
			buf.writeCharSequence(playerDisplayName, Charset.forName("utf-8"));
		}
	}
	
	public static class PlayerSyncPacketHandler implements IMessageHandler<PlayerSyncPacket, IMessage> {

		//Required by forge
		public PlayerSyncPacketHandler(){}
		
		@Override
		public IMessage onMessage(PlayerSyncPacket message, MessageContext ctx) {
			if(TerramapServer.getServer() != null) {
				TerramapServer.getServer().syncPlayers(message.remotePlayers);
			}
			return null;
		}
		

	}

}
