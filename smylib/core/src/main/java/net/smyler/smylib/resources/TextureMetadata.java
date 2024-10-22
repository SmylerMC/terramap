package net.smyler.smylib.resources;


/**
 * Still texture metadata.
 * Gives the game indications on how to handle textures in game.
 *
 * @see <a href="https://minecraft.wiki/w/Resource_pack#GUI">the Minecraft wiki</a> for more information.
 *
 * @author Smyler
 */
public class TextureMetadata {

    private final boolean blur;
    private final boolean clamp;

    public TextureMetadata(Boolean blur, Boolean clamp) {
        this.blur = blur;
        this.clamp = clamp;
    }

    public boolean blur() {
        return this.blur;
    }

    public boolean clamp() {
        return this.clamp;
    }

}
