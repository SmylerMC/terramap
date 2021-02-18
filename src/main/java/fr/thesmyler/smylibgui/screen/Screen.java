package fr.thesmyler.smylibgui.screen;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import javax.annotation.Nullable;

import org.lwjgl.input.Mouse;

import fr.thesmyler.smylibgui.RenderUtil;
import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.Utils;
import fr.thesmyler.smylibgui.widgets.IWidget;
import fr.thesmyler.smylibgui.widgets.MenuWidget;
import fr.thesmyler.smylibgui.widgets.text.FontRendererContainer;
import fr.thesmyler.terramap.config.TerramapConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;

public class Screen extends GuiScreen implements IWidget{

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
	private BackgroundType background;
	private FontRendererContainer font;

	protected int x, y, z;

	private int lastMouseX, lastMouseY;
	private int[] lastClickX = new int[Mouse.getButtonCount()];
	private int[] lastClickY = new int[Mouse.getButtonCount()];
	private long[] lastClickTime = new long[Mouse.getButtonCount()];

	private IWidget[] draggedWidget = new IWidget[Mouse.getButtonCount()];
	private int[] dClickX = new int[Mouse.getButtonCount()];
	private int[] dClickY = new int[Mouse.getButtonCount()];

	private List<MouseAction> delayedActions = new ArrayList<MouseAction>();

	private IWidget focusedWidget;
	private IWidget hoveredWidget = null; //Used when drawing to check if a widget has already been considered as hovered
	private long startHoverTime;
	
	private MenuWidget menuToShow = null;
	private int menuToShowX;
	private int menuToShowY;
	
	public Screen(int x, int y, int z, int width, int height, BackgroundType bg) {
		for(int i=0; i<this.lastClickTime.length; i++) this.lastClickTime[i] = Long.MIN_VALUE;
		this.background = bg;
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
		this.font = new FontRendererContainer(Minecraft.getMinecraft().fontRenderer);
		this.mc = Minecraft.getMinecraft();
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
		if(this.widgets.contains(widget)) {
			widget.onRemoved();
		}
		this.widgets.remove(widget);
		return this;
	}

	public Screen removeAllWidgets() {
		for(IWidget widget: this.widgets) widget.onRemoved();
		this.widgets.clear();
		return this;
	}

