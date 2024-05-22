package fr.thesmyler.smylibgui;

import fr.thesmyler.terramap.TerramapMod;
import net.minecraft.util.ResourceLocation;

public final class SmyLibGuiTextures {

    private SmyLibGuiTextures() {}

    public static final ResourceLocation WIDGET_TEXTURES;

    static {
        WIDGET_TEXTURES = new ResourceLocation(TerramapMod.MODID, "textures/gui/widgets.png");
    }

}
