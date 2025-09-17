package net.smyler.terramap;

import com.google.gson.Gson;
import net.smyler.terramap.http.HttpClient;
import net.smyler.terramap.tilesets.raster.RasterTileSetManager;
import org.apache.logging.log4j.Logger;

public interface Terramap {

    static Terramap getTerramap() {
        return InstanceHolder.instance;
    }

    String MOD_ID = "terramap";
    String STYLE_UPDATE_HOSTNAME = "styles.terramap.thesmyler.fr";  //TODO use smyler.net

    String version();

    Logger logger();

    HttpClient http();

    Gson gson();

    Gson gsonPretty();

    RasterTileSetManager rasterTileSetManager();

    class InstanceHolder {
        private static Terramap instance;
        public static void setInstance(Terramap instance) {
            instance.logger().info("Setting Terramap instance of class {}", instance.getClass().getName());
            InstanceHolder.instance = instance;
        }
    }

}
