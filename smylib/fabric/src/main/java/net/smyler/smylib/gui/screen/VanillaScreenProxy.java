package net.smyler.smylib.gui.screen;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.game.GlfwKeyboard;
import net.smyler.smylib.gui.WrappedGuiGraphics;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.Math.round;
import static java.lang.System.currentTimeMillis;
import static net.smyler.smylib.SmyLib.getGameClient;

public class VanillaScreenProxy extends net.minecraft.client.gui.screens.Screen {
    private final Screen screen;
    private final long[] lastClickTimesMs = new long[getGameClient().mouse().getButtonCount()];
    private final float[] lastClickX = new float[getGameClient().mouse().getButtonCount()];
    private final float[] lastClickY = new float[getGameClient().mouse().getButtonCount()];

    public VanillaScreenProxy(Screen screen) {
        super(Component.literal("SmyLib screen"));
        this.screen = screen;
    }


    @Override
    public void render(GuiGraphics guiGraphics, int x, int y, float partialTicks) {
        GameClient game = getGameClient();
        WrappedGuiGraphics uiDrawContext = (WrappedGuiGraphics) game.guiDrawContext();
        checkState(
                uiDrawContext.vanillaGraphics == guiGraphics,
                "Rendering a screen, but the vanilla graphic context is different"
        );
        float mouseX = game.mouse().x();
        float mouseY = game.mouse().y();

        this.screen.onUpdate(mouseX, mouseY, null);
        this.drawBackground(guiGraphics);
        uiDrawContext.scissor().cropScreen(-1f, -1f, this.width + 1f, height + 1f);
        super.render(guiGraphics, x, y, partialTicks);
        this.screen.draw(uiDrawContext, 0, 0, mouseX, mouseY, true, true, null);
    }

    @Override
    protected void init() {
        super.init();
        this.screen.width = this.width;
        this.screen.height = this.height;
        this.screen.init();
    }

    @Override
    public void onClose() {
        this.screen.onClosed();
        super.onClose();
    }

    @Override
    public boolean isPauseScreen() {
        return this.screen.shouldPauseGame();
    }

    @Override
    public boolean mouseClicked(double x, double y, int button) {
        float xf = (float) x;
        float yf = (float) y;
        boolean hasMoved = this.lastClickX[button] != xf || this.lastClickY[button] != yf;
        long ct = currentTimeMillis();
        long dt = ct - lastClickTimesMs[button];
        if (dt < 500 && !hasMoved) {
            this.screen.onDoubleClick(xf, yf, button, null);
        } else {
            this.screen.onClick(xf, yf, button, null);
        }
        this.lastClickTimesMs[button] = ct;
        this.lastClickX[button] = xf;
        this.lastClickY[button] = yf;
        return super.mouseClicked(x, y, button);
    }

    @Override
    public boolean mouseReleased(double x, double y, int button) {
        this.screen.onMouseReleased((float)x, (float)y, button, null);
        return super.mouseReleased(x, y, button);
    }

    @Override
    public boolean mouseDragged(double x, double y, int button, double dX, double dY) {
        long ct = currentTimeMillis();
        long dt = ct - this.lastClickTimesMs[button];
        this.lastClickTimesMs[button] = ct;
        this.screen.onMouseDragged((float)x, (float)y, (float)dX, (float) dY, button, null, dt);
        return super.mouseDragged(x, y, button, dX, dY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amount) {
        this.screen.onMouseWheeled((float)mouseX, (float)mouseY, (int)round(amount), null);
        return super.mouseScrolled(mouseX, mouseY, amount);
    }

    @Override
    public boolean keyReleased(int i, int j, int k) {
        this.screen.onKeyTyped('\0', GlfwKeyboard.lookupKeyCode(i), null);
        return super.keyReleased(i, j, k);
    }

    @Override
    public boolean charTyped(char c, int i) {
        this.screen.onKeyTyped(c, GlfwKeyboard.lookupKeyCode(i), null);
        return super.charTyped(c, i);
    }

    @Override
    public void mouseMoved(double d, double e) {
        super.mouseMoved(d, e);
    }

    public net.smyler.smylib.gui.screen.Screen getScreen() {
        return this.screen;
    }

    private void drawBackground(GuiGraphics guiGraphics) {
        switch(this.screen.background) {
            case NONE:
                break;
            case DEFAULT:
                this.renderBackground(guiGraphics);
                break;
            case DIRT:
                this.renderDirtBackground(guiGraphics);
                break;
            case OVERLAY:
                guiGraphics.fillGradient(0, 0, this.width, this.height, -1072689136, -804253680);
                break;
        }
    }

}
