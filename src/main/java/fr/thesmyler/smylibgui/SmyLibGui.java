package fr.thesmyler.smylibgui;

import org.apache.logging.log4j.Logger;

import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.terramap.TerramapMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public abstract class SmyLibGui {

	public static Logger logger;
	public static boolean debug;
	private static HudScreen hudScreen;
	
	public static final ResourceLocation BUTTON_TEXTURES, OPTIONS_BACKGROUND, STAT_ICONS, ICONS, WIDGET_TEXTURES;
	
	static {
		BUTTON_TEXTURES = ObfuscationReflectionHelper.getPrivateValue(GuiButton.class, null, "BUTTON_TEXTURES");
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
}
