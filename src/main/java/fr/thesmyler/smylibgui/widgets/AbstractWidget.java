package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.screen.Screen;

public abstract class AbstractWidget implements IWidget {
	
	protected boolean visible = true;
	protected int x, y, z, width, height;
	protected String tooltip = null;
	
	public AbstractWidget(int x, int y, int z, int width, int height) {
		this.x = x;
		this.y = y;
		this.z = z;
		this.width = width;
		this.height = height;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	@Override
	public int getZ() {
		return z;
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
	public boolean isVisible(Screen parent) {
		return this.visible;
	}
	
	public AbstractWidget setVisibility(boolean yesNo) {
		this.visible = yesNo;
		return this;
	}
	
	public AbstractWidget show() {
		return this.setVisibility(true);
	}
	
	public AbstractWidget hide() {
		return this.setVisibility(false);
	}
	
	
	public AbstractWidget setTooltip(String tooltip) {
		this.tooltip = tooltip;
		return this;
	}
	
	@Override
	public String getTooltipText() {
		return this.tooltip;
	}

}
