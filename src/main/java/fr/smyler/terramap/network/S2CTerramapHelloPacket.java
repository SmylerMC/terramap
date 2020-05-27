package fr.smyler.terramap.network;

import java.nio.charset.Charset;

import fr.smyler.terramap.TerramapMod;
import io.github.opencubicchunks.cubicchunks.core.server.CubeProviderServer;
import io.github.terra121.EarthGeneratorSettings;
import io.github.terra121.EarthTerrainProcessor;
import io.github.terra121.EarthWorldType;
import io.netty.buffer.ByteBuf;
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
		int vlen = buf.readInt();
		this.serverVersion = buf.readCharSequence(vlen, Charset.forName("utf-8")).toString();
		this.syncPlayers = buf.readBoolean();
		this.syncSpectators = buf.readBoolean();
		this.hasFe = buf.readBoolean();
		int nl = buf.readInt();
		String settings = buf.readCharSequence(nl, Charset.forName("utf-8")).toString();
		this.settings = new EarthGeneratorSettings(settings);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		String settingsStr = this.settings.toString();
		buf.writeInt(this.serverVersion.length());
		buf.writeCharSequence(this.serverVersion, Charset.forName("utf-8"));
		buf.writeBoolean(this.syncPlayers);
		buf.writeBoolean(this.syncSpectators);
		buf.writeBoolean(this.hasFe);
		buf.writeInt(settingsStr.length());
		buf.writeCharSequence(settingsStr, Charset.forName("utf-8"));
	}
	
	public static class S2CTerramapHelloPacketHandler implements IMessageHandler<S2CTerramapHelloPacket, IMessage> {

		//Required by forge ?
		public S2CTerramapHelloPacketHandler(){}
		
		@Override
		public IMessage onMessage(S2CTerramapHelloPacket message, MessageContext ctx) {
			TerramapMod.proxy.onServerHello(message);
			return null;
		}
		

	}
	
	public static EarthGeneratorSettings getEarthGeneratorSettingsFromWorld(World world) {
		if(world.getWorldType() instanceof EarthWorldType) {
			return ((EarthTerrainProcessor)((CubeProviderServer)world.getChunkProvider()).getCubeGenerator()).cfg;
		} else return null;
	}
	
}
