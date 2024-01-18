package net.smyler.smylib.gui;

public class DummyDrawContext implements DrawContext {

    private final Scissor scissor = new DummyScissor();

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

}
