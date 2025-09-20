package net.smyler.terramap.content;

import static net.smyler.terramap.Terramap.getTerramap;

public class ForgeWorldClient extends WorldClient {

    public ForgeWorldClient() {
        getTerramap().logger().debug("New forge world client");
    }

}
