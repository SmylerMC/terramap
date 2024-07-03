package fr.thesmyler.terramap.network;

import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonParseException;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.tilesets.raster.UrlRasterTileSet;
import org.apache.logging.log4j.util.Strings;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.TerramapConfig;
import net.smyler.terramap.tilesets.raster.RasterTileSetProvider;
import net.smyler.terramap.util.geo.WebMercatorBounds;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;


public class SP2CRasterTileSetPacket implements IMessage {

    private String id;
    private long providerVersion;
    private String[] urlPatterns;
    private Map<String, String> names;
    private Map<String, Text> copyrights;
    private int minZoom;
    private int maxZoom;
    private int displayPriority;
    private boolean isAllowedOnMinimap;
    private String comment;
    private int maxConcurrentConnections;
    private boolean debug;
    private boolean backwardCompat = false;
    private Map<Integer, WebMercatorBounds> bounds;

    public SP2CRasterTileSetPacket(UrlRasterTileSet map) {
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

    public SP2CRasterTileSetPacket() {}

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
        Map<String, Text> copyrights = new HashMap<>();
        for(int i=0; i < copyrightCount; i++) {
            String key = NetworkUtil.decodeStringFromByteBuf(buf);
            String copyrightJson = NetworkUtil.decodeStringFromByteBuf(buf);
            try {
                Text copyright = Terramap.instance().gson().fromJson(copyrightJson, Text.class);
                copyrights.put(key, copyright);
            } catch (JsonParseException e) {
                Terramap.instance().logger().warn("Received invalid map style copyright from server.");
            }
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
            NetworkUtil.encodeStringToByteBuf(Terramap.instance().gson().toJson(this.copyrights.get(key)), buf);
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

    public UrlRasterTileSet getTiledMap(RasterTileSetProvider provider) {
        UrlRasterTileSet tiledMap = new UrlRasterTileSet(
                this.urlPatterns,
                this.minZoom,
                this.maxZoom,
                this.id,
                this.displayPriority,
                this.isAllowedOnMinimap,
                provider,
                this.providerVersion,
                this.comment,
                this.maxConcurrentConnections,
                this.debug
        );
        this.names.forEach(tiledMap::setNameTranslation);
        this.copyrights.forEach(tiledMap::setTranslatedCopyright);
        this.bounds.forEach(tiledMap::setBounds);
        return tiledMap;
    }

    public static class SP2CRasterTileSetPacketTerramapHandler implements IMessageHandler<SP2CRasterTileSetPacket, IMessage> {

        public SP2CRasterTileSetPacketTerramapHandler() {}

        @Override
        public IMessage onMessage(SP2CRasterTileSetPacket message, MessageContext ctx) {
            try {
                UrlRasterTileSet map = message.getTiledMap(RasterTileSetProvider.SERVER);
                Terramap.instance().logger().debug("Got custom map style from server: {} / {}", map.getId(), String.join(";", map.getUrlPatterns()));
                if(!TerramapConfig.enableDebugMaps && map.isDebug()) {
                    Terramap.instance().logger().debug("Ignoring debug map from server: {}", map.getId());
                    return null;
                }
                Minecraft.getMinecraft().addScheduledTask(() -> TerramapClientContext.getContext().addServerRasterTileSet(map));

            } catch(Exception e) {
                Terramap.instance().logger().error("Failed to unpack a map style sent by the server");
                Terramap.instance().logger().catching(e);
                RasterTileSetProvider.SERVER.setLastError(e);
            }
            return null;
        }

    }

    public static class SP2CRasterTileSetPacketSledgehammerHandler implements IMessageHandler<SP2CRasterTileSetPacket, IMessage> {

        public SP2CRasterTileSetPacketSledgehammerHandler() {}

        @Override
        public IMessage onMessage(SP2CRasterTileSetPacket message, MessageContext ctx) {
            try {
                UrlRasterTileSet map = message.getTiledMap(RasterTileSetProvider.PROXY);
                Terramap.instance().logger().debug("Got custom map style from proxy: {} / {}", map.getId(), String.join(";", map.getUrlPatterns()));
                if(!TerramapConfig.enableDebugMaps && map.isDebug()) {
                    Terramap.instance().logger().debug("Ignoring debug map from proxy: {}", map.getId());
                    return null;
                }
                Minecraft.getMinecraft().addScheduledTask(() -> TerramapClientContext.getContext().addProxyRasterTileSet(map));
            } catch(Exception e) {
                Terramap.instance().logger().error("Failed to unpack a map style sent by the proxy");
                Terramap.instance().logger().catching(e);
                RasterTileSetProvider.PROXY.setLastError(e);
            }
            return null;
        }

    }

}
