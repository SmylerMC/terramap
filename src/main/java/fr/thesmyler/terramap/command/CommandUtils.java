package fr.thesmyler.terramap.command;

import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextFormatting;

public abstract class CommandUtils {
	
	public static final ITextComponent MESSAGE_HEADER = new TextComponentString("Terramap > ")
			.setStyle(
					new Style()
					.setColor(TextFormatting.DARK_GREEN)
					.setBold(true));
	
	public static ITextComponent addHeader(ITextComponent component) {
		return MESSAGE_HEADER.createCopy().appendSibling(component);
	}
}
