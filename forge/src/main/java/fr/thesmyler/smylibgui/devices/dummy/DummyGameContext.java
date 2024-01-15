package fr.thesmyler.smylibgui.devices.dummy;

import fr.thesmyler.smylibgui.devices.GameContext;
import fr.thesmyler.smylibgui.util.MinecraftServerInfo;
import net.smyler.smylib.threading.ThreadLocal;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.round;

public class DummyGameContext implements GameContext {

    private final ThreadLocal<AtomicInteger> width = new ThreadLocal<>(() -> new AtomicInteger(Float.floatToIntBits(720f)));
    private final ThreadLocal<AtomicInteger> height = new ThreadLocal<>(() -> new AtomicInteger(Float.floatToIntBits(480f)));
    private final ThreadLocal<AtomicInteger> scale = new ThreadLocal<>(() -> new AtomicInteger(1));
    private final ThreadLocal<String> language = new ThreadLocal<>(() -> "en-us");
    private final ThreadLocal<AtomicBoolean> isMac = new ThreadLocal<>(() -> new AtomicBoolean(false));
    private MinecraftServerInfo serverInfo = new MinecraftServerInfo("Dummy Server", "example.com", "Message of the day.", false);

    private final Path gameDirectory;

    public DummyGameContext() {
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
