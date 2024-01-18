package fr.thesmyler.smylibgui.widgets.text;

import org.jetbrains.annotations.Nullable;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.smyler.smylib.gui.Font;

import static net.minecraft.client.gui.GuiUtilRenderComponents.splitText;

public class TextWidget implements IWidget {

    protected ITextComponent component;
    protected ITextComponent[] lines;
    protected float anchorX, x, anchorY, y;
    protected final int z;
    protected float width, height, maxWidth;
    protected boolean visible = true;

    protected Color baseColor;
    protected Color backgroundColor = Color.TRANSPARENT;

    protected boolean shadow;
    protected float padding = 0;
    protected Font font;
    protected ITextComponent hovered;
    protected TextAlignment alignment;

    public TextWidget(float x, float y, int z, float maxWidth, ITextComponent component, TextAlignment alignment, Color baseColor, boolean shadow, Font font) {
        this.anchorX = x;
        this.anchorY = y;
        this.z = z;
        this.component = component;
        this.font = font;
        this.alignment = alignment;
        this.maxWidth = maxWidth;
        this.baseColor = baseColor;
        this.shadow = shadow;
        this.updateCoords();
    }

    public TextWidget(float x, float y, int z, ITextComponent component, TextAlignment alignment, Font font) {
        this(x, y, z, Float.MAX_VALUE, component, alignment, Color.WHITE, true, font);
    }

    public TextWidget(float x, float y, int z, TextAlignment alignment, Font font) {
        this(x, y, z, Float.MAX_VALUE, new TextComponentString(""), alignment, Color.WHITE, true, font);
    }

    public TextWidget(float x, float y, int z, ITextComponent component, Font font) {
        this(x, y, z, component, TextAlignment.RIGHT, font);
    }

    public TextWidget(float x, float y, int z, Font font) {
        this(x, y, z, new TextComponentString(""), TextAlignment.RIGHT, font);
    }

    public TextWidget(int z, ITextComponent component, TextAlignment alignment, Font font) {
        this(0, 0, z, Float.MAX_VALUE, component, alignment, Color.WHITE, true, font);
    }

    public TextWidget(int z, ITextComponent component, Font font) {
        this(0, 0, z, component, font);
    }

    public TextWidget(int z, Font font) {
        this(0, 0, z, new TextComponentString(""), font);
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        float w = this.getWidth();
        float h = this.getHeight();
        RenderUtil.drawRect(x, y, x + w, y + h, this.backgroundColor);
        float drawY = y + this.padding;
        for(ITextComponent line: this.lines) {
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
        this.lines = splitText(
                this.component,
                (int) Math.floor(this.maxWidth / this.font.scale()),
                Minecraft.getMinecraft().fontRenderer,
                true, false
        ).toArray(new ITextComponent[] {}); //TODO reimplement this in Font once we have an abstraction for text components
        this.height = this.lines.length * (this.font.height() + this.padding) + this.padding ;
        float w = 0;
        for(ITextComponent line: this.lines) {
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

    protected ITextComponent getComponentUnder(float x, float y) {
        if(x < this.padding || x > this.width - this.padding) return null;
        int lineIndex = (int) Math.floor((y - this.padding) / (this.font.height() + this.padding));
        if(lineIndex < 0 || lineIndex >= this.lines.length) return null;
        if(y - this.padding - lineIndex*(this.font.height() + this.padding) > this.font.height()) return null;
        ITextComponent line = this.lines[lineIndex];
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
        for(ITextComponent child: line.getSiblings()) {
            pos += this.font.getStringWidth(child.getFormattedText());
            if(pos >= x) return child;
        }
        return null;
    }

    @Override
    public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        ITextComponent clicked = this.getComponentUnder(mouseX, mouseY);
        if(clicked != null) {
            Minecraft.getMinecraft().currentScreen.handleComponentClick(clicked);
        }
        parent.setFocus(null); //We don't want to retain focus
        return false;
    }

    public ITextComponent getComponent() {
        return this.component;
    }

    public TextWidget setText(ITextComponent component) {
        this.component = component;
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
            return this.hovered.getStyle().getHoverEvent().getValue().getFormattedText();
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
