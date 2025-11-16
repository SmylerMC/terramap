package net.smyler.terramap.world;

import static net.smyler.terramap.Terramap.getTerramap;

public class ForgeWorldClientside extends WorldClientside {

    private final net.minecraft.world.World world;

    ForgeWorldClientside(net.minecraft.world.World world) {
        this.world = world;
        getTerramap().logger().debug("New forge world client");
    }

}
