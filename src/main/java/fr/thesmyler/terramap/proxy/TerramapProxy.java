package fr.thesmyler.terramap.proxy;

import fr.thesmyler.terramap.network.S2CTerramapHelloPacket;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedOutEvent;

public abstract class TerramapProxy {

	public abstract void preInit(FMLPreInitializationEvent event);
	public abstract void init(FMLInitializationEvent event);
	public abstract void onServerHello(S2CTerramapHelloPacket pkt);
	public abstract void onPlayerLoggedIn(PlayerLoggedInEvent event);
	public abstract void onPlayerLoggedOut(PlayerLoggedOutEvent event);
	public abstract float getDefaultGuiSize();
	
}
