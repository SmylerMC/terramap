package fr.thesmyler.terramap.maps;

public enum TiledMapProvider {
	
	BUILT_IN, // From the jar, should never be used
	ONLINE,   // From the online database that was downloaded on mod startup
	SERVER,   // From the Minecraft server we are currently playing on //TODO Implement
	PROXY,    // From the Sledgehammer proxy //TODO Implement
	CUSTOM;   // From the user defined config //TODO Implement

}
