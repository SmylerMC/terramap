package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.MouseHandler;

public class Lwjgl3Mouse implements Mouse {

    private final Minecraft vanilla;

    public Lwjgl3Mouse(Minecraft vanilla) {
        this.vanilla = vanilla;
    }

    @Override
    public float x() {
        return (float) this.vanilla.mouseHandler.xpos();
    }

    @Override
    public float y() {
        return (float) this.vanilla.mouseHandler.ypos();
    }

    @Override
    public int getButtonCount() {
        return 3; //FIXME Hard-coded mouse button count
    }

    @Override
    public boolean hasWheel() {
        return false;  //FIXME hard-coded mouse wheel
    }

    @Override
    public boolean isButtonPressed(int button) throws IllegalArgumentException {
        return false;  //FIXME hard-coded mouse is button pressed
    }

    @Override
    public String getButtonName(int button) throws IllegalArgumentException {
        return "Mouse button";  //FIXME hard-coded mouse get button name
    }

    @Override
    public int getButtonByName(String name) throws IllegalArgumentException {
        return 0;  //FIXME hard-coded mouse get button by name
    }

}
