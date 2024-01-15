package fr.thesmyler.smylibgui.devices;

/**
 * Wrapper around whatever is doing translation.
 */
public interface Translator {

    /**
     * Indicates whether the translator knows about the given key.
     *
     * @param key   the key
     * @return whether the translator can translate the given key
     */
    boolean hasKey(String key);

    /**
     * Translates the given key, with the given parameters.
     *
     * @param key           the key to translate
     * @param parameters    the translation parameters
     * @return the localized string
     */
    String format(String key, Object... parameters);

}
