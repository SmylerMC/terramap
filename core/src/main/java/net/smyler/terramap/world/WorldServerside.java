package net.smyler.terramap.world;

import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;

/**
 * A server-side world,
 * that is the authorizative source for the world's state and logic,
 * as opposed to a client-side world,
 * which is enslaved to a server world it mimics on the client.
 *
 * @author Smyler
 */
public interface WorldServerside extends World {

    /**
     * The directory where the world's file are saved.
     *
     * @return the world save directory.
     */
    @NotNull Path saveDirectory();

}
