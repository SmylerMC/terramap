package net.smyler.terramap.world;

import static net.smyler.terramap.Terramap.getTerramap;

public class ForgeWorldClient extends WorldClient {

    public ForgeWorldClient() {
        getTerramap().logger().debug("New forge world client");
    }

}
