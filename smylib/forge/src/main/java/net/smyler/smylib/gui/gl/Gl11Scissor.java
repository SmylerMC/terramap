package net.smyler.smylib.gui.gl;

import net.smyler.smylib.game.GameClient;
import org.lwjgl.opengl.GL11;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.*;
import static net.smyler.smylib.SmyLib.getGameClient;

/**
 * A wrapper around OpenGL 1.1's scissor feature.
 * For now, it's easier to rely on this rather than rendering to different frame buffers.
 * When scissoring is enabled, rending only affects the scissoring zone, leaving the surrounding unchanged.
 *
 * @author Smyler
 */
public class Gl11Scissor implements Scissor {

    // We keep the state here to avoid necessary GL calls
    private boolean isScissorEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
    private float x, y, width, height; // This is in the Minecraft screen coordinate space

    private final List<ScissorStackFrame> SCISSOR_POS_STACK = new LinkedList<>();

    @Override
    public void setEnabled(boolean yesNo) {
        if(yesNo && !isScissorEnabled) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        } else if(!yesNo && isScissorEnabled) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
        isScissorEnabled = yesNo;
    }

    @Override
    public boolean isEnabled() {
        return isScissorEnabled;
    }

    /**
     * Set scissoring zone.
     * This does not enable scissoring, it only sets the scissored zone.
     *
     * @see #setEnabled(boolean)
     *
     * @param x         the X position of the scissoring zone in Minecraft screen space
     * @param y         the Y position of the scissoring zone in Minecraft screen space
     * @param width     the width position of the scissoring zone in Minecraft screen space
     * @param height    the height position of the scissoring zone in Minecraft screen space
     */
    @Override
    public void cropScreen(float x, float y, float width, float height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        doScissor();
    }

    @Override
    public void cropSection(float x, float y, float width, float height) {
        float intersectX = max(x, this.x);
        float intersectY = max(y, this.y);
        float intersectWidth = min(width + x, this.width + this.x) - intersectX;
        float intersectHeight = min(height + y, this.height + this.y) - intersectY;
        this.cropScreen(intersectX, intersectY, intersectWidth, intersectHeight);
    }

    /**
     * Saves current scissoring state to an internal stack.
     *
     * @see #pop()
     */
    @Override
    public void push() {
        SCISSOR_POS_STACK.add(new ScissorStackFrame(isEnabled(), x, y, width, height));
    }

    /**
     * Reset scissoring state and zone to what they were last time {@link #push()} was called and remove the corresponding frame from the internal stack.
     */
    @Override
    public void pop() {
        SCISSOR_POS_STACK.remove(SCISSOR_POS_STACK.size() - 1).restore();
        this.doScissor();
    }

    private void doScissor() {
        GameClient game = getGameClient();
        float screenWidth = game.windowWidth();
        float screenHeight = game.windowHeight();
        double scale = game.scaleFactor();
        float y = max(0, min(screenHeight, screenHeight - this.y - height));
        float x = max(0, min(screenWidth, this.x));
        float width = max(0, min(min(this.width + this.x, this.width), screenWidth - this.x));
        float height = max(0, min(this.height, screenHeight - this.y));
        GL11.glScissor((int) round(x * scale), (int) round(y * scale), (int) round(width * scale), (int) round(height * scale));
    }

    private class ScissorStackFrame {
        final boolean enabled;
        final float x, y, width, height;
        ScissorStackFrame(boolean enabled, float x, float y, float width, float height) {
            this.enabled = enabled;
            this.x = x;
            this.y = y;
            this.width = width;
            this.height = height;
        }
        void restore() {
            Gl11Scissor.this.setEnabled(this.enabled);
            Gl11Scissor.this.x = this.x;
            Gl11Scissor.this.y = this.y;
            Gl11Scissor.this.width = this.width;
            Gl11Scissor.this.height = this.height;
        }

    }

}
