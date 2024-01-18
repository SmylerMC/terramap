package net.smyler.smylib.game;

import net.smyler.smylib.threading.DefaultThreadLocal;

public class DummyTranslator implements Translator {

    private final DefaultThreadLocal<String> language = new DefaultThreadLocal<>(() -> "en-us");

    @Override
    public String language() {
        return this.language.get();
    }

    @Override
    public boolean hasKey(String key) {
        return true;
    }

    @Override
    public String format(String key, Object... parameters) {
        return key;
    }

    public void setLanguage(String lang) {
        this.language.set(lang);
    }

}
