package fr.thesmyler.smylibgui.devices.lwjgl2;

import fr.thesmyler.smylibgui.devices.Translator;
import net.minecraft.client.resources.I18n;

public class I18nTranslator implements Translator {

    @Override
    public boolean hasKey(String key) {
        return I18n.hasKey(key);
    }

    @Override
    public String format(String key, Object... parameters) {
        return I18n.format(key, parameters);
    }

}
