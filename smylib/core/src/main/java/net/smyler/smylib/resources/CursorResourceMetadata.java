package net.smyler.smylib.resources;

/**
 * Custom resource metadata added by SmyLib.
 * Allows to define cursor properties for textures meant to be used as such.
 */
public class CursorResourceMetadata {

    private final int hotspotX, hotspotY;

    public CursorResourceMetadata(int hotspotX, int hotspotY) {
        this.hotspotX = hotspotX;
        this.hotspotY = hotspotY;
    }

    public int hotspotX() {
        return this.hotspotX;
    }

    public int hotspotY() {
        return this.hotspotY;
    }

}
