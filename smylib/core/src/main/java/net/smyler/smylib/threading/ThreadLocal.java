package net.smyler.smylib.threading;

import java.util.function.Supplier;

/**
 * A ThreadLocal that uses a supplier to get its initial value.
 *
 * @param <T>   the type of the enclosed object
 *
 * @author SmylerMC
 */
public class ThreadLocal<T> extends java.lang.ThreadLocal<T> {

    private final Supplier<T> supplier;

    public ThreadLocal(Supplier<T> supplier) {
        this.supplier = supplier;
    }

    @Override
    protected T initialValue() {
        return this.supplier.get();
    }

}
