package fr.thesmyler.terramap.network.playersync;

import java.util.UUID;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.network.NetworkUtil;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.entity.player.*;
import net.smyler.terramap.geo.GeoPoint;
import net.smyler.terramap.geo.GeoPointMutable;
import io.netty.buffer.ByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.smyler.terramap.geo.OutOfGeoBoundsException;

import static net.smyler.terramap.Terramap.getTerramap;

public class SP2CPlayerSyncPacket implements IMessage {

    protected ForgePlayerLocal[] localPlayers;
    protected PlayerSynchronized[] remotePlayers;

    public SP2CPlayerSyncPacket() {} // Required by forge

    public SP2CPlayerSyncPacket(ForgePlayerLocal[] players) {
        this.localPlayers = players;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.remotePlayers = new PlayerSynchronized[buf.readInt()];
        GeoPointMutable playerLocation = new GeoPointMutable();
        for(int i=0; i<this.remotePlayers.length; i++) {
            long leastUUID = buf.readLong();
            long mostUUID = buf.readLong();
            String nameJson = NetworkUtil.decodeStringFromByteBuf(buf);
            Text name = getTerramap().gson().fromJson(nameJson, Text.class);
            double longitude = buf.readDouble();
            double latitude = buf.readDouble();
            float azimuth = buf.readFloat();
            GameMode gamemode = GameMode.fromName(NetworkUtil.decodeStringFromByteBuf(buf));
            PlayerSynchronized player = new PlayerSynchronized(new UUID(mostUUID, leastUUID), name);
            player.setGameMode(gamemode);
            if(Double.isFinite(longitude) && Double.isFinite(latitude)) {
                playerLocation.set(longitude, latitude);
                player.setLocationAndAzimuth(playerLocation, azimuth);
            }
            this.remotePlayers[i] = player;
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeInt(this.localPlayers.length);
        for(Player player: this.localPlayers) {
            double[] coordinates;
            try {
                GeoPoint location = player.location();
                if(location == null) {
                    throw new OutOfGeoBoundsException();
                }
                coordinates = location.asArray();
            } catch(OutOfGeoBoundsException e) {
                coordinates = new double[] {Double.NaN, Double.NaN};
            }
            buf.writeLong(player.uuid().getLeastSignificantBits());
            buf.writeLong(player.uuid().getMostSignificantBits());
            String playerDisplayName = getTerramap().gson().toJson(player.displayName());
            NetworkUtil.encodeStringToByteBuf(playerDisplayName, buf);
            buf.writeDouble(coordinates[0]);
            buf.writeDouble(coordinates[1]);
            buf.writeFloat(player.azimuth());
            NetworkUtil.encodeStringToByteBuf(player.gameMode().name(), buf);
        }
    }

    public static class S2CPlayerSyncPacketHandler implements IMessageHandler<SP2CPlayerSyncPacket, IMessage> {

        //Required by forge
        public S2CPlayerSyncPacketHandler(){}

        @Override
        public IMessage onMessage(SP2CPlayerSyncPacket message, MessageContext ctx) {
            Minecraft.getMinecraft().addScheduledTask(() -> TerramapClientContext.getContext().syncPlayers(message.remotePlayers));
            return null;
        }


    }

}
