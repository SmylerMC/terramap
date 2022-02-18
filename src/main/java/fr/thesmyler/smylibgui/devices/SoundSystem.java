package fr.thesmyler.smylibgui.devices;

/**
 * An abstraction over the game's sound system.
 */
public interface SoundSystem {

    /**
     * Play a sound using its id.
     *
     * @param soundId   the id of the sound to play
     *
     * @throws IllegalArgumentException if the given ID is wrong
     */
    void playUiSound(String soundId) throws IllegalArgumentException;

    /**
     * Play the UI click sound.
     */
    void playClickSound();

    /**
     * Play the UI toast in sound.
     */
    void playToastInSound();

    /**
     * Play the UI toast out sound.
     */
    void playToastOutSound();

}
