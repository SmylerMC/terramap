package fr.thesmyler.terramap.network.playersync;

import java.util.UUID;

import fr.thesmyler.terramap.TerramapClientContext;
import fr.thesmyler.terramap.network.NetworkUtil;
import fr.thesmyler.terramap.util.geo.GeoPoint;
import fr.thesmyler.terramap.util.geo.GeoPointMutable;
import io.netty.buffer.ByteBuf;
import net.buildtheearth.terraplusplus.projection.OutOfProjectionBoundsException;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class SP2CPlayerSyncPacket implements IMessage {

    protected TerramapLocalPlayer[] localPlayers;
    protected TerramapRemotePlayer[] remotePlayers;

    public SP2CPlayerSyncPacket() {} // Required by forge

    public SP2CPlayerSyncPacket(TerramapLocalPlayer[] players) {
        this.localPlayers = players;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.remotePlayers = new TerramapRemotePlayer[buf.readInt()];
        GeoPointMutable playerLocation = new GeoPointMutable();
        for(int i=0; i<this.remotePlayers.length; i++) {
            long leastUUID = buf.readLong();
            long mostUUID = buf.readLong();
            ITextComponent name = ITextComponent.Serializer.jsonToComponent(NetworkUtil.decodeStringFromByteBuf(buf));
            double longitude = buf.readDouble();
            double latitude = buf.readDouble();
            float azimuth = buf.readFloat();
            GameType gamemode = GameType.getByName(NetworkUtil.decodeStringFromByteBuf(buf));
            TerramapRemotePlayer player = new TerramapRemotePlayer(new UUID(mostUUID, leastUUID), name);
            player.setGamemode(gamemode);
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
        for(TerramapPlayer player: this.localPlayers) {
            double[] coordinates;
            try {
                GeoPoint<?> location = player.getLocation();
                if(location == null) throw OutOfProjectionBoundsException.get();
                coordinates = location.asArray();
            } catch(OutOfProjectionBoundsException e) {
                coordinates = new double[] {Double.NaN, Double.NaN};
            }
            buf.writeLong(player.getUUID().getLeastSignificantBits());
            buf.writeLong(player.getUUID().getMostSignificantBits());
            String playerDisplayName = ITextComponent.Serializer.componentToJson(player.getDisplayName());
            NetworkUtil.encodeStringToByteBuf(playerDisplayName, buf);
            buf.writeDouble(coordinates[0]);
            buf.writeDouble(coordinates[1]);
            buf.writeFloat(player.getAzimuth());
            NetworkUtil.encodeStringToByteBuf(player.getGamemode().getName(), buf);
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