	@Override
	public void updateScreen() {
		//this.onUpdate(null);
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
						widget.onMouseReleased(event.mouseX - widget.getX(), event.mouseY - widget.getY(), event.button, this.draggedWidget[event.button]);						break;
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
				this.draggedWidget[i].onMouseDragged(this.lastClickX[i] - this.draggedWidget[i].getX(), this.lastClickY[i] - this.draggedWidget[i].getY(), this.dClickX[i], this.dClickY[i], i, this);
				this.dClickX[i] = 0;
				this.dClickY[i] = 0;
			}
		}
		this.delayedActions.clear();
		for(IWidget w: this.widgets) w.onUpdate(this);
		
		if(this.menuToShow != null) {
			if(parent != null) parent.showMenu(this.x + this.menuToShowX, this.y + this.menuToShowY, this.menuToShow);
			else {
				this.addWidget(this.menuToShow);
				int w = this.menuToShow.getWidth();
				int h = this.menuToShow.getHeight();
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
	public void drawScreen(int mouseX, int mouseY, float partialTicks) {
		this.onUpdate(null);
		this.draw(this.x, this.y, mouseX, mouseY, true, true, null);
		super.drawScreen(mouseX, mouseY, partialTicks);
	}

	@Override
	protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
		super.mouseClicked(mouseX, mouseY, mouseButton);
		long ctime = System.currentTimeMillis();
		if(ctime - this.lastClickTime[mouseButton] <= TerramapConfig.CLIENT.doubleClickDelay && this.lastClickX[mouseButton] == mouseX && this.lastClickY[mouseButton] == mouseY) {
			this.delayedActions.add(new MouseAction(MouseActionType.DOUBLE_CLICK, mouseButton, mouseX - this.getX(), mouseY - this.getY()));
		} else {
			this.delayedActions.add(new MouseAction(MouseActionType.CLICK, mouseButton, mouseX - this.getX(), mouseY - this.getY()));
			this.lastClickTime[mouseButton] = ctime;
		}
		this.lastClickX[mouseButton] = mouseX - this.getX();
		this.lastClickY[mouseButton] = mouseY - this.getY();
	}

	@Override //From GuiScreen
	protected void keyTyped(char typedChar, int keyCode) {
		this.onKeyTyped(typedChar, keyCode, null);
	}

	@Override //From IWidget
	public void onKeyTyped(char typedChar, int keyCode, @Nullable Screen parent) {
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
		this.onMouseDragged(mouseX - this.getX(), mouseY - this.getY(), dX, dY, button, null);
	}

	@Override
	public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int button, @Nullable Screen parent) {
		if(this.draggedWidget[button] == null) {
			this.dClickX[button] = 0;
			this.dClickY[button] = 0;
			this.draggedWidget[button] = this.getWidgetUnder(mouseX, mouseY);
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
				&& widget.isVisible(this);
	}

	@Override
	public void handleMouseInput() throws IOException {

		this.lastMouseX = Mouse.getEventX() * this.width / this.mc.displayWidth;
		this.lastMouseY = this.height - Mouse.getEventY() * this.height / this.mc.displayHeight - 1;

		super.handleMouseInput();

		int scroll = Mouse.getDWheel();
		if(scroll != 0) this.onMouseWheeled(this.lastMouseX, this.lastMouseY, scroll, null);
	}

	@Override
	public boolean onMouseWheeled(int mouseX, int mouseY, int amount, @Nullable Screen parent) {
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
	public void showMenu(int x, int y, MenuWidget menu) {
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
		if(this.focusedWidget instanceof Screen)
			return ((Screen) this.focusedWidget).getFocusedWidget();
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
		CLICK, RELEASE, DOUBLE_CLICK, SCROLL;
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
	public void draw(int x, int y, int mouseX, int mouseY, boolean screenHovered, boolean screenFocused, @Nullable Screen parent) {
		RenderUtil.setScissorState(true);
		RenderUtil.pushScissorPos();
		RenderUtil.scissor(x, y, this.getWidth(), this.getHeight());
		boolean mouseMoved = mouseX - x != this.lastMouseX && mouseY - y != this.lastMouseY;
		switch(this.background) {
		case NONE: break;
		case DEFAULT:
			this.drawDefaultBackground();
			break;
		case DIRT:
			this.drawBackground(0);
			break;
		case OVERLAY:
			this.drawGradientRect(x, y, x + this.width, y + this.height, 0x101010c0, 0x101010d0);
			break;
		}
		IWidget lastHoveredWidget = this.getHoveredWidget();
		IWidget wf = null;
		if(screenHovered) {
			for(IWidget widget: this.widgets) {
				if(!widget.isVisible(this) || this.isOutsideScreen(widget) || !Utils.doBoxesCollide(x + widget.getX(), y + widget.getY(), widget.getWidth(), widget.getHeight(), x, y, this.width, this.height)) continue;
				if(this.isOverWidget(mouseX - x, mouseY - y, widget)) {
					wf = widget;
					break;
				}
			}
		}
		this.hoveredWidget = wf;
		this.widgets.descendingIterator().forEachRemaining((widget) -> {
			if(!widget.isVisible(this) || this.isOutsideScreen(widget)|| !Utils.doBoxesCollide(x + widget.getX(), y + widget.getY(), widget.getWidth(), widget.getHeight(), x, y, this.width, this.height)) return;
			widget.draw(x + widget.getX(), y + widget.getY(), mouseX, mouseY, widget.equals(this.hoveredWidget), screenFocused && widget.equals(this.focusedWidget), this);
		});
		IWidget w = this.getHoveredWidget();
		if(mouseMoved || (w != null && !w.equals(lastHoveredWidget))) {
			this.startHoverTime = System.currentTimeMillis();
		}
		if(
			   parent == null
			&& w != null
			&& w.getTooltipText() != null
			&& !w.getTooltipText().isEmpty()
			&& this.startHoverTime + w.getTooltipDelay() <= System.currentTimeMillis()
		) {
			this.drawHoveringText(w.getTooltipText(), mouseX, mouseY);
		}
		this.lastMouseX = mouseX - x;
		this.lastMouseY = mouseY - y;
		RenderUtil.popScissorPos();
		RenderUtil.setScissorState(false);
	}
	
	/**
	 * Indicates whether or not the widget is worth rendering
	 * 
	 * @param widget
	 * 
	 * @return false if the widget overlaps with the screen, true otherwise
	 */
	protected boolean isOutsideScreen(IWidget widget) {
		int minX = widget.getX();
		int minY = widget.getY();
		int maxX = minX + widget.getWidth();
		int maxY = minY + widget.getHeight();
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
				Screen.this.scheduleWithDelay(this, delay);
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

	public FontRendererContainer getFont() {
		return this.font;
	}

	@Nullable public IWidget getHoveredWidget() {
		if(this.hoveredWidget instanceof Screen)
			return ((Screen) this.hoveredWidget).getHoveredWidget();
		return this.hoveredWidget;
	}

	@Override
	public void drawBackground(int tint) {
		GlStateManager.disableLighting();
		GlStateManager.disableFog();
		Tessellator tessellator = Tessellator.getInstance();
		BufferBuilder bufferBuilder = tessellator.getBuffer();
		Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGui.OPTIONS_BACKGROUND);
		GlStateManager.color(1, 1, 1, 1);
		bufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX_COLOR);
		bufferBuilder.pos(x, y + this.height, 0).tex(0, (float)this.height / 32 + tint).color(64, 64, 64, 255).endVertex();
		bufferBuilder.pos(x + this.width, y + this.height, 0).tex((float)this.width / 32, (float)this.height / 32 + tint).color(64, 64, 64, 255).endVertex();
		bufferBuilder.pos(x + this.width, y, 0).tex((float)this.width / 32, tint).color(64, 64, 64, 255).endVertex();
		bufferBuilder.pos(x, y, 0).tex(0, tint).color(64, 64, 64, 255).endVertex();
		tessellator.draw();
	}
	
	@Override
	public void drawVerticalLine(int startX, int startY, int endY, int color) {
		super.drawVerticalLine(startX, startY, endY, color);
	}
	
	@Override
	public void drawHorizontalLine(int startX, int startY, int endY, int color) {
		super.drawHorizontalLine(startX, startY, endY, color);
	}


}