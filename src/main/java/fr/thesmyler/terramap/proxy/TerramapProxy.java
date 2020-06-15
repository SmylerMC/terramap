package fr.thesmyler.terramap.proxy;

import fr.thesmyler.terramap.network.S2CTerramapHelloPacket;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TerramapProxy {

	public abstract Side getSide();
	public abstract void preInit(FMLPreInitializationEvent event);
	public abstract void init(FMLInitializationEvent event);
	public abstract void onServerHello(S2CTerramapHelloPacket pkt);
	public abstract double getDefaultGuiSize();
	
	public boolean isClient() {
		return this.getSide().equals(Side.CLIENT);
	}
	
	public boolean isDedicatedServer() {
		return this.getSide().equals(Side.SERVER);
	}
	
}
