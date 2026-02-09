package fr.thesmyler.smylibgui;

import net.smyler.terramap.Terramap;

import net.minecraft.util.ResourceLocation;

public final class SmyLibGuiTextures {

    public static final ResourceLocation WIDGET_TEXTURES;

    static {
        WIDGET_TEXTURES = new ResourceLocation(Terramap.MOD_ID, "textures/gui/widgets.png");
    }

    private SmyLibGuiTextures() {
        throw new IllegalStateException("Utility class");
    }

}
