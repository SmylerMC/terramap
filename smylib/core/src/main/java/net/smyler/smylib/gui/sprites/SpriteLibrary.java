package net.smyler.smylib.gui.sprites;

import net.smyler.smylib.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;

import static java.util.Collections.unmodifiableMap;
import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.Preconditions.checkState;
import static net.smyler.smylib.SmyLib.getLogger;

public class SpriteLibrary {

    private final Map<Identifier, Sprite> registered = new HashMap<>();
    private final Map<Identifier, Sprite> readOnly = unmodifiableMap(this.registered);

    public void registerSprite(@NotNull Identifier identifier, @NotNull Sprite sprite) {
        Sprite existing = this.registered.putIfAbsent(
                requireNonNull(identifier),
                requireNonNull(sprite)
        );
        checkState(existing == null, "Tried to register the same sprite twice: " + identifier);
    }

    public void registerSprite(@NotNull String identifier, @NotNull Sprite sprite) {
        this.registerSprite(
                Identifier.parse(identifier),
                sprite
        );
    }

    public Sprite getSprite(@NotNull Identifier identifier) {
        Sprite sprite = this.readOnly.get(identifier);
        if (sprite == null) {
            getLogger().warn("Tried to get missing sprite for identifier {}", identifier);
        }
        return sprite;
    }

    public Sprite getSprite(@NotNull String identifier) {
        return this.getSprite(Identifier.parse(identifier));
    }

    /**
     * Allow access to all registered sprites.
     *
     * @return a read-only vue over the registered sprites
     */
    public Map<Identifier, Sprite> getSprites() {
        return this.readOnly;
    }

}
