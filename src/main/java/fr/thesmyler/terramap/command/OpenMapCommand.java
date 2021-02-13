package fr.thesmyler.terramap.command;

import java.util.ArrayList;
import java.util.List;

import fr.thesmyler.smylibgui.SmyLibGui;
import fr.thesmyler.terramap.MapContext;
import fr.thesmyler.terramap.TerramapClientContext;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.client.IClientCommand;

public class OpenMapCommand implements IClientCommand {

	@Override
	public String getName() {
		return "opentmap";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return "terramap.commands.opentmap.usage";
	}

	@Override
	public List<String> getAliases() {
		return new ArrayList<String>();
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		if(!(args.length == 3)) {
			throw new CommandException("terramap.commands.opentmap.numberargs");
		}
		int zoom = CommandBase.parseInt(args[0], 0, 25);
		double lat = CommandBase.parseDouble(args[1], -90, 90);
		double lon = CommandBase.parseDouble(args[2], -180, 180);
		if(TerramapClientContext.getContext().allowsMap(MapContext.FULLSCREEN)) {
			 // The current screen is the chat, if we change screen now Minecraft will close it immediately, thinking it is closing the chat
			SmyLibGui.getHudScreen().scheduleForNextScreenUpdate(() -> TerramapClientContext.getContext().openMapAt(zoom, lon, lat));
		} else {
			throw new CommandException("terramap.commands.opentmap.couldnotopen");
		}
	}

	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return true;
	}

	@Override
	public List<String> getTabCompletions(
			MinecraftServer server,
			ICommandSender sender,
			String[] args,
			BlockPos targetPos) {
		return new ArrayList<String>();
	}

	@Override
	public boolean isUsernameIndex(String[] args, int index) {
		return false;
	}

	@Override
	public int compareTo(ICommand other) {
		return this.getName().compareTo(other.getName());
	}

	@Override
	public boolean allowUsageWithoutPrefix(ICommandSender sender, String message) {
		return false;
	}

}
