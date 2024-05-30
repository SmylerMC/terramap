package net.smyler.smylib.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.game.Key;
import net.smyler.smylib.game.Mouse;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.gui.Scissor;
import org.jetbrains.annotations.NotNull;

import static net.smyler.smylib.SmyLib.getGameClient;

public class GuiScreenProxy extends GuiScreen {

    private final Screen screen;
    final InputProcessor processor;

    public GuiScreenProxy(Screen screen) {
        this.screen = screen;
        this.processor = new InputProcessor(screen);
    }

    @Override
    public void initGui() {
        super.initGui();
        this.screen.init();
    }

    @Override
    public void handleMouseInput() {
        this.processor.processMouseEvent();
    }

    @Override
    protected void keyTyped(char typedChar, int keyCode) {
        this.screen.onKeyTyped(typedChar, Key.fromCode(keyCode), null);
    }

    @Override
    public void drawScreen(int nopX, int nopY, float partialTicks) {
        GameClient client = getGameClient();
        Mouse mouse = client.mouse();
        DrawContext context = client.guiDrawContext();
        Scissor scissor = context.scissor();
        scissor.push();
        // We need to make sure everything is visible
        scissor.cropScreen(-1f, -1f, this.width + 1f, height + 1f);
        this.drawBackground();
        super.drawScreen(nopX, nopY, partialTicks);
        float mouseX = mouse.x();
        float mouseY = mouse.y();
        this.screen.onUpdate(mouseX, mouseY, null);
        this.screen.draw(context, 0, 0, mouseX, mouseY, true, true, null);
        scissor.pop();
    }

    @Override
    public void setWorldAndResolution(@NotNull Minecraft minecraft, int width, int height) {
        this.screen.width = width;
        this.screen.height = height;
        super.setWorldAndResolution(minecraft, width, height);
    }

    @Override
    public void setGuiSize(int width, int height) {
        super.setGuiSize(width, height);
        this.screen.width = width;
        this.screen.height = height;
    }

    @Override
    public boolean doesGuiPauseGame() {
        return this.screen.shouldPauseGame();
    }

    @Override
    public void onGuiClosed() {
        this.screen.onClosed();
    }

    private void drawBackground() {
        switch(this.screen.background) {
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

    public Screen getScreen() {
        return this.screen;
    }

}
