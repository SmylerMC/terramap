package fr.thesmyler.terramap.command;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

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

//TODO Localize, but only when installed on remote
public class TerrashowCommand extends CommandBase {

	public static final String USAGE = "/terrashow <show|hide|status> [playername (optional)]";
	@Override
	public String getName() {
		return "terrashow";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return USAGE;
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		EntityPlayerMP player = null;
		EntityPlayer senderPlayer = sender instanceof EntityPlayer? (EntityPlayer) sender: null;
		if(args.length <= 0) throw new SyntaxErrorException("Too few parameters: " + USAGE); //TODO Localize
		else if(args.length > 2) throw new SyntaxErrorException("Too many parameters: " + USAGE); //TODO Localize
		else if(args.length == 2) {
			player = server.getPlayerList().getPlayerByUsername(args[1]);
		} else if(sender instanceof EntityPlayerMP) player = (EntityPlayerMP)sender;
		else throw new PlayerNotFoundException("Player name is required when executing from the server console: " + USAGE); //TODO Localize
		if(player != null && player.equals(senderPlayer)) {
			if(!PermissionManager.hasPermission(senderPlayer, Permission.UPDATE_PLAYER_VISIBILITY_SELF))
				throw new CommandException("You do not have the permission to change your visibility"); //TODO Localize
		} else {
			if(!PermissionManager.hasPermission(senderPlayer, Permission.UPDATE_PLAYER_VISIBILITY_OTHER))
				throw new CommandException("You do not have the permission to change others' visibility"); //TODO Localize
		}
		if(player == null) throw new PlayerNotFoundException("Player does not exist: " + USAGE); //TODO Localize
		UUID uuid = player.getPersistentID();
		switch(args[0]) {
		case "status":
			boolean status = TerramapServerPreferences.shouldDisplayPlayer(uuid);
			sender.sendMessage(player.getDisplayName().appendText(" is currently " + (status ? "visible": "hidden") + " on the map.")); //TODO Localize
			break;
		case "show":
			TerramapServerPreferences.setShouldDisplayPlayer(uuid, true);
			sender.sendMessage(player.getDisplayName().appendText(" is now visible on the map.")); //TODO Localize
			break;
		case "hide":
			TerramapServerPreferences.setShouldDisplayPlayer(uuid, false);
			sender.sendMessage(player.getDisplayName().appendText(" is now hidden on the map.")); //TODO Localize
			break;
		default:
			new CommandException("Invalid action: " + USAGE); //TODO Localize
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
