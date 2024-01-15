package fr.thesmyler.terramap.network;

import java.util.HashMap;
import java.util.Map;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.PacketBuffer;

public final class NetworkUtil {
    
    private NetworkUtil() {}
    
    public static void encodeStringToByteBuf(String str, ByteBuf buf) {
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.setIndex(readerIndex, writerIndex);
        packetBuffer.writeString(str);
    }

    public static String decodeStringFromByteBuf(ByteBuf buf) {
        PacketBuffer packetBuffer = getPacketBuffer(buf);
        return packetBuffer.readString(Integer.MAX_VALUE/4);
    }

    public static void encodeStringArrayToByteBuf(String[] strings, ByteBuf buf) {
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.setIndex(readerIndex, writerIndex);
        packetBuffer.writeVarInt(strings.length);
        for(String str: strings) packetBuffer.writeString(str);
    }

    public static String[] decodeStringArrayFromByteBuf(ByteBuf buf) {
        PacketBuffer packetBuffer = getPacketBuffer(buf);
        int strCount = packetBuffer.readVarInt();
        String[] strings = new String[strCount]; 
        for(int i=0; i<strCount; i++) {
            strings[i] = packetBuffer.readString(Integer.MAX_VALUE/4);
        }
        return strings;
    }
    
    public static void encodeStringMapToByteBuf(Map<String, String> map, ByteBuf buf) {
        buf.writeInt(map.size());
        for(String key: map.keySet()) {
            encodeStringToByteBuf(key, buf);
            encodeStringToByteBuf(map.get(key), buf);
        }
    }
    
    public static Map<String, String> decodeStringMapFromByteBuf(ByteBuf buf) {
        Map<String, String> map = new HashMap<>();
        int length = buf.readInt();
        for(int i=0; i<length; i++) {
            String key = decodeStringFromByteBuf(buf);
            String value = decodeStringFromByteBuf(buf);
            map.put(key, value);
        }
        return map;
    }

    private static PacketBuffer getPacketBuffer(ByteBuf buf) {
        int readerIndex = buf.readerIndex();
        int writerIndex = buf.writerIndex();
        PacketBuffer packetBuffer = new PacketBuffer(buf);
        packetBuffer.setIndex(readerIndex, writerIndex);
        return packetBuffer;
    }

}
