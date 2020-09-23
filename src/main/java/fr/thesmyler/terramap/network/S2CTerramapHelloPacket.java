package fr.thesmyler.terramap.network;

import fr.thesmyler.terramap.TerramapMod;
import io.github.terra121.EarthGeneratorSettings;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class S2CTerramapHelloPacket implements IMessage {

	public EarthGeneratorSettings settings;
	public String serverVersion;
	public boolean syncPlayers;
	public boolean syncSpectators;
	
	@Deprecated
	public boolean unused; //TODO Forge essentials - We are breaking backward compat anyway, let's remove this
	
	public S2CTerramapHelloPacket() {}
	
	public S2CTerramapHelloPacket(String serverVersion, EarthGeneratorSettings settings, boolean syncPlayers, boolean syncSpectators, boolean hasFe) {
		this.settings = settings;
		this.serverVersion = serverVersion;
		this.syncPlayers = syncPlayers;
		this.syncSpectators = syncSpectators;
		this.unused = hasFe;
	}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.serverVersion = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		this.syncPlayers = buf.readBoolean();
		this.syncSpectators = buf.readBoolean();
		this.unused = buf.readBoolean();
		String settings = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		this.settings = new EarthGeneratorSettings(settings);
	}

	@Override
	public void toBytes(ByteBuf buf) { 
		String settingsStr = this.settings.toString();
		TerramapNetworkManager.encodeStringToByteBuf(this.serverVersion, buf);
		buf.writeBoolean(this.syncPlayers);
		buf.writeBoolean(this.syncSpectators);
		buf.writeBoolean(this.unused);
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
	
}
