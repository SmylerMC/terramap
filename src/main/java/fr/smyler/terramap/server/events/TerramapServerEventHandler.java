/* IRL World Minecraft Mod
    Copyright (C) 2017  Smyler

    This program is free software: you can redistribute it and/or modify
    it under the terms of the GNU General Public License as published by
    the Free Software Foundation, either version 3 of the License, or
    (at your option) any later version.

    This program is distributed in the hope that it will be useful,
    but WITHOUT ANY WARRANTY; without even the implied warranty of
    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
    GNU General Public License for more details.

    You should have received a copy of the GNU General Public License
	along with this program. If not, see <http://www.gnu.org/licenses/>.

	The author can be contacted at smyler@mail.com
*/


package fr.smyler.terramap.server.events;

import fr.smyler.terramap.TerramapMod;
import fr.smyler.terramap.network.ProjectionSyncPacket;
import fr.smyler.terramap.network.TerramapPacketHandler;
import io.github.terra121.EarthGeneratorSettings;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

/**
 * The event subscriber for generic server events
 * 
 * @author Smyler
 *
 */
@Mod.EventBusSubscriber(modid=TerramapMod.MODID)
public final class TerramapServerEventHandler {

	/**
	 * Fired on server side when a player is about to join.
	 * 
	 * @param event
	 */
	@SubscribeEvent
	public static void onPlayerJoinServer(PlayerLoggedInEvent event){
		//Send world data to the client
		EntityPlayerMP player = (EntityPlayerMP)event.player;
		World world = player.getEntityWorld();
		EarthGeneratorSettings settings = ProjectionSyncPacket.getEarthGeneratorSettingsFromWorld(world);
		if(settings != null) {
			IMessage data = new ProjectionSyncPacket(settings);
			TerramapPacketHandler.INSTANCE.sendTo(data, player);
		}
	}
	
}
