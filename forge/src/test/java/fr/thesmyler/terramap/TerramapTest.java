package fr.thesmyler.terramap;

import com.google.gson.Gson;
import net.smyler.smylib.SmyLibTest;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.http.HttpClient;
import net.smyler.terramap.tilesets.raster.RasterTileSetManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;

public class TerramapTest extends SmyLibTest implements Terramap {

    private final Logger logger = LogManager.getLogger("Terramap unit test");
    private final RasterTileSetManager rasterTileSetManager = new RasterTileSetManager(null);

    @BeforeEach
    public void initTerramap() {
        Terramap.InstanceHolder.setInstance(this);
        this.rasterTileSetManager.reload(true);
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

    @Override
    public RasterTileSetManager rasterTileSetManager() {
        return null;
    }

}
