package fr.thesmyler.terramap.command;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

import fr.thesmyler.terramap.maps.MapStyleRegistry;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;

public class TilesetReloadCommand extends CommandBase {
	
	public static final String USAGE = "/reloadmapstyles";

	@Override
	public String getName() {
		return "reloadmapstyles";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return USAGE;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(sender instanceof EntityPlayer && !PermissionManager.hasPermission((EntityPlayer) sender, Permission.RELOAD_MAP_STYLES)) {
			throw new CommandException("You do not have the permission to use that command!");
		}
		MapStyleRegistry.loadFromConfigFile();
	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		return new ArrayList<String>();
    }

}
