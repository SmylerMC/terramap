package fr.thesmyler.terramap;

import fr.thesmyler.smylibgui.SmyLibGuiTest;
import fr.thesmyler.terramap.maps.raster.MapStylesLibrary;
import org.apache.logging.log4j.LogManager;
import org.junit.jupiter.api.BeforeEach;

public class TerramapTest extends SmyLibGuiTest {

    @BeforeEach
    public void initTerramap() {
        TerramapMod.logger = LogManager.getLogger("Terramap unit test");
        MapStylesLibrary.reload();
        TerramapClientContext.resetContext();
    }

}
