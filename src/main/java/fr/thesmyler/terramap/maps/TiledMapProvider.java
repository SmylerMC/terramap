package fr.thesmyler.terramap.maps;

public enum TiledMapProvider {
	
	BUILT_IN, // From the jar, should never be used
	ONLINE,   // From the online database that was downloaded on mod startup //TODO Implement
	SERVER,   // From the Minecraft server we are currently playing on //TODO Implement
	CONFIG;   // From the user defined config //TODO Implement

}
