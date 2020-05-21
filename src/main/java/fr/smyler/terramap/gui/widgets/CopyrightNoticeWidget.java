package fr.smyler.terramap.gui.widgets;

import fr.smyler.terramap.maps.TiledMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;

public class CopyrightNoticeWidget extends GuiButton {

	public TiledMap<?> map;

	public CopyrightNoticeWidget(int buttonId, int x, int y, TiledMap<?> map) {
		super(buttonId, x, y, "");
		this.map = map;
	}

	@Override
	public void drawButton(Minecraft mc, int mouseX, int mouseY, float partialTicks) {
		if(!this.visible) return;
		int width = this.getWidth();
		int height = this.getHeight();
		Gui.drawRect(this.x, this.y, this.x + width, this.y + height, 0xA0000000);
		int color = 0xFFFFFF;
		FontRenderer f = Minecraft.getMinecraft().fontRenderer;
		if(mouseX >= this.x && mouseX <= this.x + width
				&& mouseY >= this.y && mouseY <= this.y + height
				&& this.map.getCopyRightURL().length() > 0)
			color = 0xA0A0FF;
		f.drawString(this.map.getCopyright(), this.x + 5, this.y + 5, color);
	}

	public int getWidth() {
		FontRenderer f = Minecraft.getMinecraft().fontRenderer;
		return f.getStringWidth(this.map.getCopyright()) + 10; 
	}

	public int getHeight() {
		FontRenderer f = Minecraft.getMinecraft().fontRenderer;
		return f.FONT_HEIGHT + 10; 
	}
}
