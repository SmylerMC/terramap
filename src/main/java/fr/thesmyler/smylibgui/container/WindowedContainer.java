package fr.thesmyler.smylibgui.container;

import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.google.common.base.Preconditions;

import fr.thesmyler.smylibgui.util.Color;
import fr.thesmyler.smylibgui.util.Cursors;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.widgets.IWidget;

public class WindowedContainer extends FlexibleWidgetContainer {

    private float borderWidth = 5;
    private float effectiveBorderWidth = borderWidth;
    private float topBarHeight = 12;
    private float effectiveTopBarHeight = topBarHeight;
    private float minInnerWidth = 20;
    private final float maxInnerWidth = Float.MAX_VALUE;
    private float minInnerHeight = 20;
    private final float maxInnerHeight = Float.MAX_VALUE;
    private boolean allowVerticalResize = true;
    private boolean allowHorizontalResize = true;
    private final boolean enableCustomCursors = true;
    private boolean enableCenterDrag = false;
    private Color borderColor = Color.DARKER_OVERLAY;
    private Color centerDragWidgetColor = Color.LIGHT_OVERLAY;
    private Color titleColor = Color.WHITE;
    private final FlexibleWidgetContainer subScreen;
    private String windowTitle;
    private boolean visible = true;

    public WindowedContainer(float x, float y, int z, FlexibleWidgetContainer subScreen, String title) {
        super(x, y, z, subScreen.getWidth(), subScreen.getHeight());
        this.subScreen = subScreen;
        this.windowTitle = title;
        this.removeAllWidgets();
        this.addWidget(this.subScreen);
        this.addWidget(new RightBorderBar());
        this.addWidget(new LeftBorderBar());
        this.addWidget(new TopBorderBar());
        this.addWidget(new BottomBorderBar());
        this.addWidget(new UpperLeftCorner());
        this.addWidget(new UpperRightCorner());
        this.addWidget(new LowerLeftCorner());
        this.addWidget(new LowerRightCorner());
        this.addWidget(new TopBar());
        this.addWidget(new CenterDragWidget());
        this.updateSubScreen();
    }

    public WindowedContainer(int z, FlexibleWidgetContainer subScreen, String title) {
        this(0, 0, z, subScreen, title);
    }

    public WindowedContainer(float x, float y, int z, float width, float height, String title) {
        this(x, y, z, new FlexibleWidgetContainer(0, 0, 0, width, height), title);
    }

    public WindowedContainer(int z, String title) {
        this(0, 0, z, new FlexibleWidgetContainer(0, 0, 0, 20, 20), title);
    }

    @Override
    public void draw(
            float x,
            float y,
            float mouseX,
            float mouseY,
            boolean screenHovered,
            boolean screenFocused,
            WidgetContainer parent) {
        super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
    }

    @Override
    public void init() {
        this.updateSubScreen();
    }

    private void updateSubScreen() {
        this.subScreen.setPosition(this.effectiveBorderWidth, this.effectiveBorderWidth + this.effectiveTopBarHeight);
        this.subScreen.setSize(
                this.getWidth() - this.effectiveBorderWidth * 2,
                this.getHeight() - this.effectiveBorderWidth * 2 -  this.effectiveTopBarHeight
                );
    }

    public WidgetContainer getContent() {
        return this.subScreen;
    }


    private abstract class BaseDecorationWidget implements IWidget {

        private boolean lastHovered = false;
        private final Cursor cursor;

        public BaseDecorationWidget(Cursor cursor) {
            this.cursor = cursor;
        }

        @Override
        public int getZ() {
            return Integer.MAX_VALUE - 1;
        }

        protected abstract boolean isCursorEnabled();

