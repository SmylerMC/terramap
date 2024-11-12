package net.smyler.smylib.resources;

import net.minecraft.client.resources.IResource;
import net.minecraft.client.resources.data.AnimationMetadataSection;
import net.minecraft.client.resources.data.TextureMetadataSection;

import java.util.Arrays;
import java.util.Optional;

public class WrappedResourceMetadata implements ResourceMetadata {

    private final IResource resource;

    public WrappedResourceMetadata(IResource resource) {
        this.resource = resource;
    }

    @Override
    public Optional<TextureMetadata> texture() {
        TextureMetadataSection section = this.resource.getMetadata("texture");
        if (section == null) {
            return Optional.empty();
        }
        return Optional.of(new TextureMetadata(section.getTextureBlur(), section.getTextureClamp()));
    }

    @Override
    public Optional<AnimationMetadata> animation() {
        // Usine à gaz...
        AnimationMetadataSection section = this.resource.getMetadata("animation");
        if (section == null) {
            return Optional.empty();
        }
        AnimationMetadata.Frame[] frames = new AnimationMetadata.Frame[section.getFrameCount()];
        for (int i = 0; i < frames.length; i++) {
            if (section.frameHasTime(i)) {
                frames[i] = new AnimationMetadata.Frame(section.getFrameIndex(i), section.getFrameTime());
            } else {
                frames[i] = new AnimationMetadata.Frame(section.getFrameIndex(i));
            }
        }
        return Optional.of(new AnimationMetadata(
                section.getFrameTime(),
                section.getFrameWidth(),
                section.getFrameHeight(),
                section.isInterpolate(),
                Arrays.asList(frames)
        ));
    }

    @Override
    public Optional<GuiMetadata> gui() {
        GuiMetadataSection section = this.resource.getMetadata("gui");
        return Optional.ofNullable(section);
    }

    @Override
    public Optional<VillagerMetadata> villager() {
        VillagerMetadataSection section = this.resource.getMetadata("villager");
        return Optional.ofNullable(section);
    }

    @Override
    public Optional<DebugMetadata> debug() {
        DebugMetadataSection section = this.resource.getMetadata("debug");
        return Optional.ofNullable(section);
    }

    @Override
    public Optional<CursorResourceMetadata> cursor() {
        CursorMetadataSection section = this.resource.getMetadata("cursor");
        return Optional.ofNullable(section);
    }

}
