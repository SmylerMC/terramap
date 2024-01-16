package net.smyler.smylib.game;

import net.smyler.smylib.threading.DefaultThreadLocal;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

public class DummyMouse implements Mouse {

    private final DefaultThreadLocal<AtomicInteger> xBits = new DefaultThreadLocal<>(AtomicInteger::new);
    private final DefaultThreadLocal<AtomicInteger> yBits = new DefaultThreadLocal<>(AtomicInteger::new);
    private final DefaultThreadLocal<AtomicInteger> buttonCount = new DefaultThreadLocal<>(AtomicInteger::new);
    private final DefaultThreadLocal<AtomicBoolean> hasWheel = new DefaultThreadLocal<>(AtomicBoolean::new);
    private final DefaultThreadLocal<boolean[]> buttons = new DefaultThreadLocal<>(() -> new boolean[this.getButtonCount()]);
    private final DefaultThreadLocal<String[]> buttonNames = new DefaultThreadLocal<>(() -> new String[this.getButtonCount()]);

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
