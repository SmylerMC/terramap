package fr.thesmyler.smylibgui.devices.dummy;

import fr.thesmyler.smylibgui.devices.GameContext;
import fr.thesmyler.smylibgui.util.ThreadLocal;

import java.util.concurrent.atomic.AtomicInteger;

import static java.lang.Math.round;

public class DummyGameContext implements GameContext {

    private final ThreadLocal<AtomicInteger> width = new ThreadLocal<>(() -> new AtomicInteger(Float.floatToIntBits(720f)));
    private final ThreadLocal<AtomicInteger> height = new ThreadLocal<>(() -> new AtomicInteger(Float.floatToIntBits(480f)));
    private final ThreadLocal<AtomicInteger> scale = new ThreadLocal<>(() -> new AtomicInteger(1));
    private final ThreadLocal<String> language = new ThreadLocal<>(() -> "en-us");

    @Override
    public float getWindowWidth() {
        return Float.intBitsToFloat(this.width.get().get());
    }

    @Override
    public float getWindowHeight() {
        return Float.intBitsToFloat(this.height.get().get());
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

    @Override
    public String getLanguage() {
        return this.language.get();
    }

}
