package net.smyler.smylib.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.MultiBufferSource;
import net.smyler.smylib.game.WrappedMinecraft;
import net.smyler.smylib.gui.WrappedGuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static net.smyler.smylib.SmyLib.getGameClient;

@Mixin(GuiGraphics.class)
public class UiGraphicsInjectorMixin {

    @Inject(method = "<init>(Lnet/minecraft/client/Minecraft;Lnet/minecraft/client/renderer/MultiBufferSource$BufferSource;)V", at = @At("TAIL"))
    public void injectIntoClient(Minecraft minecraft, MultiBufferSource.BufferSource bufferSource, CallbackInfo ci) {
        if (!(getGameClient() instanceof WrappedMinecraft wrappedClient)) {
            return;
        }
        GuiGraphics thisObject = (GuiGraphics) (Object) this;
        wrappedClient.setUidDrawContext(new WrappedGuiGraphics(Minecraft.getInstance(), thisObject));
    }

}
