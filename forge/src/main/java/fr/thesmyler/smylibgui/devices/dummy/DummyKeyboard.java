package fr.thesmyler.smylibgui.devices.dummy;

import fr.thesmyler.smylibgui.devices.Key;
import fr.thesmyler.smylibgui.devices.Keyboard;
import net.smyler.smylib.threading.ThreadLocal;

import java.util.concurrent.atomic.AtomicBoolean;

public class DummyKeyboard implements Keyboard {

    private final ThreadLocal<boolean[]> states = new ThreadLocal<>(() -> new boolean[Key.values().length]);
    private final ThreadLocal<AtomicBoolean> repeats = new ThreadLocal<>(AtomicBoolean::new);

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
