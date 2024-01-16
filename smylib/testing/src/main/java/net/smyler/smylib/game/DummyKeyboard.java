package net.smyler.smylib.game;

import net.smyler.smylib.threading.DefaultThreadLocal;

import java.util.concurrent.atomic.AtomicBoolean;

public class DummyKeyboard implements Keyboard {

    private final DefaultThreadLocal<boolean[]> states = new DefaultThreadLocal<>(() -> new boolean[Key.values().length]);
    private final DefaultThreadLocal<AtomicBoolean> repeats = new DefaultThreadLocal<>(AtomicBoolean::new);

    @Override
    public boolean isKeyPressed(Key key) {
        return this.states.get()[key.ordinal()];
    }

    public void setKeyPressed(Key key, boolean yesNo) {
        this.states.get()[key.ordinal()] = yesNo;
    }

    @Override
    public void setRepeatEvents(boolean repeat) {
        this.repeats.get().set(repeat);
    }

    @Override
    public boolean isRepeatingEvents() {
        return this.repeats.get().get();
    }

}
