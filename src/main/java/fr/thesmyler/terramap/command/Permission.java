package fr.thesmyler.terramap.command;

import net.minecraftforge.server.permission.DefaultPermissionLevel;

public enum Permission {

	UPDATE_PLAYER_VISIBILITY_SELF(
			"terramap.commands.terrashow.self",
			DefaultPermissionLevel.ALL,
			"Lets players hide or show themselves on the map with /terrashow"),
	UPDATE_PLAYER_VISIBILITY_OTHER(
			"terramap.commands.terrashow.others",
			DefaultPermissionLevel.OP,
			"Lets players hide or show others on the map with /terrashow");
	
	private final String node;
	private final DefaultPermissionLevel lvl;
	private final String description;
	
	
	Permission(String node, DefaultPermissionLevel lvl, String description) {
		this.node = node;
		this.lvl = lvl;
		this.description = description;
	}
	
	public String getNodeName() {
		return this.node;
	}

	public DefaultPermissionLevel getDefaultPermissionLevel() {
		return lvl;
	}

	public String getDescription() {
		return description;
	}
	
	

}
