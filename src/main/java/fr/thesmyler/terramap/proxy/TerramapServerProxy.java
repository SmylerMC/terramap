package fr.thesmyler.terramap.proxy;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.eventhandlers.ServerTerramapEventHandler;
import fr.thesmyler.terramap.network.S2CTerramapHelloPacket;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapServerProxy extends TerramapProxy {

	@Override
	public Side getSide() {
		return Side.SERVER;
	}
	
	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapNetworkManager.registerHandlers(Side.SERVER);
		TerramapMod.logger.debug("Terramap server pre-init");
	}

	@Override
	public void init(FMLInitializationEvent event) {
		TerramapMod.logger.debug("Terramap server init");
		MinecraftForge.EVENT_BUS.register(new ServerTerramapEventHandler());

	}

	@Override
	public void onServerHello(S2CTerramapHelloPacket s) {
		// Should never be called on server
	}

	@Override
	public double getDefaultGuiSize() {
		// Don't care on server, this is just for the client config
		return 0;
	}

}
