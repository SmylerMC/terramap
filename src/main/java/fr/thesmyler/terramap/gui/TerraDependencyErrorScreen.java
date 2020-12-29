package fr.thesmyler.terramap.gui;

import fr.thesmyler.smylibgui.screen.Screen;
import fr.thesmyler.smylibgui.widgets.buttons.TextButtonWidget;
import fr.thesmyler.smylibgui.widgets.text.TextAlignment;
import fr.thesmyler.smylibgui.widgets.text.TextWidget;
import net.minecraft.client.resources.I18n;
import net.minecraftforge.fml.common.FMLCommonHandler;

public class TerraDependencyErrorScreen extends Screen {

	@Override
	public void initScreen() {
		String txt = "";
		for(int i=1; I18n.hasKey("terramap.terraerror.warn." + i); i++) {
			txt += I18n.format("terramap.terraerror.warn." + i) + "\n"; // There will be a new line at the end, but this is fine
		}
		TextWidget txtWidget = new TextWidget(txt, this.width/2, this.height/2, 10, TextAlignment.CENTER, true, this.getFont());
		txtWidget.setAnchorY((this.getHeight() - txtWidget.getHeight()) / 2);
		this.addWidget(txtWidget);
		this.addWidget(new TextButtonWidget(this.width / 2 - 75, txtWidget.getY() + txtWidget.getHeight(), 10, 150, I18n.format("terramap.terraerror.closegame"), () -> FMLCommonHandler.instance().exitJava(1, false)));
	}

	
}
