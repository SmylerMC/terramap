package fr.thesmyler.terramap.eventhandlers;

import fr.thesmyler.terramap.network.RemoteSynchronizer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.SERVER)
public class ServerTerramapEventHandler {

	@SubscribeEvent
	public void onPlayerLoggedIn(PlayerLoggedInEvent event){
		RemoteSynchronizer.sendMapStylesToClient((EntityPlayerMP) event.player);
	}
	
}
