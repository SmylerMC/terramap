package fr.thesmyler.smylibgui.widgets;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.smylibgui.container.WidgetContainer;
import fr.thesmyler.smylibgui.util.RenderUtil;
import net.minecraft.client.Minecraft;

public class WarningWidget extends AbstractWidget {

	public WarningWidget(float x, float y, int z) {
		super(x, y, z, 15, 15);
	}

	@Override
	public void draw(float x, float y, float mouseX, float mouseY, boolean hovered, boolean focused, WidgetContainer parent) {
		Minecraft.getMinecraft().getTextureManager().bindTexture(SmyLibGui.WIDGET_TEXTURES);
		RenderUtil.drawModalRectWithCustomSizedTexture(x, y, 15, 54, this.width, this.height, 256, 256);
	}
	
	public WarningWidget setPosition(float x, float y) {
		this.x = x;
		this.y = y;
		return this;
	}

}
