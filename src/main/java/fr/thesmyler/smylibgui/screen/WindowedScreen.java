package fr.thesmyler.smylibgui.screen;

import org.lwjgl.input.Cursor;
import org.lwjgl.input.Mouse;

import com.google.common.base.Preconditions;

import fr.thesmyler.smylibgui.Cursors;
import fr.thesmyler.smylibgui.widgets.IWidget;
import net.minecraft.client.gui.GuiScreen;

public class WindowedScreen extends Screen {

	private int borderWidth = 5;
	private int effectiveBorderWidth = borderWidth;
	private int topBarHeight = 12;
	private int effectiveTopBarHeight = topBarHeight;
	private int minInnerWidth = 20;
	private int maxInnerWidth = Integer.MAX_VALUE;
	private int minInnerHeight = 20;
	private int maxInnerHeight = Integer.MAX_VALUE;
	private boolean allowVerticalResize = true;
	private boolean allowHorizontalResize = true;
	private boolean enableCustomCursors = true;
	private boolean enableCenterDrag = false;
	private int borderColor = 0xB0000000;
	private int centerDragWidgetColor = 0x60000000;
	private int titleColor = 0xFFFFFFFF;
	private Screen subScreen;
	private String windowTitle;
	private boolean visible = true;
	
	public WindowedScreen(BackgroundType back, Screen subScreen, String title, int z) {
		super(back);
		this.subScreen = subScreen;
		this.windowTitle = title;
		this.z = z;
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
	}

	@Override
	public void draw(
			int x,
			int y,
			int mouseX,
			int mouseY,
			boolean screenHovered,
			boolean screenFocused,
			Screen parent) {
		super.draw(x, y, mouseX, mouseY, screenHovered, screenFocused, parent);
	}
	
	
	private void updateSubScreen() {
		this.subScreen.x = this.effectiveBorderWidth;
		this.subScreen.y = this.effectiveBorderWidth + this.effectiveTopBarHeight;
		this.subScreen.width = this.width - this.effectiveBorderWidth * 2;
		this.subScreen.height = this.height - this.effectiveBorderWidth * 2 -  this.effectiveTopBarHeight;
		this.subScreen.initScreen();
	}
	

	private abstract class BaseDecorationWidget implements IWidget {
		
		private boolean lastHovered = false;
		private Cursor cursor = null;
		
		public BaseDecorationWidget(Cursor cursor) {
			this.cursor = cursor;
		}
		
		@Override
		public int getZ() {
			return Integer.MAX_VALUE - 1;
		}
		
		protected abstract boolean isCursorEnabled();
		
		protected abstract int getBackgroundColor();
		
		@Override
		public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
			GuiScreen.drawRect(x, y, x + this.getWidth(), y + this.getHeight(), this.getBackgroundColor());
			if(this.lastHovered != hovered && !Mouse.isButtonDown(0)) {
				if(hovered && this.isCursorEnabled() && WindowedScreen.this.enableCustomCursors) Cursors.trySetCursor(this.cursor);
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
		public boolean isVisible(Screen parent) {
			return WindowedScreen.this.effectiveBorderWidth > 0;
		}
		
		@Override
		protected int getBackgroundColor() {
			return WindowedScreen.this.borderColor;
		}

				
	}
	
	private class RightBorderBar extends BorderWidget {
		
		public RightBorderBar() {
			super(Cursors.CURSOR_RESIZE_HORIZONTAL);
		}

		@Override
		public int getX() {
			return WindowedScreen.this.width - WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getY() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.height - WindowedScreen.this.effectiveBorderWidth * 2;
		}
		
		@Override
		public boolean takesInputs() {
			return WindowedScreen.this.allowHorizontalResize;
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0 || !WindowedScreen.this.allowHorizontalResize) return;
			WindowedScreen.this.trySetInnerWidth(WindowedScreen.this.getInnerWidth() + dX);
			WindowedScreen.this.updateSubScreen();
		}

		@Override
		protected boolean isCursorEnabled() {
			return WindowedScreen.this.allowHorizontalResize;
		}

	}

	private class LeftBorderBar extends BorderWidget {

		public LeftBorderBar() {
			super(Cursors.CURSOR_RESIZE_HORIZONTAL);
		}

		@Override
		public int getX() {
			return 0;
		}

