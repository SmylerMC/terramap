package fr.thesmyler.smylibgui.widget;

import java.util.ArrayList;
import java.util.List;

import fr.thesmyler.smylibgui.Screen;
import net.minecraft.client.gui.Gui;

//TODO Separators
public class RightClickWidget implements IWidget {

	protected List<RightClickMenuEntry> entries = new ArrayList<RightClickMenuEntry>();

	protected boolean visible = false;
	protected int x, y, z = 0;
	private Screen parent;
	
	public RightClickWidget(int z, Screen parent) {
		this.z = z;
		this.parent = parent;
		this.entries = new ArrayList<RightClickMenuEntry>();
	}
	
	@Override
	public void draw(int x, int y, int mouseX, int mouseY, Screen parent) {
		if(!this.visible) return;
		int width = this.getWidth();
		int height = this.getHeight();
		Gui.drawRect(this.x, this.y, this.x + width, this.y + height, 0xA0000000);
		int i = 0;
		for(RightClickMenuEntry entry: this.entries) {
			int color = 0xFFFFFF;
			int ty = 5 + this.y + (parent.getFont().FONT_HEIGHT + 5) * i++;
			if(mouseX >= this.x && mouseX <= this.x + width && mouseY >= ty && mouseY <= ty + parent.getFont().FONT_HEIGHT) color = 0xA0A0FF;
			parent.getFont().drawString(entry.getText(), this.x + 5, ty, color);
		}
	}
	
	@Override
	public void onClick(int mouseX, int mouseY, int mouseButton) {
		int i = 0;
		for(RightClickMenuEntry entry: this.entries) {
			int ty = 5 + (this.parent.getFont().FONT_HEIGHT + 5) * i++;
			if(mouseY >= ty && mouseY <= ty + this.parent.getFont().FONT_HEIGHT) entry.exec();
		}
		this.hide();
	}
	
	@Override
	public boolean onParentClick(int mouseX, int mouseY, int mouseButton, Screen parent) {
		if(this.isVisible()) {
			this.hide();
		}
		if(mouseButton == 1) {
			this.x = mouseX;
			this.y = mouseY;
			this.show();
		}
		return false;
	}

	public void hide() {
		this.visible = false;
	}
	
	public void show() {
		this.visible = true;
	}
	
	@Override
	public boolean isVisible() {
		return this.visible;
	}
	
	public void onMouseClick(int mouseX, int mouseY) {
		//This is not optimized, but I'm lazy right now and it doesn't matter here

	}
	
	public RightClickWidget addEntry(String text, Runnable action) {
		this.entries.add(new RightClickMenuEntry(text, action));
		return this;
	}
	
	@Override
	public int getWidth() {
		int mw = 0;
		for(RightClickMenuEntry e: this.entries) {
			mw = Math.max(mw, this.parent.getFont().getStringWidth(e.getText()));
		}
		return mw + 10;
	}
	
	@Override
	public int getHeight() {
		return 5 + (this.parent.getFont().FONT_HEIGHT + 5) * this.entries.size();
	}
	
	public class RightClickMenuEntry {
		private String text;
		private Runnable action;
		
		public RightClickMenuEntry(String text, Runnable action) {
			this.text = text;
			this.action = action;
		}
		
		public void exec() {
			if(this.action != null)	this.action.run();
		}
		
		public String getText() {
			return this.text;
		}
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
		return this.visible? Integer.MAX_VALUE: this.z;
	}


}