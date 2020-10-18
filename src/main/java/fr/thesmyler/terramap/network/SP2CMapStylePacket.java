package fr.thesmyler.terramap.network;

import java.util.HashMap;
import java.util.Map;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.maps.TiledMap;
import fr.thesmyler.terramap.maps.TiledMapProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SP2CMapStylePacket implements IMessage {
	
	private String id;
	private long providerVersion;
	private String urlPattern;
	private Map<String, String> names;
	private Map<String, String> copyrights;
	private int minZoom;
	private int maxZoom;
	private int displayPriority;
	private boolean isAllowedOnMinimap;
	private String comment;
		
	public SP2CMapStylePacket(TiledMap map) {
		this.id = map.getId();
		this.providerVersion = map.getProviderVersion();
		this.urlPattern = map.getUrlPattern();
		this.names = map.getUnlocalizedNames();
		this.copyrights = map.getUnlocalizedCopyrights();
		this.minZoom = map.getMinZoom();
		this.maxZoom = map.getMaxZoom();
		this.displayPriority = map.getDisplayPriority();
		this.isAllowedOnMinimap = map.isAllowedOnMinimap();
		this.comment = map.getComment();
	}
	
	public SP2CMapStylePacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		this.providerVersion = buf.readLong();
		this.urlPattern = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		int nameCount = buf.readInt();
		Map<String, String> names = new HashMap<String, String>();
		for(int i=0; i < nameCount; i++) {
			String key = TerramapNetworkManager.decodeStringFromByteBuf(buf);
			String name = TerramapNetworkManager.decodeStringFromByteBuf(buf);
			names.put(key, name);
		}
		this.names = names;
		int copyrightCount = buf.readInt();
		Map<String, String> copyrights = new HashMap<String, String>();
		for(int i=0; i < copyrightCount; i++) {
			String key = TerramapNetworkManager.decodeStringFromByteBuf(buf);
			String copyright = TerramapNetworkManager.decodeStringFromByteBuf(buf);
			copyrights.put(key, copyright);
		}
		this.copyrights = copyrights;
		this.minZoom = buf.readInt();
		this.maxZoom = buf.readInt();
		this.displayPriority = buf.readInt();
		this.isAllowedOnMinimap = buf.readBoolean();
		this.comment = TerramapNetworkManager.decodeStringFromByteBuf(buf);
	}

	@Override
	public void toBytes(ByteBuf buf) {
		TerramapNetworkManager.encodeStringToByteBuf(this.id, buf);
		buf.writeLong(this.providerVersion);
		TerramapNetworkManager.encodeStringToByteBuf(this.urlPattern, buf);
		buf.writeInt(this.names.size());
		for(String key: this.names.keySet()) {
			TerramapNetworkManager.encodeStringToByteBuf(key, buf);
			TerramapNetworkManager.encodeStringToByteBuf(this.names.get(key), buf);
		}
		buf.writeInt(this.copyrights.size());
		for(String key: this.copyrights.keySet()) {
			TerramapNetworkManager.encodeStringToByteBuf(key, buf);
			TerramapNetworkManager.encodeStringToByteBuf(this.copyrights.get(key), buf);
		}
		buf.writeInt(this.minZoom);
		buf.writeInt(this.maxZoom);
		buf.writeInt(this.displayPriority);
		buf.writeBoolean(this.isAllowedOnMinimap);
		TerramapNetworkManager.encodeStringToByteBuf(this.comment, buf);
	}
	
	public TiledMap getTiledMap(TiledMapProvider provider, int maxLoaded) {
		return new TiledMap(
				this.urlPattern,
				this.minZoom,
				this.maxZoom,
				maxLoaded,
				this.id,
				this.names,
				this.copyrights,
				this.displayPriority,
				this.isAllowedOnMinimap,
				provider,
				this.providerVersion,
				this.comment
			);
	}
	
	public static class SP2CMapStylePacketTerramapHandler implements IMessageHandler<SP2CMapStylePacket, IMessage> {

		public SP2CMapStylePacketTerramapHandler() {}
		
		@Override
		public IMessage onMessage(SP2CMapStylePacket message, MessageContext ctx) {
			TiledMap map = message.getTiledMap(TiledMapProvider.SERVER, TerramapConfig.ClientAdvanced.maxTileLoad);
			TerramapMod.logger.info("Got custom map style from server: " + map.getId() + " / " + map.getUrlPattern()); //TODO Change to debug
			Minecraft.getMinecraft().addScheduledTask(() -> TerramapRemote.getRemote().addServerMapStyle(map));
			return null;
		}
		
	}
	
	public static class SP2CMapStylePacketSledgehammerHandler implements IMessageHandler<SP2CMapStylePacket, IMessage> {

		public SP2CMapStylePacketSledgehammerHandler() {}
		
		@Override
		public IMessage onMessage(SP2CMapStylePacket message, MessageContext ctx) {
			TiledMap map = message.getTiledMap(TiledMapProvider.PROXY, TerramapConfig.ClientAdvanced.maxTileLoad);
			TerramapMod.logger.info("Got custom map style from proxy: " + map.getId() + " / " + map.getUrlPattern()); //TODO Change to debug
			Minecraft.getMinecraft().addScheduledTask(() -> TerramapRemote.getRemote().addServerMapStyle(map));
			return null;
		}
		
	}

}
