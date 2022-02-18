package fr.thesmyler.smylibgui.devices.dummy;

import fr.thesmyler.smylibgui.devices.SoundSystem;

/**
 * This is so much a dummy that it doesn't do anything at all.
 */
public class DummySoundSystem implements SoundSystem {

    @Override
    public void playUiSound(String soundId) throws IllegalArgumentException {

    }

    @Override
    public void playClickSound() {

    }

    @Override
    public void playToastInSound() {

    }

    @Override
    public void playToastOutSound() {

    }

}
