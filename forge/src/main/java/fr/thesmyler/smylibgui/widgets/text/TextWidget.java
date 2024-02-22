package fr.thesmyler.smylibgui.widgets.text;

import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.text.ImmutableText;
import net.smyler.smylib.text.Text;
import org.jetbrains.annotations.Nullable;

import net.smyler.smylib.gui.containers.WidgetContainer;
import net.smyler.smylib.Color;
import net.smyler.smylib.gui.widgets.Widget;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.smyler.smylib.gui.Font;

import static net.smyler.smylib.text.ImmutableText.ofPlainText;

public class TextWidget implements Widget {

    protected Text text;
    protected Text[] lines;
    protected float anchorX, x, anchorY, y;
    protected final int z;
    protected float width, height, maxWidth;
    protected boolean visible = true;

    protected Color baseColor;
    protected Color backgroundColor = Color.TRANSPARENT;

    protected boolean shadow;
    protected float padding = 0;
    protected Font font;
    protected Text hovered;
    protected TextAlignment alignment;

    public TextWidget(float x, float y, int z, float maxWidth, Text text, TextAlignment alignment, Color baseColor, boolean shadow, Font font) {
        this.anchorX = x;
        this.anchorY = y;
        this.z = z;
        this.text = text;
        this.font = font;
        this.alignment = alignment;
        this.maxWidth = maxWidth;
        this.baseColor = baseColor;
        this.shadow = shadow;
        this.updateCoords();
    }

    @Deprecated
    public TextWidget(float x, float y, int z, ITextComponent text, TextAlignment alignment, Font font) {
        this(x, y, z, ofPlainText(text.getUnformattedText()), alignment, font);
    }

    public TextWidget(float x, float y, int z, Text text, TextAlignment alignment, Font font) {
        this(x, y, z, Float.MAX_VALUE, text, alignment, Color.WHITE, true, font);
    }

    public TextWidget(float x, float y, int z, TextAlignment alignment, Font font) {
        this(x, y, z, Float.MAX_VALUE, ImmutableText.EMPTY, alignment, Color.WHITE, true, font);
    }

    @Deprecated
    public TextWidget(float x, float y, int z, ITextComponent text, Font font) {
        this(x, y, z, ofPlainText(text.getUnformattedText()), font);
    }

    public TextWidget(float x, float y, int z, Text text, Font font) {
        this(x, y, z, text, TextAlignment.RIGHT, font);
    }

    public TextWidget(float x, float y, int z, Font font) {
        this(x, y, z, ImmutableText.EMPTY, TextAlignment.RIGHT, font);
    }

    @Deprecated
    public TextWidget(int z, ITextComponent text, TextAlignment alignment, Font font) {
        this(z, ofPlainText(text.getUnformattedText()), alignment, font);
    }

    public TextWidget(int z, Text text, TextAlignment alignment, Font font) {
        this(0, 0, z, Float.MAX_VALUE, text, alignment, Color.WHITE, true, font);
    }

    @Deprecated
    public TextWidget(int z, ITextComponent text, Font font) {
        this(z, ofPlainText(text.getUnformattedText()), font);
    }

    public TextWidget(int z, Text text, Font font) {
        this(0, 0, z, text, font);
    }

    public TextWidget(int z, Font font) {
        this(0, 0, z, ImmutableText.EMPTY, font);
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        context.glState().enableAlpha();
        GlStateManager.enableBlend();
        float w = this.getWidth();
        float h = this.getHeight();
        context.drawRectangle(x, y, x + w, y + h, this.backgroundColor);
        float drawY = y + this.padding;
        for(Text line: this.lines) {
            String formattedText = line.getFormattedText();
            float lineWidth = this.font.getStringWidth(formattedText);
            float lx = x + this.anchorX - this.x;
            switch(this.alignment) {
                case RIGHT:
                    break;
                case LEFT:
                    lx -= lineWidth;
                    break;
                case CENTER:
                    lx -= lineWidth/2;
                    break;
            }
            this.font.drawString(lx, drawY, formattedText, this.baseColor, this.shadow);
            drawY += this.font.height() + this.padding;
        }
        this.hovered = this.getComponentUnder(mouseX - x, mouseY - y);
    }

