package fr.thesmyler.smylibgui;

import org.apache.logging.log4j.Logger;

import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.terramap.TerramapMod;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public abstract class SmyLibGui {

	public static Logger logger;
	public static boolean debug;
	private static HudScreen hudScreen;
	
	public static final ResourceLocation BUTTON_TEXTURES, OPTIONS_BACKGROUND, STAT_ICONS, ICONS, WIDGET_TEXTURES;
	public static final Font DEFAULT_FONT = new Font();
	
	static {
		BUTTON_TEXTURES = ObfuscationReflectionHelper.getPrivateValue(GuiButton.class, null, "field_146122_a");
		OPTIONS_BACKGROUND = Gui.OPTIONS_BACKGROUND;
		STAT_ICONS = Gui.STAT_ICONS;
		ICONS = Gui.ICONS;
		WIDGET_TEXTURES = new ResourceLocation(TerramapMod.MODID, "textures/gui/widgets.png");
	}
	
	public static void init(Logger logger, boolean debug) {
		SmyLibGui.logger = logger;
		SmyLibGui.debug = debug;
		hudScreen = new HudScreen();
		MinecraftForge.EVENT_BUS.register(hudScreen);
	}
	
	public static HudScreen getHudScreen() {
		return hudScreen;
	}
	
	public static double getMinecraftGuiScale() {
		double[] acceptableFactors = {0.5d, 1.0d, 2.0d, 4.0d, 8.0d};
		double bestFactor = acceptableFactors[0];
		ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
		double computedFactor = scaledRes.getScaleFactor();
		for(double factor: acceptableFactors)
			if(Math.abs(computedFactor - factor) < Math.abs(bestFactor - computedFactor)) bestFactor = factor;
		return bestFactor;
	}
	
	public static String getLanguage() {
		return Minecraft.getMinecraft().gameSettings.language;
	}
}
