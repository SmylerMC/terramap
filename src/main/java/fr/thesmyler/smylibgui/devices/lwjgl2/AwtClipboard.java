package fr.thesmyler.smylibgui.devices.lwjgl2;

import fr.thesmyler.smylibgui.devices.Clipboard;
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
