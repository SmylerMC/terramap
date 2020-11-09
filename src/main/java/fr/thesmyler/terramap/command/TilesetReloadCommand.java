package fr.thesmyler.terramap.command;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.thesmyler.terramap.TerramapMod;
import fr.thesmyler.terramap.TerramapVersion;
import fr.thesmyler.terramap.TerramapVersion.ReleaseType;
import fr.thesmyler.terramap.maps.MapStyleRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class TilesetReloadCommand extends CommandBase {
	
	private static final TerramapVersion FIRST_LOCALIZED_VERSION = new TerramapVersion(1, 0, 0, ReleaseType.BETA, 6, 3);

	@Override
	public String getName() {
		return "reloadmapstyles";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return CommandUtils.getStringForSender("terramap.commands.reloadmapstyles.usage", CommandUtils.senderSupportsLocalization(sender, FIRST_LOCALIZED_VERSION));
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		boolean clientLocalizes = CommandUtils.senderSupportsLocalization(sender, FIRST_LOCALIZED_VERSION);
		if(sender instanceof EntityPlayer && !PermissionManager.hasPermission((EntityPlayer) sender, Permission.RELOAD_MAP_STYLES)) {
			throw new CommandException(CommandUtils.getStringForSender("terramap.commands.reloadmapstyles.forbidden", clientLocalizes));
		}
		try {
			MapStyleRegistry.loadFromConfigFile();
			sender.sendMessage(CommandUtils.getComponentForSender("terramap.commands.reloadmapstyles.done", clientLocalizes));
		} catch(Exception e) {
			sender.sendMessage(CommandUtils.getComponentForSender("terramap.commands.reloadmapstyles.error", clientLocalizes));
			TerramapMod.logger.error("Error when reloading map styles!");
			TerramapMod.logger.catching(e);
		}
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return new ArrayList<String>();
    }

}
