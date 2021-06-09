package fr.thesmyler.terramap.warp;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import fr.thesmyler.terramap.network.NetworkUtil;
import io.netty.buffer.ByteBuf;

/**
 * 
 * @author Smyler
 * 
 * TODO Implement warps
 *
 */
public class Warp {

    private Map<String, String> properties = new HashMap<String, String>();

    public Warp(String id) {
        this.setProperty("id", id);
    }

    public String getId() {
        return this.getProperty("id");
    }

    public boolean hasProperty(String key) {
        return this.properties.containsKey(key);
    }

    public String getProperty(String property) {
        return this.properties.getOrDefault(property, "");
    }

    public void setProperty(String key, String value) {
        if(value == null || value.length() <= 0) {
            this.properties.remove(key);
        } else {
            this.properties.put(key, value);
        }
    }

    public Set<String> getPropertyKeys() {
        return this.properties.keySet();
    }

    /**
     * Returns a warp with the same id as this one and the same properties as this one for the specified ones
     * 
     * @param properties
     * @return
     */
    public Warp getCopyWithStrippedProperties(String... properties) {
        Warp warp = new Warp(this.getId());
        for(String key: properties) {
            warp.setProperty(key, this.getProperty(key));
        }
        return warp;
    }

    public void encodeToByteBuf(ByteBuf buf) {
        buf.writeInt(this.properties.size() - 1); // Id is assumed
        NetworkUtil.encodeStringToByteBuf(this.getId(), buf);
        for(String key: this.properties.keySet()) {
            if(key.equals("id")) continue;
            NetworkUtil.encodeStringToByteBuf(key, buf);
            NetworkUtil.encodeStringToByteBuf(this.properties.get(key), buf);
        }
    }

    public void encodeToByteBuf(ByteBuf buf, String ... properties) {
        Map<String, String> validProperties = new HashMap<String, String>();
        for(String key: properties) {
            if(!key.equals("id") && this.properties.containsKey(key)) {
                validProperties.put(key, this.getProperty(key));
            }
        }
        buf.writeInt(validProperties.size());
        NetworkUtil.encodeStringToByteBuf(this.getId(), buf);
        for(String key: validProperties.keySet()) {
            NetworkUtil.encodeStringToByteBuf(key, buf);
            NetworkUtil.encodeStringToByteBuf(validProperties.get(key), buf);
        }
    }

    public static Warp readWarpFromByteBuf(ByteBuf buf) {
        int keyCount = buf.readInt();
        String id = NetworkUtil.decodeStringFromByteBuf(buf);
        Warp warp = new Warp(id);
        for(int i=0; i<keyCount; i++) {
            String key = NetworkUtil.decodeStringFromByteBuf(buf);
            String value = NetworkUtil.decodeStringFromByteBuf(buf);
            warp.setProperty(key, value);
        }
        return warp;
    }

}
