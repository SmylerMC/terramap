package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.SmyLibGuiTextures;
import net.smyler.smylib.gui.containers.WidgetContainer;
import fr.thesmyler.smylibgui.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.gui.widgets.AbstractSolidWidget;

import static net.smyler.smylib.Color.WHITE;

public class WarningWidget extends AbstractSolidWidget {

    public WarningWidget(float x, float y, int z) {
        super(x, y, z, 15, 15);
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        context.glState().setColor(WHITE);
        Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGuiTextures.WIDGET_TEXTURES);
        RenderUtil.drawModalRectWithCustomSizedTexture(x, y, 15, 54, this.width, this.height, 256, 256);
    }

    public WarningWidget setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

}
