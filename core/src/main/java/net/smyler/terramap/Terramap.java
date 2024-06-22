package net.smyler.terramap;

import com.google.gson.Gson;
import net.smyler.terramap.http.HttpClient;
import org.apache.logging.log4j.Logger;

public interface Terramap {

    static Terramap instance() {
        return InstanceHolder.instance;
    }

    String MOD_ID = "terramap";

    Logger logger();

    HttpClient http();

    Gson gson();

    Gson gsonPretty();

    class InstanceHolder {
        private static Terramap instance;
        public static void setInstance(Terramap instance) {
            instance.logger().info("Setting Terramap instance of class {}", instance.getClass().getName());
            InstanceHolder.instance = instance;
        }
    }

}
