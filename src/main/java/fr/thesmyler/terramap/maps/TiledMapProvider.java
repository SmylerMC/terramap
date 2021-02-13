package fr.thesmyler.terramap.maps;

public enum TiledMapProvider {
	
	BUILT_IN, // From the jar, should never be used
	INTERNAL, // Loaded from code, usually debug maps
	ONLINE,   // From the online database that was downloaded on mod startup
	SERVER,   // From the Minecraft server we are currently playing on
	PROXY,    // From the Sledgehammer proxy
	CUSTOM;   // From the user defined config
	
	private Exception error = null;
	
	public synchronized void setLastError(Exception e) {
		this.error = e;
	}
	
	public synchronized Exception getLastError() {
		return this.error;
	}

}
