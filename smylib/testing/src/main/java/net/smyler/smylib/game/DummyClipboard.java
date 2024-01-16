package net.smyler.smylib.game;

public class DummyClipboard implements Clipboard {

    private String content = "";

    @Override
    public String getContent() {
        return this.content;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }

}
