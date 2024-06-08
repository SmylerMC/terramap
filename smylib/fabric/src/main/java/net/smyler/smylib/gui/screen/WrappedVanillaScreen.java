package net.smyler.smylib.gui.screen;

import net.minecraft.client.gui.screens.Screen;

public class WrappedVanillaScreen extends VanillaScreen {

    private final Screen wrapped;

    public WrappedVanillaScreen(Screen wrapped) {
        super(BackgroundOption.DEFAULT);
        this.wrapped = wrapped;
    }

    public Screen getWrapped() {
        return this.wrapped;
    }

}
