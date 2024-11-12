package net.smyler.smylib.gui;

import net.smyler.smylib.Identifier;
import net.smyler.smylib.gui.sprites.Sprite;
import org.jetbrains.annotations.NotNull;

/**
 * A mouse cursor.
 * Animations are not supported.
 *
 * @author Smyler
 */
public interface Cursor {

    @NotNull Identifier identifier();

    int width();

    int height();

    int hotspotX();

    int hotspotY();

    Sprite sprite();

}
