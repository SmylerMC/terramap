package net.smyler.smylib.game;

import net.smyler.smylib.game.Clipboard;
import net.minecraft.client.gui.GuiScreen;

public class AwtClipboard implements Clipboard {

    @Override
    public String getContent() {
        return GuiScreen.getClipboardString();
    }

    @Override
    public void setContent(String content) {
        GuiScreen.setClipboardString(content);
    }

}
