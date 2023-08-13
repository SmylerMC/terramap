package fr.thesmyler.smylibgui.devices.dummy;

import fr.thesmyler.smylibgui.devices.Clipboard;

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