    protected void updateCoords() {
        this.lines = this.font.wrapToWidth(this.text, this.maxWidth);
        this.height = this.lines.length * (this.font.height() + this.padding) + this.padding ;
        float w = 0;
        for(Text line: this.lines) {
            String ft = line.getFormattedText();
            w = Math.max(w, this.font.getStringWidth(ft));
        }
        this.width = w + this.padding * 2;
        this.x = this.anchorX;
        switch(this.alignment) {
            case RIGHT:
                this.x -= this.padding;
                break;
            case LEFT:
                this.x -= this.width - this.padding;
                break;
            case CENTER:
                this.x -= this.width/2;
                break;
        }
        this.y = this.anchorY;
    }

    protected Text getComponentUnder(float x, float y) {
        if(x < this.padding || x > this.width - this.padding) return null;
        int lineIndex = (int) Math.floor((y - this.padding) / (this.font.height() + this.padding));
        if(lineIndex < 0 || lineIndex >= this.lines.length) return null;
        if(y - this.padding - lineIndex*(this.font.height() + this.padding) > this.font.height()) return null;
        Text line = this.lines[lineIndex];
        float pos = this.padding;
        float lineWidth = this.font.getStringWidth(line.getFormattedText());
        switch(this.alignment) {
            case RIGHT:
                break;
            case LEFT:
                pos = this.width - lineWidth;
                break;
            case CENTER:
                pos = (this.width - lineWidth) / 2;
                break;
        }
        for(Text child: line) {
            pos += this.font.getStringWidth(child.getFormattedText());
            if(pos >= x) return child;
        }
        return null;
    }

    @Override
    public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        Text clicked = this.getComponentUnder(mouseX, mouseY);
        if(clicked != null) {
            //FIXME TextWidget click
            //Minecraft.getMinecraft().currentScreen.handleComponentClick(clicked);
        }
        parent.setFocus(null); //We don't want to retain focus
        return false;
    }

    public Text getText() {
        return this.text;
    }

    @Deprecated
    public TextWidget setText(ITextComponent component) {
        //FIXME do not use ITextComponent at all in TextWidget
        return this.setText(ofPlainText(component.getUnformattedText()));
    }

    public TextWidget setText(Text text) {
        this.text = text;
        this.updateCoords();
        return this;
    }

    @Override
    public float getX() {
        return this.x;
    }

    public float getAnchorX() {
        return this.anchorX;
    }

    public TextWidget setAnchorX(float x) {
        this.anchorX = x;
        this.updateCoords();
        return this;
    }

    @Override
    public float getY() {
        return this.y;
    }

    public float getAnchorY() {
        return this.anchorY;
    }

    public TextWidget setAnchorY(float y) {
        this.anchorY = y;
        this.updateCoords();
        return this;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public float getWidth() {
        return this.width;
    }

    public float getMaxWidth() {
        return this.maxWidth;
    }

    public TextWidget setMaxWidth(float maxWidth) {
        this.maxWidth = maxWidth;
        this.updateCoords();
        return this;
    }

    @Override
    public float getHeight() {
        return this.height;
    }

    public Color getBaseColor() {
        return this.baseColor;
    }

    public TextWidget setBaseColor(Color color) {
        this.baseColor = color;
        return this;
    }

    public boolean hasShadow() {
        return this.shadow;
    }

    public TextWidget setShadow(boolean shadow) {
        this.shadow = shadow;
        return this;
    }

    public TextAlignment getAlignment() {
        return this.alignment;
    }

    public TextWidget setAlignment(TextAlignment alignment) {
        this.alignment = alignment;
        this.updateCoords();
        return this;
    }

    @Override
    public long getTooltipDelay() {
        return 0;
    }

    @Override
    public String getTooltipText() {
        try {
            //TODO Adapt to non text tooltips
            //FIXME TextWidget hover tooltip
            return "";
            //return this.hovered.getStyle().getHoverEvent().getValue().getFormattedText();
        } catch(NullPointerException e) {
            return null;
        }
    }

    public Color getBackgroundColor() {
        return this.backgroundColor;
    }

    public TextWidget setBackgroundColor(Color color) {
        this.backgroundColor = color;
        return this;
    }

    public float getPadding() {
        return padding;
    }

    public TextWidget setPadding(float padding) {
        this.padding = padding;
        this.updateCoords();
        return this;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visible;
    }

    public TextWidget setVisibility(boolean yesNo) {
        this.visible = yesNo;
        return this;
    }

    public TextWidget show() {
        return this.setVisibility(true);
    }

    public TextWidget hide() {
        return this.setVisibility(false);
    }

    public Font getFont() {
        return this.font;
    }

    public TextWidget setFont(Font font) {
        this.font = font;
        this.updateCoords();
        return this;
    }

}
