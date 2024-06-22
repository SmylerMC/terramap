package net.smyler.terramap;

import net.smyler.smylib.Identifier;

public final class TerramapResources {

    public static final Identifier TEXTURES = new Identifier(Terramap.MOD_ID, "textures");
    public static final Identifier GUI_TEXTURES = TEXTURES.resolve("gui");
    public static final Identifier SPRITES = GUI_TEXTURES.resolve("sprites");

}
