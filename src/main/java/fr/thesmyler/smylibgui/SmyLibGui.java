package fr.thesmyler.smylibgui;

import fr.thesmyler.smylibgui.devices.*;
import fr.thesmyler.smylibgui.devices.lwjgl2.Lwjgl2Keyboard;
import fr.thesmyler.smylibgui.devices.lwjgl2.Lwjgl2Mouse;
import fr.thesmyler.smylibgui.devices.lwjgl2.ScaledResolutionGameWindow;
import org.apache.logging.log4j.Logger;

import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.smylibgui.util.Font;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public final class SmyLibGui {

    public static Logger logger;
    public static boolean debug;

    public static final Font DEFAULT_FONT = new Font();

    private static Mouse mouse;
    private static Keyboard keyboard;
    private static GameWindow window;

    public static void init(Logger logger, boolean debug) {
        SmyLibGui.logger = logger;
        SmyLibGui.debug = debug;
        mouse = new Lwjgl2Mouse();
        keyboard = new Lwjgl2Keyboard();
        window = new ScaledResolutionGameWindow();
        MinecraftForge.EVENT_BUS.register(HudScreen.class);
        MinecraftForge.EVENT_BUS.register(window);
    }

    public static Mouse getMouse() {
        return mouse;
    }

    public static Keyboard getKeyboard() {
        return keyboard;
    }

    public static GameWindow getGameWindow() {
        return window;
    }

    public static String getLanguage() {
        return Minecraft.getMinecraft().gameSettings.language;
    }

    private SmyLibGui() {}

}
