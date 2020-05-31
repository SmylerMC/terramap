package fr.thesmyler.terramap.command;

import java.util.UUID;

import fr.thesmyler.terramap.config.TerramapConfiguration;
import fr.thesmyler.terramap.network.TerramapNetworkManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;

//TODO Tab completion
//TODO Save on disk
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
		if(args.length <= 0) throw new CommandException("Too few parameters: " + USAGE);
		else if(args.length > 2) throw new CommandException("Too many parameters: " + USAGE);
		else if(args.length == 2) player = server.getPlayerList().getPlayerByUsername(args[1]);
		else if(sender instanceof EntityPlayerMP)player = (EntityPlayerMP)sender;
		else throw new CommandException("Player name is required when executing from the server console: " + USAGE);
		if(player == null) throw new CommandException("Player does not exist: " + USAGE);
		UUID uuid = player.getPersistentID();
		switch(args[0]) {
		case "status":
			boolean status = TerramapNetworkManager.playersWithDisplayPreferences.getOrDefault(uuid, TerramapConfiguration.playersOptInToDisplayDefault);
			sender.sendMessage(player.getDisplayName().appendText(" is currently " + (status ? "visible": "hidden") + " on the map."));
			break;
		case "show":
			TerramapNetworkManager.playersWithDisplayPreferences.put(uuid, true);
			sender.sendMessage(player.getDisplayName().appendText(" is now visible on the map."));
			break;
		case "hide":
			TerramapNetworkManager.playersWithDisplayPreferences.put(uuid, false);
			sender.sendMessage(player.getDisplayName().appendText(" is now hidden on the map."));
			break;
		default:
			new CommandException("Invalid action: " + USAGE);
		}

	}

}
