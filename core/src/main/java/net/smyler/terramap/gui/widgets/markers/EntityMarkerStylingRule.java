package net.smyler.terramap.gui.widgets.markers;

import org.jetbrains.annotations.NotNull;

import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.function.TrivialFunctions.truePredicate;
import static net.smyler.smylib.typing.Cast.uncheckedCast;

/**
 * A rule that specifies how to style a map entity marker.
 * This is similar to a CSS rule when used in a {@link EntityMarkerStylingRuleset ruleset}.
 * <br>
 * This is generic in order to work with any entity implementation.
 *
 * @param <T> the type this rule applies to
 */
public class EntityMarkerStylingRule<T> {
    final Selector<T> selector;
    final Function<T, EntityMarkerStyle> styleFunction;

    /**
     * Creates a new styling rule given a selector and a style function.
     * <ul>
     *  <li>The selector will be evaluated in order to check whether the rule should apply to a given entity.</li>
     *  <li>The style function is called once per entity, and only if the selector matched it.</li>
     * </ul>
     *
     * @param selector      the selector
     * @param styleFunction the styling function
     */
    public EntityMarkerStylingRule(Selector<T> selector, Function<T, EntityMarkerStyle> styleFunction) {
        this.selector = selector;
        this.styleFunction = styleFunction;
    }

    /**
     * Modifies a style according to this rule for a given entity.
     *
     * @param base      the style to modify
     * @param entity    the entity to apply the rule for
     */
    public void applyTo(EntityMarkerStyle base, @NotNull Object entity) {
        requireNonNull(entity);
        if (!this.selector.clazz.isAssignableFrom(entity.getClass())) {
            return;
        }
        // The uncheck cast here is fine as we checked the class first,
        // all constructor make sure the types T for the selector and the function matches,
        // and everything is immutable.
        T castedEntity = uncheckedCast(entity);
        if (!this.selector.predicate.test(castedEntity)) {
            return;
        }
        base.updateFromOther(this.styleFunction.apply(castedEntity));
    }

    /**
     * A rule selector defines whether a rule should be applied to an entity.
     * It has two components:
     * <ul>
     *  <li>a class the entity must be, extend or implement</li>
     *  <li>a predicate that must be true for the entity</li>
     * </ul>
     * <br>
     * While the predicate could check the entity by itself,
     * storing it alongside allows for simpler generic syntax and gives more room for optimizations.
     *
     * @param <T> the type this selector matches
     */
    public static class Selector<T> {
        final @NotNull Class<T> clazz;
        final @NotNull Predicate<T> predicate;

        public Selector(@NotNull Class<T> clazz, @NotNull Predicate<T> predicate) {
            requireNonNull(clazz);
            requireNonNull(predicate);
            this.clazz = clazz;
            this.predicate = predicate;
        }

        public Selector(@NotNull Class<T> clazz) {
            this(clazz, truePredicate());
        }
    }

}
