package fr.thesmyler.smylibgui;

import fr.thesmyler.smylibgui.widget.RightClickWidget;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;

public class TestScreen extends Screen {

	private GuiScreen parent;
	
	public TestScreen(GuiScreen parent) {
		super(Screen.BackgroundType.DIRT);
		this.parent = parent;
	}
	
	@Override
	public void initScreen() {
		this.removeAllWidgets();
		this.addWidget(new RightClickWidget(1, this)
				 .addEntry("close", () -> {
					Minecraft.getMinecraft().displayGuiScreen(this.parent);})
				 .addEntry("test1", null)
				 .addEntry("test2", null));
	}
	
}
