package net.smyler.smylib.resources;

import net.minecraft.client.resources.data.IMetadataSection;

public class CursorMetadataSection extends CursorResourceMetadata implements IMetadataSection {

    public CursorMetadataSection(int hotspotX, int hotspotY) {
        super(hotspotX, hotspotY);
    }

}
