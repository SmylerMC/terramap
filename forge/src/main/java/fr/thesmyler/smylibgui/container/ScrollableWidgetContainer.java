package fr.thesmyler.smylibgui.container;

import net.smyler.smylib.Color;
import fr.thesmyler.smylibgui.util.RenderUtil;
import net.smyler.smylib.gui.containers.FlexibleWidgetContainer;
import net.smyler.smylib.gui.widgets.Widget;
import fr.thesmyler.smylibgui.widgets.ScrollbarWidget;
import fr.thesmyler.smylibgui.widgets.ScrollbarWidget.ScrollbarOrientation;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget;
import fr.thesmyler.smylibgui.widgets.buttons.TexturedButtonWidget.IncludedTexturedButtons;
import net.smyler.smylib.gui.DrawContext;
import net.smyler.smylib.gui.containers.WidgetContainer;

import static net.smyler.smylib.Preconditions.checkArgument;

/**
 * A {@link FlexibleWidgetContainer} which contains another {@link FlexibleWidgetContainer} and handles the difference in size
 * between the two using scrollbars.
 *
 * @author SmylerMC
 */
public class ScrollableWidgetContainer extends FlexibleWidgetContainer {
    
    private final FlexibleWidgetContainer content;
    private final ScrollbarWidget verticalScrollbar = new ScrollbarWidget(Integer.MAX_VALUE, ScrollbarOrientation.VERTICAL);
    private final ScrollbarWidget horizontalScrollbar = new ScrollbarWidget(Integer.MAX_VALUE, ScrollbarOrientation.HORIZONTAL);
    private final TexturedButtonWidget button = new TexturedButtonWidget(Integer.MAX_VALUE, IncludedTexturedButtons.BLANK_15);

    private boolean verticalWasVisibleLastUpdate = false;
    private boolean horizontalWasVisibleLastUpdate = false;
    
    private Color backgroundColor = Color.TRANSPARENT;
    private Color contourColor = Color.TRANSPARENT;
    private float contourSize = 2f;
    
    public ScrollableWidgetContainer(float x, float y, int z, float width, float height, FlexibleWidgetContainer content) {
        super(x, y, z, width, height);
        checkArgument(content.getZ() != this.verticalScrollbar.getZ(), "Invalid z level for content");
        this.content = content;
        this.addWidget(this.content);
        this.addWidget(this.verticalScrollbar);
        this.addWidget(this.horizontalScrollbar);
        this.addWidget(this.button);
        this.init();
    }
    
    @Override
    public void onUpdate(float mouseX, float mouseY, WidgetContainer parent) {
        super.onUpdate(mouseX, mouseY, parent);
        float thisWidth = this.getWidth(), thisHeight = this.getHeight();
        float contWidth = this.content.getWidth(), contHeight = this.content.getHeight();
        this.horizontalScrollbar.setViewPort((thisWidth - 15f) / contWidth);
        this.verticalScrollbar.setViewPort((thisHeight - 15f)/ contHeight);
        boolean vertVisible = this.verticalScrollbar.isVisible(this);
        boolean horzVisible = this.horizontalScrollbar.isVisible(this);
        this.content.setPosition(
                horzVisible ? (thisWidth - contWidth - 15) * this.horizontalScrollbar.getProgress(): 0f,
                vertVisible ? (thisHeight - contHeight - 15) * this.verticalScrollbar.getProgress(): 0f
            );
        if(this.verticalWasVisibleLastUpdate != vertVisible || this.horizontalWasVisibleLastUpdate != horzVisible) this.updateScrollbars();
        this.verticalWasVisibleLastUpdate = vertVisible;
        this.horizontalWasVisibleLastUpdate = horzVisible;
    }

