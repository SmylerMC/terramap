package fr.thesmyler.terramap.command;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapVersion;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;

public abstract class CommandUtils {

	public static String getStringForSender(String key, boolean clientSupportsLocalization) {
		if(clientSupportsLocalization) {
			return key; // The client will handler the translation
		} else {
			return TerramapMod.proxy.localize(key);
		}
	}
	
	public static ITextComponent getComponentForSender(String key, boolean clientSupportsLocalization, Object...objects) {
		if(clientSupportsLocalization) {
			return new TextComponentTranslation(key, objects); // The client will handler the translation
		} else {
			return new TextComponentString(TerramapMod.proxy.localize(key, objects));
		}
	}
	
	public static boolean senderSupportsLocalization(ICommandSender sender, TerramapVersion minVersion) {
		return sender instanceof EntityPlayerMP && minVersion.isOlder(TerramapVersion.getClientVersion((EntityPlayerMP) sender));
	}
}
