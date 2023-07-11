package fr.thesmyler.terramap.config;

import fr.thesmyler.terramap.gui.screens.SavedMainScreenState;
import fr.thesmyler.terramap.maps.SavedMapState;
import net.buildtheearth.terraplusplus.generator.EarthGeneratorSettings;

public class SavedTerramapState {

    public boolean hasShownWelcome = false;
    public EarthGeneratorSettings generatorSettings = null;
    public SavedMapState minimap = new SavedMapState();
    public SavedMainScreenState mainScreen = new SavedMainScreenState();

}