    @Override
    public boolean onMouseWheeled(float mouseX, float mouseY, int amount, WidgetContainer parent) {
        boolean vertVis = this.verticalScrollbar.isVisible(this);
        boolean horzVis = this.horizontalScrollbar.isVisible(this);
        if(horzVis && !vertVis) {
            if(amount > 0) this.horizontalScrollbar.scrollBackward();
            else this.horizontalScrollbar.scrollForward();
        } else if(vertVis) {
            if(amount > 0) this.verticalScrollbar.scrollBackward();
            else this.verticalScrollbar.scrollForward();
        }
        return super.onMouseWheeled(mouseX, mouseY, amount, parent);
    }

    @Override
    public void init() {
        this.updateScrollbars();
    }
    
    public void updateScrollbars() {
        float width = this.getWidth(), height = this.getHeight();
        this.verticalScrollbar.setPosition(width - this.verticalScrollbar.getWidth(), 0f)
            .setLength(height - this.horizontalScrollbar.getHeight());
        this.horizontalScrollbar.setPosition(0f, height - this.horizontalScrollbar.getHeight())
            .setLength(width - this.verticalScrollbar.getWidth());
        if(!this.verticalScrollbar.isVisible(this)) this.horizontalScrollbar.setLength(this.getWidth());
        if(!this.horizontalScrollbar.isVisible(this)) this.verticalScrollbar.setLength(this.getHeight());
        this.verticalWasVisibleLastUpdate = this.verticalScrollbar.isVisible(this);
        this.horizontalWasVisibleLastUpdate = this.horizontalScrollbar.isVisible(this);
        this.button.setX(width - 15).setY(height - 15).setVisibility(this.horizontalWasVisibleLastUpdate && this.verticalWasVisibleLastUpdate);
    }

    @Override
    public void draw(DrawContext context, float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, WidgetContainer parent) {
        context.drawRectangle(x, y, x + this.getWidth(), y + this.getHeight(), this.backgroundColor);
        super.draw(context, x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
        RenderUtil.drawRectWithContour(x, y, x + this.getWidth(), y + this.getHeight(), Color.TRANSPARENT, this.contourSize, this.contourColor);
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public ScrollableWidgetContainer setBackgroundColor(Color backgroundColor) {
        this.backgroundColor = backgroundColor;
        return this;
    }

    public Color getContourColor() {
        return contourColor;
    }

    public ScrollableWidgetContainer setContourColor(Color contourColor) {
        this.contourColor = contourColor;
        return this;
    }

    public float getContourSize() {
        return contourSize;
    }

    public ScrollableWidgetContainer setContourSize(float contourSize) {
        this.contourSize = contourSize;
        return this;
    }

    public FlexibleWidgetContainer getContent() {
        return content;
    }

    /**
     * @deprecated Content should be added to a {@link ScrollableWidgetContainer} using its content container,
     * retrievable via {@link ScrollableWidgetContainer#getContent()}
     */
    @Override
    @Deprecated
    public WidgetContainer addWidget(Widget widget) {
        return super.addWidget(widget);
    }

    /**
     * @deprecated Content should be added to a {@link ScrollableWidgetContainer} using its content container,
     * retrievable via {@link ScrollableWidgetContainer#getContent()}. Removing content from it should therefore be avoided.
     *
     * @throws IllegalStateException if trying to remove the content widget container
     */
    @Override
    @Deprecated
    public WidgetContainer removeWidget(Widget widget) {
        if (this.content.equals(widget))
            throw new IllegalStateException("Cannot remove the content container from a scrollable content container!");
        return super.removeWidget(widget);
    }
    /**
     * @deprecated Content should be added to a {@link ScrollableWidgetContainer} using its content container,
     * retrievable via {@link ScrollableWidgetContainer#getContent()}. Removing content from it should therefore be avoided.
     *
     * @throws IllegalStateException because calling this method would remove the content widget container
     */
    @Override
    public WidgetContainer removeAllWidgets() {
        throw new IllegalStateException("Cannot remove the content container from a scrollable content container!");
    }
}
