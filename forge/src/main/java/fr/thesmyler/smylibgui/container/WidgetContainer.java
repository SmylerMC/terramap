package fr.thesmyler.smylibgui.container;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Nullable;

import net.smyler.smylib.game.Key;
import fr.thesmyler.smylibgui.util.Scissor;
import fr.thesmyler.smylibgui.util.Util;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import net.smyler.smylib.gui.Font;

import static net.smyler.smylib.SmyLib.getGameClient;

/**
 * A {@link WidgetContainer} is a containers that stores widgets and redistributes events to them.
 * A {@link WidgetContainer} can also process scheduled tasks.
 * Each {@link fr.thesmyler.smylibgui.screen.Screen} has its own {@link WidgetContainer} into which widgets can be added.
 *
 * @see FlexibleWidgetContainer
 * @see ScrollableWidgetContainer
 * @see WindowedContainer
 * @see SlidingPanelWidget
 * @see TestingWidgetContainer
 *
 * @author SmylerMC
 *
 */
public abstract class WidgetContainer implements IWidget{

    protected final TreeSet<IWidget> widgets = new TreeSet<>(
            (w2, w1) -> {
                if (w2 != null && w2.equals(w1)) return 0;
                if (w1 == null && w2 == null) return 0;
                if (w1 == null) return -1;
                if (w2 == null) return 1;
                int r = Integer.compare(w1.getZ(), w2.getZ());
                return r == 0 ? w1.hashCode() - w2.hashCode() : r;
            }
    );
    
    private List<ScheduledTask> scheduledForUpdatePre = new ArrayList<>();
    private List<ScheduledTask> scheduledForUpdatePost = new ArrayList<>();

    private final int z;
    private boolean doScissor = true;

    private final float[] lastClickX = new float[getGameClient().getMouse().getButtonCount()];
    private final float[] lastClickY = new float[getGameClient().getMouse().getButtonCount()];
    private final long[] lastClickTime = new long[getGameClient().getMouse().getButtonCount()];

    private final IWidget[] draggedWidget = new IWidget[getGameClient().getMouse().getButtonCount()];
    private final float[] dClickX = new float[getGameClient().getMouse().getButtonCount()];
    private final float[] dClickY = new float[getGameClient().getMouse().getButtonCount()];
    private final long[] dClickT = new long[getGameClient().getMouse().getButtonCount()];

    private final List<MouseAction> delayedActions = new ArrayList<>();

    private IWidget focusedWidget;
    private IWidget hoveredWidget = null; // Used when drawing to check if a widget has already been considered as hovered

    private MenuWidget menuToShow = null;
    private float menuToShowX;
    private float menuToShowY;

    private Font font = getGameClient().getDefaultFont();

