package net.smyler.smylib.function;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * Utility class for functional interfaces.
 *
 * @author Smyler
 */
public final class TrivialFunctions {

    private static final Runnable NO_OP = () -> {
        // Do nothing
    };

    /**
     * @return a predicate that always returns true
     * @param <T> the parameter type of the predicate
     */
    public static <T> Predicate<T> truePredicate() {
        return t -> true;
    }

    /**
     * @return a predicate that always returns false
     * @param <T> the parameter type of the predicate
     */
    public static <T> Predicate<T> falsePredicate() {
        return t -> false;
    }

    /**
     * @return a runnable that does nothing
     */
    public static Runnable noOp() {
        return NO_OP;
    }

    /**
     * @return a consumer that does nothing
     * @param <T> the parameter type of the consumer
     */
    public static <T> Consumer<T> noOpConsumer() {
        return t -> {
            // Do nothing
        };
    }

    /**
     * Constant function factory.
     *
     * @param constantValue the value to return
     * @return a function that always returns the same value
     * @param <P> the parameter type of the function
     * @param <R> the return type of the function
     */
    public static <P, R> Function<@Nullable P, @Nullable R> constantFunction(@Nullable R constantValue) {
        return arg -> constantValue;
    }

    /**
     * @return a function that always returns null
     * @param <P> the parameter type of the function
     * @param <R> the return type of the function
     */
    public static <P, R> Function<@Nullable P, @Nullable R> nullFunction() {
        return arg -> null;
    }

    /**
     * Constant function factory. The function shall not return null.
     *
     * @param constantValue the non-null value to return
     * @return a function that always returns the same non-null value
     * @param <P> the parameter type of the function
     * @param <R> the return type of the function
     * @throws NullPointerException if the constant value is null
     */
    public static <P, R> Function<@Nullable P, @NotNull R> nonNullConstantFunction(@NotNull R constantValue) {
        requireNonNull(constantValue);
        return arg -> constantValue;
    }

    private TrivialFunctions() {
        throw new IllegalStateException("Utility class");
    }

}
