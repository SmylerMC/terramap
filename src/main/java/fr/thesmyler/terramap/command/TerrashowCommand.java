package fr.thesmyler.terramap.command;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import fr.thesmyler.terramap.TerramapVersion;
import fr.thesmyler.terramap.TerramapVersion.ReleaseType;
import fr.thesmyler.terramap.config.TerramapServerPreferences;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.PlayerNotFoundException;
import net.minecraft.command.SyntaxErrorException;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldServer;

public class TerrashowCommand extends CommandBase {
	
	private static final TerramapVersion FIRST_LOCALIZED_VERSION = new TerramapVersion(1, 0, 0, ReleaseType.BETA, 6, 3);
	
	@Override
	public String getName() {
		return "terrashow";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return CommandUtils.getStringForSender("terramap.commands.terrashow.usage", CommandUtils.senderSupportsLocalization(sender, FIRST_LOCALIZED_VERSION));
	}

	//FIXME checking permission for null player
	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		boolean clientSupportsLocalize = CommandUtils.senderSupportsLocalization(sender, FIRST_LOCALIZED_VERSION);
		EntityPlayerMP player = null;
		EntityPlayer senderPlayer = sender instanceof EntityPlayer? (EntityPlayer) sender: null;
		if(args.length <= 0) throw new SyntaxErrorException(CommandUtils.getStringForSender("terramap.commands.terrashow.too_few_parameters", clientSupportsLocalize));
		else if(args.length > 2) throw new SyntaxErrorException(CommandUtils.getStringForSender("terramap.commands.terrashow.too_many_parameters", clientSupportsLocalize));
		else if(args.length == 2) {
			player = server.getPlayerList().getPlayerByUsername(args[1]);
		} else if(sender instanceof EntityPlayerMP) player = (EntityPlayerMP)sender;
		else throw new PlayerNotFoundException(CommandUtils.getStringForSender("terramap.commands.terrashow.console_player_name", clientSupportsLocalize));
		if(player != null && player.equals(senderPlayer)) {
			if(senderPlayer == null || !PermissionManager.hasPermission(senderPlayer, Permission.UPDATE_PLAYER_VISIBILITY_SELF))
				throw new CommandException(CommandUtils.getStringForSender("terramap.commands.terrashow.cannot_change_own_visibility", clientSupportsLocalize));
		} else {
			if(senderPlayer == null || !PermissionManager.hasPermission(senderPlayer, Permission.UPDATE_PLAYER_VISIBILITY_OTHER))
				throw new CommandException(CommandUtils.getStringForSender("terramap.commands.terrashow.cannot_change_others_visibility", clientSupportsLocalize));
		}
		if(player == null) throw new PlayerNotFoundException(CommandUtils.getStringForSender("terramap.commands.terrashow.noplayer", clientSupportsLocalize));
		UUID uuid = player.getPersistentID();
		WorldServer world = player.getServerWorld();
		switch(args[0]) {
		case "status":
			String key = TerramapServerPreferences.shouldDisplayPlayer(world, uuid) ? "terramap.commands.terrashow.getvisible": "terramap.commands.terrashow.gethidden";
			sender.sendMessage(CommandUtils.getComponentForSender(key, clientSupportsLocalize, player.getDisplayName().getFormattedText()));
			break;
		case "show":
			TerramapServerPreferences.setShouldDisplayPlayer(world, uuid, true);
			
			sender.sendMessage(CommandUtils.getComponentForSender("terramap.commands.terrashow.setvisible", clientSupportsLocalize, player.getDisplayName().getFormattedText()));
			break;
		case "hide":
			TerramapServerPreferences.setShouldDisplayPlayer(world, uuid, false);
			sender.sendMessage(CommandUtils.getComponentForSender("terramap.commands.terrashow.setvisible", clientSupportsLocalize, player.getDisplayName().getFormattedText()));
			break;
		default:
			throw new CommandException(CommandUtils.getStringForSender("terramap.commands.terrashow.invalid_action", clientSupportsLocalize));
		}

	}
	
	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
		String[] s = {"show", "hide", "status"};
        if (args.length == 1) {
            return getListOfStringsMatchingLastWord(args, s);
        } else {
            return getListOfStringsMatchingLastWord(args, server.getOnlinePlayerNames());
        }
    }

}
