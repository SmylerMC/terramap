package fr.thesmyler.smylibgui.devices.lwjgl2;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.devices.Mouse;

public class Lwjgl2Mouse implements Mouse {

    private long doubleClickDelay = 200;

    @Override
    public float getX() {
        int x = org.lwjgl.input.Mouse.getX();
        return (float) x / SmyLibGui.getGameContext().getScaleFactor();
    }

    @Override
    public float getY() {
        int y = org.lwjgl.input.Mouse.getY();
        int scale = SmyLibGui.getGameContext().getScaleFactor();
        float height = SmyLibGui.getGameContext().getWindowHeight();
        return height - (float) y / scale - 1;
    }

    @Override
    public int getButtonCount() {
        return org.lwjgl.input.Mouse.getButtonCount();
    }

    @Override
    public boolean hasWheel() {
        return org.lwjgl.input.Mouse.hasWheel();
    }

    @Override
    public boolean isButtonPressed(int button) {
        return org.lwjgl.input.Mouse.isButtonDown(button);
    }

    @Override
    public long getDoubleClickDelay() {
        return this.doubleClickDelay;
    }

    @Override
    public void setDoubleClickDelay(long delay) {
        this.doubleClickDelay = delay;
    }

    @Override
    public String getButtonName(int button) throws IllegalArgumentException {
        return org.lwjgl.input.Mouse.getButtonName(button);
    }

    @Override
    public int getButtonByName(String name) throws IllegalArgumentException {
        if (name == null) throw new NullPointerException("Button name cannot be null");
        int index = org.lwjgl.input.Mouse.getButtonIndex(name);
        if (index < 0) throw new IllegalArgumentException("Button is not recognised");
        return index;
    }

}
