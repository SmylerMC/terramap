package net.smyler.terramap.gui.widgets.markers;

import net.smyler.smylib.gui.sprites.Sprite;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.function.TrivialFunctions.constantFunction;


/**
 * A set of cascading styling rules to apply to entity map markers.
 * Behaves like aa CSS stylesheet.
 *
 * @author Smyler
 */
public class EntityMarkerStylingRuleset {
    private final List<EntityMarkerStylingRule<?>> rules = new ArrayList<>();

    /**
     * Creates a new ruleset with a default style that applies the given sprite.
     *
     * @param defaultSprite the sprite for the default style
     */
    public EntityMarkerStylingRuleset(@NotNull Sprite defaultSprite) {
        this.add(Object.class, requireNonNull(defaultSprite));
    }

    /**
     * Evaluates the rules in this ruleset to compute the style to apply to an entity's marker.
     *
     * @param entity the entity
     * @return the style to apply to the marker
     */
    public EntityMarkerStyle getStyleFor(@NotNull Object entity) {
        requireNonNull(entity);
        EntityMarkerStyle style = new EntityMarkerStyle();
        for (EntityMarkerStylingRule<?> rule : this.rules) {
            rule.applyTo(style, entity);
        }
        return style;
    }

    public void add(EntityMarkerStylingRule<?> rule) {
        this.rules.add(rule);
    }

    public <T> void add(Class<T> clazz, Predicate<T> predicate, Function<T, EntityMarkerStyle> styleFunction) {
        this.add(new EntityMarkerStylingRule<>(
                new EntityMarkerStylingRule.Selector<>(clazz, predicate),
                styleFunction
        ));
    }

    public <T> void add(Class<T> clazz, Predicate<T> predicate, EntityMarkerStyle style) {
        this.add(clazz, predicate, constantFunction(style.clone()));
    }

    public <T> void add(Class<T> clazz, Predicate<T> predicate, Sprite sprite) {
        this.add(clazz, predicate, constantFunction(new EntityMarkerStyle(sprite)));
    }

    public <T> void add(Class<T> clazz, EntityMarkerStyle style) {
        this.add(new EntityMarkerStylingRule<>(new EntityMarkerStylingRule.Selector<>(clazz), constantFunction(style.clone())));
    }

    public <T> void add(Class<T> clazz, Sprite sprite) {
        this.add(clazz, new EntityMarkerStyle(sprite));
    }

}
