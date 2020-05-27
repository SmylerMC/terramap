package fr.thesmyler.terramap.gui.widgets;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;

public class RightClickMenu extends Gui {

	protected List<RightClickMenuEntry> entries = new ArrayList<RightClickMenuEntry>();

	protected boolean visible = false;
	protected int x, y = 0;
	
	protected FontRenderer fontRenderer;

	public void init(FontRenderer fontRenderer) {
		this.fontRenderer = fontRenderer;
		this.entries = new ArrayList<RightClickMenuEntry>();
	}
	
	public void showAt(int x, int y) {
		this.x = x;
		this.y = y;
		this.visible = true;
	}

	public void hide() {
		this.visible = false;
	}


	public void draw(int mouseX, int mouseY, float partialTicks) {
		if(!this.visible) return;
		int width = this.getWidth();
		int height = this.getHeight();
		Gui.drawRect(this.x, this.y, this.x + width, this.y + height, 0xA0000000);
		int i = 0;
		for(RightClickMenuEntry entry: this.entries) {
			int color = 0xFFFFFF;
			int ty = 5 + this.y + (this.fontRenderer.FONT_HEIGHT + 5) * i++;
			if(mouseX >= this.x && mouseX <= this.x + width && mouseY >= ty && mouseY <= ty + this.fontRenderer.FONT_HEIGHT) color = 0xA0A0FF;
			this.fontRenderer.drawString(entry.getText(), this.x + 5, ty, color);
		}
	}
	
	public void onMouseClick(int mouseX, int mouseY) {
		//This is not optimized, but I'm lazy right now and it doesn't matter here
		int width = this.getWidth();
		int i = 0;
		for(RightClickMenuEntry entry: this.entries) {
			int ty = 5 + this.y + (this.fontRenderer.FONT_HEIGHT + 5) * i++;
			if(mouseX >= this.x && mouseX <= this.x + width && mouseY >= ty && mouseY <= ty + this.fontRenderer.FONT_HEIGHT) entry.exec();
		}
	}
	
	public void addEntry(String text, ClickAction action) {
		this.entries.add(new RightClickMenuEntry(text, action));
	}
	
	public boolean isDisplayed() {
		return this.visible;
	}
	
	public int getWidth() {
		int mw = 0;
		for(RightClickMenuEntry e: this.entries) {
			mw = Math.max(mw, this.fontRenderer.getStringWidth(e.getText()));
		}
		return mw + 10;
	}
	
	public int getHeight() {
		return 5 + (this.fontRenderer.FONT_HEIGHT + 5) * this.entries.size();
	}
	
	public class RightClickMenuEntry {
		private String text;
		private ClickAction action;
		
		public RightClickMenuEntry(String text, ClickAction action) {
			this.text = text;
			this.action = action;
		}
		
		public void exec() {
			this.action.onClick();
		}
		
		public String getText() {
			return this.text;
		}
	}
	
	public interface ClickAction {
		public void onClick();
	}

}