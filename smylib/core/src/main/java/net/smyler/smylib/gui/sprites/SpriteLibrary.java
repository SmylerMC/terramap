package net.smyler.smylib.gui.sprites;

import net.smyler.smylib.Identifier;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static java.util.Objects.requireNonNull;
import static net.smyler.smylib.Preconditions.checkArgument;
import static net.smyler.smylib.Preconditions.checkState;

public class SpriteLibrary {

    private final Map<Identifier, Sprite> registered = new HashMap<>();

    public SpriteLibrary() {
        for(SmyLibSprites sprite: SmyLibSprites.values()) {
            try {
                SmyLibSprites other = SmyLibSprites.valueOf(sprite.identifier.path.toUpperCase(Locale.ROOT));
                checkArgument(other == sprite, "");
            } catch (IllegalArgumentException e) {
                throw new IllegalStateException("SmyLib sprite enum name does not match identifier: " + sprite.identifier);
            }
            this.registerSprite(sprite.identifier, sprite.sprite);
        }
    }

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
        return this.registered.get(requireNonNull(identifier));
    }

    public Sprite getSprite(@NotNull String identifier) {
        return this.registered.get(Identifier.parse(identifier));
    }

}
