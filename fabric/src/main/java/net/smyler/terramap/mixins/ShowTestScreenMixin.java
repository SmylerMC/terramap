package net.smyler.terramap.mixins;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.TitleScreen;
import net.smyler.smylib.game.GameClient;
import net.smyler.smylib.gui.screen.test.TestScreen;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static java.lang.System.getProperty;
import static net.smyler.smylib.SmyLib.getGameClient;

@Mixin(Minecraft.class)
public class ShowTestScreenMixin {

    @Inject(method = "setScreen(Lnet/minecraft/client/gui/screens/Screen;)V", at = @At("HEAD"), cancellable = true)
    private void onSetScreen(Screen screen, CallbackInfo ci) {
        if (!"true".equalsIgnoreCase(getProperty("terramap.debug"))) {
            return;
        }
        if (screen instanceof TitleScreen) {
            GameClient gameClient = getGameClient();
            gameClient.displayScreen(new TestScreen(gameClient.getCurrentScreen()));
            ci.cancel();
        }
    }

}
