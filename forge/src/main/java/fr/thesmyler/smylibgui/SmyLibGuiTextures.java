package fr.thesmyler.smylibgui;

import net.minecraft.util.ResourceLocation;
import net.smyler.terramap.Terramap;

public final class SmyLibGuiTextures {

    private SmyLibGuiTextures() {}

    public static final ResourceLocation WIDGET_TEXTURES;

    static {
        WIDGET_TEXTURES = new ResourceLocation(Terramap.MOD_ID, "textures/gui/widgets.png");
    }

}
