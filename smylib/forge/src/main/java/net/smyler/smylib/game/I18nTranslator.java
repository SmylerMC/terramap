package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import net.smyler.smylib.game.Translator;
import net.minecraft.client.resources.I18n;

public class I18nTranslator implements Translator {

    @Override
    public String language() {
        return Minecraft.getMinecraft().gameSettings.language;
    }


    @Override
    public boolean hasKey(String key) {
        return I18n.hasKey(key);
    }

    @Override
    public String format(String key, Object... parameters) {
        return I18n.format(key, parameters);
    }

}
