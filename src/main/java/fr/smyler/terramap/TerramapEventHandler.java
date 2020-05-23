package fr.smyler.terramap;

import fr.smyler.terramap.network.ProjectionSyncPacket;
import fr.smyler.terramap.network.TerramapPacketHandler;
import io.github.terra121.EarthGeneratorSettings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * The event subscriber for generic server events
 * 
 * @author Smyler
 *
 */
@Mod.EventBusSubscriber(modid=TerramapMod.MODID)
public final class TerramapEventHandler {


	@SubscribeEvent
	public static void onPlayerLoggedIn(PlayerLoggedInEvent event){
		//Send world data to the client
		TerramapMod.proxy.onPlayerLoggedIn(event);
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		World world = player.getEntityWorld();
		EarthGeneratorSettings settings = ProjectionSyncPacket.getEarthGeneratorSettingsFromWorld(world);
		if(settings != null) {
			IMessage data = new ProjectionSyncPacket(settings);
			TerramapPacketHandler.INSTANCE.sendTo(data, player);
		}
	}
	
	@SubscribeEvent
	public static void onPlayerLoggedOut(PlayerLoggedOutEvent event) {
		TerramapMod.proxy.onPlayerLoggedOut(event);
	}
	
}
