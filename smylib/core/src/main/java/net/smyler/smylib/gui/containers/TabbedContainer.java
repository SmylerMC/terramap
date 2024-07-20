package net.smyler.smylib.gui.containers;

import net.smyler.smylib.Color;
import net.smyler.smylib.gui.Font;
import net.smyler.smylib.gui.UiDrawContext;
import net.smyler.smylib.gui.widgets.Widget;
import net.smyler.smylib.text.ImmutableText;
import net.smyler.smylib.text.Text;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Math.min;
import static net.smyler.smylib.Color.WHITE;

public class TabbedContainer extends FlexibleWidgetContainer {

    public static final Color UNSELECTED_COLOR = new Color(0, 0, 0, 200);
    public static final Color LIGHT_TRANSPARENT = new Color(237, 237, 237, 175);

    private final float BUTTON_HORIZONTAL_OFFSET = 2f;

    private final float buttonBaseHeight = 20f;
    private final float buttonSelectedHeight = 24f;

    private final List<TabContainer> tabs = new ArrayList<>();
    private TabContainer selectedTab;

    private float buttonWidth = 80f;
    private boolean centerTabs = false;
    private float centeringOffset = 0f;

    public TabbedContainer(float x, float y, int z, float width, float height) {
        super(x, y, z, width, height);
    }

    public TabContainer createTab(Text title) {
        TabContainer tab = new TabContainer(0, this.tabs.size(), title);
        TabButton button = tab.button;
        this.tabs.add(tab);
        if (this.tabs.size() == 1) {
            tab.setActive();
        }
        this.addWidget(tab).addWidget(button);
        return tab;
    }

