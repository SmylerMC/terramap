package net.smyler.smylib.mixins;

import net.minecraft.client.gui.Font;
import net.smyler.smylib.gui.PatchedFont;
import org.joml.Matrix4f;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;


@Mixin(Font.class)
public abstract class FontMixinNoShadowOffset implements PatchedFont {

    @Unique
    private boolean cancelShadowOffset = false;

    @Redirect(
            method = "drawInternal(Ljava/lang/String;FFIZLorg/joml/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;Lnet/minecraft/client/gui/Font$DisplayMode;IIZ)I",
            at = @At(
                    value = "INVOKE",
                    target = "Lorg/joml/Matrix4f;translate(Lorg/joml/Vector3fc;)Lorg/joml/Matrix4f;",
                    remap = false  // Method is from JOML, it isn't obfuscated
            )
    )
    public Matrix4f applyShadowOffset(Matrix4f matrix, Vector3fc offset) {
        if (!this.cancelShadowOffset) {
            matrix.translate(offset);
        }
        return matrix;
    }

    public void smylib$setCancelShadowOffset(boolean cancelShadowOffset) {
        this.cancelShadowOffset = cancelShadowOffset;
    }

}
