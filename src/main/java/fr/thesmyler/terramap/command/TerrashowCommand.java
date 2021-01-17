package fr.thesmyler.terramap.command;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import fr.thesmyler.terramap.TerramapVersion;
import fr.thesmyler.terramap.TerramapVersion.ReleaseType;
import fr.thesmyler.terramap.command.TranslationContextBuilder.TranslationContext;
import fr.thesmyler.terramap.config.TerramapServerPreferences;
import fr.thesmyler.terramap.permissions.Permission;
import fr.thesmyler.terramap.permissions.PermissionManager;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.WorldServer;

public class TerrashowCommand extends CommandBase {
	
	private final TranslationContextBuilder contextBuilder = new TranslationContextBuilder(new TerramapVersion(1, 0, 0, ReleaseType.BETA, 6, 3));
	
	@Override
	public String getName() {
		return "terrashow";
	}

	@Override
	public String getUsage(ICommandSender sender) {
		return this.contextBuilder.createNewContext(sender).getText("terramap.commands.terrashow.usage");
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
		TranslationContext transCtx = this.contextBuilder.createNewContext(sender);

		EntityPlayerMP player = null;
		EntityPlayer senderPlayer = sender instanceof EntityPlayer? (EntityPlayer) sender: null;
		if(args.length <= 0) transCtx.syntaxException("terramap.commands.terrashow.too_few_parameters");
		else if(args.length > 2) transCtx.syntaxException("terramap.commands.terrashow.too_many_parameters");
		else if(args.length == 2) {
			player = server.getPlayerList().getPlayerByUsername(args[1]);
		} else if(sender instanceof EntityPlayerMP) player = (EntityPlayerMP)sender;
		else transCtx.playerNotFoundException("terramap.commands.terrashow.console_player_name");
		
		if(player != null && player.equals(senderPlayer)) {
			if(senderPlayer != null && !PermissionManager.hasPermission(senderPlayer, Permission.UPDATE_PLAYER_VISIBILITY_SELF))
				transCtx.commandException("terramap.commands.terrashow.cannot_change_own_visibility");
		} else {
			if(senderPlayer != null && !PermissionManager.hasPermission(senderPlayer, Permission.UPDATE_PLAYER_VISIBILITY_OTHER))
				transCtx.commandException("terramap.commands.terrashow.cannot_change_others_visibility");
		}
		
		if(player == null) transCtx.playerNotFoundException("terramap.commands.terrashow.noplayer");
		UUID uuid = player.getPersistentID();
		WorldServer world = player.getServerWorld();
		ITextComponent message = transCtx.getComponent("terramap.commands.terrashow.invalid_action");
		switch(args[0]) {
		case "status":
			String key = TerramapServerPreferences.shouldDisplayPlayer(world, uuid) ? "terramap.commands.terrashow.getvisible": "terramap.commands.terrashow.gethidden";
			message = transCtx.getComponent(key, player.getDisplayName().getFormattedText());
			break;
		case "show":
			TerramapServerPreferences.setShouldDisplayPlayer(world, uuid, true);
			message = transCtx.getComponent("terramap.commands.terrashow.setvisible", player.getDisplayName().getFormattedText());
			break;
		case "hide":
			TerramapServerPreferences.setShouldDisplayPlayer(world, uuid, false);
			message = transCtx.getComponent("terramap.commands.terrashow.sethidden", player.getDisplayName().getFormattedText());
			break;
		default:
			transCtx.commandException("terramap.commands.terrashow.invalid_action");
		}
		message = CommandUtils.addHeader(message.setStyle(new Style().setColor(TextFormatting.GREEN).setBold(false)));
		sender.sendMessage(message);
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
