package net.smyler.smylib.gui.popups;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.gui.screen.PopupScreen;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

import net.smyler.smylib.gui.DrawContext;


public class PopupScreenImplementation extends PopupScreen {

    private final GuiScreen other;

    public PopupScreenImplementation(GuiScreen backgroundScreen, Popup popup) {
        super(popup);
        this.other = backgroundScreen;
    }

    @Override
    public void init() {
        if(this.other != null) this.other.setWorldAndResolution(Minecraft.getMinecraft(), (int)this.getWidth(), (int)this.getHeight());
        super.init();
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        if(this.other != null) this.other.drawScreen((int) mouseX, (int) mouseY, 0);
        context.drawRectangle(0, 0, this.getWidth(), this.getHeight(), this.getPopup().getShadingColor());
        super.draw(context, x, y, mouseX, mouseY, hovered, focused, parent);
    }


    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        this.other.updateScreen();
        Popup popup = this.getPopup();
        this.getPopup().setPosition(
                (this.getWidth() - popup.getWidth()) / 2,
                (this.getHeight() - popup.getHeight()) / 2
        );
        super.onUpdate(mouseX, mouseY, parent);
    }

    public GuiScreen getBackgroundScreen() {
        return this.other;
    }

}
