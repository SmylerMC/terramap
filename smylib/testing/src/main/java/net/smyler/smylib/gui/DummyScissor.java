package net.smyler.smylib.gui;

public class DummyScissor implements Scissor {
    @Override
    public void setEnabled(boolean yesNo) {

    }

    @Override
    public boolean isEnabled() {
        return false;
    }

    @Override
    public void cropScreen(float x, float y, float width, float height) {

    }

    @Override
    public void cropSection(float x, float y, float width, float height) {

    }

    @Override
    public void push() {

    }

    @Override
    public void pop() {

    }
}
