package fr.thesmyler.smylibgui.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.container.WidgetContainer;
import net.smyler.smylib.Animation;
import net.smyler.smylib.Animation.AnimationState;
import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.smyler.smylib.gui.Font;

public class MenuWidget implements IWidget {

    protected List<MenuEntry> entries = new ArrayList<>();
    protected MenuEntry hoveredEntry;
    protected MenuWidget displayedSubMenu;

    protected boolean visible = false;
    protected float x, y;
    protected int z;
    protected final Font font;
    private boolean isSubMenu = false;
    private boolean openOnClick = false;

    protected float padding = 4;

    public static final Color DEFAULT_COLOR_SEPARATOR = new Color(0x50FFFFFF);
    public static final Color DEFAULT_COLOR_BORDER = new Color(0xA0FFFFFF);
    public static final Color DEFAULT_COLOR_BACKGROUND = new Color(0xE0000000);
    public static final Color DEFAULT_COLOR_HOVERED = new Color(0x40C0C0C0);
    public static final Color DEFAULT_COLOR_TEXT_NORMAL = new Color(0xFFFFFFFF);
    public static final Color DEFAULT_COLOR_TEXT_DISABLED = new Color(0xFF808080);
    public static final Color DEFAULT_COLOR_TEXT_HOVERED = new Color(0xFF8080FF);
    protected Color separatorColor = DEFAULT_COLOR_SEPARATOR;
    protected Color borderColor = DEFAULT_COLOR_BORDER;
    protected Color backgroundColor = DEFAULT_COLOR_BACKGROUND;
    protected Color hoveredColor = DEFAULT_COLOR_HOVERED;
    protected Color textColor = DEFAULT_COLOR_TEXT_NORMAL;
    protected Color disabledTextColor = DEFAULT_COLOR_TEXT_DISABLED;
    protected Color hoveredTextColor = DEFAULT_COLOR_TEXT_HOVERED;

    protected final Animation mainAnimation = new Animation(150);
    protected final Animation hoverAnimation = new Animation(150);

    public MenuWidget(int z, Font font) {
        this.z = z;
        this.font = font;
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean mouseHoverMenu, boolean hasFocus, WidgetContainer parent) {
        this.mainAnimation.update();
        this.hoverAnimation.update();
        float width = this.getWidth();
        float height = this.getHeight();
        float fh = this.font.height();
        float lh = fh + padding * 2;
        float sh = 3;
        float dw = this.font.getStringWidth(" >");
        GlStateManager.enableAlpha();
        Color separatorColor = this.mainAnimation.fadeColor(this.separatorColor);
        Color borderColor = this.mainAnimation.fadeColor(this.borderColor);
        Color backgroundColor = this.mainAnimation.fadeColor(this.backgroundColor);
        Color hoveredColor = this.hoverAnimation.fadeColor(this.mainAnimation.fadeColor(this.hoveredColor));
        Color textColor = this.mainAnimation.fadeColor(this.textColor);
        Color disabledTextColor = this.mainAnimation.fadeColor(this.disabledTextColor);
        Color hoveredTextColor = this.mainAnimation.fadeColor(this.hoveredTextColor);
        RenderUtil.drawRect(x, y, x + width, y + height, backgroundColor);
        RenderUtil.drawRect(x, y, x + 1, y + height, borderColor);
        RenderUtil.drawRect(x + width, y, x + width + 1, y + height, borderColor);
        RenderUtil.drawRect(x, y, x + width, y+1, borderColor);
        RenderUtil.drawRect(x, y + height, x + width + 1, y + height + 1, borderColor);
        float ty = y;
        for(MenuEntry entry: this.entries) {
            int tx = 0;
            if(entry.text != null) {
                boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= ty && mouseY <= ty + lh - 1;
                Color c = textColor;
                if(!entry.enabled) c = disabledTextColor;
                else if(hovered || (entry.getSubMenu() != null && entry.getSubMenu().equals(this.displayedSubMenu))) {
                    if(!entry.equals(this.hoveredEntry)) {
                        this.hoveredEntry = entry;
                        this.hoverAnimation.start(AnimationState.ENTER);
                        this.hoverAnimation.update();
                        hoveredColor = this.hoverAnimation.fadeColor(this.mainAnimation.fadeColor(this.hoveredColor));
                    }
                    tx += 3 * this.hoverAnimation.getProgress();
                    c = hoveredTextColor;
                    RenderUtil.drawRect(x+1, ty+1, x + width, ty + fh + padding*2 -1, hoveredColor);
                }
                MenuWidget subMenu = entry.getSubMenu();
                if(this.displayedSubMenu != null && mouseHoverMenu && this.displayedSubMenu.equals(subMenu) && !hovered) {
                    this.hideSubMenu(parent);
                }
                if(subMenu != null && hovered && this.displayedSubMenu == null) {
                    this.displayedSubMenu = subMenu;
                    parent.scheduleBeforeNextUpdate(() -> parent.addWidget(subMenu));
                    float subX = x + width - parent.getX();
                    float subY = ty - parent.getY();
                    float subH = subMenu.getHeight();
                    float subW = subMenu.getWidth();
                    if(subY + subH > parent.getHeight()) subY = parent.getHeight() - subH - 1;
                    if(subX + subW > parent.getWidth()) subX -= subW + width + 1;
                    subMenu.z = this.z + 1;
                    subMenu.isSubMenu = true;
                    subMenu.show(subX, subY);
                }
                this.font.drawString(x + padding*2 + tx, ty + padding, entry.getText(), c, false);
                if(subMenu != null) this.font.drawString(x + width - dw - padding, ty + padding, " >", c, false);
                ty += lh;
            } else {
                RenderUtil.drawRect(x + 1, ty + sh/2, x + width, ty + sh/2 + 1, separatorColor);
                ty += sh;
            }
        }
        GlStateManager.disableAlpha();
    }

