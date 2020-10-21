package fr.thesmyler.smylibgui.widgets;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.thesmyler.smylibgui.Animation;
import fr.thesmyler.smylibgui.Animation.AnimationState;
import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.text.FontRendererContainer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public class MenuWidget implements IWidget {

	protected List<MenuEntry> entries = new ArrayList<MenuEntry>();
	protected MenuEntry hoveredEntry;
	protected MenuWidget displayedSubMenu;

	protected boolean visible = false;
	protected int x, y, z = 0;
	protected FontRendererContainer font;
	private boolean isSubMenu = false;
	private boolean openOnClick = false;

	protected int padding = 4;
	protected int separatorColor = 0x50FFFFFF;
	protected int borderColor = 0xA0FFFFFF;
	protected int backgroundColor = 0xE0000000;
	protected int hoveredColor = 0x40C0C0C0;
	protected int textColor = 0xFFFFFFFF;
	protected int disabledTextColor = 0xFF808080;
	protected int hoveredTextColor = 0xFF8080FF;

	protected Animation mainAnimation = new Animation(150);
	protected Animation hoverAnimation = new Animation(150);

	public MenuWidget(int z, FontRendererContainer font) {
		this.z = z;
		this.font = font;
		this.entries = new ArrayList<MenuEntry>();
	}

	@Override
	public void draw(int x, int y, int mouseX, int mouseY, boolean mouseHoverMenu, boolean hasFocus, Screen parent) {
		this.mainAnimation.update();
		this.hoverAnimation.update();
		int width = this.getWidth();
		int height = this.getHeight();
		int fh = this.font.FONT_HEIGHT;
		int lh = fh + padding * 2;
		int sh = 3;
		int dw = this.font.getStringWidth(" >");
		GlStateManager.enableAlpha();
		int separatorColor = this.mainAnimation.fadeColor(this.separatorColor);
		int borderColor = this.mainAnimation.fadeColor(this.borderColor);
		int backgroundColor = this.mainAnimation.fadeColor(this.backgroundColor);
		int hoveredColor = this.hoverAnimation.fadeColor(this.mainAnimation.fadeColor(this.hoveredColor));
		int textColor = this.mainAnimation.fadeColor(this.textColor);
		int disabledTextColor = this.mainAnimation.fadeColor(this.disabledTextColor);
		int hoveredTextColor = this.mainAnimation.fadeColor(this.hoveredTextColor);
		Gui.drawRect(x, y, x + width, y + height, backgroundColor);
		Gui.drawRect(x, y, x + 1, y + height, borderColor);
		Gui.drawRect(x + width, y, x + width + 1, y + height, borderColor);
		Gui.drawRect(x, y, x + width, y+1, borderColor);
		Gui.drawRect(x, y + height, x + width + 1, y + height + 1, borderColor);
		int ty = y;
		for(MenuEntry entry: this.entries) {
			int tx = 0;
			if(entry.text != null) {
				boolean hovered = mouseX >= x && mouseX <= x + width && mouseY >= ty && mouseY <= ty + lh - 1;
				int c = textColor;
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
					Gui.drawRect(x+1, ty+1, x + width, ty + fh + padding*2 -1, hoveredColor);
				}
				MenuWidget subMenu = entry.getSubMenu();
				if(this.displayedSubMenu != null && mouseHoverMenu && this.displayedSubMenu.equals(subMenu) && !hovered) {
					this.hideSubMenu(parent);
				}
				if(subMenu != null && hovered && this.displayedSubMenu == null) {
					this.displayedSubMenu = subMenu;
					parent.scheduleForNextScreenUpdate(()->{parent.addWidget(subMenu);});
					int subX = x + width - parent.getX();
					int subY = ty - parent.getY();
					int subH = subMenu.getHeight();
					int subW = subMenu.getWidth();
					if(subY + subH > parent.height) subY = parent.height - subH - 1;
					if(subX + subW > parent.width) subX = subX -= subW + width + 1;
					subMenu.z = this.z + 1;
					subMenu.isSubMenu = true;
					subMenu.show(subX, subY);
				}
				this.font.drawString(entry.getText(), x + padding*2 + tx, ty + padding, c);
				if(subMenu != null) this.font.drawString(" >", x + width - dw - padding, ty + padding, c);
				ty += lh;
			} else {
				Gui.drawRect(x + 1, ty + sh/2, x + width, ty + sh/2 + 1, separatorColor);
				ty += sh;
			}
		}
		GlStateManager.disableAlpha();
	}

	@Override
	public boolean onClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		if(mouseButton == 0) {
			int ty = 0;
			int width = this.getWidth();
			int fh = this.font.FONT_HEIGHT;
			int lh = fh + padding * 2;
			int sh = 3;
			for(MenuEntry entry: this.entries) {
				int h = entry.text == null ? sh: lh;
				boolean hovered = mouseX >= 0 && mouseX < width && mouseY >= ty && mouseY <= ty + h - 1;
				if(hovered) {
					if(entry.text != null && entry.enabled && entry.action != null ) {
						entry.exec();
						this.hide(parent);
						if(this.isSubMenu) {
							return true;
						}
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
	public boolean onDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		return false; //We want to intercept double clicks
	}

	@Override
	public boolean onParentClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		if(this.isSubMenu) return true;
		if(this.isVisible(parent)) {
			this.hide(parent);
			return false;
		}
		if(mouseButton == 1 && this.openOnClick) {
			int x = mouseX;
			int y = mouseY;
			int w = this.getWidth();
			int h = this.getHeight();
			if(x + w > parent.getWidth()) x -= w;
			if(y + h > parent.getHeight()) y -= h;
			this.show(x, y);
			parent.setFocus(this);
			return false;
		}
		return true;
	}

	@Override
	public boolean onParentDoubleClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		if(mouseButton == 1) return this.onParentClick(mouseX, mouseY, mouseButton, parent);
		return true;
	}

	@Override
	public void onUpdate(Screen parent) {
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
	public int getWidth() {
		int mw = 0;
		for(MenuEntry e: this.entries) {
			mw = Math.max(mw, this.font.getStringWidth(e.getText()));
		}
		return mw + padding * 4 + this.font.getStringWidth(" >");
	}

	@Override
	public int getHeight() {
		int h = 0;
		int fh = this.font.FONT_HEIGHT;
		int lh = fh + padding * 2;
		int sh = 3;
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

	public void hide(@Nullable Screen parent) {
		this.hideSubMenu(parent);
		if(parent != null) parent.scheduleForNextScreenUpdate(()->{
			this.visible = false;
		});
		else this.visible = false;
		if(parent != null && this.equals(parent.getFocusedWidget())) {
			parent.setFocus(null);
		}

	}

	public void hideSubMenu(Screen parent) {
		MenuWidget m = this.displayedSubMenu;
		if(m != null) {
			m.hide(parent);
			if(parent != null) {
				parent.scheduleForNextScreenUpdate(()->{
					parent.removeWidget(m);
				});
			}
		}
		this.displayedSubMenu = null;
	}

	public void show(int x, int y) {
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
	public boolean isVisible(Screen parent) {
		return this.visible;
	}

	public int getPadding() {
		return padding;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}

	public int getSeparatorColor() {
		return separatorColor;
	}

	public void setSeparatorColor(int separatorColor) {
		this.separatorColor = separatorColor;
	}

	public int getBorderColor() {
		return borderColor;
	}

	public void setBorderColor(int borderColor) {
		this.borderColor = borderColor;
	}

	public int getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(int backgroundColor) {
		this.backgroundColor = backgroundColor;
	}

	public int getHoveredColor() {
		return hoveredColor;
	}

	public void setHoveredColor(int hoveredColor) {
		this.hoveredColor = hoveredColor;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

	public int getHoveredTextColor() {
		return hoveredTextColor;
	}

	public void setHoveredTextColor(int hoveredTextColor) {
		this.hoveredTextColor = hoveredTextColor;
	}

	public int getDisabledTextColor() {
		return disabledTextColor;
	}

	public void setDisabledTextColor(int disabledTextColor) {
		this.disabledTextColor = disabledTextColor;
	}

	public class MenuEntry {

		public String text;
		private Runnable action;
		public boolean enabled;
		private MenuWidget subMenu = null;

		private MenuEntry(String text, Runnable action, MenuWidget menu, boolean enabled) {
			this.text = text;
			this.action = action;
			this.subMenu = menu;
			this.enabled = enabled;
		}
		public void exec() {
			if(this.action != null && this.enabled)	this.action.run();
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