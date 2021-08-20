package fr.thesmyler.terramap.network;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.util.Strings;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.maps.raster.TiledMapProvider;
import fr.thesmyler.terramap.maps.raster.imp.UrlTiledMap;
import fr.thesmyler.terramap.util.geo.WebMercatorBounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

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
    private int maxConcurrentConnections;
    private boolean debug;
    private boolean backwardCompat = false;
    private Map<Integer, WebMercatorBounds> bounds;

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
        this.maxConcurrentConnections = map.getMaxConcurrentRequests();
        this.debug = map.isDebug();
        this.bounds = new HashMap<>();
        for(int i=map.getMinZoom(); i <= map.getMaxZoom(); i++) {
            WebMercatorBounds bound = map.getBounds(i);
            if(bound != null) this.bounds.put(i, bound);
        }
    }

    public SP2CMapStylePacket() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        this.bounds = new HashMap<>();
        this.id = NetworkUtil.decodeStringFromByteBuf(buf);
        this.providerVersion = buf.readLong();
        String urlPattern = NetworkUtil.decodeStringFromByteBuf(buf);
        int nameCount = buf.readInt();
        Map<String, String> names = new HashMap<>();
        for(int i=0; i < nameCount; i++) {
            String key = NetworkUtil.decodeStringFromByteBuf(buf);
            String name = NetworkUtil.decodeStringFromByteBuf(buf);
            names.put(key, name);
        }
        this.names = names;
        int copyrightCount = buf.readInt();
        Map<String, String> copyrights = new HashMap<>();
        for(int i=0; i < copyrightCount; i++) {
            String key = NetworkUtil.decodeStringFromByteBuf(buf);
            String copyright = NetworkUtil.decodeStringFromByteBuf(buf);
            copyrights.put(key, copyright);
        }
        this.copyrights = copyrights;
        this.minZoom = buf.readInt();
        this.maxZoom = buf.readInt();
        this.displayPriority = buf.readInt();
        this.isAllowedOnMinimap = buf.readBoolean();
        this.comment = NetworkUtil.decodeStringFromByteBuf(buf);
        if(!Strings.isBlank(urlPattern)) { // Pre 1.0.0-beta7 packet
            this.maxConcurrentConnections = 2;
            this.urlPatterns = new String[] {urlPattern};
            this.debug = false;
            return;
        }
        this.maxConcurrentConnections = buf.readInt();
        this.urlPatterns = NetworkUtil.decodeStringArrayFromByteBuf(buf);
        this.debug = buf.readBoolean();
        
        if(buf.isReadable()) {
            int length = buf.readInt();
            for(int i=0; i<length; i++) {
                int zoom = buf.readInt();
                int lowerX = buf.readInt();
                int lowerY = buf.readInt();
                int upperX = buf.readInt();
                int upperY = buf.readInt();
                this.bounds.put(zoom, new WebMercatorBounds(lowerX, lowerY, upperX, upperY));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        NetworkUtil.encodeStringToByteBuf(this.id, buf);
        buf.writeLong(this.providerVersion);
        String singleUrl = this.backwardCompat ? this.urlPatterns[0]: "";
        NetworkUtil.encodeStringToByteBuf(singleUrl, buf);
        buf.writeInt(this.names.size());
        for(String key: this.names.keySet()) {
            NetworkUtil.encodeStringToByteBuf(key, buf);
            NetworkUtil.encodeStringToByteBuf(this.names.get(key), buf);
        }
        buf.writeInt(this.copyrights.size());
        for(String key: this.copyrights.keySet()) {
            NetworkUtil.encodeStringToByteBuf(key, buf);
            NetworkUtil.encodeStringToByteBuf(this.copyrights.get(key), buf);
        }
        buf.writeInt(this.minZoom);
        buf.writeInt(this.maxZoom);
        buf.writeInt(this.displayPriority);
        buf.writeBoolean(this.isAllowedOnMinimap);
        NetworkUtil.encodeStringToByteBuf(this.comment, buf);
        buf.writeInt(this.maxConcurrentConnections);
        NetworkUtil.encodeStringArrayToByteBuf(this.urlPatterns, buf);
        buf.writeBoolean(this.debug);
        buf.writeInt(this.bounds.size());
        for(int zoom: this.bounds.keySet()) {
            buf.writeInt(zoom);
            WebMercatorBounds bounds = this.bounds.get(zoom);
            buf.writeInt(bounds.lowerX);
            buf.writeInt(bounds.lowerY);
            buf.writeInt(bounds.upperX);
            buf.writeInt(bounds.upperY);
        }
    }

    public void setBackwardCompat() {
        this.backwardCompat = true;
    }

    public boolean getBackwardCompat() {
        return this.backwardCompat;
    }

    public UrlTiledMap getTiledMap(TiledMapProvider provider) {
        return new UrlTiledMap(
                this.urlPatterns,
                this.minZoom,
                this.maxZoom,
                this.id,
                this.names,
                this.copyrights,
                this.displayPriority,
                this.isAllowedOnMinimap,
                provider,
                this.providerVersion,
                this.comment,
                this.maxConcurrentConnections,
                this.debug,
                this.bounds
                );
    }

    public static class SP2CMapStylePacketTerramapHandler implements IMessageHandler<SP2CMapStylePacket, IMessage> {

        public SP2CMapStylePacketTerramapHandler() {}

        @Override
        public IMessage onMessage(SP2CMapStylePacket message, MessageContext ctx) {
            try {
                UrlTiledMap map = message.getTiledMap(TiledMapProvider.SERVER);
                TerramapMod.logger.debug("Got custom map style from server: " + map.getId() + " / " + String.join(";", map.getUrlPatterns()));
                if(!TerramapConfig.enableDebugMaps && map.isDebug()) {
                    TerramapMod.logger.debug("Ignoring debug map from server: " + map.getId());
                    return null;
                }
                Minecraft.getMinecraft().addScheduledTask(() -> TerramapClientContext.getContext().addServerMapStyle(map));

            } catch(Exception e) {
                TerramapMod.logger.error("Failed to unpack a map style sent by the server");
                TerramapMod.logger.catching(e);
                TiledMapProvider.SERVER.setLastError(e);
            }
            return null;
        }

    }

    public static class SP2CMapStylePacketSledgehammerHandler implements IMessageHandler<SP2CMapStylePacket, IMessage> {

        public SP2CMapStylePacketSledgehammerHandler() {}

        @Override
        public IMessage onMessage(SP2CMapStylePacket message, MessageContext ctx) {
            try {
                UrlTiledMap map = message.getTiledMap(TiledMapProvider.PROXY);
                TerramapMod.logger.debug("Got custom map style from proxy: " + map.getId() + " / " + String.join(";", map.getUrlPatterns()));
                if(!TerramapConfig.enableDebugMaps && map.isDebug()) {
                    TerramapMod.logger.debug("Ignoring debug map from proxy: " + map.getId());
                    return null;
                }
                Minecraft.getMinecraft().addScheduledTask(() -> TerramapClientContext.getContext().addProxyMapStyle(map));
            } catch(Exception e) {
                TerramapMod.logger.error("Failed to unpack a map style sent by the proxy");
                TerramapMod.logger.catching(e);
                TiledMapProvider.PROXY.setLastError(e);
            }
            return null;
        }

    }

}
