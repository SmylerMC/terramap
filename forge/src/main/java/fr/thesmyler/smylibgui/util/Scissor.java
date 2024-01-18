package fr.thesmyler.smylibgui.util;

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
public class Scissor {

    // We keep the state here to avoid necessary GL calls
    private static boolean isScissorEnabled = GL11.glIsEnabled(GL11.GL_SCISSOR_TEST);
    private static float x, y, width, height; // This is in the Minecraft screen coordinate space

    private static final List<ScissorStackFrame> SCISSOR_POS_STACK = new LinkedList<>();

    /**
     * Enable or disable scissoring
     *
     * @param yesNo whether to enable scissoring
     */
    public static void setScissorState(boolean yesNo) {
        if(yesNo && !isScissorEnabled) {
            GL11.glEnable(GL11.GL_SCISSOR_TEST);
        } else if(!yesNo && isScissorEnabled) {
            GL11.glDisable(GL11.GL_SCISSOR_TEST);
        }
        isScissorEnabled = yesNo;
    }

    /**
     * @return whether scissor is enabled in OpenGL
     */
    private static boolean isScissorEnabled() {
        return isScissorEnabled;
    }

    /**
     * Set scissoring zone.
     * This does not enable scissoring, it only sets the scissored zone.
     *
     * @see #setScissorState(boolean)
     *
     * @param x         the X position of the scissoring zone in Minecraft screen space
     * @param y         the Y position of the scissoring zone in Minecraft screen space
     * @param width     the width position of the scissoring zone in Minecraft screen space
     * @param height    the height position of the scissoring zone in Minecraft screen space
     */
    public static void scissor(float x, float y, float width, float height) {
        Scissor.x = x;
        Scissor.y = y;
        Scissor.width = width;
        Scissor.height = height;
        doScissor();
    }

    public static void scissorIntersecting(float x, float y, float width, float height) {
        float intersectX = max(x, Scissor.x);
        float intersectY = max(y, Scissor.y);
        float intersectWidth = min(width + x, Scissor.width + Scissor.x) - intersectX;
        float intersectHeight = min(height + y, Scissor.height + Scissor.y) - intersectY;
        scissor(intersectX, intersectY, intersectWidth, intersectHeight);
    }

    /**
     * Saves current scissoring state to an internal stack.
     *
     * @see #pop()
     */
    public static void push() {
        SCISSOR_POS_STACK.add(new ScissorStackFrame(isScissorEnabled(), x, y, width, height));
    }

    /**
     * Reset scissoring state and zone to what they were last time {@link #push()} was called and remove the corresponding frame from the internal stack.
     */
    public static void pop() {
        SCISSOR_POS_STACK.remove(SCISSOR_POS_STACK.size() - 1).restore();
        doScissor();
    }

    private static void doScissor() {
        GameClient game = getGameClient();
        float screenWidth = game.windowWidth();
        float screenHeight = game.windowHeight();
        double scale = game.scaleFactor();
        float y = max(0, min(screenHeight, screenHeight - Scissor.y - height));
        float x = max(0, min(screenWidth, Scissor.x));
        float width = max(0, min(min(Scissor.width + Scissor.x, Scissor.width), screenWidth - Scissor.x));
        float height = max(0, min(Scissor.height, screenHeight - Scissor.y));
        GL11.glScissor((int) round(x * scale), (int) round(y * scale), (int) round(width * scale), (int) round(height * scale));
    }

    private static class ScissorStackFrame {
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
            Scissor.setScissorState(this.enabled);
            Scissor.x = this.x;
            Scissor.y = this.y;
            Scissor.width = this.width;
            Scissor.height = this.height;
        }

    }

}
