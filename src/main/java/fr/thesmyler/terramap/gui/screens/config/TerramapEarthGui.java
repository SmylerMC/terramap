package fr.thesmyler.terramap.gui.screens.config;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.terramap.TerramapClientContext;
import net.buildtheearth.terraplusplus.control.EarthGui;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiCreateWorld;

public class TerramapEarthGui extends EarthGui {
	
	private static final GuiCreateWorld FAKE_GUI = new GuiCreateWorld(null);
	private Screen parent;

	public TerramapEarthGui(Screen parent, EarthGeneratorSettings stg) {
		super(FAKE_GUI, Minecraft.getMinecraft());
		this.parent = parent;
		if(stg != null) this.settings = stg;
	}
	
    @Override
    public void mouseClicked(int mouseX, int mouseY, int mouseEvent) {
        if(this.doneButton.mousePressed(this.mc, mouseX, mouseY)) {
        	TerramapClientContext.getContext().setGeneratorSettings(this.settings);
			TerramapClientContext.getContext().saveSettings();
			Minecraft.getMinecraft().displayGuiScreen(this.parent);
			return;
        } else if(this.cancelButton.mousePressed(this.mc, mouseX, mouseY)){
			Minecraft.getMinecraft().displayGuiScreen(this.parent);
        	return;
        } else {
        	super.mouseClicked(mouseX, mouseY, mouseEvent);
        }
    }

}
