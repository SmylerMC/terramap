package net.smyler.smylib.gui.screen;

import net.smyler.smylib.gui.containers.RootContainer;


/**
 * A {@link Screen} is where everything happens in SmyLib.
 *
 * @author Smyler
 */
public class Screen extends RootContainer {

    final BackgroundOption background;

    float width, height;

    public Screen(BackgroundOption background) {
        this.background = background;
    }

    public boolean shouldPauseGame() {
        return false;
    }

    public void onClosed() { }

    @Override
    public float getX() {
        return 0f;
    }

    @Override
    public float getY() {
        return 0f;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

}
