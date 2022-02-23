package fr.thesmyler.smylibgui;

import fr.thesmyler.smylibgui.devices.*;
import fr.thesmyler.smylibgui.devices.dummy.*;
import fr.thesmyler.smylibgui.devices.lwjgl2.*;
import fr.thesmyler.smylibgui.screen.TestScreen;
import fr.thesmyler.smylibgui.util.DummyFont;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import fr.thesmyler.smylibgui.screen.HudScreen;
import fr.thesmyler.smylibgui.util.Font;
import net.minecraftforge.common.MinecraftForge;

import static fr.thesmyler.smylibgui.SmyLibGuiContext.JUNIT;

public final class SmyLibGui {

    private static boolean init = false;
    private static SmyLibGuiContext context;
    private static Logger logger;
    private static Mouse mouse;
    private static Keyboard keyboard;
    private static GameContext gameContext;
    private static Font defaultFont;
    private static SoundSystem soundSystem;
    private static Translator translator;

    public static void init(Logger logger, SmyLibGuiContext context) {
        if (init && context != getContext()) throw new IllegalStateException("SmyLibGui has already been initialized with a different state");
        if (logger == null) logger = LogManager.getLogger("SmyLibGui");
        SmyLibGui.logger = logger;
        SmyLibGui.context = context;
        init = true;
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
    }

    private static void initLwjgl2() {
        mouse = new Lwjgl2Mouse();
        keyboard = new Lwjgl2Keyboard();
        gameContext = new MinecraftGameContext();
        defaultFont = new Font();
        soundSystem = new MinecraftSoundSystem();
        translator = new I18nTranslator();
        MinecraftForge.EVENT_BUS.register(HudScreen.class);
        MinecraftForge.EVENT_BUS.register(gameContext);
    }

    private static void initLwjgl2TestScreen() {
        initLwjgl2();
        MinecraftForge.EVENT_BUS.register(TestScreen.class);
    }

    private static void initJunit() {
        mouse = new DummyMouse();
        getTestMouse().setButtonCount(3);
        getTestMouse().setHasWheel(true);
        keyboard = new DummyKeyboard();
        gameContext = new DummyGameContext();
        defaultFont = new DummyFont(1f);
        soundSystem = new DummySoundSystem();
        translator = new DummyTranslator();
    }

    public static Mouse getMouse() {
        checkInit();
        return mouse;
    }

    public static Keyboard getKeyboard() {
        checkInit();
        return keyboard;
    }

    public static GameContext getGameContext() {
        checkInit();
        return gameContext;
    }

    public static Font getDefaultFont() {
        return defaultFont;
    }

    public static SoundSystem getSoundSystem() {
        checkInit();
        return soundSystem;
    }

    public static Logger getLogger() {
        checkInit();
        return logger;
    }

    public static Translator getTranslator() {
        checkInit();
        return translator;
    }

    public static SmyLibGuiContext getContext() {
        checkInit();
        return context;
    }

    public static DummyMouse getTestMouse() {
        checkInitAndTest();
        return (DummyMouse) mouse;
    }

    public static DummyKeyboard getTestKeyboard() {
        checkInitAndTest();
        return (DummyKeyboard) keyboard;
    }

    public static DummyGameContext getTestGameContext() {
        checkInitAndTest();
        return (DummyGameContext) gameContext;
    }

    private SmyLibGui() {
        throw new IllegalStateException();
    }

    private static void checkInit() {
        if (!init) throw new IllegalStateException("SmyLibGui has not been initialized.");
    }

    private static void checkInitAndTest() {
        checkInit();
        if (context != JUNIT) throw new IllegalStateException("SmyLibGui is not running in a test context.");
    }

}
