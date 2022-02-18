package fr.thesmyler.smylibgui;

import fr.thesmyler.smylibgui.devices.*;
import fr.thesmyler.smylibgui.devices.dummy.DummyGameWindow;
import fr.thesmyler.smylibgui.devices.dummy.DummyKeyboard;
import fr.thesmyler.smylibgui.devices.dummy.DummyMouse;
import fr.thesmyler.smylibgui.devices.lwjgl2.Lwjgl2Keyboard;
import fr.thesmyler.smylibgui.devices.lwjgl2.Lwjgl2Mouse;
import fr.thesmyler.smylibgui.devices.lwjgl2.ScaledResolutionGameWindow;
import fr.thesmyler.smylibgui.screen.TestScreen;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.smylibgui.util.Font;
import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;

public final class SmyLibGui {

    public static final Font DEFAULT_FONT = new Font();

    private static boolean init = false;
    private static SmyLibGuiContext context;
    private static Logger logger;
    private static Mouse mouse;
    private static Keyboard keyboard;
    private static GameWindow window;

    public static void init(Logger logger, SmyLibGuiContext context) {
        if (init && context != getContext()) throw new IllegalStateException("SmyLibGui has already been initialized with a different state");
        if (logger == null) logger = LogManager.getLogger("SmyLibGui");
        SmyLibGui.logger = logger;
        SmyLibGui.context = context;
        switch (context) {
            case LWJGL2:
                initLwjgl2();
                break;
            case JUNIT:
                initJunit();
                break;
            case LWJGL2_TEST_SCREEN:
                initLwjgl2TestScreen();
                break;
        }
        init = true;
    }

    private static void initLwjgl2() {
        mouse = new Lwjgl2Mouse();
        keyboard = new Lwjgl2Keyboard();
        window = new ScaledResolutionGameWindow();
        MinecraftForge.EVENT_BUS.register(HudScreen.class);
        MinecraftForge.EVENT_BUS.register(window);
    }

    private static void initLwjgl2TestScreen() {
        initLwjgl2();
        MinecraftForge.EVENT_BUS.register(TestScreen.class);
    }

    private static void initJunit() {
        mouse = new DummyMouse();
        keyboard = new DummyKeyboard();
        window = new DummyGameWindow();
    }

    public static Mouse getMouse() {
        checkInit();
        return mouse;
    }

    public static Keyboard getKeyboard() {
        checkInit();
        return keyboard;
    }

    public static GameWindow getGameWindow() {
        checkInit();
        return window;
    }

    public static Logger getLogger() {
        checkInit();
        return logger;
    }

    public static SmyLibGuiContext getContext() {
        checkInit();
        return context;
    }

    public static String getLanguage() {
        return Minecraft.getMinecraft().gameSettings.language;
    }

    private SmyLibGui() {
        throw new IllegalStateException();
    }

    private static void checkInit() {
        if (!init) throw new IllegalStateException("SmyLibGui has not been initialized");
    }

}
