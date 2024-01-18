package net.smyler.smylib.gui;

public class DummyDrawContext implements DrawContext {

    private final Scissor scissor = new DummyScissor();
    private final GlState state = new DummyGlState();

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

    @Override
    public GlState glState() {
        return this.state;
    }

}
