package fr.thesmyler.terramap.network;

import java.util.UUID;

import fr.thesmyler.terramap.network.playersync.PlayerSyncStatus;
import io.netty.buffer.ByteBuf;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CTerramapHelloPacket implements IMessage {

	public String serverVersion;
	public EarthGeneratorSettings worldSettings;
	public UUID worldUUID;
	public PlayerSyncStatus syncPlayers;
	public PlayerSyncStatus syncSpectators;
	public boolean enablePlayerRadar;
	public boolean enableAnimalRadar;
	public boolean enableMobRadar;
	public boolean enableDecoRadar;
	public boolean hasWarpSupport;
	
	//TODO Warp support
		
	public S2CTerramapHelloPacket() {}
	
	public S2CTerramapHelloPacket(
			String serverVersion,
			EarthGeneratorSettings settings,
			UUID worldUUID,
			PlayerSyncStatus syncPlayers,
			PlayerSyncStatus syncSpectators,
			boolean enablePlayerRadar,
			boolean enableAnimalRadar,
			boolean enableMobRadar,
			boolean enableDecoRadar,
			boolean warpSupport) {
		this.worldSettings = settings;
		this.serverVersion = serverVersion;
		this.worldUUID = worldUUID;
		this.syncPlayers = syncPlayers;
		this.syncSpectators = syncSpectators;
		this.enableAnimalRadar = enableAnimalRadar;
		this.enablePlayerRadar = enablePlayerRadar;
		this.enableMobRadar = enableMobRadar;
		this.enableDecoRadar = enableDecoRadar;
		this.hasWarpSupport = warpSupport;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.serverVersion = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		String jsonWorldSettings = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		if(jsonWorldSettings.length() > 0) {
			this.worldSettings = EarthGeneratorSettings.parse(jsonWorldSettings);
		} else {
			this.worldSettings = null;
		}
		long leastUUID = buf.readLong();
		long mostUUID = buf.readLong();
		this.worldUUID = new UUID(mostUUID, leastUUID);
		this.syncPlayers = PlayerSyncStatus.getFromNetworkCode(buf.readByte());
		this.syncSpectators = PlayerSyncStatus.getFromNetworkCode(buf.readByte());
		this.enablePlayerRadar = buf.readBoolean();
		this.enableAnimalRadar = buf.readBoolean();
		this.enableMobRadar = buf.readBoolean();
		this.enableDecoRadar = buf.readBoolean();
		this.hasWarpSupport = buf.readBoolean();
	}

	@Override
	public void toBytes(ByteBuf buf) {
		TerramapNetworkManager.encodeStringToByteBuf(this.serverVersion, buf);
		TerramapNetworkManager.encodeStringToByteBuf(this.worldSettings.toString(), buf);
		buf.writeLong(this.worldUUID.getLeastSignificantBits());
		buf.writeLong(this.worldUUID.getMostSignificantBits());
		buf.writeByte(this.syncPlayers.VALUE);
		buf.writeByte(this.syncSpectators.VALUE);
		buf.writeBoolean(this.enablePlayerRadar);
		buf.writeBoolean(this.enableAnimalRadar);
		buf.writeBoolean(this.enableMobRadar);
		buf.writeBoolean(this.enableDecoRadar);
		buf.writeBoolean(this.hasWarpSupport);
	}
	
	public static class S2CTerramapHelloPacketHandler implements IMessageHandler<S2CTerramapHelloPacket, IMessage> {

		//Required by forge
		public S2CTerramapHelloPacketHandler(){}
		
		@Override
		public IMessage onMessage(S2CTerramapHelloPacket message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(()->{RemoteSynchronizer.onServerHello(message);});
			return null;
		}
		

	}
	
	
	
}
