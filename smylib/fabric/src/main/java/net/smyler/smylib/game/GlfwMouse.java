package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import org.lwjgl.glfw.GLFW;

public class GlfwMouse implements Mouse {

    private final Minecraft vanilla;

    public GlfwMouse(Minecraft vanilla) {
        this.vanilla = vanilla;
    }

    @Override
    public float x() {
        double scale = (double)this.vanilla.getWindow().getGuiScaledWidth() / (double)this.vanilla.getWindow().getScreenWidth();
        return (float) (this.vanilla.mouseHandler.xpos() * scale);
    }

    @Override
    public float y() {
        double scale = (double)this.vanilla.getWindow().getGuiScaledHeight() / (double)this.vanilla.getWindow().getScreenHeight();
        return (float) (this.vanilla.mouseHandler.ypos() * scale);
    }

    @Override
    public int getButtonCount() {  //TODO this should probably be renamed
        // Max button count supported by GLFW
        return 8;
    }

    @Override
    public boolean hasWheel() {  //TODO this is useless and should probably be removed
        return true;
    }

    @Override
    public boolean isButtonPressed(int button) throws IllegalArgumentException {
        return GLFW.glfwGetMouseButton(this.vanilla.getWindow().getWindow(), button) == GLFW.GLFW_PRESS;
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
