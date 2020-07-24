package fr.thesmyler.smylibgui;

import org.apache.logging.log4j.Logger;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public abstract class SmyLibGui {

	public static Logger logger;
	public static boolean debug;
	
	public static final ResourceLocation BUTTON_TEXTURES, OPTIONS_BACKGROUND, STAT_ICONS, ICONS;
	
	static {
		BUTTON_TEXTURES = ObfuscationReflectionHelper.getPrivateValue(GuiButton.class, null, "BUTTON_TEXTURES");
		OPTIONS_BACKGROUND = Gui.OPTIONS_BACKGROUND;
		STAT_ICONS = Gui.STAT_ICONS;
		ICONS = Gui.ICONS;
	}
	
	public static void init(Logger logger, boolean debug) {
		SmyLibGui.logger = logger;
		SmyLibGui.debug = debug;
	}
}
