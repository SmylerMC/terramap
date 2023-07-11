package fr.thesmyler.terramap.util.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;
import org.junit.jupiter.api.Test;

import static net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings.BTE_DEFAULT_SETTINGS;
import static org.junit.jupiter.api.Assertions.*;

class EarthGeneratorSettingsAdapterTest {

    private final EarthGeneratorSettings bteSettings = EarthGeneratorSettings.parse(BTE_DEFAULT_SETTINGS);
    private final Gson gson = new GsonBuilder()
            .registerTypeAdapter(EarthGeneratorSettings.class, new EarthGeneratorSettingsAdapter())
            .create();

    @Test
    public void canSerializeBTESettings() {
        assertEquals(bteSettings.toString(), this.gson.toJson(this.bteSettings));
    }

    @Test
    public void canDeserializeBTESettings() {
        assertEquals(
                this.bteSettings.toString(),
                this.gson.fromJson(BTE_DEFAULT_SETTINGS, EarthGeneratorSettings.class).toString()
        );
    }

}