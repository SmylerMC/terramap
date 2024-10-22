package net.smyler.smylib.resources;

import net.minecraft.client.resources.IResource;
import net.smyler.smylib.Identifier;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.io.InputStream;

public class WrappedResource implements Resource {

    private final Identifier identifier;
    private final IResource vanilla;

    public WrappedResource(@NotNull Identifier identifier, @NotNull IResource vanilla) {
        this.identifier = identifier;
        this.vanilla = vanilla;
    }

    @Override
    public @NotNull Identifier identifier() {
        return this.identifier;
    }

    @Override
    public @NotNull InputStream inputStream() {
        return this.vanilla.getInputStream();
    }

    @Override
    public @NotNull ResourceMetadata metadata() {
        return new WrappedResourceMetadata(this.vanilla);
    }

    @Override
    public void close() throws IOException {
        this.vanilla.close();
    }

}
