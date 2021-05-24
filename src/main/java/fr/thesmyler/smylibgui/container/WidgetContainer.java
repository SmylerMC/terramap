package fr.thesmyler.smylibgui.container;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.util.Font;
import fr.thesmyler.smylibgui.util.RenderUtil;
import fr.thesmyler.smylibgui.util.Util;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget;

public abstract class WidgetContainer implements IWidget{

    protected TreeSet<IWidget> widgets = new TreeSet<IWidget>(
            (w2, w1) -> {
                if(w2 != null && w2.equals(w1)) return 0;
                if(w1 == null && w2 == null) return 0;
                if(w1 == null) return Integer.MIN_VALUE;
                if(w2 == null) return Integer.MAX_VALUE;
                int z1 = w1.getZ();
                int z2 = w2.getZ();
                if(z1 == Integer.MAX_VALUE) return Integer.MAX_VALUE;
                if(z1 == Integer.MIN_VALUE) return Integer.MIN_VALUE;
                if(z2 == Integer.MAX_VALUE) return Integer.MIN_VALUE;
                if(z2 == Integer.MIN_VALUE) return Integer.MAX_VALUE;
                int r = z1 - z2;
                return r == 0? w1.hashCode() - w2.hashCode(): r;
            }
            );
    protected List<ScheduledTask> scheduledForNextUpdate = new ArrayList<ScheduledTask>();

    protected int z;
    protected boolean doScissor = true;

    private float[] lastClickX = new float[Mouse.getButtonCount()];
    private float[] lastClickY = new float[Mouse.getButtonCount()];
    private long[] lastClickTime = new long[Mouse.getButtonCount()];

    private IWidget[] draggedWidget = new IWidget[Mouse.getButtonCount()];
    private float[] dClickX = new float[Mouse.getButtonCount()];
    private float[] dClickY = new float[Mouse.getButtonCount()];
    private long[] dClickT = new long[Mouse.getButtonCount()];

    private List<MouseAction> delayedActions = new ArrayList<MouseAction>();

    private IWidget focusedWidget;
    private IWidget hoveredWidget = null; // Used when drawing to check if a widget has already been considered as hovered

    private MenuWidget menuToShow = null;
    private float menuToShowX;
    private float menuToShowY;

    private Font font = SmyLibGui.DEFAULT_FONT;

    public WidgetContainer(int z) {
        for(int i=0; i<this.lastClickTime.length; i++) this.lastClickTime[i] = Long.MIN_VALUE;
        this.z = z;
    }

    public void init() {}

    public WidgetContainer addWidget(IWidget widget) {
        this.widgets.add(widget);
        return this;
    }

    public WidgetContainer removeWidget(IWidget widget) {
        if(this.widgets.contains(widget)) {
            widget.onRemoved();
        }
        this.widgets.remove(widget);
        return this;
    }

    public WidgetContainer removeAllWidgets() {
        for(IWidget widget: this.widgets) widget.onRemoved();
        this.widgets.clear();
        return this;
    }

    @Override
    public void onUpdate(float mouseX, float mouseY, @Nullable WidgetContainer parent) {

        long ctime = System.currentTimeMillis();
        int j = 0;
        while(j < this.scheduledForNextUpdate.size()) {
            ScheduledTask task = this.scheduledForNextUpdate.get(j);
            if(ctime > task.getWhen()) {
                task.execute();
                this.scheduledForNextUpdate.remove(j);
            } else {
                j++;
            }
        }

        for(MouseAction event: this.delayedActions) {
            boolean processed = false;
            for(IWidget widget: this.widgets) {
                boolean propagate = true;
                if(!this.isOverWidget(event.mouseX, event.mouseY, widget)) {
                    switch(event.type) {
                        case CLICK:
                            propagate = widget.onParentClick(event.mouseX, event.mouseY, event.button, this);
                            break;
                        case DOUBLE_CLICK:
                            propagate = widget.onParentDoubleClick(event.mouseX, event.mouseY, event.button, this);
                            break;
                        case RELEASE:
                            widget.onMouseReleased(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button, this.draggedWidget[event.button]);
                            if(widget.equals(this.draggedWidget[event.button])) {
                                this.draggedWidget[event.button] = null;
                            }
                            break;
                        default:
                            break;
                    }
                } else {
                    if(!(widget.takesInputs() && widget.isVisible(this))) continue;
                    switch(event.type) {
                        case CLICK:
                            propagate = widget.onClick(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button, this);
                            if(!propagate) this.focusedWidget = widget;
                            break;
                        case DOUBLE_CLICK:
                            propagate = widget.onDoubleClick(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button, this);
                            if(!propagate) this.focusedWidget = widget;
                            break;
                        case RELEASE:
                            this.draggedWidget[event.button] = null;
                            widget.onMouseReleased(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button, this.draggedWidget[event.button]);
                            break;
                        case SCROLL:
                            propagate = widget.onMouseWheeled(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button, this);
                            break;
                    }
                }
                if(!propagate) {
                    processed = true;
                    break;
                }
            }
            if(!processed && (event.type.equals(MouseActionType.CLICK) || event.type.equals(MouseActionType.CLICK))) {
                this.focusedWidget = null;
            }
        }
        for(int i=0; i < this.draggedWidget.length; i++) {
            if(this.draggedWidget[i] != null) {
                this.draggedWidget[i].onMouseDragged(this.lastClickX[i] - this.draggedWidget[i].getX(), this.lastClickY[i] - this.draggedWidget[i].getY(), this.dClickX[i], this.dClickY[i], i, this, this.dClickT[i]);
                this.dClickX[i] = 0;
                this.dClickY[i] = 0;
                this.dClickT[i] = 0;
            }
        }
        this.delayedActions.clear();
        for(IWidget w: this.widgets) w.onUpdate(mouseX - this.getX(), mouseY - this.getY(), this);

        if(this.menuToShow != null) {
            if(parent != null) parent.showMenu(this.getX() + this.menuToShowX, this.getY() + this.menuToShowY, this.menuToShow);
            else {
                this.addWidget(this.menuToShow);
                float w = this.menuToShow.getWidth();
                float h = this.menuToShow.getHeight();
                if(this.menuToShowX + w > this.getWidth()) this.menuToShowX -= w;
                if(this.menuToShowY + h > this.getHeight()) this.menuToShowY -= h;
                this.menuToShow.show(this.menuToShowX, this.menuToShowY);
            }
            this.menuToShow = null;
            this.menuToShowX = 0;
            this.menuToShowY = 0;
        }
    }

