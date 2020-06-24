package fr.thesmyler.smylibgui;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.widget.IWidget;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;

//TODO Keyboard events
//TODO Mouse wheel
//TODO Propagate events to child screens
public class Screen extends GuiScreen implements IWidget{

	public static final long DOUBLE_CLICK_DELAY = 500; //TODO get that from the system

	protected TreeSet<IWidget> widgets = new TreeSet<IWidget>((w1, w2) -> w1.getZ() - w2.getZ());
	private BackgroundType background;

	protected int x, y, z;

	private int[] lastClickX = new int[Mouse.getButtonCount()];
	private int[] lastClickY = new int[Mouse.getButtonCount()];
	private long[] lastClickTime = new long[Mouse.getButtonCount()];

	private IWidget[] draggedWidget = new IWidget[Mouse.getButtonCount()];
	private int[] dClickX = new int[Mouse.getButtonCount()];
	private int[] dClickY = new int[Mouse.getButtonCount()];

	private List<MouseAction> delayedActions = new ArrayList<MouseAction>();

	public Screen(int x, int y, int z, int width, int height, BackgroundType bg) {
		for(int i=0; i<this.lastClickTime.length; i++) this.lastClickTime[i] = Long.MIN_VALUE;
		this.background = bg;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
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
		super.updateScreen();
		for(MouseAction event: this.delayedActions) {
			for(IWidget widget: this.widgets) {

				if(this.isOverWidget(event.mouseX, event.mouseY, widget)) {
					if(!(widget.isEnabled() && widget.isVisible())) continue;
					switch(event.type) {
					case CLICK:
						widget.onClick(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button);
						break;
					case DOUBLE_CLICK:
						widget.onDoubleClick(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button);
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
					break;
				} else {
					boolean propagate = true;
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
					if(!propagate) break;
				}
			}
		}
		for(int i=0; i < this.draggedWidget.length; i++) {
			if(this.draggedWidget[i] != null) {
				this.draggedWidget[i].onMouseDragged(this.dClickX[i], this.dClickY[i], i);
				this.dClickX[i] = 0;
				this.dClickY[i] = 0;
			}
		}
		this.delayedActions.clear();
	}

	@Override
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.draw(this.x, this.y, mouseX, mouseY, null);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		long ctime = System.currentTimeMillis();
		if(ctime - this.lastClickTime[mouseButton] <= DOUBLE_CLICK_DELAY && this.lastClickX[mouseButton] == mouseX && this.lastClickY[mouseButton] == mouseY) {
			this.delayedActions.add(new MouseAction(MouseActionType.DOUBLE_CLICK, mouseButton, mouseX - this.getX(), mouseY - this.getY()));
		} else {
			this.delayedActions.add(new MouseAction(MouseActionType.CLICK, mouseButton, mouseX - this.getX(), mouseY - this.getY()));
		}
		this.lastClickTime[mouseButton] = ctime;
		this.lastClickX[mouseButton] = mouseX;
		this.lastClickY[mouseButton] = mouseY;
		super.mouseClicked(mouseX, mouseY, mouseButton);        
	}

	@Override
	protected void mouseReleased(int mouseX, int mouseY, int button) {
		super.mouseReleased(mouseX, mouseY, button);
		this.delayedActions.add(new MouseAction(MouseActionType.RELEASE, button, mouseX - this.getX(), mouseY - this.getY()));
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
	public void draw(int x, int y, int mouseX, int mouseY, Screen parent) { //TODO Draw vanilla widgets
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
		this.widgets.descendingIterator().forEachRemaining((widget) -> {
			if(!widget.isVisible()) return;
			widget.draw(x + widget.getX(), y + widget.getY(), mouseX, mouseY, this);
		}); {
		}
	}
	
	public FontRenderer getFont() {
		return this.fontRenderer;
	}

}