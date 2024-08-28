package net.smyler.terramap.http;

import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;

import static org.apache.logging.log4j.LogManager.getLogger;


public class TerramapHttpClientTest {

    @Test
    void canRequest() throws ExecutionException, InterruptedException, IOException {
        Logger logger = getLogger("HTTP test");
        HttpClient client = new TerramapHttpClient(logger, Files.createTempDirectory("terramap-tests"));
        String content = new String(client.get("https://tile-c.openstreetmap.fr/hot/7/66/38.png").get());
        content = new String(client.get("https://tile-c.openstreetmap.fr/hot/7/66/38.png").get());
    }

}
