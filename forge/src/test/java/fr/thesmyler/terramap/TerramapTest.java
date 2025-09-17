package fr.thesmyler.terramap;

import com.google.gson.Gson;
import net.smyler.smylib.SmyLibTest;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.http.HttpClient;
import net.smyler.terramap.tilesets.raster.RasterTileSetManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;

public class TerramapTest extends SmyLibTest {

    @BeforeEach
    public void initTerramap() {
        Terramap.InstanceHolder.setInstance(new TestTerramapImplementation());
    }

    private static final class TestTerramapImplementation implements Terramap {

        private final Logger logger = LogManager.getLogger("Terramap unit test");
        private final RasterTileSetManager rasterTileSetManager = new RasterTileSetManager(null);

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
            return this.rasterTileSetManager;
        }

    }

}
