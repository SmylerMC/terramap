package fr.thesmyler.terramap.network;

import java.util.HashMap;
import java.util.Map;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapRemote;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.maps.UrlTiledMap;
import fr.thesmyler.terramap.maps.TiledMapProvider;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

//TODO Test map style sync with various remotes
public class SP2CMapStylePacket implements IMessage {
	
	private String id;
	private long providerVersion;
	private String[] urlPatterns;
	private Map<String, String> names;
	private Map<String, String> copyrights;
	private int minZoom;
	private int maxZoom;
	private int displayPriority;
	private boolean isAllowedOnMinimap;
	private String comment;
	private int maxConcurrentConnection;
		
	public SP2CMapStylePacket(UrlTiledMap map) {
		this.id = map.getId();
		this.providerVersion = map.getProviderVersion();
		this.urlPatterns = map.getUrlPatterns();
		this.names = map.getUnlocalizedNames();
		this.copyrights = map.getUnlocalizedCopyrights();
		this.minZoom = map.getMinZoom();
		this.maxZoom = map.getMaxZoom();
		this.displayPriority = map.getDisplayPriority();
		this.isAllowedOnMinimap = map.isAllowedOnMinimap();
		this.comment = map.getComment();
		this.maxConcurrentConnection = map.getMaxConcurrentRequests();
	}
	
	public SP2CMapStylePacket() {}

	@Override
	public void fromBytes(ByteBuf buf) {
		this.id = TerramapNetworkManager.decodeStringFromByteBuf(buf);
		this.providerVersion = buf.readLong();
		String urlPattern = TerramapNetworkManager.decodeStringFromByteBuf(buf);
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
		if(buf.isReadable()) {
			this.maxConcurrentConnection = buf.readInt();
			this.urlPatterns = TerramapNetworkManager.decodeStringArrayFromByteBuf(buf);
		} else {
			this.maxConcurrentConnection = 2;
			this.urlPatterns = new String[] {urlPattern};
			return;
		}
	}

	@Override
	public void toBytes(ByteBuf buf) {
		TerramapNetworkManager.encodeStringToByteBuf(this.id, buf);
		buf.writeLong(this.providerVersion);
		TerramapNetworkManager.encodeStringToByteBuf(this.urlPatterns[0], buf);
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
		buf.writeInt(this.maxConcurrentConnection);
		TerramapNetworkManager.encodeStringArrayToByteBuf(this.urlPatterns, buf);
	}
	
	public UrlTiledMap getTiledMap(TiledMapProvider provider, int maxLoaded) {
		return new UrlTiledMap(
				this.urlPatterns,
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
				this.comment,
				this.maxConcurrentConnection
			);
	}
	
	public static class SP2CMapStylePacketTerramapHandler implements IMessageHandler<SP2CMapStylePacket, IMessage> {

		public SP2CMapStylePacketTerramapHandler() {}
		
		@Override
		public IMessage onMessage(SP2CMapStylePacket message, MessageContext ctx) {
			UrlTiledMap map = message.getTiledMap(TiledMapProvider.SERVER, TerramapConfig.maxTileLoad);
			TerramapMod.logger.debug("Got custom map style from server: " + map.getId() + " / " + String.join(";", map.getUrlPatterns()));
			Minecraft.getMinecraft().addScheduledTask(() -> TerramapRemote.getRemote().addServerMapStyle(map));
			return null;
		}
		
	}
	
	public static class SP2CMapStylePacketSledgehammerHandler implements IMessageHandler<SP2CMapStylePacket, IMessage> {

		public SP2CMapStylePacketSledgehammerHandler() {}
		
		@Override
		public IMessage onMessage(SP2CMapStylePacket message, MessageContext ctx) {
			UrlTiledMap map = message.getTiledMap(TiledMapProvider.PROXY, TerramapConfig.maxTileLoad);
			TerramapMod.logger.debug("Got custom map style from proxy: " + map.getId() + " / " + String.join(";", map.getUrlPatterns()));
			Minecraft.getMinecraft().addScheduledTask(() -> TerramapRemote.getRemote().addProxyMapStyle(map));
			return null;
		}
		
	}

}
