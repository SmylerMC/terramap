package net.smyler.smylib.function;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Utility class for functional interfaces.
 *
 * @author Smyler
 */
public final class TrivialFunctions {

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
     * Constant function factory.
     *
     * @param constantValue the value to return
     * @return a function that always returns the same value
     * @param <P> the parameter type of the function
     * @param <R> the return type of the function
     */
    public static <P, R> Function<P, R> constantFunction(R constantValue) {
        return arg -> constantValue;
    }

    private  TrivialFunctions() {
        throw new IllegalStateException("Utility class");
    }

}
