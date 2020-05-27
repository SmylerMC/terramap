package fr.thesmyler.terramap.forgeessentials;

public class FeWarp {

	protected String name;
	protected double x;
	protected double z;
	// fe also saves y, pitch and yaw, but since this is for network and it's not needed clientside, we are ignoring it
	
	public FeWarp(String name, double x, double z) {
		this.name = name;
		this.x = x;
		this.z = z;
	}

	public String getName() {
		return name;
	}

	public double getX() {
		return x;
	}

	public double getZ() {
		return z;
	}
	
}
