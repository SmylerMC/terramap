package net.smyler.smylib.resources;

import java.util.Optional;

/**
 * Resource metadata parsed from .mcmeta files.
 * Metadata sections specific to pack.mcmeta and irrelevant to resources (e.g. pack or filter)
 * are not exposed by SmyLib.
 *
 * @author Smyler
 */
public interface ResourceMetadata {

    Optional<TextureMetadata> texture();

    Optional<AnimationMetadata> animation();

    Optional<GuiMetadata> gui();

    Optional<VillagerMetadata> villager();

    Optional<DebugMetadata> debug();

}