    @Override
    public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        if(mouseButton == 0) {
            float ty = 0;
            float width = this.getWidth();
            float fh = this.font.height();
            float lh = fh + padding * 2;
            float sh = 3;
            for(MenuEntry entry: this.entries) {
                float h = entry.text == null ? sh: lh;
                boolean hovered = mouseX >= 0 && mouseX < width && mouseY >= ty && mouseY <= ty + h - 1;
                if(hovered) {
                    if(entry.text != null && entry.enabled && entry.action != null ) {
                        entry.exec();
                        this.hide(parent);
                        return this.isSubMenu;
                    }
                    return false;
                }
                ty += h;
            }
        } else {
            return false;
        }
        return true;
    }

    @Override
    public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        return false; // We want to intercept double clicks
    }

    @Override
    public boolean onParentClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        if(this.isSubMenu) return true;
        if(this.isVisible(parent)) {
            this.hide(parent);
            return false;
        }
        if(mouseButton == 1 && this.openOnClick) {
            float x = mouseX;
            float y = mouseY;
            float w = this.getWidth();
            float h = this.getHeight();
            if(x + w > parent.getWidth()) x -= w;
            if(y + h > parent.getHeight()) y -= h;
            this.show(x, y);
            parent.setFocus(this);
            return false;
        }
        return true;
    }

    @Override
    public boolean onParentDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
        if(mouseButton == 1) return this.onParentClick(mouseX, mouseY, mouseButton, parent);
        return true;
    }

    public MenuEntry addEntry(String text, Runnable action) {
        return this.addEntry(text, action, null, true);
    }

    public MenuEntry addEntry(String text, MenuWidget submenu) {
        return this.addEntry(text, null, submenu, true);
    }

    public MenuEntry addEntry(String text) {
        return this.addEntry(text, null, null, false);
    }

    public MenuEntry addSeparator() {
        return this.addEntry(null, null, null, false);
    }

    public MenuEntry addEntry(String text, Runnable action, MenuWidget submenu, boolean enabled) {
        MenuEntry e = new MenuEntry(text, action, submenu, enabled);
        this.entries.add(e);
        return e;
    }

    @Override
    public float getWidth() {
        float mw = 0;
        for(MenuEntry e: this.entries) {
            mw = Math.max(mw, this.font.getStringWidth(e.getText()));
        }
        return mw + padding * 4 + this.font.getStringWidth(" >");
    }

    @Override
    public float getHeight() {
        float h = 0;
        float fh = this.font.height();
        float lh = fh + padding * 2;
        float sh = 3;
        for(MenuEntry entry: this.entries) {
            if(entry.text != null) {
                h += lh;
            } else {
                h += sh;
            }
        }
        return h-1;
    }

    @Override
    public float getX() {
        return this.x;
    }

    @Override
    public float getY() {
        return this.y;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    public void hide(@Nullable WidgetContainer parent) {
        this.hideSubMenu(parent);
        if(parent != null) parent.scheduleBeforeNextUpdate(() -> this.visible = false);
        else this.visible = false;
        if(parent != null && this.equals(parent.getFocusedWidget())) {
            parent.setFocus(null);
        }

    }

    public void hideSubMenu(WidgetContainer parent) {
        MenuWidget m = this.displayedSubMenu;
        if(m != null) {
            m.hide(parent);
            if(parent != null) {
                parent.scheduleBeforeNextUpdate(() -> parent.removeWidget(m));
            }
        }
        this.displayedSubMenu = null;
    }

    public void show(float x, float y) {
        this.x = x;
        this.y = y;
        if(!this.visible)
            this.mainAnimation.start(AnimationState.ENTER);
        this.hoveredEntry = null;
        this.visible = true;
    }

    public void useAsRightClick() {
        this.openOnClick = true;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visible;
    }

    public float getPadding() {
        return padding;
    }

    public void setPadding(float padding) {
        this.padding = padding;
    }

    public Color getSeparatorColor() {
        return separatorColor;
    }

    public void setSeparatorColor(Color separatorColor) {
        this.separatorColor = separatorColor;
    }

    public Color getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(Color borderColor) {
        this.borderColor = borderColor;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public void setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
    }

    public Color getHoveredColor() {
        return hoveredColor;
    }

    public void setHoveredColor(Color hoveredColor) {
        this.hoveredColor = hoveredColor;
    }

    public Color getTextColor() {
        return textColor;
    }

    public void setTextColor(Color textColor) {
        this.textColor = textColor;
    }

    public Color getHoveredTextColor() {
        return hoveredTextColor;
    }

    public void setHoveredTextColor(Color hoveredTextColor) {
        this.hoveredTextColor = hoveredTextColor;
    }

    public Color getDisabledTextColor() {
        return disabledTextColor;
    }

    public void setDisabledTextColor(Color disabledTextColor) {
        this.disabledTextColor = disabledTextColor;
    }

    public static class MenuEntry {

        public final String text;
        private final Runnable action;
        public boolean enabled;
        private final MenuWidget subMenu;

        private MenuEntry(String text, Runnable action, MenuWidget menu, boolean enabled) {
            this.text = text;
            this.action = action;
            this.subMenu = menu;
            this.enabled = enabled;
        }
        public void exec() {
            if(this.action != null && this.enabled) this.action.run();
        }

        public String getText() {
            return this.text;
        }

        public MenuWidget getSubMenu() {
            return this.subMenu;
        }

        public boolean isSeparator() {
            return this.text == null;
        }

    }


}