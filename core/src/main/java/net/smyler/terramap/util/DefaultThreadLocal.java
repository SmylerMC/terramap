package net.smyler.terramap.util;

import java.util.function.Supplier;

public class DefaultThreadLocal<T> extends ThreadLocal<T> {

    private final Supplier<T> defaultValueSupplier;

    public DefaultThreadLocal(Supplier<T> defaultValueSupplier) {
        this.defaultValueSupplier = defaultValueSupplier;
    }

    @Override
    protected T initialValue() {
        return this.defaultValueSupplier.get();
    }

}
