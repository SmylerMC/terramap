package net.smyler.smylib.gui.gl;

import net.smyler.smylib.game.GameClient;
import org.lwjgl.opengl.GL20;

import java.util.LinkedList;
import java.util.List;

import static java.lang.Math.*;
import static net.smyler.smylib.SmyLib.getGameClient;
import static net.smyler.smylib.math.Math.clamp;

/**
 * A wrapper around OpenGL 2.0's scissor feature.
 * For now, it's easier to rely on this rather than rendering to different frame buffers.
 *
 * @author Smyler
 */
public class Gl20Scissor implements Scissor {

    private boolean isScissorEnabled = GL20.glIsEnabled(GL20.GL_SCISSOR_TEST);
    private float x, y, width, height; // This is in the Minecraft screen coordinate space

    private final List<ScissorStackFrame> scissorPosStack = new LinkedList<>();

    @Override
    public void setEnabled(boolean yesNo) {
        if(yesNo && !this.isScissorEnabled) {
            GL20.glEnable(GL20.GL_SCISSOR_TEST);
        } else if(!yesNo && this.isScissorEnabled) {
            GL20.glDisable(GL20.GL_SCISSOR_TEST);
        }
        this.isScissorEnabled = yesNo;
    }

    @Override
    public boolean isEnabled() {
        return this.isScissorEnabled;
    }

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

    @Override
    public void push() {
        this.scissorPosStack.add(new ScissorStackFrame(isEnabled(), x, y, width, height));
    }

    @Override
    public void pop() {
        this.restore(this.scissorPosStack.remove(this.scissorPosStack.size() - 1));
        this.doScissor();
    }

    private void doScissor() {
        GameClient game = getGameClient();
        float screenWidth = game.windowWidth();
        float screenHeight = game.windowHeight();
        double scale = game.scaleFactor();
        float leftX = clamp(this.x, 0f, screenWidth);
        float topY = clamp(screenHeight - this.y - this.height, 0f, screenHeight);
        float rightX = clamp(this.x + this.width, 0f, screenWidth);
        float bottomY = clamp(screenHeight - this.y, 0f, screenHeight);
        float width = rightX - leftX;
        float height = bottomY - topY;
        GL20.glScissor((int) round(leftX * scale), (int) round(topY * scale), (int) round(width * scale), (int) round(height * scale));
    }

    private void restore(ScissorStackFrame frame) {
        this.setEnabled(frame.enabled);
        this.x = frame.x;
        this.y = frame.y;
        this.width = frame.width;
        this.height = frame.height;
    }

    private record ScissorStackFrame(boolean enabled, float x, float y, float width, float height) {
    }

}
