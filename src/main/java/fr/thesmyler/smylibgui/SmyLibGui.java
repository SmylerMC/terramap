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
import org.lwjgl.input.Mouse;

public final class SmyLibGui {

    public static Logger logger;
    public static boolean debug;

    public static final Font DEFAULT_FONT = new Font();

    private SmyLibGui() {}

    public static void init(Logger logger, boolean debug) {
        SmyLibGui.logger = logger;
        SmyLibGui.debug = debug;
        MinecraftForge.EVENT_BUS.register(HudScreen.class);
    }

    public static double getMinecraftGuiScale() {
        ScaledResolution scaledRes = new ScaledResolution(Minecraft.getMinecraft());
        return scaledRes.getScaleFactor();
    }

    public static String getLanguage() {
        return Minecraft.getMinecraft().gameSettings.language;
    }

    public static int getMouseButtonCount() {
        return debug ? 3: Mouse.getButtonCount();
    }

}
