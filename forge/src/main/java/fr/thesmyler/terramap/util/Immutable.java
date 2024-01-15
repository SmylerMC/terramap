package fr.thesmyler.terramap.util;

/**
 * An immutable object that can be converted to a mutable one.
 *
 * @param <T> the type of the mutable counterpart of the implementing class.
 * @author SmylerMC
 */
public interface Immutable<T extends Mutable<?>> {

    /**
     * @return a mutable object equivalent to this one.
     */
    T getMutable();

}
