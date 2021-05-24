package fr.thesmyler.terramap;

/**
 * Represents the context in which a map exists, ie. it's either the fullscreen map, a minimap, or something else
 * 
 * @author SmylerMC
 *
 */
public enum MapContext {

    /**
     * Main fullscreen map
     */
    FULLSCREEN,

    /**
     * HUD minimap
     */
    MINIMAP,

    /**
     * Map preview
     */
    PREVIEW;

}
