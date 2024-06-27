package fr.thesmyler.terramap;

import com.google.gson.Gson;
import fr.thesmyler.terramap.maps.raster.MapStylesLibrary;
import net.smyler.smylib.SmyLibTest;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.http.HttpClient;
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
    public String version() {
        return "0.0.0";
    }

    @Override
    public Logger logger() {
        return this.logger;
    }

    @Override
    public HttpClient http() {
        this.logger.warn("HTTP client not implemented in tests");
        return null;
    }

    @Override
    public Gson gson() {
        return this.gsonPretty();
    }

    @Override
    public Gson gsonPretty() {
        this.logger.warn("GSON not implemented in tests");
        return null;
    }

}
