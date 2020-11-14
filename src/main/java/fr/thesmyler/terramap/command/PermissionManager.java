package fr.thesmyler.terramap.command;

import java.util.UUID;

import com.mojang.authlib.GameProfile;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.server.permission.PermissionAPI;

/**
 * Makes it easier to manage permissions
 *
 * @author SmylerMC
 *
 */
public abstract class PermissionManager {

	public static void registerNodes() {
		registerForgeNodes();
	}
	
	private static void registerForgeNodes() {
		for(Permission perm: Permission.values()) {
			PermissionAPI.registerNode(
					perm.getNodeName(),
					perm.getDefaultPermissionLevel(),
					perm.getDescription()
				);
		}
	}
	
	public static boolean hasPermission(EntityPlayer player, Permission perm) {
		return PermissionAPI.hasPermission(player, perm.getNodeName());
	}
	
	public static boolean hasPermission(GameProfile profile, Permission perm) {
		return PermissionAPI.hasPermission(profile, perm.getNodeName(), null);
	}
	
	public static boolean hasPermission(UUID uuid, Permission perm) {
		return PermissionAPI.hasPermission(new GameProfile(uuid, null), perm.getNodeName(), null);
	}
	
}
