package net.smyler.smylib.gui;

public class Lwjgl2DrawContext implements DrawContext {

    private final Scissor scissor = new Gl11Scissor();

    @Override
    public Scissor scissor() {
        return this.scissor;
    }

}
