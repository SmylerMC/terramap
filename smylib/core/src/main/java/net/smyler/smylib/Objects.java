package net.smyler.smylib;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Supplier;

import static java.util.Objects.requireNonNull;

/**
 * Extends features of {@link java.util.Objects}.
 * Implements methods missing in Java 8 while trying to be functionally equivalent to newer versions.
 *
 * @author Smyler
 */
public final class Objects {


    /**
     * Returns the first argument if it is non-null and the second argument if it is.
     *
     * @param object        an object
     * @param defaultObject a fallback object to return if <code>object</code> is null
     * @return              the first argument if it is not null or the second argument if it is
     * @param <T>           a type
     * @throws              NullPointerException if both arguments are null
     */
    public static <T> T requireNonNullElse(@Nullable T object, @NotNull T defaultObject) {
        if (object == null) {
            return requireNonNull(defaultObject, "Default object cannot be null when requiring a non null object.");
        }
        return object;
    }

    /**
     * Returns the first argument if it is non-null or and object supplied by the second argument if it is.
     *
     * @param object                an object
     * @param defaultObjectSupplier a fallback object supplier to query for a return value if <code>object</code> is null
     * @return                      the first argument if it is not null or an object supplied by the second argument if it is
     * @param <T>                   a type
     * @throws                      NullPointerException if both arguments are null or the first argument is null and the second argument supplied a null value.
     */
    public static <T> T requireNonNullElseGet(@Nullable T object, @NotNull Supplier<T> defaultObjectSupplier) {
        if (object == null) {
            requireNonNull(defaultObjectSupplier, "Default object supplier cannot be null when requiring a non null object.");
            return requireNonNull(defaultObjectSupplier.get(), "Supplied default object cannot be null when requiring a non null object.");
        }
        return object;
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, U, V> Optional<V> optionalBiMap(Optional<T> o1, Optional<U> o2, BiFunction<? super T, ? super U, ? extends V> biFunction) {
        if (o1.isEmpty() || o2.isEmpty()) {
            return Optional.empty();
        }
        T t = o1.get();
        U u = o2.get();
        return Optional.ofNullable(biFunction.apply(t, u));
    }

    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T, U, V> Supplier<Optional<V>> optionalBiMapSupplier(Optional<T> o1, Optional<U> o2, BiFunction<? super T, ? super U, ? extends V> biFunction) {
        return () -> optionalBiMap(o1, o2, biFunction);
    }

}
