package fr.thesmyler.smylibgui.devices.dummy;

import fr.thesmyler.smylibgui.devices.Translator;

public class DummyTranslator implements Translator {

    @Override
    public boolean hasKey(String key) {
        return true;
    }

    @Override
    public String format(String key, Object... parameters) {
        return key;
    }

}
