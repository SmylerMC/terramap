package net.smyler.smylib.proxy;

import net.minecraft.client.Minecraft;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.smyler.smylib.SmyLib;
import net.smyler.smylib.SmyLibForgeMod;
import net.smyler.smylib.game.WrappedMinecraft;
import net.smyler.smylib.gui.screen.test.TestScreen;


@SuppressWarnings("unused")  // Injected by FML into SmyLibForgeMod
public class SmyLibClientProxy implements SmyLibSidedProxy {

    @Override
    public void init(SmyLibForgeMod mod, FMLInitializationEvent event) {
        WrappedMinecraft game = new WrappedMinecraft(Minecraft.getMinecraft());
        MinecraftForge.EVENT_BUS.register(game);
        SmyLib.initializeGameClient(game, mod.logger());
        game.init();
        MinecraftForge.EVENT_BUS.register(TestScreen.class);
    }

}
