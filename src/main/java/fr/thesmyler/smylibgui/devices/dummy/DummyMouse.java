package fr.thesmyler.smylibgui.devices.dummy;

import fr.thesmyler.smylibgui.devices.Mouse;
import fr.thesmyler.smylibgui.util.ThreadLocal;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DummyMouse implements Mouse {

    private final ThreadLocal<AtomicInteger> xBits = new ThreadLocal<>(AtomicInteger::new);
    private final ThreadLocal<AtomicInteger> yBits = new ThreadLocal<>(AtomicInteger::new);
    private final ThreadLocal<AtomicInteger> buttonCount = new ThreadLocal<>(AtomicInteger::new);
    private final ThreadLocal<AtomicBoolean> hasWheel = new ThreadLocal<>(AtomicBoolean::new);
    private final ThreadLocal<boolean[]> buttons = new ThreadLocal<>(() -> new boolean[this.getButtonCount()]);
    private final ThreadLocal<String[]> buttonNames = new ThreadLocal<>(() -> new String[this.getButtonCount()]);

    @Override
    public float getX() {
        return Float.intBitsToFloat(this.xBits.get().get());
    }

    @Override
    public float getY() {
        return Float.intBitsToFloat(this.yBits.get().get());
    }

    @Override
    public int getButtonCount() {
        return this.buttonCount.get().get();
    }

    @Override
    public boolean hasWheel() {
        return this.hasWheel.get().get();
    }

    @Override
    public boolean isButtonPressed(int button) throws IllegalArgumentException {
        if (button < 0 || button > this.getButtonCount()) throw new IllegalArgumentException("Invalid button id: " + button);
        return this.buttons.get()[button];
    }

    @Override
    public String getButtonName(int button) throws IllegalArgumentException {
        if (button < 0 || button > this.getButtonCount()) throw new IllegalArgumentException("Invalid button id: " + button);
        return this.buttonNames.get()[button];
    }

    @Override
    public int getButtonByName(String name) throws IllegalArgumentException {
        if (name == null) throw new NullPointerException("Button name cannot be null");
        String[] names = this.buttonNames.get();
        for (int i = 0; i < names.length; i++) {
            if (name.equals(names[i])) return i;
        }
        throw new IllegalArgumentException("Button " + name + " does not exist");
    }

}