    @Override
    public void onKeyTyped(char typedChar, int keyCode, @Nullable WidgetContainer parent) {
        if(this.focusedWidget != null) {
            this.focusedWidget.onKeyTyped(typedChar, keyCode, this);
        }
    }

    @Override
    public boolean onClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        long ctime = System.currentTimeMillis();
        this.delayedActions.add(new MouseAction(MouseActionType.CLICK, mouseButton, mouseX, mouseY));
        this.lastClickTime[mouseButton] = ctime;
        this.lastClickX[mouseButton] = mouseX;
        this.lastClickY[mouseButton] = mouseY;
        return false;
    }

    @Override
    public boolean onDoubleClick(float mouseX, float mouseY, int mouseButton, @Nullable WidgetContainer parent) {
        long ctime = System.currentTimeMillis();
        this.delayedActions.add(new MouseAction(MouseActionType.DOUBLE_CLICK, mouseButton, mouseX, mouseY));
        this.lastClickTime[mouseButton] = ctime;
        this.lastClickX[mouseButton] = mouseX;
        this.lastClickY[mouseButton] = mouseY;
        return false;
    }

    @Override
    public void onMouseReleased(float mouseX, float mouseY, int mouseButton, @Nullable IWidget draggedWidget) {
        this.delayedActions.add(new MouseAction(MouseActionType.RELEASE, mouseButton, mouseX, mouseY));
    }

    @Override
    public void onMouseDragged(float mouseX, float mouseY, float dX, float dY, int button, @Nullable WidgetContainer parent, long dt) {
        if(this.draggedWidget[button] == null) {
            this.dClickX[button] = 0;
            this.dClickY[button] = 0;
            this.draggedWidget[button] = this.getWidgetUnder(mouseX, mouseY);
        }
        this.lastClickX[button] = mouseX;
        this.lastClickY[button] = mouseY;
        this.dClickX[button] += dX;
        this.dClickY[button] += dY;
        this.dClickT[button] += dt;
    }

    /**
     * 
     * @param x position relative to this screen's origin
     * @param y position relative to this screen's origin
     * @return
     */
    @Nullable 
    protected IWidget getWidgetUnder(float x, float y) {
        for(IWidget widget: this.widgets) if(this.isOverWidget(x, y, widget)) return widget;
        return null;
    }

    /**
     * 
     * @param x position relative to this screen's origin
     * @param y position relative to this screen's origin
     * @param widget
     * 
     * @return a boolean indicating whether or not the specified point is over a widget
     */
    protected boolean isOverWidget(float x, float y, IWidget widget) {
        return
                widget.getX() <= x
                && widget.getX() + widget.getWidth() >= x
                && widget.getY() <= y
                && widget.getY() + widget.getHeight() >= y
                && widget.isVisible(this);
    }

    @Override
    public boolean onMouseWheeled(float mouseX, float mouseY, int amount, @Nullable WidgetContainer parent) {
        this.delayedActions.add(new MouseAction(MouseActionType.SCROLL, amount, mouseX, mouseY));
        return false;
    }

    /**
     * Register the given widget to gain focus at the next screen update
     * 
     * @param widget
     */
    public void setFocus(IWidget widget) {
        this.scheduleForNextScreenUpdate(() -> {
            this.focusedWidget = widget;
        });
    }

    /**
     * Show that menu at next update, pass recursively to parent screen
     * 
     * @param x
     * @param y
     * @param menu
     */
    public void showMenu(float x, float y, MenuWidget menu) {
        this.menuToShow = menu;
        this.menuToShowX = x;
        this.menuToShowY = y;
    }

    /**
     * 
     * @return the current focused widget, goes recursive if it is a screen
     * 
     */
    public IWidget getFocusedWidget() {
        if(this.focusedWidget instanceof WidgetContainer)
            return ((WidgetContainer) this.focusedWidget).getFocusedWidget();
        return this.focusedWidget;
    }

    private class MouseAction {

        MouseActionType type;
        int button;
        float mouseX;
        float mouseY;

        public MouseAction(MouseActionType type, int button, float mouseX, float mouseY) {
            this.button = button;
            this.type = type;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }

    private enum MouseActionType {
        CLICK, RELEASE, DOUBLE_CLICK, SCROLL;
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, @Nullable WidgetContainer parent) {
        boolean wasScissor = RenderUtil.isScissorEnabled();
        if(this.doScissor) {
            RenderUtil.setScissorState(true);
            RenderUtil.pushScissorPos();
            RenderUtil.scissor(x, y, this.getWidth(), this.getHeight());
        }
        IWidget wf = null;
        if(screenHovered) {
            for(IWidget widget: this.widgets) {
                if(!widget.isVisible(this) || this.isOutsideScreen(widget) || !Util.doBoxesCollide(x + widget.getX(), y + widget.getY(), widget.getWidth(), widget.getHeight(), x, y, this.getWidth(), this.getHeight())) continue;
                if(this.isOverWidget(mouseX - x, mouseY - y, widget)) {
                    wf = widget;
                    break;
                }
            }
        }
        this.hoveredWidget = wf;
        this.widgets.descendingIterator().forEachRemaining((widget) -> {
            if(
                    !widget.isVisible(this) ||
                    this.isOutsideScreen(widget) ||
                    !Util.doBoxesCollide(
                            x + widget.getX(),
                            y + widget.getY(),
                            widget.getWidth(),
                            widget.getHeight(),
                            x,
                            y,
                            this.getWidth(),
                            this.getHeight()))
                return;
            widget.draw(x + widget.getX(), y + widget.getY(), mouseX, mouseY, widget.equals(this.hoveredWidget), screenFocused && widget.equals(this.focusedWidget), this);
        });
        if(this.doScissor) {
            RenderUtil.popScissorPos();
            RenderUtil.setScissorState(wasScissor);
        }
    }

    /**
     * Indicates whether or not the widget is worth rendering
     * 
     * @param widget
     * 
     * @return false if the widget overlaps with the screen, true otherwise
     */
    protected boolean isOutsideScreen(IWidget widget) {
        float minX = widget.getX();
        float minY = widget.getY();
        float maxX = minX + widget.getWidth();
        float maxY = minY + widget.getHeight();
        return maxX < 0 || minX > this.getWidth() || maxY < 0 || minY > this.getHeight();
    }

    /**
     * Allows the user to add callback to be run at the next screen update, before the widgets are updated
     * if you have to remove or add widgets after the screen is initialized, this is how to do it
     * 
     * @param run
     */
    public void scheduleForNextScreenUpdate(Runnable run) {
        this.scheduledForNextUpdate.add(new ScheduledTask(System.currentTimeMillis(), run));
    }

    public void scheduleWithDelay(Runnable run, long delay) {
        long t = System.currentTimeMillis() + delay;
        this.scheduledForNextUpdate.add(new ScheduledTask(t, run));
    }

    public void scheduleAtInterval(Runnable run, long delay) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                run.run();
                WidgetContainer.this.scheduleWithDelay(this, delay);
            }

        };
        this.scheduledForNextUpdate.add(new ScheduledTask(System.currentTimeMillis(), task));
    }

    public void scheduleAtUpdate(Runnable run) {
        this.scheduleAtInterval(run, 0);
    }

    public void cancellAllScheduled() {
        this.scheduledForNextUpdate.clear();
    }

    public Font getFont() {
        return this.font;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public boolean doesScissor() {
        return this.doScissor;
    }

    public void setDoScissor(boolean yesNo) {
        this.doScissor = yesNo;
    }

    @Nullable public IWidget getHoveredWidget() {
        if(this.hoveredWidget instanceof WidgetContainer)
            return ((WidgetContainer) this.hoveredWidget).getHoveredWidget();
        return this.hoveredWidget;
    }

}