package fr.thesmyler.terramap;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import fr.thesmyler.terramap.util.json.EarthGeneratorSettingsAdapter;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import net.smyler.smylib.SmyLibTest;
import net.smyler.smylib.json.TextJsonAdapter;
import net.smyler.smylib.text.Text;
import net.smyler.terramap.Terramap;
import net.smyler.terramap.http.HttpClient;
import net.smyler.terramap.tilesets.raster.RasterTileSetManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.BeforeEach;

public class TerramapTest extends SmyLibTest {

    @BeforeEach
    public void initTerramap() {
        Terramap instance = new TestTerramapImplementation();
        Terramap.InstanceHolder.setInstance(instance);
        instance.rasterTileSetManager().loadBuiltIns();
    }

    private static final class TestTerramapImplementation implements Terramap {

        private final Logger logger = LogManager.getLogger("Terramap unit test");
        private final RasterTileSetManager rasterTileSetManager = new RasterTileSetManager(null);

        private final Gson gson = new GsonBuilder()
                .registerTypeAdapter(EarthGeneratorSettings.class, new EarthGeneratorSettingsAdapter())
                .registerTypeHierarchyAdapter(Text.class, new TextJsonAdapter())
                .setPrettyPrinting()
                .create();


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
            this.logger.warn("GSON not fully implemented in tests");
            return this.gson;
        }

        @Override
        public RasterTileSetManager rasterTileSetManager() {
            return this.rasterTileSetManager;
        }

    }

}
