package fr.thesmyler.terramap.command;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.thesmyler.terramap.TerramapVersion;
import fr.thesmyler.terramap.TerramapVersion.ReleaseType;
import fr.thesmyler.terramap.command.TranslationContextBuilder.TranslationContext;
import fr.thesmyler.terramap.maps.MapStylesLibrary;
import fr.thesmyler.terramap.maps.TiledMapProvider;
import fr.thesmyler.terramap.permissions.Permission;
import fr.thesmyler.terramap.permissions.PermissionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;

public class TilesetReloadCommand extends CommandBase {
	
	private final TranslationContextBuilder translationContextBuilder = new TranslationContextBuilder(new TerramapVersion(1, 0, 0, ReleaseType.BETA, 6, 3));

	@Override
	public String getName() {
		return "reloadmapstyles";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return this.translationContextBuilder.createNewContext(sender).getText("terramap.commands.reloadmapstyles.usage");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		TranslationContext ctx = this.translationContextBuilder.createNewContext(sender);
		if(sender instanceof EntityPlayer && !this.checkPermission(server, sender)) {
			ctx.commandException("terramap.commands.reloadmapstyles.forbidden");
		}
		ITextComponent msg = ctx.getComponent("terramap.commands.reloadmapstyles.done");
		MapStylesLibrary.loadFromConfigFile();
		if(TiledMapProvider.CUSTOM.getLastError() == null) {
			msg.setStyle(new Style().setColor(TextFormatting.GREEN).setBold(false));
		} else {
			msg = ctx.getComponent("terramap.commands.reloadmapstyles.error");
			msg.setStyle(new Style().setColor(TextFormatting.RED).setBold(false));
		}
		msg = CommandUtils.addHeader(msg);
		sender.sendMessage(msg);
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return new ArrayList<String>();
    }

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		if(!(sender instanceof EntityPlayer)) return true;
		return PermissionManager.hasPermission((EntityPlayer) sender, Permission.RELOAD_MAP_STYLES);
	}
	
	

}
