package net.smyler.terramap.gui.widgets.markers;

import net.smyler.smylib.gui.sprites.Sprite;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static java.util.Objects.requireNonNull;

/**
 * A style that defines how an entity map marker should look and behave.
 *
 * @author Smyler
 */
public class EntityMarkerStyle implements Cloneable {
    private @Nullable Sprite sprite;

    public EntityMarkerStyle(@Nullable Sprite sprite) {
        this.sprite = sprite;
    }

    public EntityMarkerStyle() {
        this(null);
    }

    /**
     * Applies a give style on top of this one.
     *
     * @param other the other style to apply
     */
    public void updateFromOther(@NotNull EntityMarkerStyle other) {
        requireNonNull(other);
        if (other.sprite != null) {
            this.sprite = other.sprite;
        }
    }

    public Sprite sprite() {
        return this.sprite;
    }

    public EntityMarkerStyle clone() {
        try {
            // It is safe to return the call from super as Sprites are immutable
            return (EntityMarkerStyle) super.clone();
        } catch (CloneNotSupportedException e) {
            throw new IllegalStateException(this.getClass() + " should be cloneable", e);
        }
    }

}
