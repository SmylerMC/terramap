package net.smyler.smylib.resources;

import java.io.Closeable;
import java.io.InputStream;
import net.smyler.smylib.Identifier;
import org.jetbrains.annotations.NotNull;

/**
 * A resource from the currently loaded resource packs.
 *
 * @author Smyler
 */
public interface Resource extends Closeable {

    /**
     * @return this resource's unique identifier
     */
    @NotNull Identifier identifier();

    /**
     * @return an {@link InputStream input stream} to this resource's binary content
     */
    @NotNull InputStream inputStream();

    /**
     * @return the metadata attached to this resource (usually from a .mcmeta file)
     */
    @NotNull ResourceMetadata metadata();

}
