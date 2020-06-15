package fr.thesmyler.terramap.network;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapUtils;
import io.github.opencubicchunks.cubicchunks.core.server.CubeProviderServer;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.EarthTerrainProcessor;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CTerramapHelloPacket implements IMessage {

	public EarthGeneratorSettings settings;
	public String serverVersion;
	public boolean syncPlayers;
	public boolean syncSpectators;
	public boolean hasFe; //Forge essentials
	
	public S2CTerramapHelloPacket() {}
	
	public S2CTerramapHelloPacket(String serverVersion, EarthGeneratorSettings settings, boolean syncPlayers, boolean syncSpectators, boolean hasFe) {
		this.settings = settings;
		this.serverVersion = serverVersion;
		this.syncPlayers = syncPlayers;
		this.syncSpectators = syncSpectators;
		this.hasFe = hasFe;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.serverVersion = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		this.syncPlayers = buf.readBoolean();
		this.syncSpectators = buf.readBoolean();
		this.hasFe = buf.readBoolean();
		String settings = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		this.settings = new EarthGeneratorSettings(settings);
	}

	@Override
	public void toBytes(ByteBuf buf) { 
		String settingsStr = this.settings.toString();
		TerramapNetworkManager.encodeStringToByteBuf(this.serverVersion, buf);
		buf.writeBoolean(this.syncPlayers);
		buf.writeBoolean(this.syncSpectators);
		buf.writeBoolean(this.hasFe);
		TerramapNetworkManager.encodeStringToByteBuf(settingsStr, buf);
	}
	
	public static class S2CTerramapHelloPacketHandler implements IMessageHandler<S2CTerramapHelloPacket, IMessage> {

		//Required by forge
		public S2CTerramapHelloPacketHandler(){}
		
		@Override
		public IMessage onMessage(S2CTerramapHelloPacket message, MessageContext ctx) {
			Minecraft.getMinecraft().addScheduledTask(()->{TerramapMod.proxy.onServerHello(message);});
			return null;
		}
		

	}
	
	public static EarthGeneratorSettings getEarthGeneratorSettingsFromWorld(World world) {
		if(TerramapUtils.isEarthWorld(world)) {
			return ((EarthTerrainProcessor)((CubeProviderServer)world.getChunkProvider()).getCubeGenerator()).cfg;
		} else return null;
	}
	
}
