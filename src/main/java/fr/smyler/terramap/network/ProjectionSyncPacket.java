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

public class ProjectionSyncPacket implements IMessage {

	public EarthGeneratorSettings settings;
	
	public ProjectionSyncPacket() {}
	
	public ProjectionSyncPacket(EarthGeneratorSettings settings) {
		this.settings = settings;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		int nl = buf.readInt();
		String settings = buf.readCharSequence(nl, Charset.forName("ascii")).toString(); //No fancy utf?
		this.settings = new EarthGeneratorSettings(settings);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		String str = this.settings.toString();
		buf.writeInt(str.length());
		buf.writeCharSequence(str, Charset.forName("ascii"));
	}
	
	public static class ProjectionSyncPacketHandler implements IMessageHandler<ProjectionSyncPacket, IMessage> {

		//Required by forge ?
		public ProjectionSyncPacketHandler(){}
		
		@Override
		public IMessage onMessage(ProjectionSyncPacket message, MessageContext ctx) {
			TerramapMod.proxy.onSyncProjection(message.settings);
			return null;
		}
		

	}
	
	public static EarthGeneratorSettings getEarthGeneratorSettingsFromWorld(World world) {
		if(world.getWorldType() instanceof EarthWorldType) {
			return ((EarthTerrainProcessor)((CubeProviderServer)world.getChunkProvider()).getCubeGenerator()).cfg;
		} else return null;
	}
	
}