        protected abstract Color getBackgroundColor();

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            RenderUtil.drawRect(x, y, x + this.getWidth(), y + this.getHeight(), this.getBackgroundColor());
            if(this.lastHovered != hovered && !Mouse.isButtonDown(0)) {
                if(hovered && this.isCursorEnabled() && WindowedContainer.this.enableCustomCursors) Cursors.trySetCursor(this.cursor);
                else if(Mouse.getNativeCursor() == this.cursor) Cursors.trySetCursor(null);
                this.lastHovered = hovered;
            }
        }
    }

    private abstract class BorderWidget extends BaseDecorationWidget {

        public BorderWidget(Cursor cursor) {
            super(cursor);
        }

        @Override
        public boolean isVisible(WidgetContainer parent) {
            return WindowedContainer.this.effectiveBorderWidth > 0;
        }

        @Override
        protected Color getBackgroundColor() {
            return WindowedContainer.this.borderColor;
        }


    }

    private class RightBorderBar extends BorderWidget {

        public RightBorderBar() {
            super(Cursors.CURSOR_RESIZE_HORIZONTAL);
        }

        @Override
        public float getX() {
            return WindowedContainer.this.getWidth() - WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getY() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.getHeight() - WindowedContainer.this.effectiveBorderWidth * 2;
        }

        @Override
        public boolean takesInputs() {
            return WindowedContainer.this.allowHorizontalResize;
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0 || !WindowedContainer.this.allowHorizontalResize) return;
            WindowedContainer.this.trySetInnerWidth(WindowedContainer.this.getInnerWidth() + dX);
            WindowedContainer.this.updateSubScreen();
        }

        @Override
        protected boolean isCursorEnabled() {
            return WindowedContainer.this.allowHorizontalResize;
        }

    }

    private class LeftBorderBar extends BorderWidget {

        public LeftBorderBar() {
            super(Cursors.CURSOR_RESIZE_HORIZONTAL);
        }

        @Override
        public float getX() {
            return 0;
        }

        @Override
        public float getY() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.getHeight() - WindowedContainer.this.effectiveBorderWidth * 2;
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0 || !WindowedContainer.this.allowHorizontalResize) return;
            if(WindowedContainer.this.trySetInnerWidth(WindowedContainer.this.getInnerWidth() - dX)) {
                WindowedContainer.this.setX(WindowedContainer.this.getX() + dX);
            }
            WindowedContainer.this.updateSubScreen();
        }

        @Override
        protected boolean isCursorEnabled() {
            return WindowedContainer.this.allowHorizontalResize;
        }

    }

    private class BottomBorderBar extends BorderWidget {

        public BottomBorderBar() {
            super(Cursors.CURSOR_RESIZE_VERTICAL);
        }

        @Override
        public float getX() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getY() {
            return WindowedContainer.this.getHeight() - WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.getWidth() - WindowedContainer.this.effectiveBorderWidth * 2;
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0 || !WindowedContainer.this.allowVerticalResize) return;
            WindowedContainer.this.trySetInnerHeight(WindowedContainer.this.getInnerHeight() + dY);
            WindowedContainer.this.updateSubScreen();
        }

        @Override
        protected boolean isCursorEnabled() {
            return WindowedContainer.this.allowVerticalResize;
        }

    }

    private class TopBorderBar extends BorderWidget {

        public TopBorderBar() {
            super(Cursors.CURSOR_RESIZE_VERTICAL);
        }

        @Override
        public float getX() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getY() {
            return 0;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.getWidth() - WindowedContainer.this.effectiveBorderWidth * 2;
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0 || !WindowedContainer.this.allowVerticalResize) return;
            if(WindowedContainer.this.trySetInnerHeight(WindowedContainer.this.getInnerHeight() - dY)) {
                WindowedContainer.this.setY(WindowedContainer.this.getY() + dY);
            }
            WindowedContainer.this.updateSubScreen();
        }

        @Override
        protected boolean isCursorEnabled() {
            return WindowedContainer.this.allowVerticalResize;
        }

    }

    private class UpperLeftCorner extends BorderWidget {

        public UpperLeftCorner() {
            super(Cursors.CURSOR_RESIZE_DIAGONAL_1);
        }

        @Override
        public float getX() {
            return 0;
        }

        @Override
        public float getY() {
            return 0;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0) return;
            if(WindowedContainer.this.allowHorizontalResize && WindowedContainer.this.trySetInnerWidth(WindowedContainer.this.getInnerWidth() - dX)) {
                WindowedContainer.this.setX(WindowedContainer.this.getX() + dX);
            }
            if(WindowedContainer.this.allowVerticalResize && WindowedContainer.this.trySetInnerHeight(WindowedContainer.this.getInnerHeight() - dY)) {
                WindowedContainer.this.setY(WindowedContainer.this.getY() + dY);
            }
            WindowedContainer.this.updateSubScreen();
        }

        @Override
        protected boolean isCursorEnabled() {
            return WindowedContainer.this.allowHorizontalResize || WindowedContainer.this.allowVerticalResize;
        }

    }

    private class LowerLeftCorner extends BorderWidget {

        public LowerLeftCorner() {
            super(Cursors.CURSOR_RESIZE_DIAGONAL_2);
        }

        @Override
        public float getX() {
            return 0;
        }

        @Override
        public float getY() {
            return WindowedContainer.this.getHeight() - WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0) return;
            if(WindowedContainer.this.allowVerticalResize) WindowedContainer.this.trySetInnerHeight(WindowedContainer.this.getInnerHeight() + dY);
            if(WindowedContainer.this.allowHorizontalResize && WindowedContainer.this.trySetInnerWidth(WindowedContainer.this.getInnerWidth() - dX)) {
                WindowedContainer.this.setX(WindowedContainer.this.getX() + dX);
            }
            WindowedContainer.this.updateSubScreen();
        }

        @Override
        protected boolean isCursorEnabled() {
            return WindowedContainer.this.allowHorizontalResize || WindowedContainer.this.allowVerticalResize;
        }

    }

    private class LowerRightCorner extends BorderWidget {

        public LowerRightCorner() {
            super(Cursors.CURSOR_RESIZE_DIAGONAL_1);
        }

        @Override
        public float getX() {
            return WindowedContainer.this.getWidth() - WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getY() {
            return WindowedContainer.this.getHeight() - WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0) return;
            if(WindowedContainer.this.allowHorizontalResize) WindowedContainer.this.trySetInnerWidth(WindowedContainer.this.getInnerWidth() + dX);
            if(WindowedContainer.this.allowVerticalResize) WindowedContainer.this.trySetInnerHeight(WindowedContainer.this.getInnerHeight() + dY);
            WindowedContainer.this.updateSubScreen();
        }

        @Override
        protected boolean isCursorEnabled() {
            return WindowedContainer.this.allowHorizontalResize || WindowedContainer.this.allowVerticalResize;
        }

    }

    private class UpperRightCorner extends BorderWidget {

        public UpperRightCorner() {
            super(Cursors.CURSOR_RESIZE_DIAGONAL_2);
        }

        @Override
        public float getX() {
            return WindowedContainer.this.getWidth() - WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getY() {
            return 0;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0) return;
            if(WindowedContainer.this.allowHorizontalResize) WindowedContainer.this.trySetInnerWidth(WindowedContainer.this.getInnerWidth() + dX);
            if(WindowedContainer.this.allowVerticalResize && WindowedContainer.this.trySetInnerHeight(WindowedContainer.this.getInnerHeight() - dY)) {
                WindowedContainer.this.setY(WindowedContainer.this.getY() + dY);
            }
            WindowedContainer.this.updateSubScreen();
        }

        @Override
        protected boolean isCursorEnabled() {
            return WindowedContainer.this.allowHorizontalResize || WindowedContainer.this.allowVerticalResize;
        }

    }

    private class TopBar extends BaseDecorationWidget {

        public TopBar() {
            super(Cursors.CURSOR_MOVE);
        }

        @Override
        public float getX() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getY() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.getWidth() - WindowedContainer.this.effectiveBorderWidth * 2;
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.effectiveTopBarHeight;
        }

        @Override
        public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
            super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
            float width = this.getWidth();
            String toDraw = parent.getFont().trimStringToWidth(WindowedContainer.this.windowTitle, this.getWidth());
            float titleY = y + (this.getHeight() - WindowedContainer.this.effectiveBorderWidth - parent.getFont().height()) / 2 + 1;
            parent.getFont().drawCenteredString(x + width / 2, titleY, toDraw, WindowedContainer.this.titleColor, true);
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0) return;

            WindowedContainer.this.setPosition(WindowedContainer.this.getX() + dX, WindowedContainer.this.getY() + dY);
        }

        @Override
        protected boolean isCursorEnabled() {
            return true;
        }

        @Override
        public boolean isVisible(WidgetContainer parent) {
            return WindowedContainer.this.effectiveTopBarHeight > 0;
        }

        @Override
        protected Color getBackgroundColor() {
            return WindowedContainer.this.borderColor;
        }

    }

    private class CenterDragWidget extends BaseDecorationWidget {

        public CenterDragWidget() {
            super(Cursors.CURSOR_MOVE);
        }

        @Override
        public float getX() {
            return WindowedContainer.this.effectiveBorderWidth;
        }

        @Override
        public float getY() {
            return WindowedContainer.this.effectiveBorderWidth + WindowedContainer.this.effectiveTopBarHeight;
        }

        @Override
        public float getWidth() {
            return WindowedContainer.this.getInnerWidth();
        }

        @Override
        public float getHeight() {
            return WindowedContainer.this.getInnerHeight();
        }

        @Override
        protected boolean isCursorEnabled() {
            return true;
        }

        @Override
        public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int mouseButton, WidgetContainer parent, long dt) {
            if(mouseButton != 0) return;
            WindowedContainer.this.setPosition(WindowedContainer.this.getX() + dX, WindowedContainer.this.getY() + dY);
        }

        @Override
        public boolean onClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
            return false;
        }

        @Override
        public boolean onParentClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
            return false;
        }

        @Override
        public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
            return false;
        }

        @Override
        public boolean onParentDoubleClick(float mouseX, float mouseY, int mouseButton, WidgetContainer parent) {
            return false;
        }

        @Override
        public boolean onMouseWheeled(float mouseX, float mouseY, int amount, WidgetContainer parent) {
            return false;
        }

        @Override
        public boolean isVisible(WidgetContainer parent) {
            return WindowedContainer.this.enableCenterDrag;
        }

        @Override
        protected Color getBackgroundColor() {
            return WindowedContainer.this.centerDragWidgetColor;
        }

    }

    public float getInnerWidth() {
        return this.getWidth() - 2*this.effectiveBorderWidth;
    }

    public float getInnerHeight() {
        return this.getHeight() - 2*this.effectiveBorderWidth - this.effectiveTopBarHeight;
    }

    private boolean trySetInnerWidth(float width) {
        if(width >= this.minInnerWidth && width <= this.maxInnerWidth) {
            this.setWidth(width + this.effectiveBorderWidth * 2);
            return true;
        }
        return false;
    }

    private boolean trySetInnerHeight(float height) {
        if(height >= this.minInnerHeight && height <= this.maxInnerHeight) {
            this.setHeight(height + this.effectiveBorderWidth * 2 + this.effectiveTopBarHeight);
            return true;
        }
        return false;
    }

    public WindowedContainer trySetInnerDimensions(float width, float height) {
        boolean validWidth = width >= this.minInnerWidth && width <= this.maxInnerWidth;
        boolean validHeight = height >= this.minInnerHeight && height <= this.maxInnerHeight;
        float newWidth = width + this.effectiveBorderWidth * 2;
        float newHeight = height + this.effectiveBorderWidth * 2 + this.effectiveTopBarHeight;

        if(validWidth && validHeight) {
            this.setSize(newWidth, newHeight);
        } else if(validHeight) {
            this.setHeight(newHeight);
        } else if(validWidth) {
            this.setWidth(newWidth);
        }
        if(validWidth || validHeight) this.updateSubScreen();
        return this;
    }

    public WindowedContainer setMinInnerWidth(float width) {
        Preconditions.checkArgument(width > 0, "inner width needs to be striclty positive");
        this.minInnerWidth = width;
        return this;
    }

    public WindowedContainer setMaxInnerWidth(float width) {
        Preconditions.checkArgument(width > 0, "inner width needs to be striclty positive");
        this.minInnerWidth = width;
        return this;
    }

    public WindowedContainer setMinInnerHeight(float height) {
        Preconditions.checkArgument(height > 0, "inner height needs to be striclty positive");
        this.minInnerHeight = height;
        return this;
    }

    public WindowedContainer setMaxInnerHeight(float height) {
        Preconditions.checkArgument(height > 0, "inner height needs to be striclty positive");
        this.minInnerHeight = height;
        return this;
    }

    /**
     * @return the window's title
     */
    public String getWindowTitle() {
        return windowTitle;
    }

    /**
     * @param windowTitle - the window title to set
     */
    public void setWindowTitle(String windowTitle) {
        this.windowTitle = windowTitle;
    }

    public float getBorderWidth() {
        return this.effectiveBorderWidth;
    }

    public WindowedContainer setBorderWidth(float width) {
        Preconditions.checkArgument(width > 0, "border width needs to be strictly positive");
        if(this.borderWidth == this.effectiveBorderWidth)
            this.effectiveBorderWidth = width;
        this.borderWidth = width;
        this.updateSubScreen();
        return this;
    }

    public boolean isBorderless() {
        return this.effectiveBorderWidth == 0;
    }

    public WindowedContainer setBorderless(boolean yesNo) {
        if(yesNo) this.effectiveBorderWidth = 0;
        else this.effectiveBorderWidth = this.borderWidth;
        this.updateSubScreen();
        return this;
    }

    public boolean allowsVerticalResize() {
        return this.allowVerticalResize;
    }

    public boolean allowsHorizontalResize() {
        return this.allowHorizontalResize;
    }

    public WindowedContainer setAllowVerticalResize(boolean yesNo) {
        this.allowVerticalResize = yesNo;
        return this;
    }

    public WindowedContainer setAllowHorizontalResize(boolean yesNo) {
        this.allowHorizontalResize = yesNo;
        return this;
    }

    public boolean hasTopBar() {
        return this.effectiveTopBarHeight > 0;
    }

    public WindowedContainer setEnableTopBar(boolean yesNo) {
        if(yesNo) this.effectiveTopBarHeight = this.topBarHeight;
        else this.effectiveTopBarHeight = 0;
        this.updateSubScreen();
        return this;
    }

    public WindowedContainer setTopBarHeight(float height) {
        Preconditions.checkArgument(height > 0, "title bar height must be strictly positive");
        if(this.effectiveTopBarHeight == this.topBarHeight) this.effectiveTopBarHeight = height;
        this.topBarHeight = height;
        return this;
    }

    public boolean getHasCenterDrag() {
        return this.enableCenterDrag;
    }

    public WindowedContainer setEnableCenterDrag(boolean yesNo) {
        this.enableCenterDrag = yesNo;
        return this;
    }

    public Color getBorderColor() {
        return this.borderColor;
    }

    public WindowedContainer setBorderColor(Color color) {
        this.borderColor = color;
        return this;
    }

    public Color getCenterDragColor() {
        return this.centerDragWidgetColor;
    }

    public WindowedContainer setCenterDragColor(Color color) {
        this.centerDragWidgetColor = color;
        return this;
    }

    public Color getTitleColor() {
        return this.titleColor;
    }

    public WindowedContainer setTitleColor(Color color) {
        this.titleColor = color;
        return this;
    }

    @Override
    public boolean isVisible(WidgetContainer parent) {
        return this.visible;
    }

    public WindowedContainer setVisibility(boolean yesNo) {
        this.visible = yesNo;
        return this;
    }

}