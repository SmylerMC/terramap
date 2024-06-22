package fr.thesmyler.terramap;

import fr.thesmyler.terramap.maps.raster.MapStylesLibrary;
import net.smyler.smylib.SmyLibTest;
import net.smyler.terramap.Terramap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;

public class TerramapTest extends SmyLibTest implements Terramap {

    private final Logger logger = LogManager.getLogger("Terramap unit test");
    @BeforeEach
    public void initTerramap() {
        Terramap.InstanceHolder.setInstance(this);
        MapStylesLibrary.reload();
        TerramapClientContext.resetContext();
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

}
