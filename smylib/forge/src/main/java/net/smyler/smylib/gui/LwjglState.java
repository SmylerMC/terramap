package net.smyler.smylib.gui;

import net.minecraft.client.renderer.GlStateManager;

public class LwjglState implements GlState {
    @Override
    public void enableAlpha() {
        GlStateManager.enableAlpha();
    }

    @Override
    public void disableAlpha() {
        GlStateManager.disableAlpha();
    }

}
