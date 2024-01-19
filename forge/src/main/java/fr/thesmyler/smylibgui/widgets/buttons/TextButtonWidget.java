package fr.thesmyler.smylibgui.widgets.buttons;

import fr.thesmyler.smylibgui.SmyLibGuiTextures;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.smyler.smylib.gui.DrawContext;

import static net.smyler.smylib.Color.WHITE;
import static net.smyler.smylib.SmyLib.getGameClient;

public class TextButtonWidget extends AbstractButtonWidget {

    protected String str;
    protected Color enabledTextColor = Color.LIGHT_GRAY;
    protected Color activeTextColor = Color.SELECTION;
    protected Color disabledTextColor = Color.MEDIUM_GRAY;

    public TextButtonWidget(float x, float y, int z, float width, String str, Runnable onClick, Runnable onDoubleClick) {
        super(x, y, z, width, getGameClient().defaultFont().height() + 11, onClick, onDoubleClick);
        this.str = str;
    }

    public TextButtonWidget(float x, float y, int z, float width, String str, Runnable onClick) {
        this(x, y, z, width, str, onClick, null);
    }

    public TextButtonWidget(float x, float y, int z, float width, String str) {
        this(x, y, z, width, str, null, null);
        this.enabled = false;
    }

    public TextButtonWidget(int z, String str, Runnable onClick, Runnable onDoubleClick) {
        this(0, 0, z, getGameClient().defaultFont().height() + 20, str, onClick, onDoubleClick);
    }

    public TextButtonWidget(int z, String str, Runnable onClick) {
        this(z, str, onClick, null);
    }

    public TextButtonWidget(int z, String str) {
        this(z, str, null, null);
        this.enabled = false;
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean hasFocus, WidgetContainer parent) {
        Minecraft mc = Minecraft.getMinecraft();
        mc.getTextureManager().bindTexture(SmyLibGuiTextures.BUTTON_TEXTURES);
        context.glState().setColor(WHITE);
        int textureDelta = 1;
        Color textColor = this.enabledTextColor;
        if (!this.isEnabled()) {
            textColor = this.disabledTextColor;
            textureDelta = 0;
        }
        else if (hovered || hasFocus) {
            textColor = this.activeTextColor;
            textureDelta = 2;
        }
        float leftWidth = this.width / 2;
        float rightWidth = leftWidth;
        leftWidth += this.width % 2;
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        RenderUtil.drawTexturedModalRect(x, y, 0, 0, 46 + textureDelta * 20, leftWidth, 20);
        RenderUtil.drawTexturedModalRect(x + leftWidth, y, 0, 200 - rightWidth, 46 + textureDelta * 20, rightWidth, 20);
        parent.getFont().drawCenteredString(x + this.width / 2, y + (this.height - 8) / 2, this.getText(), textColor, true);

    }

    public String getText() {
        return str;
    }

    public void setText(String str) {
        this.str = str;
    }

    public TextButtonWidget setWidth(float width) {
        this.width = width;
        return this;
    }

    public Color getEnabledTextColor() {
        return enabledTextColor;
    }

    public void setEnabledTextColor(Color enabledTextColor) {
        this.enabledTextColor = enabledTextColor;
    }

    public Color getActiveTextColor() {
        return activeTextColor;
    }

    public void setActiveTextColor(Color activeTextColor) {
        this.activeTextColor = activeTextColor;
    }

    public Color getDisabledTextColor() {
        return disabledTextColor;
    }

    public void setDisabledTextColor(Color disabledTextColor) {
        this.disabledTextColor = disabledTextColor;
    }

}
