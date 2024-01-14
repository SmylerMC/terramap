package fr.thesmyler.smylibgui.screen;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.RootContainer;
import fr.thesmyler.smylibgui.devices.Key;
import fr.thesmyler.smylibgui.util.Scissor;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import net.minecraft.client.gui.GuiScreen;

import static fr.thesmyler.smylibgui.SmyLibGui.getMouse;

/**
 * A {@link Screen} is where everything happens in SmyLibGui.
 * This class extends from Minecraft's {@link GuiScreen} and can be used as such.
 * <br>
 * To use this class, add widgets to your screen by retrieving its {@link WidgetContainer} using {@link #getContent()}.
 *
 * @author SmylerMC
 */
public class Screen extends GuiScreen {

    private final WidgetContainer container = new Container();


    private final InputProcessor processor;

    private final BackgroundOption background;

    public Screen(BackgroundOption background) {
        this.background = background;
        this.processor = new InputProcessor(this.container);
    }

    public WidgetContainer getContent() {
        return this.container;
    }

    @Override
    public void drawScreen(int nopX, int nopY, float partialTicks) {
        Scissor.push();
        // We need to make sure everything is visible
        Scissor.scissor(-1f, -1f, this.width + 1f, this.height + 1f);
        this.drawBackground();
        super.drawScreen(nopX, nopY, partialTicks);
        float mouseX = getMouse().getX();
        float mouseY = getMouse().getY();
        this.onUpdate();
        this.container.onUpdate(mouseX, mouseY, null);
        this.container.draw(0, 0, mouseX, mouseY, true, true, null);
        Scissor.pop();
    }

    public void onUpdate() {}

    @Override
    public void initGui() {
        super.initGui();
        this.container.init();
    }

    @Override
    public void handleMouseInput() {
        this.processor.processMouseEvent();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        this.container.onKeyTyped(typedChar, Key.fromCode(keyCode), null);
    }


    @Deprecated @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) {
        this.warnNotToCall();
        this.container.onClick(mouseX, mouseY, mouseButton, null);
    }

    @Deprecated @Override
    protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
        this.warnNotToCall();
        this.container.onMouseReleased(mouseX, mouseY, mouseButton, null);
    }

    @Deprecated @Override
    protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
        this.warnNotToCall();
    }

    private void warnNotToCall() {
        SmyLibGui.getLogger().warn("Something called SmyLibGui's ScreenGui native vanilla input handling methods. This could cause weird behavior, call the IWidget floating point variants instead!");
        StackTraceElement[] lines = Thread.currentThread().getStackTrace();
        for(int i=1; i<lines.length; i++) SmyLibGui.getLogger().warn(lines[i]);
    }

    private void drawBackground() {
        switch(this.background) {
            case NONE:
                break;
            case DEFAULT:
                this.drawDefaultBackground();
                break;
            case DIRT:
                this.drawBackground(0);
                break;
            case OVERLAY:
                this.drawGradientRect(0, 0, this.width, this.height, -1072689136, -804253680);
                break;
        }
    }

    private class Container extends RootContainer {

        public Container() {
            super(Screen.this);
        }

        @Override
        public float getX() {
            return 0;
        }

        @Override
        public float getY() {
            return 0;
        }

        @Override
        public float getWidth() {
            return Screen.this.width;
        }

        @Override
        public float getHeight() {
            return Screen.this.height;
        }

    }

}
