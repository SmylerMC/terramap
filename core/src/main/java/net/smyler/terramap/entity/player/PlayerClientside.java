package net.smyler.terramap.entity.player;

import net.smyler.smylib.Identifier;

/**
 * A player with client-side rendering properties
 * (most importantly, a skin).
 *
 * @author Smyler
 */
public interface PlayerClientside extends Player {

    /**
     * The player's skin.
     *
     * @return the player's skin
     */
    Identifier skin();

}