		@Override
		public int getY() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.height - WindowedScreen.this.effectiveBorderWidth * 2;
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0 || !WindowedScreen.this.allowHorizontalResize) return;
			if(WindowedScreen.this.trySetInnerWidth(WindowedScreen.this.getInnerWidth() - dX)) WindowedScreen.this.x += dX;
			WindowedScreen.this.updateSubScreen();
		}

		@Override
		protected boolean isCursorEnabled() {
			return WindowedScreen.this.allowHorizontalResize;
		}

	}

	private class BottomBorderBar extends BorderWidget {

		public BottomBorderBar() {
			super(Cursors.CURSOR_RESIZE_VERTICAL);
		}

		@Override
		public int getX() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getY() {
			return WindowedScreen.this.height - WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.width - WindowedScreen.this.effectiveBorderWidth * 2;
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0 || !WindowedScreen.this.allowVerticalResize) return;
			WindowedScreen.this.trySetInnerHeight(WindowedScreen.this.getInnerHeight() + dY);
			WindowedScreen.this.updateSubScreen();
		}

		@Override
		protected boolean isCursorEnabled() {
			return WindowedScreen.this.allowVerticalResize;
		}

	}

	private class TopBorderBar extends BorderWidget {

		public TopBorderBar() {
			super(Cursors.CURSOR_RESIZE_VERTICAL);
		}

		@Override
		public int getX() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getY() {
			return 0;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.width - WindowedScreen.this.effectiveBorderWidth * 2;
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0 || !WindowedScreen.this.allowVerticalResize) return;
			if(WindowedScreen.this.trySetInnerHeight(WindowedScreen.this.getInnerHeight() - dY)) WindowedScreen.this.y += dY;
			WindowedScreen.this.updateSubScreen();
		}

		@Override
		protected boolean isCursorEnabled() {
			return WindowedScreen.this.allowVerticalResize;
		}

	}
	
	private class UpperLeftCorner extends BorderWidget {

		public UpperLeftCorner() {
			super(Cursors.CURSOR_RESIZE_DIAGONAL_1);
		}

		@Override
		public int getX() {
			return 0;
		}

		@Override
		public int getY() {
			return 0;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0) return;
			if(WindowedScreen.this.allowHorizontalResize && WindowedScreen.this.trySetInnerWidth(WindowedScreen.this.getInnerWidth() - dX)) WindowedScreen.this.x += dX;
			if(WindowedScreen.this.allowVerticalResize && WindowedScreen.this.trySetInnerHeight(WindowedScreen.this.getInnerHeight() - dY)) WindowedScreen.this.y += dY;
			WindowedScreen.this.updateSubScreen();
		}

		@Override
		protected boolean isCursorEnabled() {
			return WindowedScreen.this.allowHorizontalResize || WindowedScreen.this.allowVerticalResize;
		}

	}
	
	private class LowerLeftCorner extends BorderWidget {

		public LowerLeftCorner() {
			super(Cursors.CURSOR_RESIZE_DIAGONAL_2);
		}

		@Override
		public int getX() {
			return 0;
		}

		@Override
		public int getY() {
			return WindowedScreen.this.height - WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0) return;
			if(WindowedScreen.this.allowVerticalResize) WindowedScreen.this.trySetInnerHeight(WindowedScreen.this.getInnerHeight() + dY);
			if(WindowedScreen.this.allowHorizontalResize && WindowedScreen.this.trySetInnerWidth(WindowedScreen.this.getInnerWidth() - dX)) WindowedScreen.this.x += dX;
			WindowedScreen.this.updateSubScreen();
		}

		@Override
		protected boolean isCursorEnabled() {
			return WindowedScreen.this.allowHorizontalResize || WindowedScreen.this.allowVerticalResize;
		}

	}
	
	private class LowerRightCorner extends BorderWidget {

		public LowerRightCorner() {
			super(Cursors.CURSOR_RESIZE_DIAGONAL_1);
		}

		@Override
		public int getX() {
			return WindowedScreen.this.width - WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getY() {
			return WindowedScreen.this.height - WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0) return;
			if(WindowedScreen.this.allowHorizontalResize) WindowedScreen.this.trySetInnerWidth(WindowedScreen.this.getInnerWidth() + dX);
			if(WindowedScreen.this.allowVerticalResize) WindowedScreen.this.trySetInnerHeight(WindowedScreen.this.getInnerHeight() + dY);
			WindowedScreen.this.updateSubScreen();
		}

		@Override
		protected boolean isCursorEnabled() {
			return WindowedScreen.this.allowHorizontalResize || WindowedScreen.this.allowVerticalResize;
		}

	}
	
	private class UpperRightCorner extends BorderWidget {

		public UpperRightCorner() {
			super(Cursors.CURSOR_RESIZE_DIAGONAL_2);
		}

		@Override
		public int getX() {
			return WindowedScreen.this.width - WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getY() {
			return 0;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0) return;
			if(WindowedScreen.this.allowHorizontalResize) WindowedScreen.this.trySetInnerWidth(WindowedScreen.this.getInnerWidth() + dX);
			if(WindowedScreen.this.allowVerticalResize && WindowedScreen.this.trySetInnerHeight(WindowedScreen.this.getInnerHeight() - dY)) WindowedScreen.this.y += dY;
			WindowedScreen.this.updateSubScreen();
		}

		@Override
		protected boolean isCursorEnabled() {
			return WindowedScreen.this.allowHorizontalResize || WindowedScreen.this.allowVerticalResize;
		}

	}

	private class TopBar extends BaseDecorationWidget {

		public TopBar() {
			super(Cursors.CURSOR_MOVE);
		}

		@Override
		public int getX() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getY() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.width - WindowedScreen.this.effectiveBorderWidth * 2;
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.effectiveTopBarHeight;
		}

		@Override
		public void draw(int x, int y, int mouseX, int mouseY, boolean hovered, boolean focused, Screen parent) {
			super.draw(x, y, mouseX, mouseY, hovered, focused, parent);
			int width = this.getWidth();
			String toDraw = parent.getFont().trimStringToWidth(WindowedScreen.this.windowTitle, this.getWidth());
			int titleY = y + (this.getHeight() - WindowedScreen.this.effectiveBorderWidth - parent.getFont().FONT_HEIGHT) / 2 + 1;
			parent.getFont().drawCenteredString(x + width / 2, titleY, toDraw, WindowedScreen.this.titleColor, true);
		}

		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0) return;
			WindowedScreen.this.x += dX;
			WindowedScreen.this.y += dY;
		}

		@Override
		protected boolean isCursorEnabled() {
			return true;
		}

		@Override
		public boolean isVisible(Screen parent) {
			return WindowedScreen.this.effectiveTopBarHeight > 0;
		}

		@Override
		protected int getBackgroundColor() {
			return WindowedScreen.this.borderColor;
		}

	}
	
	private class CenterDragWidget extends BaseDecorationWidget {

		public CenterDragWidget() {
			super(Cursors.CURSOR_MOVE);
		}

		@Override
		public int getX() {
			return WindowedScreen.this.effectiveBorderWidth;
		}

		@Override
		public int getY() {
			return WindowedScreen.this.effectiveBorderWidth + WindowedScreen.this.effectiveTopBarHeight;
		}

		@Override
		public int getWidth() {
			return WindowedScreen.this.getInnerWidth();
		}

		@Override
		public int getHeight() {
			return WindowedScreen.this.getInnerHeight();
		}

		@Override
		protected boolean isCursorEnabled() {
			return true;
		}
		
		@Override
		public void onMouseDragged(int mouseX, int mouseY, int dX, int dY, int mouseButton, Screen parent) {
			if(mouseButton != 0) return;
			WindowedScreen.this.x += dX;
			WindowedScreen.this.y += dY;
		}
		
		@Override
		public boolean onClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
			return false;
		}

		@Override
		public boolean onParentClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
			return false;
		}

		@Override
		public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
			return false;
		}

		@Override
		public boolean onParentDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
			return false;
		}

		@Override
		public boolean onMouseWheeled(int mouseX, int mouseY, int amount, Screen parent) {
			return false;
		}

		@Override
		public boolean isVisible(Screen parent) {
			return WindowedScreen.this.enableCenterDrag;
		}

		@Override
		protected int getBackgroundColor() {
			return WindowedScreen.this.centerDragWidgetColor;
		}
		
	}
	
	public int getInnerWidth() {
		return this.width - 2*this.effectiveBorderWidth;
	}
	
	public int getInnerHeight() {
		return this.height - 2*this.effectiveBorderWidth - this.effectiveTopBarHeight;
	}
	
	private boolean trySetInnerWidth(int width) {
		if(width >= this.minInnerWidth && width <= this.maxInnerWidth) {
			this.width = width + this.effectiveBorderWidth * 2;
			return true;
		}
		return false;
	}
	
	private boolean trySetInnerHeight(int height) {
		if(height >= this.minInnerHeight && height <= this.maxInnerHeight) {
			this.height = height + this.effectiveBorderWidth * 2 + this.effectiveTopBarHeight;
			return true;
		}
		return false;
	}
	
	public WindowedScreen trySetInnerDimensions(int width, int height) {
		boolean update = false;
		if(width >= this.minInnerWidth && width <= this.maxInnerWidth) {
			this.width = width + this.effectiveBorderWidth * 2;
			update = true;
		}
		if(height >= this.minInnerHeight && height <= this.maxInnerHeight) {
			this.height = height + this.effectiveBorderWidth * 2 + this.effectiveTopBarHeight;
			update = true;
		}
		if(update) this.updateSubScreen();
		return this;
	}

	public WindowedScreen setX(int x) {
		this.x = x;
		return this;
	}

	public WindowedScreen setY(int y) {
		this.y = y;
		return this;
	}

	public WindowedScreen setWidth(int w) {
		this.width = w;
		this.updateSubScreen();
		return this;
	}

	public WindowedScreen setHeight(int h) {
		this.height = h;
		this.updateSubScreen();
		return this;
	}
	
	public WindowedScreen setMinInnerWidth(int width) {
		Preconditions.checkArgument(width > 0, "inner width needs to be striclty positive");
		this.minInnerWidth = width;
		return this;
	}
	
	public WindowedScreen setMaxInnerWidth(int width) {
		Preconditions.checkArgument(width > 0, "inner width needs to be striclty positive");
		this.minInnerWidth = width;
		return this;
	}
	
	public WindowedScreen setMinInnerHeight(int height) {
		Preconditions.checkArgument(height > 0, "inner height needs to be striclty positive");
		this.minInnerHeight = height;
		return this;
	}
	
	public WindowedScreen setMaxInnerHeight(int height) {
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
	
	public int getBorderWidth() {
		return this.effectiveBorderWidth;
	}
	
	public WindowedScreen setBorderWidth(int width) {
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

	public WindowedScreen setBorderless(boolean yesNo) {
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
	
	public WindowedScreen setAllowVerticalResize(boolean yesNo) {
		this.allowVerticalResize = yesNo;
		return this;
	}
	
	public WindowedScreen setAllowHorizontalResize(boolean yesNo) {
		this.allowHorizontalResize = yesNo;
		return this;
	}
	
	public boolean hasTopBar() {
		return this.effectiveTopBarHeight > 0;
	}
	
	public WindowedScreen setEnableTopBar(boolean yesNo) {
		if(yesNo) this.effectiveTopBarHeight = this.topBarHeight;
		else this.effectiveTopBarHeight = 0;
		this.updateSubScreen();
		return this;
	}
	
	public WindowedScreen setTopBarHeight(int height) {
		Preconditions.checkArgument(height > 0, "title bar height must be strictly positive");
		if(this.effectiveTopBarHeight == this.topBarHeight) this.effectiveTopBarHeight = height;
		this.topBarHeight = height;
		return this;
	}
	
	public boolean getHasCenterDrag() {
		return this.enableCenterDrag;
	}
	
	public WindowedScreen setEnableCenterDrag(boolean yesNo) {
		this.enableCenterDrag = yesNo;
		return this;
	}
	
	public int getBorderColor() {
		return this.borderColor;
	}
	
	public WindowedScreen setBorderColor(int color) {
		this.borderColor = color;
		return this;
	}
	
	public int getCenterDragColor() {
		return this.centerDragWidgetColor;
	}
	
	public WindowedScreen setCenterDragColor(int color) {
		this.centerDragWidgetColor = color;
		return this;
	}
	
	public int getTitleColor() {
		return this.titleColor;
	}
	
	public WindowedScreen setTitleColor(int color) {
		this.titleColor = color;
		return this;
	}
	
	@Override
	public boolean isVisible(Screen parent) {
		return this.visible;
	}
	
	public WindowedScreen setVisibility(boolean yesNo) {
		this.visible = yesNo;
		return this;
	}
	
}