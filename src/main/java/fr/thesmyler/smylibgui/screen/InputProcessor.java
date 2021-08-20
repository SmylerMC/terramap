package fr.thesmyler.smylibgui.screen;

import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.terramap.config.TerramapConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;

class InputProcessor {

    private final WidgetContainer container;

    private int touchContactsCount = 0;
    private final boolean[] mouseButtonsPressed = new boolean[Mouse.getButtonCount()];
    private final float[] lastClickX = new float[Mouse.getButtonCount()];
    private final float[] lastClickY = new float[Mouse.getButtonCount()];
    private final long[] lastClickTime = new long[Mouse.getButtonCount()];
    private int lastClickedButton = -1;
    private final Minecraft mc = Minecraft.getMinecraft();

    public InputProcessor(WidgetContainer container) {
        this.container = container;
    }

    public void processMouseEvent() {
        // We override that so we can make float math instead of being stuck with ints
        ScaledResolution resolution = new ScaledResolution(this.mc);
        float mouseX = (float)Mouse.getX() / resolution.getScaleFactor();
        float mouseY = resolution.getScaledHeight() - (float)Mouse.getY() / resolution.getScaleFactor() - 1;
        int mouseButton = Mouse.getEventButton();
        long ctime = System.currentTimeMillis();

        if(Mouse.getEventButtonState()) {
            if (this.mc.gameSettings.touchscreen && this.touchContactsCount++ > 0) return;
            this.mouseButtonsPressed[mouseButton] = true;
            if(ctime - this.lastClickTime[mouseButton] <= TerramapConfig.CLIENT.doubleClickDelay && this.lastClickX[mouseButton] == mouseX && this.lastClickY[mouseButton] == mouseY) {
                this.container.onDoubleClick(mouseX, mouseY, mouseButton, null);
            } else {
                this.container.onClick(mouseX, mouseY, mouseButton, null);
            }
            this.lastClickedButton = mouseButton;
            this.lastClickTime[mouseButton] = ctime;
            this.lastClickX[mouseButton] = mouseX;
            this.lastClickY[mouseButton] = mouseY;
        } else if(mouseButton >= 0) {
            if(this.mc.gameSettings.touchscreen && --this.touchContactsCount > 0) return;
            this.mouseButtonsPressed[mouseButton] = false;
            this.lastClickedButton = -1;
            this.container.onMouseReleased(mouseX, mouseY, mouseButton, null);
        } else if(this.lastClickedButton >= 0 && this.mouseButtonsPressed[this.lastClickedButton]) {
            float dX = mouseX - this.lastClickX[this.lastClickedButton];
            float dY = mouseY - this.lastClickY[this.lastClickedButton];
            long dt = ctime - this.lastClickTime[this.lastClickedButton];
            this.lastClickX[this.lastClickedButton] = mouseX;
            this.lastClickY[this.lastClickedButton] = mouseY;
            this.lastClickTime[this.lastClickedButton] = ctime;
            this.container.onMouseDragged(mouseX, mouseY, dX, dY, this.lastClickedButton, null, dt);
        }

        int scroll = Mouse.getDWheel();
        if(scroll != 0) this.container.onMouseWheeled(mouseX, mouseY, scroll, null);
    }

}
