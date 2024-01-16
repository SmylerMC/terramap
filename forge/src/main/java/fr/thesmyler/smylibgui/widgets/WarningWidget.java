package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.SmyLibGuiTextures;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.RenderUtil;
import net.minecraft.client.Minecraft;

import static net.smyler.smylib.Color.WHITE;
import static fr.thesmyler.smylibgui.util.RenderUtil.applyColor;

public class WarningWidget extends AbstractSolidWidget {

    public WarningWidget(float x, float y, int z) {
        super(x, y, z, 15, 15);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        applyColor(WHITE);
        Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGuiTextures.WIDGET_TEXTURES);
        RenderUtil.drawModalRectWithCustomSizedTexture(x, y, 15, 54, this.width, this.height, 256, 256);
    }

    public WarningWidget setPosition(float x, float y) {
        this.x = x;
        this.y = y;
        return this;
    }

}
