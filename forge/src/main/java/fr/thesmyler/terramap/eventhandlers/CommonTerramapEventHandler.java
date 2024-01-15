package fr.thesmyler.terramap.eventhandlers;

import fr.thesmyler.terramap.TerramapConfig;
import fr.thesmyler.terramap.saving.server.TerramapServerPreferences;
import fr.thesmyler.terramap.network.RemoteSynchronizer;
import fr.thesmyler.terramap.util.TerramapUtil;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.WorldServer;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerChangedDimensionEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;

public class CommonTerramapEventHandler {

    private long tickCounter = 0;

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event){
        if(!event.player.world.isRemote) {
            EntityPlayerMP player = (EntityPlayerMP) event.player;
            RemoteSynchronizer.sendHelloToClient(player);
            RemoteSynchronizer.sendTpCommandToClient(player);
        }
    }

    @SubscribeEvent
    public void onChangeDimension(PlayerChangedDimensionEvent event) {
        if(!event.player.world.isRemote)
            RemoteSynchronizer.sendHelloToClient((EntityPlayerMP) event.player);
    }

    @SubscribeEvent
    public void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
        RemoteSynchronizer.playersToUpdate.remove(event.player.getPersistentID());
    }


    @SubscribeEvent
    public void onWorldTick(WorldTickEvent event) {
        if(event.phase.equals(TickEvent.Phase.END) || event.world.isRemote) return;
        WorldServer world = event.world.getMinecraftServer().worlds[0]; //event.world has no entity or players
        if(TerramapConfig.SERVER.synchronizePlayers && TerramapUtil.isServerEarthWorld(world) && this.tickCounter == 0) {
            RemoteSynchronizer.syncPlayers(world);
        }
        this.tickCounter = (this.tickCounter+1) % TerramapConfig.SERVER.syncInterval;
    }

    @SubscribeEvent
    public void onWorldLoads(WorldEvent.Load event) {
        if(!event.getWorld().isRemote) {
            WorldServer world = ((WorldServer)event.getWorld());
            TerramapServerPreferences.loadWorldPreferences(world);
        }
    }

}
