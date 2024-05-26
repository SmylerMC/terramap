package net.smyler.smylib.gui.screen;

import net.minecraft.client.gui.GuiScreen;

public class WrappedVanillaScreen extends VanillaScreen {

    private final GuiScreen wrapped;

    public WrappedVanillaScreen(GuiScreen wrapped) {
        super(BackgroundOption.DEFAULT);
        this.wrapped = wrapped;
    }

    public GuiScreen getWrapped() {
        return wrapped;
    }

}