    public WidgetContainer(int z) {
        Arrays.fill(this.lastClickTime, Long.MIN_VALUE);
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
        this.processTasks(ctime, this.scheduledForUpdatePre);

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
                    if(!(widget.takesInputs() && widget.isVisible(this))) {
                        if (!widget.onInteractWhenNotTakingInputs(event.mouseX, event.mouseY, event.button, this)) {
                            processed = true;
                            break;
                        }
                    } else {
                        switch (event.type) {
                            case CLICK:
                                propagate = widget.onClick(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button, this);
                                if (!propagate) this.focusedWidget = widget;
                                break;
                            case DOUBLE_CLICK:
                                propagate = widget.onDoubleClick(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button, this);
                                if (!propagate) this.focusedWidget = widget;
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
                }
                if(!propagate) {
                    processed = true;
                    break;
                }
            }
            if(!processed && (event.type.equals(MouseActionType.CLICK) || event.type.equals(MouseActionType.DOUBLE_CLICK))) {
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
        float thisx = this.getX();
        float thisy = this.getY();
        for(IWidget w: this.widgets) w.onUpdate(mouseX - thisx, mouseY - thisy, this);

        if(this.menuToShow != null) {
            if(parent != null) parent.showMenu(thisx + this.menuToShowX, thisy + this.menuToShowY, this.menuToShow);
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
        
        this.processTasks(ctime, this.scheduledForUpdatePost);
    }
    
    private void processTasks(long currentTime, List<ScheduledTask> tasks) {
        int j = tasks.size();
        while(--j >= 0) {
            ScheduledTask task = tasks.get(j);
            if(currentTime > task.getWhen()) {
                task.execute();
                tasks.remove(j);
            }
        }
    }

    @Override
    public void onKeyTyped(char typedChar, Key key, @Nullable WidgetContainer parent) {
        if(this.focusedWidget != null) {
            this.focusedWidget.onKeyTyped(typedChar, key, this);
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
            IWidget widget = this.getWidgetUnder(mouseX, mouseY);
            if (widget != null && widget.takesInputs()) {
                this.draggedWidget[button] = widget;
            }
        }
        this.lastClickX[button] = mouseX;
        this.lastClickY[button] = mouseY;
        this.dClickX[button] += dX;
        this.dClickY[button] += dY;
        this.dClickT[button] += dt;
    }

    /**
     * @param x position relative to this screen's origin
     * @param y position relative to this screen's origin
     *
     * @return the {@link IWidget} with the highest z value at the given point
     */
    @Nullable 
    protected IWidget getWidgetUnder(float x, float y) {
        for(IWidget widget: this.widgets) if(this.isOverWidget(x, y, widget)) return widget;
        return null;
    }

    /**
     * 
     * @param x         position relative to this screen's origin
     * @param y         position relative to this screen's origin
     * @param widget    the widget to check
     * 
     * @return a boolean indicating whether the specified point is over a widget
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
     * @param widget    the widget to focus
     */
    public void setFocus(IWidget widget) {
        this.scheduleBeforeNextUpdate(() -> this.focusedWidget = widget);
    }

    /**
     * Show that menu at next update, pass recursively to parent screen
     * 
     * @param x     the X position where to show the menu
     * @param y     the y menu where to show the menu
     * @param menu  the {@link MenuWidget} to show
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

    private static class MouseAction {

        final MouseActionType type;
        final int button;
        final float mouseX;
        final float mouseY;

        public MouseAction(MouseActionType type, int button, float mouseX, float mouseY) {
            this.button = button;
            this.type = type;
            this.mouseX = mouseX;
            this.mouseY = mouseY;
        }
    }

    private enum MouseActionType {
        CLICK, RELEASE, DOUBLE_CLICK, SCROLL
    }

    @Override
    public int getZ() {
        return this.z;
    }

    @Override
    public void draw(float x, float y, float mouseX, float mouseY, boolean screenHovered, boolean screenFocused, @Nullable WidgetContainer parent) {
        if(this.doScissor) {
            Scissor.push();
            Scissor.setScissorState(true);
            Scissor.scissorIntersecting(x, y, this.getWidth(), this.getHeight());
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
            Scissor.pop();
        }
    }

    /**
     * Indicates whether the widget is worth rendering
     * 
     * @param widget    the widget to check
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
     * Schedule a task to be run the next time the container updates, before the rest of is updated.
     * 
     * @param run   the task to run
     */
    public void scheduleBeforeNextUpdate(Runnable run) {
        this.scheduledForUpdatePre.add(new ScheduledTask(System.currentTimeMillis(), run));
    }

    /**
     * Schedule a task to be run after a given delay when the container updates, before the rest is updated.
     * 
     * @param run   the task to run
     * @param delay delay to wait before executing the task in milliseconds
     */
    public void scheduleBeforeUpdate(Runnable run, long delay) {
        long t = System.currentTimeMillis() + delay;
        this.scheduledForUpdatePre.add(new ScheduledTask(t, run));
    }

    /**
     * Schedule a task to run at a given interval (as good as possible), when the container updates, before the rest is updated
     * 
     * @param run   the task to run
     * @param delay delay to wait between each execution of the task
     */
    public void scheduleAtIntervalBeforeUpdate(Runnable run, long delay) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                run.run();
                WidgetContainer.this.scheduleBeforeUpdate(this, delay);
            }

        };
        this.scheduledForUpdatePre.add(new ScheduledTask(System.currentTimeMillis(), task));
    }

    /**
     * Schedule a task to be run before each container update
     * 
     * @param run   the task to run
     */
    public void scheduleBeforeEachUpdate(Runnable run) {
        this.scheduleAtIntervalBeforeUpdate(run, 0);
    }

    /**
     * Schedule a task to be run after the next container update (could be just after the current one).
     * 
     * @param run   the task to run
     */
    public void scheduleAfterNextUpdate(Runnable run) {
        this.scheduledForUpdatePost.add(new ScheduledTask(System.currentTimeMillis(), run));
    }

    /**
     * Schedule a task to be run after a given delay when the container updates, after the rest is updated.
     * 
     * @param run   the task to run
     * @param delay delay to wait before executing the task in milliseconds
     */
    public void scheduleAfterUpdate(Runnable run, long delay) {
        long t = System.currentTimeMillis() + delay;
        this.scheduledForUpdatePost.add(new ScheduledTask(t, run));
    }

    /**
     * Schedule a task to run at a given interval (as good as possible), when the container updates, after the rest is updated
     * 
     * @param run   the task to run
     * @param delay delay to wait between each execution of the task
     */
    public void scheduleAtIntervalAfterUpdate(Runnable run, long delay) {
        Runnable task = new Runnable() {

            @Override
            public void run() {
                run.run();
                WidgetContainer.this.scheduleAfterUpdate(this, delay);
            }

        };
        this.scheduledForUpdatePost.add(new ScheduledTask(System.currentTimeMillis(), task));
    }

    /**
     * Schedule a task to be run after each container update
     * 
     * @param run   the task to run
     */
    public void scheduleAfterEachUpdate(Runnable run) {
        this.scheduleAtIntervalAfterUpdate(run, 0);
    }
    
    public void cancelAllScheduled() {
        // We create new instances instead of clearing because we could be iterating over the lists
        this.scheduledForUpdatePre = new ArrayList<>();
        this.scheduledForUpdatePost = new ArrayList<>();
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