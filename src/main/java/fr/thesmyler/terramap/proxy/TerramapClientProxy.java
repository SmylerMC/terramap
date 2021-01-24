package fr.thesmyler.terramap.proxy;

import java.io.File;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.config.TerramapClientPreferences;
import fr.thesmyler.terramap.eventhandlers.ClientTerramapEventHandler;
import fr.thesmyler.terramap.gui.HudScreenHandler;
import fr.thesmyler.terramap.gui.screens.TerraDependencyErrorScreen;
import fr.thesmyler.terramap.gui.widgets.markers.MarkerControllerManager;
import fr.thesmyler.terramap.input.KeyBindings;
import fr.thesmyler.terramap.maps.MapStyleRegistry;
import fr.thesmyler.terramap.maps.UrlRasterTile;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.network.NetworkPlayerInfo;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.world.GameType;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

public class TerramapClientProxy extends TerramapProxy {

	@Override
	public Side getSide() {
		return Side.CLIENT;
	}

	@Override
	public void preInit(FMLPreInitializationEvent event) {
		TerramapMod.logger.debug("Terramap client pre-init");
		if(!TerramapMod.isTerraDependencyValid()) return;
		TerramapNetworkManager.registerHandlers(Side.CLIENT);
		File clientPrefs = new File(event.getModConfigurationDirectory().getAbsoluteFile() + "/" + TerramapClientPreferences.FILENAME);
		TerramapClientPreferences.setFile(clientPrefs);
		TerramapClientPreferences.load();
	}

	@Override
	public void init(FMLInitializationEvent event) {
		TerramapMod.logger.debug("Terramap client init");
		if(!TerramapMod.isTerraDependencyValid()) {
			MinecraftForge.EVENT_BUS.register(new Object() {
				@SubscribeEvent
				public void onGuiScreenInit(InitGuiEvent event) {
					if(event.getGui() instanceof GuiMainMenu)
					Minecraft.getMinecraft().displayGuiScreen(new TerraDependencyErrorScreen());
				}
			});
			return;
		}
		SmyLibGui.init(TerramapMod.logger, false);
		MinecraftForge.EVENT_BUS.register(new ClientTerramapEventHandler());
		KeyBindings.registerBindings();
		UrlRasterTile.registerErrorTexture();
		MarkerControllerManager.registerBuiltInControllers();
		MapStyleRegistry.loadBuiltIns();
		MapStyleRegistry.loadFromOnline(TerramapMod.STYLE_UPDATE_HOSTNAME);
	}

	@Override
	public double getGuiScaleForConfig() {
		double[] acceptableFactors = {0.5d, 1.0d, 2.0d, 4.0d, 8.0d};
		double bestFactor = acceptableFactors[0];
		ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
		double computedFactor = scaledRes.getScaleFactor();
		for(double factor: acceptableFactors)
			if(Math.abs(computedFactor - factor) < Math.abs(bestFactor - computedFactor)) bestFactor = factor;
		return bestFactor;
	}

	@Override
	public void onServerStarting(FMLServerStartingEvent event) {
		// Nothing to do here
	}

	@Override
	public GameType getGameMode(EntityPlayer e) {
		if(e instanceof AbstractClientPlayer) {
			NetworkPlayerInfo i = Minecraft.getMinecraft().getConnection().getPlayerInfo(e.getUniqueID());
			if(i != null) return i.getGameType();
		}
		if(e instanceof EntityPlayerMP) {
			EntityPlayerMP player = (EntityPlayerMP)e;
			return player.interactionManager.getGameType();
		}
		TerramapMod.logger.error("Failed to determine player gamemode.");
		return GameType.SURVIVAL;
	}

	@Override
	public void onConfigChanged(OnConfigChangedEvent event) {
		if (event.getModID().equals(TerramapMod.MODID)) {
			if(TerramapMod.proxy.isClient() && SmyLibGui.getHudScreen() != null) {
				HudScreenHandler.updateMinimap();
			}
		}
	}

}
