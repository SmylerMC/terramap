package fr.thesmyler.smylibgui;

import fr.thesmyler.terramap.TerramapMod;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

public final class SmyLibGuiTextures {

    private SmyLibGuiTextures() {}

    public static final ResourceLocation BUTTON_TEXTURES, OPTIONS_BACKGROUND, STAT_ICONS, ICONS, WIDGET_TEXTURES;

    static {
        BUTTON_TEXTURES = ObfuscationReflectionHelper.getPrivateValue(GuiButton.class, null, "field_146122_a");
        OPTIONS_BACKGROUND = Gui.OPTIONS_BACKGROUND;
        STAT_ICONS = Gui.STAT_ICONS;
        ICONS = Gui.ICONS;
        WIDGET_TEXTURES = new ResourceLocation(TerramapMod.MODID, "textures/gui/widgets.png");
    }

}
