package net.smyler.terramap.minecraft.world;

import net.smyler.terramap.minecraft.world.WorldClient;

import static net.smyler.terramap.Terramap.getTerramap;

public class ForgeWorldClient extends WorldClient {

    public ForgeWorldClient() {
        getTerramap().logger().debug("New forge world client");
    }

}
