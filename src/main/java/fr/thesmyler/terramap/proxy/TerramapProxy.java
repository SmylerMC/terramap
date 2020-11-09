package fr.thesmyler.terramap.proxy;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.world.GameType;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.relauncher.Side;

public abstract class TerramapProxy {

	public abstract Side getSide();
	public abstract void preInit(FMLPreInitializationEvent event);
	public abstract void init(FMLInitializationEvent event);
	public abstract void onServerStarting(FMLServerStartingEvent event);
	public abstract double getGuiScaleForConfig();
	public abstract void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event);
	public abstract GameType getGameMode(EntityPlayer e);
	
	// I18n is only is client sided, but we need to localize string on the server when sending command feedback to client which do not have the mod
	public abstract String localize(String key, Object... objects);
	
	public boolean isClient() {
		return this.getSide().equals(Side.CLIENT);
	}
	
	public boolean isDedicatedServer() {
		return this.getSide().equals(Side.SERVER);
	}
	
}
