package fr.thesmyler.terramap.network.playersync;

import fr.thesmyler.terramap.network.RemoteSynchronizer;
import io.netty.buffer.ByteBuf;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.IMessageHandler;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class C2SPRegisterForUpdatesPacket implements IMessage {

    public boolean update = false;

    public C2SPRegisterForUpdatesPacket() {}

    public C2SPRegisterForUpdatesPacket(boolean update) {
        this.update = update;
    }

    @Override
    public void fromBytes(ByteBuf buf) {
        this.update = buf.readBoolean();
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(this.update);
    }

    public static class C2SRegisterForUpdatesPacketHandler implements IMessageHandler<C2SPRegisterForUpdatesPacket, IMessage>{

        @Override
        public C2SPRegisterForUpdatesPacket onMessage(C2SPRegisterForUpdatesPacket message, MessageContext ctx) {
            EntityPlayerMP player = ctx.getServerHandler().player;
            WorldServer world = player.getServerWorld();
            if(message.update) world.addScheduledTask(() -> RemoteSynchronizer.registerPlayerForUpdates(player));
            else world.addScheduledTask(()-> RemoteSynchronizer.unregisterPlayerForUpdates(player));
            return null;
        }

    }

}
