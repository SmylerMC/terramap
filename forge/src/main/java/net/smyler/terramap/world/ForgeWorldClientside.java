package net.smyler.terramap.world;

import static net.smyler.terramap.Terramap.getTerramap;

public class ForgeWorldClientside extends WorldClientside {

    public ForgeWorldClientside() {
        getTerramap().logger().debug("New forge world client");
    }

}
