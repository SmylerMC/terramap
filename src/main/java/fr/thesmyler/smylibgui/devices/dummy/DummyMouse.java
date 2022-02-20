package fr.thesmyler.smylibgui.devices.dummy;

import fr.thesmyler.smylibgui.devices.Mouse;
import fr.thesmyler.smylibgui.util.ThreadLocal;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class DummyMouse implements Mouse {

    private final ThreadLocal<AtomicInteger> xBits = new ThreadLocal<>(AtomicInteger::new);
    private final ThreadLocal<AtomicInteger> yBits = new ThreadLocal<>(AtomicInteger::new);
    private final ThreadLocal<AtomicInteger> buttonCount = new ThreadLocal<>(AtomicInteger::new);
    private final ThreadLocal<AtomicBoolean> hasWheel = new ThreadLocal<>(AtomicBoolean::new);
    private final ThreadLocal<boolean[]> buttons = new ThreadLocal<>(() -> new boolean[this.getButtonCount()]);
    private final ThreadLocal<String[]> buttonNames = new ThreadLocal<>(() -> new String[this.getButtonCount()]);
    private final ThreadLocal<AtomicLong> doubleClickDelay = new ThreadLocal<>(() -> new AtomicLong(200));

    @Override
    public float getX() {
        return Float.intBitsToFloat(this.xBits.get().get());
    }

    public void setX(float x) {
        this.xBits.get().set(Float.floatToIntBits(x));
    }

    @Override
    public float getY() {
        return Float.intBitsToFloat(this.yBits.get().get());
    }

    public void setY(float y) {
        this.yBits.get().set(Float.floatToIntBits(y));
    }

    @Override
    public int getButtonCount() {
        return this.buttonCount.get().get();
    }

    public void setButtonCount(int count) {
        this.buttonCount.get().set(count);
        this.buttons.set(Arrays.copyOf(this.buttons.get(), count));
        this.buttonNames.set(Arrays.copyOf(this.buttonNames.get(), count));
    }

    @Override
    public boolean hasWheel() {
        return this.hasWheel.get().get();
    }

    public void setHasWheel(boolean hasWheel) {
        this.hasWheel.get().set(hasWheel);
    }

    @Override
    public boolean isButtonPressed(int button) throws IllegalArgumentException {
        if (button < 0 || button > this.getButtonCount()) throw new IllegalArgumentException("Invalid button id: " + button);
        return this.buttons.get()[button];
    }

    @Override
    public long getDoubleClickDelay() {
        return this.doubleClickDelay.get().get();
    }

    @Override
    public void setDoubleClickDelay(long delay) {
        this.doubleClickDelay.get().set(delay);
    }

    public void setButtonPressed(int button, boolean yesNo) {
        if (button < 0 || button > this.getButtonCount()) throw new IllegalArgumentException("Invalid button id: " + button);
        this.buttons.get()[button] = yesNo;
    }

    @Override
    public String getButtonName(int button) throws IllegalArgumentException {
        if (button < 0 || button > this.getButtonCount()) throw new IllegalArgumentException("Invalid button id: " + button);
        return this.buttonNames.get()[button];
    }

    public void setButtonName(int button, String name) {
        if (button < 0 || button > this.getButtonCount()) throw new IllegalArgumentException("Invalid button id: " + button);
        this.buttonNames.get()[button] = name;
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
