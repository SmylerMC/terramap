package net.smyler.smylib.gui;

public class Lwjgl2DrawContext implements DrawContext {

    private final Scissor scissor = new Gl11Scissor();
    private final GlState glState = new LwjglState();

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

    @Override
    public GlState glState() {
        return this.glState;
    }

}