    @Override
    public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, @Nullable WidgetContainer parent) {
        super.draw(context, x, y, mouseX, mouseY, screenHovered, screenFocused, parent);

        float height = this.getHeight();
        float width = this.getWidth();
        float topContentY =  y + this.buttonSelectedHeight;
        context.drawRectangle(x, topContentY,  x + this.selectedTab.button.getX() + 1f, topContentY + 1f, LIGHT_TRANSPARENT);
        context.drawRectangle(x + 1f, topContentY + 1f,  x + this.selectedTab.button.getX() + 2f, topContentY + 2f, UNSELECTED_COLOR);

        context.drawRectangle(x + this.selectedTab.button.getX() + this.selectedTab.button.getWidth() - 1f, topContentY,  x + width, topContentY + 1f, LIGHT_TRANSPARENT);
        context.drawRectangle(x + this.selectedTab.button.getX() + this.selectedTab.button.getWidth() - 2f, topContentY + 1f,  x + width, topContentY + 2f, UNSELECTED_COLOR);

        context.drawRectangle(x + 1f, y + height - 2f, x + width, y + height - 1f, UNSELECTED_COLOR);
        context.drawRectangle(x, y + height - 1f, x + width, y + height, LIGHT_TRANSPARENT);

        context.drawRectangle(x, topContentY + 1f, x + 1f, y + height - 1f, LIGHT_TRANSPARENT);
        context.drawRectangle(x + 1f, topContentY + 2f, x + 2f, y + height - 2f, UNSELECTED_COLOR);

        context.drawRectangle(x + width - 1f, topContentY + 1f, x + width, y + height - 1f, LIGHT_TRANSPARENT);
        context.drawRectangle(x + width - 2f, topContentY + 2f, x + width - 1f, y + height - 2f, UNSELECTED_COLOR);
    }

    @Override
    public void init() {
        super.init();
        this.updateButtonWidths(this.getFont());
    }

    private void updateButtonWidths(Font font) {
        float buttonWidth = this.tabs.stream()
                .map(TabContainer::getButton)
                .map(TabButton::getText)
                .map(font::computeWidth)
                .map(f -> f + TabButton.INNER_TEXT_OFFSET * 2f)
                .max(Float::compareTo)
                .orElse(0f);
        float maxButtonWidth = (this.getWidth() - BUTTON_HORIZONTAL_OFFSET * 2f) / this.tabs.size();
        this.buttonWidth = min(buttonWidth, maxButtonWidth);
        if (this.centerTabs) {
            this.centeringOffset = (this.getWidth() - this.buttonWidth * this.tabs.size() - BUTTON_HORIZONTAL_OFFSET * 2f) / 2f;
        } else {
            this.centeringOffset = 0f;
        }
    }

    public TabbedContainer setCenterTabs(boolean centerTabs) {
        this.centerTabs = centerTabs;
        return this;
    }

    public boolean isCenterTags() {
        return this.centerTabs;
    }

    public class TabContainer extends WidgetContainer {

        boolean selected = false;
        private final TabButton button;
        private final Color backgroundColor = new Color(0, 0, 0, 30);

        private TabContainer(int z, int index, Text title) {
            super(z);
            this.setDoScissor(false);
            this.button = new TabButton(index, this, title);
        }

        public void setActive() {
            for (TabContainer tab : tabs) {
                tab.selected = false;
            }
            this.selected = true;
            TabbedContainer.this.selectedTab = this;
        }

        @Override
        public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, @Nullable WidgetContainer parent) {
            context.drawRectangle(x, y, x + this.getWidth(), y + this.getHeight(), this.backgroundColor);
            super.draw(context, x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
        }

        @Override
        public float getX() {
            return BUTTON_HORIZONTAL_OFFSET;
        }

        @Override
        public float getY() {
            return TabbedContainer.this.buttonSelectedHeight + 2f;
        }

        @Override
        public float getWidth() {
            return TabbedContainer.this.getWidth() - 4f;
        }

        @Override
        public float getHeight() {
            return TabbedContainer.this.getHeight() - TabbedContainer.this.buttonSelectedHeight - 4f;
        }

        @Override
        public boolean isVisible(WidgetContainer parent) {
            return this.selected;
        }

        private TabButton getButton() {
            return button;
        }

    }

    public class TabButton implements Widget {

        private final TabContainer tab;
        private final int index;
        private Text text;
        private static final float INNER_TEXT_OFFSET = 7f;

        private TabButton(int index, TabContainer tab, Text text) {
            this.tab = tab;
            this.index = index;
            this.text = text;
        }

        @Override
        public float getX() {
            return this.index * TabbedContainer.this.buttonWidth + BUTTON_HORIZONTAL_OFFSET + TabbedContainer.this.centeringOffset;
        }

        @Override
        public float getY() {
            if (this.tab.selected) {
                return 0;
            } else {
                return TabbedContainer.this.buttonSelectedHeight - TabbedContainer.this.buttonBaseHeight;
            }
        }

        @Override
        public int getZ() {
            return 0;
        }

        @Override
        public float getWidth() {
            return TabbedContainer.this.buttonWidth;
        }

        @Override
        public float getHeight() {
            if (this.tab.selected) {
                return TabbedContainer.this.buttonSelectedHeight;
            } else {
                return TabbedContainer.this.buttonBaseHeight;
            }
        }

        @Override
        public void draw(UiDrawContext context, float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            float width = this.getWidth();
            float height = this.getHeight();
            float innerDownY = y + height;
            float outerDownY = y + height;
            Color outerOutline;
            Color innerOutline;
            Color backgroundColor;
            if (this.tab.selected) {
                outerOutline = UNSELECTED_COLOR;
                innerOutline = WHITE;
                innerDownY += 1f;
                backgroundColor = this.tab.backgroundColor;
            } else {
                if (hovered) {
                    innerOutline = WHITE;
                } else {
                    innerOutline = LIGHT_TRANSPARENT;
                }
                outerOutline = UNSELECTED_COLOR;
                backgroundColor = UNSELECTED_COLOR;
            }

            context.drawRectangle(x + 2f, y + 2f, x + TabbedContainer.this.buttonWidth - 2f, y + height, backgroundColor);

            context.drawRectangle(x, y, x + 1f, outerDownY, outerOutline);
            context.drawRectangle(x + 1f, y, x + width - 1f, y + 1f, outerOutline);
            context.drawRectangle(x + width - 1f, y, x + width, outerDownY, outerOutline);

            context.drawRectangle(x + 1f, y + 1f, x + 2f, innerDownY, innerOutline);
            context.drawRectangle(x + 2f, y + 1f, x + width - 2f, y + 2f, innerOutline);
            context.drawRectangle(x + width - 2f, y + 1f, x + width - 1f, innerDownY, innerOutline);

            if (hovered && !this.tab.selected) {
                context.drawRectangle(x + 2f, y + height - 1f, x + width - 2f, y + height, WHITE);
            }

            Font font = parent.getFont();
            float textY = (height - font.height()) / 2;

            float textWidth = font.computeWidth(this.text);
            Text text = this.text;
            if (textWidth > this.getWidth() - 14f) {
                text = font.trimRight(text, this.getWidth() - 14f - font.computeWidth("..."));
                text = ImmutableText.of(text).withNewSiblings(ImmutableText.ofPlainText("..."));
            }
            font.drawCentered(x + this.getWidth() / 2f, y + textY + 2f,  text, WHITE, true);
        }

        @Override
        public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
            this.tab.setActive();
            return false;
        }

        @Override
        public long getTooltipDelay() {
            return 1000;
        }

        @Override
        public @Nullable String getTooltipText() {
            return this.text.getFormattedText();
        }

        public Text getText() {
            return this.text;
        }

        public TabButton setText(Text text) {
            this.text = text;
            return this;
        }

    }

}
