package net.smyler.smylib.game;

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
