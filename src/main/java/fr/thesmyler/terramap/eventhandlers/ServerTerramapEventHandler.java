package fr.thesmyler.terramap.eventhandlers;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapVersion;
import fr.thesmyler.terramap.TerramapConfig;
import fr.thesmyler.terramap.network.RemoteSynchronizer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerTerramapEventHandler {

    @SubscribeEvent
    public void onPlayerLoggedIn(PlayerLoggedInEvent event){
        EntityPlayerMP player = (EntityPlayerMP) event.player;
        RemoteSynchronizer.sendMapStylesToClient(player);
        TerramapVersion remoteVersion = TerramapVersion.getClientVersion(player);

        if(remoteVersion == null) { // Not installed on client
            try {
                if(TerramapConfig.SERVER.joinWithoutModMessage.length() > 0)
                    player.sendMessage(ITextComponent.Serializer.fromJsonLenient(TerramapConfig.SERVER.joinWithoutModMessage));
            } catch(Exception e) {
                TerramapMod.logger.error("Failed to send custom join message to client, make sure your json text is valid");
            }
        } else if(remoteVersion.isOlder(TerramapMod.OLDEST_COMPATIBLE_CLIENT)){
            try {
                if(TerramapConfig.SERVER.joinWithOutdatedModMessage.length() > 0)
                    player.sendMessage(ITextComponent.Serializer.fromJsonLenient(TerramapConfig.SERVER.joinWithOutdatedModMessage));
            } catch(Exception e) {
                TerramapMod.logger.error("Failed to send custom join message to client, make sure your json text is valid");
            }
        }
    }

}
