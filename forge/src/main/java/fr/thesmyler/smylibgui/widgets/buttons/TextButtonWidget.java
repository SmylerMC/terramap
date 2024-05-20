package fr.thesmyler.smylibgui.widgets.buttons;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.Sprite;
import net.smyler.smylib.gui.SpriteLibrary;
import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.DrawContext;

import static net.smyler.smylib.SmyLib.getGameClient;

public class TextButtonWidget extends AbstractButtonWidget {

    protected String str;
    protected Color enabledTextColor = Color.LIGHT_GRAY;
    protected Color activeTextColor = Color.SELECTION;
    protected Color disabledTextColor = Color.MEDIUM_GRAY;

    private final Sprite buttonSprite;
    private final Sprite buttonDisabledSprite;
    private final Sprite buttonHighlightedSprite;

    public TextButtonWidget(float x, float y, int z, float width, String str, Runnable onClick, Runnable onDoubleClick) {
        super(x, y, z, width, getGameClient().defaultFont().height() + 11, onClick, onDoubleClick);
        this.str = str;
        SpriteLibrary sprites = getGameClient().sprites();
        this.buttonSprite = sprites.getSprite(new Identifier("minecraft", "button"));
        this.buttonDisabledSprite = sprites.getSprite(new Identifier("minecraft", "button_disabled"));
        this.buttonHighlightedSprite = sprites.getSprite(new Identifier("minecraft", "button_highlighted"));
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

        Sprite sprite = this.buttonSprite;
        Color textColor = this.enabledTextColor;
        if (!this.isEnabled()) {
            textColor = this.disabledTextColor;
            sprite = this.buttonDisabledSprite;
        }
        else if (hovered || hasFocus) {
            textColor = this.activeTextColor;
            sprite = this.buttonHighlightedSprite;
        }

        float leftWidth = this.width / 2;
        float rightWidth = leftWidth;
        leftWidth += this.width % 2;

        context.drawSpriteCropped(x, y, sprite, 0d, 0d, sprite.width() - leftWidth, 0d);
        context.drawSpriteCropped(x + leftWidth, y, sprite, sprite.width() - rightWidth, 0d, 0d, 0d);
        parent.getFont().drawCentered(x + this.width / 2, y + (this.height - 8) / 2, this.getText(), textColor, true);

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
