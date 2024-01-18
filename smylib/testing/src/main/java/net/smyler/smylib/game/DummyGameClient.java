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
    public float windowWidth() {
        return Float.intBitsToFloat(this.width.get().get());
    }

    public void setWindowWidth(float width) {
        this.width.get().set(Float.floatToIntBits(width));
    }

    @Override
    public float windowHeight() {
        return Float.intBitsToFloat(this.height.get().get());
    }

    public void setWindowHeight(float height) {
        this.width.get().set(Float.floatToIntBits(height));
    }

    @Override
    public int nativeWindowWidth() {
        return round(this.windowWidth() * this.scaleFactor());
    }

    @Override
    public int nativeWindowHeight() {
        return round(this.windowHeight() * this.scaleFactor());
    }

    @Override
    public int scaleFactor() {
        return this.scale.get().get();
    }

    public void setScaleFactor(int scale) {
        this.scale.get().set(scale);
    }

    @Override
    public boolean isMac() {
        return this.isMac.get().get();
    }

    @Override
    public Path gameDirectory() {
        return this.gameDirectory;
    }

    @Override
    public MinecraftServerInfo currentServerInfo() {
        return this.serverInfo;
    }

    @Override
    public Mouse mouse() {
        return this.mouse;
    }

    @Override
    public Keyboard keyboard() {
        return this.keyboard;
    }

    @Override
    public Clipboard clipboard() {
        return this.clipboard;
    }

    @Override
    public SoundSystem soundSystem() {
        return this.soundSystem;
    }

    @Override
    public Translator translator() {
        return this.translator;
    }

    @Override
    public Font defaultFont() {
        return this.font;
    }

    @Override
    public boolean isGlAvailabale() {
        return false;
    }

    public void setIsMac(boolean yesNo) {
        this.isMac.get().set(yesNo);
    }

    public MinecraftServerInfo setServerInfo(MinecraftServerInfo info) {
        return this.serverInfo;
    }

}
