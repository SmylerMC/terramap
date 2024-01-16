package net.smyler.smylib.game;

import net.smyler.smylib.gui.DummyFont;
import net.smyler.smylib.gui.Font;
import net.smyler.smylib.threading.DefaultThreadLocal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.round;

public class DummyGameClient implements GameClient {

    private final DefaultThreadLocal<AtomicInteger> width = new DefaultThreadLocal<>(() -> new AtomicInteger(Float.floatToIntBits(720f)));
    private final DefaultThreadLocal<AtomicInteger> height = new DefaultThreadLocal<>(() -> new AtomicInteger(Float.floatToIntBits(480f)));
    private final DefaultThreadLocal<AtomicInteger> scale = new DefaultThreadLocal<>(() -> new AtomicInteger(1));
    private final DefaultThreadLocal<String> language = new DefaultThreadLocal<>(() -> "en-us");
    private final DefaultThreadLocal<AtomicBoolean> isMac = new DefaultThreadLocal<>(() -> new AtomicBoolean(false));
    private final MinecraftServerInfo serverInfo = new MinecraftServerInfo("Dummy Server", "example.com", "Message of the day.", false);
    private final Mouse mouse = new DummyMouse();
    private final Keyboard keyboard = new DummyKeyboard();
    private final Clipboard clipboard = new DummyClipboard();
    private final SoundSystem soundSystem = new DummySoundSystem();
    private final Translator translator = new DummyTranslator();
    private final Font font = new DummyFont(1f);

    private final Path gameDirectory;

    public DummyGameClient() {
        try {
            this.gameDirectory = Files.createTempDirectory("smylibgui");
            Files.createDirectories(this.gameDirectory);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public float getWindowWidth() {
        return Float.intBitsToFloat(this.width.get().get());
    }

    public void setWindowWidth(float width) {
        this.width.get().set(Float.floatToIntBits(width));
    }

    @Override
    public float getWindowHeight() {
        return Float.intBitsToFloat(this.height.get().get());
    }

    public void setWindowHeight(float height) {
        this.width.get().set(Float.floatToIntBits(height));
    }

    @Override
    public int getNativeWindowWidth() {
        return round(this.getWindowWidth() * this.getScaleFactor());
    }

    @Override
    public int getNativeWindowHeight() {
        return round(this.getWindowHeight() * this.getScaleFactor());
    }

    @Override
    public int getScaleFactor() {
        return this.scale.get().get();
    }

    public void setScaleFactor(int scale) {
        this.scale.get().set(scale);
    }

    @Override
    public String getLanguage() {
        return this.language.get();
    }

    @Override
    public boolean isMac() {
        return this.isMac.get().get();
    }

    @Override
    public Path getGameDirectory() {
        return this.gameDirectory;
    }

    @Override
    public MinecraftServerInfo getCurrentServerInfo() {
        return this.serverInfo;
    }

    @Override
    public Mouse getMouse() {
        return this.mouse;
    }

    @Override
    public Keyboard getKeyboard() {
        return this.keyboard;
    }

    @Override
    public Clipboard getClipboard() {
        return this.clipboard;
    }

    @Override
    public SoundSystem getSoundSystem() {
        return this.soundSystem;
    }

    @Override
    public Translator getTranslator() {
        return this.translator;
    }

    @Override
    public Font getDefaultFont() {
        return this.font;
    }

    @Override
    public boolean isGlAvailabale() {
        return false;
    }

    public void setIsMac(boolean yesNo) {
        this.isMac.get().set(yesNo);
    }

    public void setLanguage(String lang) {
        this.language.set(lang);
    }

    public MinecraftServerInfo setServerInfo(MinecraftServerInfo info) {
        return this.serverInfo;
    }

}
