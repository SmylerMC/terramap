package fr.thesmyler.terramap.eventhandlers;

import java.util.Map;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapConfig;
import fr.thesmyler.terramap.network.RemoteSynchronizer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.handshake.NetworkDispatcher;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerTerramapEventHandler {

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		EntityPlayerMP player = (EntityPlayerMP) event.player;
		Map<String, String> modList = NetworkDispatcher.get(player.connection.netManager).getModList();
		RemoteSynchronizer.sendMapStylesToClient(player);
    	String remoteVersion = modList.get(TerramapMod.MODID);
    	if(remoteVersion == null) { // Not installed on client
    		try {
    			if(TerramapConfig.joinWithoutModMessage.length() > 0)
    				player.sendMessage(ITextComponent.Serializer.fromJsonLenient(TerramapConfig.joinWithoutModMessage));
    		} catch(Exception e) {
    			TerramapMod.logger.error("Failed to send custom join message to client, make sure your json text is valid");
    		}
    	} else if(
    				remoteVersion.contains("1.0.0-beta5") ||
    				remoteVersion.contains("1.0.0-beta4") ||
    				remoteVersion.contains("1.0.0-beta3") ||
    				remoteVersion.contains("1.0.0-beta2") ||
    				remoteVersion.contains("1.0.0-beta1")
    			){
    		try {
    			if(TerramapConfig.joinWithOutdatedModMessage.length() > 0)
    				player.sendMessage(ITextComponent.Serializer.fromJsonLenient(TerramapConfig.joinWithOutdatedModMessage));
    		} catch(Exception e) {
    			TerramapMod.logger.error("Failed to send custom join message to client, make sure your json text is valid");
    		}
    	}
	}
	
}
