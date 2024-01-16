package net.smyler.smylib.game;

import net.smyler.smylib.game.Key;
import net.smyler.smylib.game.Keyboard;

public class Lwjgl2Keyboard implements Keyboard {

    @Override
    public boolean isKeyPressed(Key key) {
        return org.lwjgl.input.Keyboard.isKeyDown(key.code);
    }

    @Override
    public void setRepeatEvents(boolean repeat) {
        org.lwjgl.input.Keyboard.enableRepeatEvents(repeat);
    }

    @Override
    public boolean isRepeatingEvents() {
        return org.lwjgl.input.Keyboard.areRepeatEventsEnabled();
    }

}
