package fr.thesmyler.smylibgui.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.text.FontRendererContainer;
import fr.thesmyler.smylibgui.widgets.IWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

//TODO Keyboard events
//TODO Mouse wheel
//TODO Propagate events to child screens
public class Screen extends GuiScreen implements IWidget{

	public static final long DOUBLE_CLICK_DELAY = 500; //TODO get that from the system

	protected TreeSet<IWidget> widgets = new TreeSet<IWidget>(
			(w2, w1) -> {
				if(w1 != null && w1.equals(w2)) return 0;
				if(w1 == null && w2 == null) return 0;
				if(w1 == null) return Integer.MIN_VALUE;
				if(w2 == null) return Integer.MAX_VALUE;
				int r = w1.getZ() - w2.getZ();
				return r == 0? 1: r;
			}
		);
	protected List<ScheduledTask> scheduledForNextUpdate = new ArrayList<ScheduledTask>();
	private BackgroundType background;
	private FontRendererContainer font;

	protected int x, y, z;

	private int[] lastClickX = new int[Mouse.getButtonCount()];
	private int[] lastClickY = new int[Mouse.getButtonCount()];
	private long[] lastClickTime = new long[Mouse.getButtonCount()];

	private IWidget[] draggedWidget = new IWidget[Mouse.getButtonCount()];
	private int[] dClickX = new int[Mouse.getButtonCount()];
	private int[] dClickY = new int[Mouse.getButtonCount()];
	
	private IWidget focusedWidget;

	private List<MouseAction> delayedActions = new ArrayList<MouseAction>();

	//TODO That should be private
	protected IWidget hoveredWidget = null; //Used when drawing to check if a widget has already been considered as hovered

	public Screen(int x, int y, int z, int width, int height, BackgroundType bg) {
		for(int i=0; i<this.lastClickTime.length; i++) this.lastClickTime[i] = Long.MIN_VALUE;
		this.background = bg;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.font = new FontRendererContainer(Minecraft.getMinecraft().fontRenderer);
	}

	public Screen(BackgroundType bg) {
		this(0, 0, 0, 10, 10, bg);
	}

	public Screen() {
		this(BackgroundType.DEFAULT);
	}

	public void initScreen() {}

	@Override
	public void initGui() {
		super.initGui();
		this.initScreen();
	}

	public Screen addWidget(IWidget widget) {
		this.widgets.add(widget);
		return this;
	}

	public Screen removeWidget(IWidget widget) {
		this.widgets.remove(widget);
		return this;
	}

	public Screen removeAllWidgets() {
		this.widgets.clear();
		return this;
	}

	@Override
	public void updateScreen() {
		this.onUpdate(null);
	}
	
