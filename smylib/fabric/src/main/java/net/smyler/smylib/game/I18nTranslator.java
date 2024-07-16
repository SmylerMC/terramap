package net.smyler.smylib.game;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.language.I18n;

public class I18nTranslator implements Translator {

    private final Minecraft minecraft;

    public I18nTranslator(Minecraft minecraft) {
        this.minecraft = minecraft;
    }

    @Override
    public String language() {
        return this.minecraft.getLanguageManager().getSelected();
    }

    @Override
    public boolean hasKey(String key) {
        return I18n.exists(key);
    }

    @Override
    public String format(String key, Object... parameters) {
        return I18n.get(key, parameters);
    }

}
