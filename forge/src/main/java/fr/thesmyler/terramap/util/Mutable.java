package fr.thesmyler.terramap.util;

/**
 * A mutable object that can be converted to an immutable one.
 *
 * @param <T> the type of the immutable counterpart of the implementing class.
 * @author SmylerMC
 */
public interface Mutable<T extends Immutable<?>> {

    /**
     * @return an immutable object equivalent to this one.
     */
    T getImmutable();

}