	@Override
	public void onUpdate(@Nullable Screen parent) {
		super.updateScreen();
		
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
						break;
					default:
						break;
					}
				} else {
					if(!(widget.isEnabled() && widget.isVisible())) continue;
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
						widget.onMouseReleased(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button, this.draggedWidget[event.button]);
						if(widget.equals(this.draggedWidget[event.button])) {
							this.draggedWidget[event.button] = null;
						}
						break;
					default:
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
				this.draggedWidget[i].onMouseDragged(this.dClickX[i], this.dClickY[i], i, this);
				this.dClickX[i] = 0;
				this.dClickY[i] = 0;
			}
		}
		this.delayedActions.clear();
		for(IWidget w: this.widgets) w.onUpdate(this);
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.draw(this.x, this.y, mouseX, mouseY, true, true, null);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		long ctime = System.currentTimeMillis();
		if(ctime - this.lastClickTime[mouseButton] <= DOUBLE_CLICK_DELAY && this.lastClickX[mouseButton] == mouseX && this.lastClickY[mouseButton] == mouseY) {
			this.delayedActions.add(new MouseAction(MouseActionType.DOUBLE_CLICK, mouseButton, mouseX - this.getX(), mouseY - this.getY()));
		} else {
			this.delayedActions.add(new MouseAction(MouseActionType.CLICK, mouseButton, mouseX - this.getX(), mouseY - this.getY()));
		}
		this.lastClickTime[mouseButton] = ctime;
		this.lastClickX[mouseButton] = mouseX - this.getX();
		this.lastClickY[mouseButton] = mouseY - this.getY();
	}
	
	@Override //From GuiScreen
	protected void keyTyped(char typedChar, int keyCode) {
		this.onKeyTyped(typedChar, keyCode, null);
	}
	
	@Override //From IWidget
	public void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {
		//TODO Use tab to change the focused widget
		if(this.focusedWidget != null) {
			this.focusedWidget.onKeyTyped(typedChar, keyCode, this);
		}
	}
	
	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		long ctime = System.currentTimeMillis();
		this.delayedActions.add(new MouseAction(MouseActionType.CLICK, mouseButton, mouseX, mouseY));
		this.lastClickTime[mouseButton] = ctime;
		this.lastClickX[mouseButton] = mouseX;
		this.lastClickY[mouseButton] = mouseY;
		return false;
	}
	
	@Override
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, @Nullable Screen parent) {
		long ctime = System.currentTimeMillis();
		this.delayedActions.add(new MouseAction(MouseActionType.DOUBLE_CLICK, mouseButton, mouseX, mouseY));
		this.lastClickTime[mouseButton] = ctime;
		this.lastClickX[mouseButton] = mouseX;
		this.lastClickY[mouseButton] = mouseY;
		return false;
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int mouseButton) {
		super.mouseReleased(mouseX, mouseY, mouseButton);
		this.onMouseReleased(mouseX - this.getX(), mouseY - this.getY(), mouseButton, null);
	}
	
	@Override
	public void onMouseReleased(int mouseX, int mouseY, int mouseButton, @Nullable IWidget draggedWidget) {
		this.delayedActions.add(new MouseAction(MouseActionType.RELEASE, mouseButton, mouseX, mouseY));
	}

	@Override
	protected void mouseClickMove(int mouseX, int mouseY, int button, long timeSinceLastClick) {
		super.mouseClickMove(mouseX, mouseY, button, timeSinceLastClick);
		int dX = mouseX - this.lastClickX[button];
		int dY = mouseY - this.lastClickY[button];
		if(this.draggedWidget[button] == null) {
			this.dClickX[button] = 0;
			this.dClickY[button] = 0;
			this.draggedWidget[button] = this.getWidgetUnder(mouseX - this.getX(), mouseY - this.getY());
		}
		this.lastClickX[button] = mouseX;
		this.lastClickY[button] = mouseY;
		this.onMouseDragged(dX, dY, button, null);
	}
	
	@Override
	public void onMouseDragged(int dX, int dY, int button, @Nullable Screen parent) {
		if(this.draggedWidget[button] == null) {
			this.dClickX[button] = 0; //TODO
			this.dClickY[button] = 0;
		}
		this.dClickX[button] += dX;
		this.dClickY[button] += dY;
	}

	/**
	 * 
	 * @param x position relative to this screen's origin
	 * @param y position relative to this screen's origin
	 * @return
	 */
	@Nullable 
	protected IWidget getWidgetUnder(int x, int y) {
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
	protected boolean isOverWidget(int x, int y, IWidget widget) {
		return
				widget.getX() <= x
				&& widget.getX() + widget.getWidth() >= x
				&& widget.getY() <= y
				&& widget.getY() + widget.getHeight() >= y
				&& widget.isVisible();
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
	
	public IWidget getFocusedWidget() {
		return this.focusedWidget;
	}

	private class MouseAction {

		MouseActionType type;
		int button;
		int mouseX;
		int mouseY;

		public MouseAction(MouseActionType type, int button, int mouseX, int mouseY) {
			this.button = button;
			this.type = type;
			this.mouseX = mouseX;
			this.mouseY = mouseY;
		}
	}

	private enum MouseActionType {
		CLICK, RELEASE, DOUBLE_CLICK;
	}

	public enum BackgroundType {
		NONE, DEFAULT, DIRT, OVERLAY;
	}

	@Override
	public int getX() {
		return this.x;
	}

	@Override
	public int getY() {
		return this.y;
	}

	@Override
	public int getZ() {
		return this.z;
	}

	@Override
	public int getWidth() {
		return this.width;
	}

	@Override
	public int getHeight() {
		return this.height;
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean screenHovered, boolean screenFocused, @Nullable Screen parent) { //TODO Draw vanilla widgets
		switch(this.background) {
		case NONE: break;
		case DEFAULT:
			this.drawDefaultBackground(); //TODO Re-implement
			break;
		case DIRT:
			this.drawBackground(0); //TODO Re-implement
			break;
		case OVERLAY:
			this.drawGradientRect(this.x, this.y, this.x + this.width, this.y + this.height, 0x101010c0, 0x101010d0);
			break;
		}
		this.hoveredWidget = null;
		if(screenHovered) {
			for(IWidget widget: this.widgets) {
				if(!widget.isVisible()) continue;
				if(widget.isVisible() && this.isOverWidget(mouseX - x, mouseY - y, widget)) {
					this.hoveredWidget = widget;
					break;
				}
			}
		}
		this.widgets.descendingIterator().forEachRemaining((widget) -> {
			if(!widget.isVisible()) return;
			widget.draw(x + widget.getX(), y + widget.getY(), mouseX, mouseY, widget.equals(this.hoveredWidget), screenFocused && widget.equals(this.focusedWidget), this);
		}); {
		}
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
		Runnable task = () -> {
			run.run();
			this.scheduleWithDelay(run, delay);
		};
		this.scheduledForNextUpdate.add(new ScheduledTask(System.currentTimeMillis(), task));
	}

	public FontRendererContainer getFont() {
		return this.font;
	}

}