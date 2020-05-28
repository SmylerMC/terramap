package fr.thesmyler.terramap.network.mapsync;

import java.nio.charset.Charset;
import java.util.UUID;

import fr.thesmyler.terramap.TerramapServer;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CPlayerSyncPacket implements IMessage {

	protected TerramapLocalPlayer[] localPlayers;
	protected TerramapRemotePlayer[] remotePlayers;

	public S2CPlayerSyncPacket() {} //Required by forge

	public S2CPlayerSyncPacket(TerramapLocalPlayer[] players) {
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
			boolean spec = buf.readBoolean();
			int nameLen = buf.readInt();
			String name = buf.readCharSequence(nameLen, Charset.forName("utf-8")).toString();
			this.remotePlayers[i] = new TerramapRemotePlayer(uuid, name, x, z, spec);
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
			buf.writeBoolean(player.isSpectator());
			String playerDisplayName = player.getDisplayName();
			buf.writeInt(playerDisplayName.length());
			buf.writeCharSequence(playerDisplayName, Charset.forName("utf-8"));
		}
	}

	public static class S2CPlayerSyncPacketHandler implements IMessageHandler<S2CPlayerSyncPacket, IMessage> {

		//Required by forge
		public S2CPlayerSyncPacketHandler(){}

		@Override
		public IMessage onMessage(S2CPlayerSyncPacket message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(()->{TerramapServer.getServer().syncPlayers(message.remotePlayers);});
			return null;
		}


	}

}
